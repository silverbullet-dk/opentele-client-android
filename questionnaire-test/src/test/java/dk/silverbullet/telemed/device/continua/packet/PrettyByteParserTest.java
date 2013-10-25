package dk.silverbullet.telemed.device.continua.packet;

import static org.junit.Assert.*;

import org.junit.Test;


public class PrettyByteParserTest {
	@Test
	public void parsesPrettyBytes() {
		byte[] parsedBytes = PrettyByteParser.parse("E2 00 00 32");
		
		assertEquals(4, parsedBytes.length);
		assertEquals((byte) 0xE2, parsedBytes[0]);
		assertEquals((byte) 0x00, parsedBytes[1]);
		assertEquals((byte) 0x00, parsedBytes[2]);
		assertEquals((byte) 0x32, parsedBytes[3]);
	}
}
