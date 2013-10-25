package dk.silverbullet.telemed.device.continua.packet;

import static org.junit.Assert.*;

import org.junit.Test;

import dk.silverbullet.telemed.device.continua.packet.SystemId;

public class SystemIdTest {
	@Test
	public void canBeInstantiatedWithLong() {
		SystemId systemId = new SystemId(0x8877665544332211L);
		
		assertEquals(0x8877665544332211L, systemId.asLong());
	}

	@Test
	public void canBeInstantiatedWithHexString() {
		SystemId systemId = new SystemId("8877665544332211");
		
		assertEquals(0x8877665544332211L, systemId.asLong());
	}
	
	@Test
	public void canFormatAsHexString() {
		SystemId systemId = new SystemId(0x8877665544332211L);
		
		assertEquals("8877665544332211", systemId.asString());
	}
	
	@Test
	public void implementsEquals() {
		assertEquals(new SystemId("12345"), new SystemId("12345"));
		assertFalse(new SystemId("12345").equals("54321"));
	}
}
