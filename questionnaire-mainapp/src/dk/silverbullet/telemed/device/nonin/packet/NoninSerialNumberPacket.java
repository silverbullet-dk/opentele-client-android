package dk.silverbullet.telemed.device.nonin.packet;

import android.util.Log;
import dk.silverbullet.telemed.utils.Util;

import java.io.IOException;
import java.util.Arrays;

public class NoninSerialNumberPacket extends NoninPacket {
    private static final String TAG = Util.getTag(NoninSerialNumberPacket.class);

    private static final int DATALENGTH_POSITON = 2;
    private static final int EXPECTED_DATA_LENGTH = 0x0b;

    private static final int CHECKSUM_POSITION = 13;
    private static final int CHECKSUM_RANGE_START = 3;
    private static final int CHECKSUM_RANGE_END = 12;

    private static final int SERIAL_POSITION_START = 4;
    private static final int SERIAL_POSITION_END = 12;


    public String serial;
    private Integer[] data;

    @SuppressWarnings("unused")
    public NoninSerialNumberPacket(Integer[] data) throws IOException {
        Log.d(TAG, "Building serial number packet");

        this.data = data;
        failOnInvalidDataLength();
        failOnInvalidChecksum();

        serial = parseSerial();


    }

    private String parseSerial() {
        Integer[] serialData = Arrays.copyOfRange(data, SERIAL_POSITION_START, SERIAL_POSITION_END + 1);

        String serial  = "";
        for(int digit: serialData) {
            serial += (char)digit;
        }

        return serial;
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
        if(data[DATALENGTH_POSITON] != EXPECTED_DATA_LENGTH) {
            throw new IOException("Expected data length indication to be:" + EXPECTED_DATA_LENGTH + ", but was:" + Integer.toHexString(data[DATALENGTH_POSITON]));
        }
    }
}
