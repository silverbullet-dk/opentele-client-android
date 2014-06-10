package dk.silverbullet.telemed.device.nonin.packet;

import java.io.IOException;

public class NoninPacketFactory {
    private static final int OPCODE_POSITON = 1;
    private static final short OPCODE_MEASUREMENT_DATAFORMAT_13_MOST_SIGNIFICANT_BYTE = 0x00;
    private static final short OPCODE_MEASUREMENT_DATAFORMAT_13_LEAST_SIGNIFICANT_BYTE = 0x0D;
    private static final short OPCODE_SERIAL_NUMBER = 0xF4;



    public static NoninPacket packetFromInts(Integer[] data) throws IOException {
        if(data[OPCODE_POSITON] == OPCODE_SERIAL_NUMBER) {
            return new NoninSerialNumberPacket(data);
        } else if(hasExpectedOpCode(data)) {
            return new NoninMeasurementPacket(data);
        } else {
            throw new IOException("Unknown OpCode:" + Integer.toHexString(data[OPCODE_POSITON]));
        }
    }

    private static boolean hasExpectedOpCode(Integer[] data) {
        boolean opCodeMSBIsAsExpected = data[OPCODE_POSITON] == OPCODE_MEASUREMENT_DATAFORMAT_13_MOST_SIGNIFICANT_BYTE;
        boolean opCodeLSBIsAsExpected = false;
        if(opCodeMSBIsAsExpected) {
            opCodeLSBIsAsExpected = data[OPCODE_POSITON + 1] == OPCODE_MEASUREMENT_DATAFORMAT_13_LEAST_SIGNIFICANT_BYTE;
        }

        return opCodeMSBIsAsExpected && opCodeLSBIsAsExpected;
    }
}
