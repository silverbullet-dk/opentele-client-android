package dk.silverbullet.telemed.device.nonin.packet;

import java.io.IOException;

import android.util.Log;
import dk.silverbullet.telemed.utils.Util;

public class NoninPacketFactory {
    private static final String TAG = Util.getTag(NoninPacketFactory.class);
    private static final int OPCODE_POSITON = 1;
    private static final short OPCODE_SERIAL_NUMBER = 0xF4;

    public static NoninSerialNumberPacket serialNumberPacket(Integer[] data) throws IOException {
        if(data[OPCODE_POSITON] == OPCODE_SERIAL_NUMBER) {
            return new NoninSerialNumberPacket(data);
        } else {
            throw new IOException("Unknown OpCode:" + Integer.toHexString(data[OPCODE_POSITON]));
        }
    }

    public static NoninMeasurementPacket measurementPacket(Integer[] read) throws IOException {
        return new NoninMeasurementPacket(read);
    }

    // Check a data package
    public static boolean checkGenericResponse(Integer[] data, int length, short opCode, int idCode) {
        // We know what the 4 first bytes should be
        if(data[0] != 0x02
        || data[1] != opCode
        || data[2] != length-4
        || data[3] != idCode) {
            // Some data in the header failed to pass
            Log.d(TAG, "Header didn't pass as valid");
            return false;
        }

        // Calculate if the checksum passes
        int checkSum = data[3];
        int index = 4;

        while(0 < (length-- -6)) {
            checkSum += data[index++];
        }

        // Check the checksum and the end byte
        if(data[index++] != (0xFF & checkSum)
        || data[index++] != 0x03) {
            Log.d(TAG, "Checksum or ETX failed, got "+(0xFF&checkSum)+" expected "+data[index-2]+"as checksum");
            return false;
        }

        // If we didn't find any fails, then we assume we have a success
        return true;
    }

    public static boolean isSerialNumberPacket(Integer[] data, int dataLength) {

        return checkGenericResponse(data, dataLength, OPCODE_SERIAL_NUMBER, 0x2);
    }
}
