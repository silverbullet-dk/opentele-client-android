package dk.silverbullet.telemed.device.continua;

import static org.junit.Assert.*;

import org.junit.Test;

import dk.silverbullet.telemed.device.continua.ContinuaPacketTag;

public class ContinuaPacketTagTest {
	@Test
	public void canFindTagForValue() {
		assertEquals(ContinuaPacketTag.AARQ_APDU, ContinuaPacketTag.packetTagForValue(0xE200));
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void failsWhenFindingTagForUnknownValue() {
		ContinuaPacketTag.packetTagForValue(123);
	}
	
	@Test
	public void knowsWhichTagValuesAreDefined() {
		assertTrue(ContinuaPacketTag.isKnownTagValue(0xE400));
	}
	
	@Test
	public void knowsWhichTagValuesAreUnknown() {
		assertFalse(ContinuaPacketTag.isKnownTagValue(123));
	}
}
