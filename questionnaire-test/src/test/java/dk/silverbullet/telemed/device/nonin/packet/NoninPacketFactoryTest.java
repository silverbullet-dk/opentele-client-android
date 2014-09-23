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

        NoninPacket packet = NoninPacketFactory.serialNumberPacket(serialNumberPacket);

        assertTrue(packet instanceof NoninSerialNumberPacket);

    }

    @Test(expected = IOException.class)
    public void thowsIOExceptionOnUnknownPacketOpCodeInPositionOne() throws IOException {
        Integer[] packetWithUnknownIdAtPosition1 = {
                0x02, //STX
                0xAA //OP_CODE
        };
        NoninPacketFactory.serialNumberPacket(packetWithUnknownIdAtPosition1);
    }


    @Test(expected = IOException.class)
    public void thowsIOExceptionOnUnknownPacketOpCodeInPositionTwo() throws IOException {
        Integer[] packetWithUnknownIdAtPosition2 = {
                0x02, //STX
                0x00, //OP_CODE
                0xff
        };
        NoninPacketFactory.serialNumberPacket(packetWithUnknownIdAtPosition2);
    }
}
