package dk.silverbullet.telemed.device.continua.packet.output;

import static org.junit.Assert.*;
import org.junit.Test;

import dk.silverbullet.telemed.device.continua.packet.output.OrderedByteWriter;

public class ByteWriterTest {
	@Test
	public void testCanWrite() {
		byte[] expectedData = {
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

        OrderedByteWriter writer = new OrderedByteWriter();
        writer.writeByte(0x12);
        writer.writeShort(0x1234);
        writer.writeInt(0x12345678);
        writer.writeLong(0x1234567890abcdefL);
        
        assertArrayEquals(expectedData, writer.getBytes());
	}
}
