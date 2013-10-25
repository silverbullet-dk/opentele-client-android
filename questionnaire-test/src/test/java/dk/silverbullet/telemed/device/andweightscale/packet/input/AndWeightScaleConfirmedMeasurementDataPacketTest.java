package dk.silverbullet.telemed.device.andweightscale.packet.input;

import static org.junit.Assert.*;

import org.junit.Test;

import dk.silverbullet.telemed.device.andweightscale.Weight;
import dk.silverbullet.telemed.device.andweightscale.Weight.Unit;
import dk.silverbullet.telemed.device.continua.packet.PrettyByteParser;

public class AndWeightScaleConfirmedMeasurementDataPacketTest {
	static String confirmedMeasurementWithWeightsInKg = "00 B8 "+ // STRING.length
			"10 00 "+ // invoke-id
			"01 01 "+ // CHOICE 
			"00 B2 "+ // CHOICE .length
			"00 00 "+ // obj-handle
			"FF FF FF FF "+ // event-time
			"0D 1E "+ // event-type = ??
			"00 A8 "+ // event-info.length
			"F0 00 "+ // ScanReportInfoFixed
			"00 00 "+ // report-no
			"00 05 "+ // count
			"00 A0 "+ // length (16 0)
			"00 01  00 03  00 1A  0A 56  00 04  FE 00  00 F0  09 90  00 08  20 12 06 04 08 26 39 00  09 96 00 02 06 C3 " + // 24,0 kg
			"00 01  00 03  00 1A  0A 56  00 04  FE 00  1C 48  09 90  00 08  20 12 06 04 10 31 15 00  09 96 00 02 06 C3 " + // 72,4 kg
			"00 01  00 03  00 1A  0A 56  00 04  FE 00  00 DC  09 90  00 08  20 12 06 05 16 35 12 00  09 96 00 02 06 C3 " + // 22,0 kg
			"00 01  00 03  00 1A  0A 56  00 04  FE 00  22 B0  09 90  00 08  20 12 06 05 16 43 35 00  09 96 00 02 06 C3 " + // 88,8 kg
			"00 01  00 03  00 1A  0A 56  00 04  FE 00  04 EC  09 90  00 08  20 12 06 05 16 44 48 00  09 96 00 02 06 C3 "; // 12,6 kg
	
	static String confirmedMeasurementWithNewestWeightInLbs = "00 B8 "+ // STRING.length
			"10 00 "+ // invoke-id
			"01 01 "+ // CHOICE 
			"00 B2 "+ // CHOICE .length
			"00 00 "+ // obj-handle
			"FF FF FF FF "+ // event-time
			"0D 1E "+ // event-type = ??
			"00 A8 "+ // event-info.length
			"F0 00 "+ // ScanReportInfoFixed
			"00 00 "+ // report-no
			"00 05 "+ // count
			"00 A0 "+ // length (16 0)
			"00 01  00 03  00 1A  0A 56  00 04  FE 00  00 F0  09 90  00 08  20 12 06 04 08 26 39 00  09 96 00 02 06 C3 " + // 24,0 kg
			"00 01  00 03  00 1A  0A 56  00 04  FE 00  1C 48  09 90  00 08  20 12 06 04 10 31 15 00  09 96 00 02 06 C3 " + // 72,4 kg
			"00 01  00 03  00 1A  0A 56  00 04  FE 00  00 DC  09 90  00 08  20 12 06 05 16 35 12 00  09 96 00 02 06 C3 " + // 22,0 kg
			"00 01  00 03  00 1A  0A 56  00 04  FE 00  22 B0  09 90  00 08  20 12 06 05 16 43 35 00  09 96 00 02 06 C3 " + // 88,8 kg
			"00 01  00 03  00 1A  0A 56  00 04  FF 00  06 3A  09 90  00 08  20 12 06 05 16 44 48 00  09 96 00 02 06 E0 "; // 159,4 lbs
	
	@Test
	public void canParseWeight() throws Exception {
		AndWeightScaleConfirmedMeasurementDataPacket packet = new AndWeightScaleConfirmedMeasurementDataPacket(PrettyByteParser.parse(confirmedMeasurementWithWeightsInKg));
		
		assertEquals(new Weight(12.6F, Unit.KG), packet.getWeight());
		assertTrue(packet.getTimestamp() > 0);
	}
	
	@Test
	public void knowsWhenNewestMeasurementIsInLbs() throws Exception {
		AndWeightScaleConfirmedMeasurementDataPacket packet = new AndWeightScaleConfirmedMeasurementDataPacket(PrettyByteParser.parse(confirmedMeasurementWithNewestWeightInLbs));
		
		assertEquals(new Weight(159.4f, Unit.LBS), packet.getWeight());
		assertTrue(packet.getTimestamp() > 0);
	}
}
