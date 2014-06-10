package dk.silverbullet.telemed.device.nonin.packet;

import org.junit.Test;

import java.io.IOException;
import static org.junit.Assert.assertEquals;
public class NoninSerialNumberPacketTest {

    @Test
    public void canParseSerialNumber() throws IOException {
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

        NoninSerialNumberPacket noninSerialNumberPacket = new NoninSerialNumberPacket(serialNumberPacket);

        assertEquals("501493200", noninSerialNumberPacket.serial);
    }

    @Test(expected = IOException.class)
    public void throwsIOExceptionOnInvalidChecksum() throws IOException {
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
                0xba, // checksum     <------ Should have been 0xca
                0x03, //ETX
        };

        new NoninSerialNumberPacket(serialNumberPacket);
    }

    @Test(expected = IOException.class)
    public void throwsIoExceptionOnUnexpectedLength() throws IOException {
        Integer[] serialNumberPacket = {
                0x02, //STX
                0xF4, //OP_CODE
                0x0a, //Data size  <------ Should have been 0x0b
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

        new NoninSerialNumberPacket(serialNumberPacket);
    }

}
