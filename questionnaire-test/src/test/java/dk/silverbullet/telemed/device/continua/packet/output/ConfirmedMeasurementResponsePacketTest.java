package dk.silverbullet.telemed.device.continua.packet.output;

import static org.junit.Assert.*;

import org.junit.Test;

import dk.silverbullet.telemed.device.continua.packet.PrettyByteParser;
import dk.silverbullet.telemed.device.continua.packet.output.ConfirmedMeasurementResponsePacket;

public class ConfirmedMeasurementResponsePacketTest {

	@Test
	public void test() {
		byte[] expectedBytes = PrettyByteParser.parse(
				"E7 00 " + // APDU CHOICE Type (PrstApdu)
				"00 12 " + // CHOICE.length = 18
				"00 10 " + // OCTET STRING.length = 16
				"80 00 " + // invoke-id (mirrored from invocation)
				"02 01 " + // CHOICE(Remote Operation Response | Confirmed Event Report)
				"00 0A " + // CHOICE.length = 10
				"00 00 " + // obj-handle = 0 (MDS object)
				"00 00 00 00 " + // currentTime = 0
				"0D 1D " + // event-type = MDC_NOTI_SCAN_REPORT_FIXED
				"00 00" // event-reply-info.length = 0
				);
		
		ConfirmedMeasurementResponsePacket packet = new ConfirmedMeasurementResponsePacket(0x8000, 0x0D1D);
		assertArrayEquals(expectedBytes, packet.getContents());
	}
}
