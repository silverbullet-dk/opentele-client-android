package dk.silverbullet.telemed.device.monica.packet;

import static org.junit.Assert.*;

import java.util.Date;

import org.junit.Test;

public class CBlockMessageTest {
	public static final String initialCBlock = "C\u0000\u0000@\u0000@\u0000@\u0000@\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000A`\u0008";
	public static final String cBlock = "C\u0000\u0000B+B+B+B+\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000A%A%A%A%&&&&A`\u0008";

	@Test
	public void canParseInitialEmptyCBlock() {
		CBlockMessage message = new CBlockMessage(new Date(), initialCBlock);
		
		assertFloatArrayEquals(new float[]{0, 0, 0, 0}, message.getMHR());
		assertFloatArrayEquals(new float[]{0, 0, 0, 0}, message.getFHR1());
	}

	@Test
	public void canParseValidInput() {
		CBlockMessage message = new CBlockMessage(new Date(), cBlock);
		
		assertFloatArrayEquals(new float[]{73.25f, 73.25f, 73.25f, 73.25f}, message.getMHR());
		assertFloatArrayEquals(new float[]{138.75f, 138.75f, 138.75f, 138.75f}, message.getFHR1());
	}
	
	private void assertFloatArrayEquals(float[] expected, float[] actual) {
		assertEquals("Array length should be equal", expected.length, actual.length);
		for (int i=0; i<expected.length; i++) {
			assertEquals("Values at position " + i + " should be equal", expected[i], actual[i], 0.001f);
		}
	}
}
