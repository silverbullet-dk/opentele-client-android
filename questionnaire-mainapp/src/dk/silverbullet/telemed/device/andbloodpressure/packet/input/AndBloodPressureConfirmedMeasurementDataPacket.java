package dk.silverbullet.telemed.device.andbloodpressure.packet.input;

import java.io.IOException;

import dk.silverbullet.telemed.device.UnexpectedPacketFormatException;
import dk.silverbullet.telemed.device.continua.packet.input.ConfirmedMeasurementDataPacket;
import dk.silverbullet.telemed.device.continua.packet.input.OrderedByteReader;

public class AndBloodPressureConfirmedMeasurementDataPacket extends ConfirmedMeasurementDataPacket {
    private static final long NO_TIME = -1;

    private long newestTimestampForBloodPressure;
    private long newestTimestampForPulse;

    private int systolicBloodPressure;
    private int diastolicBloodPressure;
    private int meanArterialPressure;
    private int pulse;

    public AndBloodPressureConfirmedMeasurementDataPacket(byte[] contents) throws IOException {
        super(contents);
    }

    @Override
    protected void readObjects(int eventType, OrderedByteReader in) throws IOException {
        if (eventType != MDC_NOTI_SCAN_REPORT_FIXED) {
            throw new UnexpectedPacketFormatException("Unexpected event-type 0x" + Integer.toString(eventType, 16)
                    + "(expected 0x0D1D)");
        }

        newestTimestampForBloodPressure = NO_TIME;
        newestTimestampForPulse = NO_TIME;

        int numberOfMeasurements = in.readShort(); // ScanReportInfoFixed.obs-scan-fixed.count
        in.readShort(); // ScanReportInfoFixed.obs-scan-fixed.length

        for (int i = 0; i < numberOfMeasurements; i++) {
            int objectHandle = in.readShort(); // ScanReportInfoFixed.obs-scan-fixed.value[0].obj-handle
            if (objectHandle == 1) {
                readBloodPressureMeasurement(in);
            } else if (objectHandle == 2) {
                readPulseMeasurement(in);
            } else {
                throw new UnexpectedPacketFormatException("Unexpected object handle: '" + objectHandle + "'");
            }
        }
    }

    private void readBloodPressureMeasurement(OrderedByteReader in) throws IOException {
        checkShort(in, "ScanReportInfoFixed.obs-scan-fixed.value[0].obs-val-data.length", 18);
        checkShort(in, "Compound Object count", 3);
        checkShort(in, "Compound Object length", 6);
        int currentSystolicBloodPressure = in.readShort();
        int currentDiastolicBloodPressure = in.readShort();
        int currentMeanArterialPressure = in.readShort();
        long timestamp = in.readLong();
        if (timestamp > newestTimestampForBloodPressure) {
            systolicBloodPressure = currentSystolicBloodPressure;
            diastolicBloodPressure = currentDiastolicBloodPressure;
            meanArterialPressure = currentMeanArterialPressure;
            newestTimestampForBloodPressure = timestamp;
        }
    }

    private void readPulseMeasurement(OrderedByteReader in) throws IOException {
        checkShort(in, "ScanReportInfoFixed.obs-scan-fixed.value[0].obs-val-data.length", 10);
        int currentPulse = in.readShort();
        long timestamp = in.readLong();
        if (timestamp > newestTimestampForPulse) {
            pulse = currentPulse;
            newestTimestampForPulse = timestamp;
        }
    }

    public int getSystolicBloodPressure() {
        return systolicBloodPressure;
    }

    public int getDiastolicBloodPressure() {
        return diastolicBloodPressure;
    }

    public int getMeanArterialPressure() {
        return meanArterialPressure;
    }

    public int getPulse() {
        return pulse;
    }

    public boolean hasBloodPressure() {
        return newestTimestampForBloodPressure != NO_TIME;
    }

    public boolean hasPulse() {
        return newestTimestampForPulse != NO_TIME;
    }

    public long getBloodPressureTimestamp() {
        return newestTimestampForBloodPressure;
    }

    public long getPulseTimestamp() {
        return newestTimestampForPulse;
    }

    @Override
    public String toString() {
        return "AndBloodPressureConfirmedMeasurementDataPacket [newestTimestampForBloodPressure="
                + newestTimestampForBloodPressure + ", newestTimestampForPulse=" + newestTimestampForPulse
                + ", systolicBloodPressure=" + systolicBloodPressure + ", diastolicBloodPressure="
                + diastolicBloodPressure + ", meanArterialPressure=" + meanArterialPressure + ", pulse=" + pulse + "]";
    }
}
