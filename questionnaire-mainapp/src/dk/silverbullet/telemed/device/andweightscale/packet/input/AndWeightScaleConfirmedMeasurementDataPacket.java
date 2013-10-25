package dk.silverbullet.telemed.device.andweightscale.packet.input;

import java.io.IOException;

import dk.silverbullet.telemed.device.UnexpectedPacketFormatException;
import dk.silverbullet.telemed.device.andweightscale.Weight;
import dk.silverbullet.telemed.device.andweightscale.Weight.Unit;
import dk.silverbullet.telemed.device.continua.packet.input.ConfirmedMeasurementDataPacket;
import dk.silverbullet.telemed.device.continua.packet.input.OrderedByteReader;

public class AndWeightScaleConfirmedMeasurementDataPacket extends ConfirmedMeasurementDataPacket {
    private static final int NUMBER_OF_DECIMALS_1 = 0xFF00;
    private static final int NUMBER_OF_DECIMALS_2 = 0xFE00;
    private static final int MDC_DIM_KILO_G = 0x06C3;
    private static final int MDC_DIM_LB = 0x06E0;
    private static final long NO_TIME = -1;

    private long newestTimestamp;
    private Weight newestWeight;

    public AndWeightScaleConfirmedMeasurementDataPacket(byte[] contents) throws IOException {
        super(contents);
    }

    protected void readObjects(int eventType, OrderedByteReader in) throws IOException {
        if (eventType != MDC_NOTI_SCAN_REPORT_VAR) {
            throw new UnexpectedPacketFormatException("Unexpected event-type 0x" + Integer.toString(eventType, 16)
                    + " (expected 0x0D1E)");
        }
        newestTimestamp = NO_TIME;

        int numberOfMeasurements = in.readShort(); // ScanReportInfoFixed.obs-scan-fixed.count
        in.readShort(); // ScanReportInfoFixed.obs-scan-fixed.length

        for (int i = 0; i < numberOfMeasurements; i++) {
            checkShort(in, "obj-handle", 0x0001);
            checkShort(in, "Number of attributes?", 0x0003);
            checkShort(in, "Length of measurement", 0x001A);
            checkShort(in, "??", 0x0A56);

            float weight = readWeight(in);
            long timestamp = readTimestamp(in);
            Unit unit = readUnit(in);

            if (timestamp > newestTimestamp) {
                newestWeight = new Weight(weight, unit);
                newestTimestamp = timestamp;
            }
        }
    }

    private float readWeight(OrderedByteReader in) throws IOException {
        checkShort(in, "Length of nu-observed-value", 0x0004);

        float divisionFactor = readDivisionFactor(in);
        int weightInSomeUnit = in.readShort() & 0xFFFF /* Make unsigned */;
        return weightInSomeUnit / divisionFactor;
    }

    private float readDivisionFactor(OrderedByteReader in) throws IOException {
        int baseCode = in.readShort();
        if (baseCode == NUMBER_OF_DECIMALS_1) {
            return 10;
        }
        if (baseCode == NUMBER_OF_DECIMALS_2) {
            return 100;
        }
        throw new UnexpectedPacketFormatException("Unexpected base code 0x" + Integer.toString(baseCode, 16)
                + "(expected 0xFF00 or 0xFE00)");
    }

    private long readTimestamp(OrderedByteReader in) throws IOException {
        checkShort(in, "MDC_ATTR_TIME_STAMP_ABS", 0x0990);
        checkShort(in, "Length of timestamp", 0x0008);
        return in.readLong();
    }

    private Unit readUnit(OrderedByteReader in) throws IOException {
        checkShort(in, "MDC_ATTR_UNIT_CODE", 0x0996);
        checkShort(in, "Length of unit code", 0x0002);

        int unitCode = in.readShort();
        if (unitCode == MDC_DIM_KILO_G) {
            return Unit.KG;
        }
        if (unitCode == MDC_DIM_LB) {
            return Unit.LBS;
        }
        throw new UnexpectedPacketFormatException("Unexpected unit code 0x" + Integer.toString(unitCode, 16)
                + "(expected 0x06C3 or 0x06E0)");
    }

    public Weight getWeight() {
        return newestWeight;
    }

    public long getTimestamp() {
        return newestTimestamp;
    }

    @Override
    public String toString() {
        return "AndWeightScaleConfirmedMeasurementDataPacket [newestTimestamp=0x" + Long.toString(newestTimestamp, 16)
                + ", newestWeight=" + newestWeight + "]";
    }
}
