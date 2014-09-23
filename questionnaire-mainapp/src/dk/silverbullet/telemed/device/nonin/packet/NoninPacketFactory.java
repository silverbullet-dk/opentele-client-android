package dk.silverbullet.telemed.device.nonin.packet;

import java.io.IOException;

public class NoninPacketFactory {
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

    public static boolean isSerialNumberPacket(Integer[] data) {
        return data[OPCODE_POSITON] == OPCODE_SERIAL_NUMBER;
    }
}
