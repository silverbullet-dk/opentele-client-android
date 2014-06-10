package dk.silverbullet.telemed.device.nonin.packet;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.IOException;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@RunWith(MockitoJUnitRunner.class)
public class NoninPacketCollectorTest {
    @Mock
    private PacketReceiver receiver;

    private int[] serialNumberPacket = {
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

    private int[] serialNumberPacketWithCheckSumError = {
            0x02, //STX
            0xF4, //OP_CODE
            0x0b, //Data size
            0x02, //ID code
            0x35, //Serial number
            0x30, //Serial number
            0x31, //Serial number
            0x34, //Serial number
            0x36, //Serial number, should have been 0x39
            0x33, //Serial number
            0x32, //Serial number
            0x30, //Serial number
            0x30, //Serial number
            0xca, // checksum
            0x03, //ETX
    };

    private int[] MemoryMeasurementPacketWithChecksumError = {
            0x02, // STX
            0x00, // OP_CODE Most significant byte
            0x0d, // OP_CODE Least significant byte
            0x00, // Data length Most siginifant byte
            0x0e, // Data length Least siginifant byte
            0x20, // Date information
            0x07, // Date information
            0x10, // Date information
            0x01, // Date information
            0x00, // Date information
            0x00, // Date information
            0x54, // Date information
            0x00, // Date information
            0x02, // Status Most significant byte
            0x00, // Status Least significant byte
            0x00, // Pulse rate most significant byte
            0x4c, // Pulse rate Least significant byte, should have been 0x3c
            0x00, // Reserved for future use
            0x63, // SpO2
            0x2d, // Checksum
            0x03  // ETX
    };

    private int[] validNonMemoryMeasurementPacket = {
            0x02, // STX
            0x00, // OP_CODE Most significant byte
            0x0d, // OP_CODE Least significant byte
            0x00, // Data length Most siginifant byte
            0x0e, // Data length Least siginifant byte
            0x20, // Date information
            0x07, // Date information
            0x10, // Date information
            0x01, // Date information
            0x00, // Date information
            0x00, // Date information
            0x54, // Date information
            0x00, // Date information
            0x02, // Status Most significant byte
            0x00, // Status Least significant byte
            0x00, // Pulse rate most significant byte
            0x3c, // Pulse rate Least significant byte
            0x00, // Reserved for future use
            0x63, // SpO2
            0x2d, // Checksum
            0x03  // ETX
    };

    private int[] validMemoryMeasurementPacket = {
            0x02, // STX
            0x00, // OP_CODE Most significant byte
            0x0d, // OP_CODE Least significant byte
            0x00, // Data length Most siginifant byte
            0x0e, // Data length Least siginifant byte
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

    @Test
    public void canHandleFlowWithMultipleMeasurements() {
        NoninPacketCollector packetCollector = new NoninPacketCollector();
        packetCollector.setListener(receiver);

        packetCollector.receive(0x00); //NULL start sync
        for(int i: validMemoryMeasurementPacket) {
            packetCollector.receive(i);
        }


        for(int i: serialNumberPacket) {
            packetCollector.receive(i);
        }

        packetCollector.receive(0x00); //NULL start sync
        for(int i: validNonMemoryMeasurementPacket) {
            packetCollector.receive(i);
        }

        verify(receiver, times(2)).addMeasurement(any(NoninMeasurementPacket.class));
        verify(receiver).setSerialNumber(any(NoninSerialNumberPacket.class));
        verifyNoMoreInteractions(receiver);
    }

    @Test
    public void canHandleFlowWithChecksumErrors() {
        NoninPacketCollector packetCollector = new NoninPacketCollector();
        packetCollector.setListener(receiver);

        packetCollector.receive(0x00); //NULL start sync
        for(int i: MemoryMeasurementPacketWithChecksumError) {
            packetCollector.receive(i);
        }


        for(int i: serialNumberPacketWithCheckSumError) {
            packetCollector.receive(i);
        }

        verify(receiver, times(2)).error(any(IOException.class));
        verifyNoMoreInteractions(receiver);
    }

}
