package dk.silverbullet.telemed.device.nonin.packet;

import android.util.Log;
import dk.silverbullet.telemed.utils.Util;

import java.io.IOException;
import java.util.Arrays;

public class NoninMeasurementPacket extends NoninPacket {
    private static final String TAG = Util.getTag(NoninMeasurementPacket.class);

    //All positions are offset by one because we wont see the leading 0x00 byte
    private static final int SPO2_POSITION = 18;
    private static final int PULSE_MOST_SIGNIFICANT_BYTE_POSITION = 15;
    private static final int PULSE_LEAST_SIGNIFICANT_BYTE_POSITION = 16;

    private static final int STATUS_MOST_SIGNIFICANT_BYTE_POSITON = 13;
    private static final int STATUS_LEAST_SIGNIFICANT_BYTE_POSITON = 14;

    private static final int STATUS_MEASUREMENT_IS_FROM_MEMORY_FLAG = 0x10;
    private static final int STATUS_MEASUREMENT_MISSING_FLAG = 0x1;

    private static final int CHECKSUM_RANGE_START = 5;
    private static final int CHECKSUM_RANGE_END = 18;
    private static final int CHECKSUM_POSITION = 19;
    private static final int DATALENGTH__MOST_SIGNIFICANT_BYTE_POSITON = 3;
    private static final int DATALENGTH__LEAST_SIGNIFICANT_BYTE_POSITON = 4;
    private static final int EXPECTED_DATA_LENGTH_MOST_SIGNIFICANT_BYTE = 0x0;
    private static final int EXPECTED_DATA_LENGTH_LEAST_SIGNIFICANT_BYTE = 0xe;


    private Integer[] data;
    public int sp02;
    public int pulse;
    public boolean isFromMemory;
    public boolean isDataMissing;

    @SuppressWarnings("unused")
    public NoninMeasurementPacket(Integer[] data) throws IOException {
        this.data = data;
        if(data == null) {
            throw new IOException("Got null data");
        }
        Log.d(TAG, "Building measurement packet");

        failOnInvalidDataLength();
        failOnInvalidChecksum();

        this.isDataMissing = isMeasurementMissing();
        this.sp02 = getSpO2();
        this.pulse = getPulse();
        this.isFromMemory = getIsFromMemoryStatus();
        
    }

    private boolean isMeasurementMissing() {
        if((data[STATUS_MOST_SIGNIFICANT_BYTE_POSITON] & STATUS_MEASUREMENT_MISSING_FLAG) == STATUS_MEASUREMENT_MISSING_FLAG) {
            return true;
        }

        if(this.getPulse() == 511) {
            return true;
        }

        if (this.getSpO2() == 127) {
            return true;
        }

        return false;
    }


    private int getSpO2() {
        return data[SPO2_POSITION];
    }

    private int getPulse() {
        String mostSignificantByte = Integer.toHexString(data[PULSE_MOST_SIGNIFICANT_BYTE_POSITION]);
        String leastSignificantByte = Integer.toHexString(data[PULSE_LEAST_SIGNIFICANT_BYTE_POSITION]);

        return  Integer.parseInt(mostSignificantByte + leastSignificantByte, 16);
    }

    private boolean getIsFromMemoryStatus() {
        return (data[STATUS_LEAST_SIGNIFICANT_BYTE_POSITON] & STATUS_MEASUREMENT_IS_FROM_MEMORY_FLAG) == STATUS_MEASUREMENT_IS_FROM_MEMORY_FLAG;
    }

    private void failOnInvalidChecksum() throws IOException {
        Integer[] dataUnderChecksum = Arrays.copyOfRange(data, CHECKSUM_RANGE_START, CHECKSUM_RANGE_END + 1); // adding 1 because range end is exclusive
        int calculatedChecksum = calculateChecksum(dataUnderChecksum);
        int expectedChecksum = data[CHECKSUM_POSITION];

        if(calculatedChecksum != expectedChecksum) {
            throw new IOException("Expected checksum to be:" + expectedChecksum + " but calculated checksum was:" + calculatedChecksum);
        }
    }

    private void failOnInvalidDataLength() throws IOException {
        if(data[DATALENGTH__MOST_SIGNIFICANT_BYTE_POSITON] != EXPECTED_DATA_LENGTH_MOST_SIGNIFICANT_BYTE) {
            throw new IOException("Expected data length(most significant byte) indication to be:" + EXPECTED_DATA_LENGTH_MOST_SIGNIFICANT_BYTE + ", but was:" + Integer.toHexString(data[DATALENGTH__MOST_SIGNIFICANT_BYTE_POSITON]));
        }

        if(data[DATALENGTH__LEAST_SIGNIFICANT_BYTE_POSITON] != EXPECTED_DATA_LENGTH_LEAST_SIGNIFICANT_BYTE) {
            throw new IOException("Expected data length(least significant byte) indication to be:" + EXPECTED_DATA_LENGTH_LEAST_SIGNIFICANT_BYTE + ", but was:" + Integer.toHexString(data[DATALENGTH__LEAST_SIGNIFICANT_BYTE_POSITON]));
        }
    }
}
