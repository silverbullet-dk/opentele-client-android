package dk.silverbullet.telemed.device.nonin.packet.input;

import java.io.IOException;

import dk.silverbullet.telemed.device.UnexpectedPacketFormatException;
import dk.silverbullet.telemed.device.continua.packet.MeasurementOutOfLimitsException;
import dk.silverbullet.telemed.device.continua.packet.input.ConfirmedMeasurementDataPacket;
import dk.silverbullet.telemed.device.continua.packet.input.OrderedByteReader;

public class NoninConfirmedMeasurementDataPacket extends ConfirmedMeasurementDataPacket {
    private int saturation;
    private int pulse;

    public NoninConfirmedMeasurementDataPacket(byte[] contents) throws IOException {
        super(contents);
    }

    public NoninConfirmedMeasurementDataPacket(int saturation, int pulse, int invokeId, int eventTime) {
        super(invokeId, eventTime);

        this.saturation = saturation;
        this.pulse = pulse;
    }

    @Override
    protected void readObjects(int eventType, OrderedByteReader in) throws IOException {
        if (eventType != MDC_NOTI_SCAN_REPORT_FIXED) {
            throw new UnexpectedPacketFormatException("Unexpected event-type 0x" + Integer.toString(eventType, 16)
                    + "(expected 0x0D1D)");
        }

        checkShort(in, "ScanReportInfoFixed.obs-scan-fixed.count", 0x0002);
        checkShort(in, "ScanReportInfoFixed.obs-scan-fixed.length", 0x001C);

        checkShort(in, "ScanReportInfoFixed.obs-scan-fixed.value[0].obj-handle", 0x0001);
        checkShort(in, "ScanReportInfoFixed.obs-scan-fixed.value[0].obs-val-data.length", 0x000A);
        saturation = in.readShort(); // Basic-Nu-Observed-Value
        in.readLong(); // Absolute-Time-Stamp, BCD encoded UTC-likeg
        checkShort(in, "ScanReportInfoFixed.obs-scan-fixed.value[1].obj-handle", 0x000A);
        checkShort(in, "ScanReportInfoFixed.obs-scan-fixed.value[1].obs-val-data.length", 0x000A);
        pulse = in.readShort();
        in.readLong(); // Absolute-Time-Stamp, BCD encoded UTC-like

        // Check that the given values are within valid domain:

        if (saturation < 50 || saturation > 105) { // Arbitrary values considered far outside measuring domain
            throw new MeasurementOutOfLimitsException("Invalid saturation value: " + saturation);
        }

        if (pulse < 10 || pulse > 350) { // Arbitrary values considered far outside measuring domain
            throw new MeasurementOutOfLimitsException("Invalid pulse value: " + pulse);
        }
    }

    public int getPulse() {
        return pulse;
    }

    public int getSaturation() {
        return saturation;
    }

    @Override
    public String toString() {
        return "NoninConfirmedMeasurementDataPacket [getPulse()=" + getPulse() + ", getSaturation()=" + getSaturation()
                + ", getInvokeId()=" + getInvokeId() + ", getEventTime()=" + getEventTime() + ", getTag()=" + getTag()
                + ", length()=" + length() + "]";
    }
}
