package dk.silverbullet.telemed.device.continua.packet.input;

import static org.junit.Assert.*;

import org.junit.Test;

import dk.silverbullet.telemed.device.continua.packet.PrettyByteParser;
import dk.silverbullet.telemed.device.continua.packet.SystemId;
import dk.silverbullet.telemed.device.continua.packet.input.AssociationRequestPacket;

public class AssociationRequestPacketTest {
    static String noninAssociationRequest =
    		// First 4 bytes are header bytes
    		// "E2 00 " + // APDU CHOICE
            // "00 32 " + // COICE.length = 50
            "80 00 00 00 " + // assoc-version
            "00 01 00 2A " + // data-proto-list.count=1 | length=42
            "50 79 " + // data-proto-id=20601
            "00 26 " + // data-proto-info length = 38
            "80 00 00 00 " + // protocolVersion
            "80 00 " + // encoding rules = MDER or PER
            "80 00 00 00 " + // nomenclatureVersion
            "00 00 00 00 " + // functionalUnits – no test association
                             // capabilities
            "00 80 00 00 " + // systemType = sys-type-agent
            "00 08 " + // system-id length = 8 and value (manufacturer- and
                       // device- specific)
            "00 1C 05 01 00 00 95 33 " + // (Nonin \"BDA\")
            "01 91 " + // dev-config-id – extended configuration
            "00 01 " + // data-req-mode-flags
            "01 00 " + // data-req-init-agent-count, data-req-init-manager-count
            "00 00 00 00"; // optionList.count = 0 | optionList.length = 0

	@Test
	public void readsSystemId() throws Exception {
		AssociationRequestPacket packet = new AssociationRequestPacket(PrettyByteParser.parse(noninAssociationRequest));
		
		assertEquals(new SystemId("001C050100009533"), packet.getSystemId());
	}
}
