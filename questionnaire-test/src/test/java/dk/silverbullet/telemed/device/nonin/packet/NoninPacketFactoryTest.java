package dk.silverbullet.telemed.device.nonin.packet;

import org.junit.Test;

import java.io.IOException;
import static org.junit.Assert.assertTrue;
public class NoninPacketFactoryTest {

    @Test
    public void canRecognizeSerialNumberPacket() throws IOException {
        Integer[] serialNumberPacket = {
                0x02, //STX
                0xF4, //OP_CODE
                0x0b, //Data size
                0x02, //ID code
                0x35, //Serial number
                0x30, //Serial number
                0x31, //Serial number
                0x34, //Serial number
                0x39, //Serial number
                0x33, //Serial number
                0x32, //Serial number
                0x30, //Serial number
                0x30, //Serial number
                0xca, // checksum
                0x03, //ETX
        };

        NoninPacket packet = NoninPacketFactory.packetFromInts(serialNumberPacket);

        assertTrue(packet instanceof NoninSerialNumberPacket);

    }

    @Test
    public void canRecognizeMeasurementPacket() throws IOException {
        Integer[] measurementPacket = {
                0x02, // STX
                0x00, // OP_CODE Most significant byte
                0x0d, // OP_CODE Least significant byte
                0x00, // Data length Most significant byte
                0x0e, // Data length Least significant byte
                0x20, // Date information
                0x07, // Date information
                0x10, // Date information
                0x01, // Date information
                0x18, // Date information
                0x48, // Date information
                0x21, // Date information
                0x00, // Date information
                0x02, // Status Most significant byte
                0x10, // Status Least significant byte
                0x00, // Pulse rate most significant byte
                0x34, // Pulse rate Least significant byte
                0x00, // Reserved for future use
                0x63, // SpO2
                0x62, // Checksum
                0x03 // ETX
        };

        NoninPacket packet = NoninPacketFactory.packetFromInts(measurementPacket);

        assertTrue(packet instanceof NoninMeasurementPacket);
    }

    @Test(expected = IOException.class)
    public void thowsIOExceptionOnUnknownPacketOpCodeInPositionOne() throws IOException {
        Integer[] packetWithUnknownIdAtPosition1 = {
                0x02, //STX
                0xAA //OP_CODE
        };
        NoninPacketFactory.packetFromInts(packetWithUnknownIdAtPosition1);
    }


    @Test(expected = IOException.class)
    public void thowsIOExceptionOnUnknownPacketOpCodeInPositionTwo() throws IOException {
        Integer[] packetWithUnknownIdAtPosition2 = {
                0x02, //STX
                0x00, //OP_CODE
                0xff
        };
        NoninPacketFactory.packetFromInts(packetWithUnknownIdAtPosition2);
    }
}
