package dk.silverbullet.telemed.device.continua.packet.output;

import static org.junit.Assert.*;

import org.junit.Test;

import dk.silverbullet.telemed.device.continua.packet.PrettyByteParser;
import dk.silverbullet.telemed.device.continua.packet.SystemId;
import dk.silverbullet.telemed.device.continua.packet.output.AssociationResponsePacket;

public class AssociationResponsePacketTest {
	@Test
	public void knowsHowToSerialize() throws Exception {
		byte[] expectedBytes = PrettyByteParser.parse(
				"E3 00 " + // APDU CHOICE Type (AareApdu)
				"00 2C " + // CHOICE.length = 44
				"00 00 " + // result = accepted
				"50 79 " + // data-proto-id = 20601
				"00 26 " + // data-proto-info length = 38
				"80 00 00 00 " + // protocolVersion
				"80 00 " + // encoding rules = MDER
				"80 00 00 00 " + // nomenclatureVersion 
				"00 00 00 00 " + // functionalUnits – normal Association
				"80 00 00 00 " + // systemType = sys-type-manager
				"00 08 " + // system-id length = 8 and value (manufacturer- and device- specific)
				"88 77 66 55 44 33 22 11 " + // System id (made-up!) 
				"00 00 " + // Manager’s response to config-id is always 0
				"00 00 " + // Manager’s response to data-req-mode-flags is always 0
				"00 00 " + // data-req-init-agent-count and data-req-init-manager-count are always 0
				"00 00 " + // optionList.count = 0
				"00 00" // optionList.length = 0
				);
		
		AssociationResponsePacket packet = new AssociationResponsePacket(new SystemId("8877665544332211"));
		assertArrayEquals(expectedBytes, packet.getContents());
	}
}
