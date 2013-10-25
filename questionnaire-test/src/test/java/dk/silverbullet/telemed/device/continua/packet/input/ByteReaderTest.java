package dk.silverbullet.telemed.device.continua.packet.input;

import static org.junit.Assert.assertEquals;
import java.io.IOException;
import org.junit.Test;

import dk.silverbullet.telemed.device.continua.packet.input.OrderedByteReader;

public class ByteReaderTest {

    @Test
    public void testReadData() throws IOException {
        byte[] data = {
                // One byte
                (byte) 0x12,
                // One short: 0x1234
                (byte) 0x12, (byte) 0x34,
                // One int: 0x12345678
                (byte) 0x12, (byte) 0x34, (byte) 0x56, (byte) 0x78,
                // One long: 0x1234567890abcdef
                (byte) 0x12, (byte) 0x34, (byte) 0x56, (byte) 0x78, (byte) 0x90, (byte) 0xab, (byte) 0xcd, (byte) 0xef
        // EOF
        };

        OrderedByteReader reader = new OrderedByteReader(data);
        assertEquals(0x12, reader.readByte());
        assertEquals(0x1234, reader.readShort());
        assertEquals(0x12345678, reader.readInt());
        assertEquals(0x1234567890abcdefL, reader.readLong());
    }
}
