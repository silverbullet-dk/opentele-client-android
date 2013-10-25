package dk.silverbullet.telemed.device.andbloodpressure.packet.input;

import static org.junit.Assert.*;

import org.junit.Test;

import dk.silverbullet.telemed.device.continua.packet.PrettyByteParser;

public class AndBloodPressureConfirmedMeasurementDataPacketTest {
	static String confirmedMeasurementWithBloodPressure = "00 86 " + // OCTET STRING.length
			"10 00 " + // invoke-id
			"01 01 " + // Choice
			"00 80 " + // Choice length (128)
			"00 00 " + // Obj-handle (MDS object)
			"FF FF FF FF " + // Event-time (RelativeTime is not supported)
			"0D 1D " + // Event-type (MDC _NOTI_SCA N_REPORT_FIXED )
			"00 76 " + // event-info length = 112
			"F0 00 " + // ScanReportInfoFixed.data-req-id = 0xF000 
			"00 00 " + // ScanReportInfoFixed.scan-report-no = 0
			"00 05 " + // ScanReportInfoFixed.obs-scan-fixed.count = 5
			"00 6E " + // ScanReportInfoFixed.obs-scan-fixed.length = 110
			"00 01 " + // ScanReportInfoFixed.obs-scan-fixed.value[0].obj-handle = 1
			"00 12 " + // ScanReportInfoFixed.obs-scan-fixed.value[0].obs-val-data.length = 18 
			"00 03 " + // Compound Object count (3 entries)
			"00 06 " + // Compound Object length (6 bytes)
			"00 7E " + // Systolic = 126
			"00 47 " + // Diastolic = 71 
			"00 56 " + // MAP = 86 
			"20 13 02 25 21 46 01 00 " + // Absolute-Time-Stamp = 2013-02-25 21:46:01.00 
			"00 01 " + // ScanReportInfoFixed.obs-scan-fixed.value[1].obj-handle = 1
			"00 12 " + // ScanReportInfoFixed.obs-scan-fixed.value[1].obs-val-data.length = 18 
			"00 03 " + 
			"00 06 " + 
			"00 82 " + // Systolic = 130
			"00 42 " + // Diastolic = 66 
			"00 67 " + // MAP
			"20 13 02 25 21 47 31 00 " + // Absolute-Time-Stamp = 2013-02-25 21:47:31.00 
			"00 01 " + 
			"00 12 " + 
			"00 03 " + 
			"00 06 " + 
			"00 7F " + // Systolic
			"00 48 " + // Diastolic
			"00 54 " + // MAP
			"20 13 02 25 22 27 48 00 " + // Absolute-Time-Stamp = 2013-02-25 22:27:48.00 
			"00 01 " + 
			"00 12 " + 
			"00 03 " + 
			"00 06 " + 
			"00 72 " + // Systolic = 114
			"00 43 " + // Diastolic = 67 
			"00 5A " + // MAP = 90 
			"20 13 02 25 22 34 25 00 "+ // Absolute-Time-Stamp = 2013-02-25 22:34:25.00 
			"00 01 " + 
			"00 12 " + 
			"00 03 " + 
			"00 06 " + 
			"00 6D " + // Systolic
			"00 4D " + // Diastolic
			"00 59 " + // MAP
			"20 13 02 25 22 30 38 00 "; // Absolute-Time-Stamp = 2013-02-25 22:30:38.00 

	static String confirmedMeasurementWithPulse = "00 5E " + // OCTET STRING.length (94 )
			"10 00 " + // invoke-id
			"01 01 " + 
			"00 58 " + 
			"00 00 " + 
			"FF FF FF FF " + 
			"0D 1D " + 
			"00 4E " + // event-info length
			"F0 00 " + 
			"00 00 " + 
			"00 05 " + 
			"00 46 " + 
			"00 02 " + // obj-handle
			"00 0A " + 
			"00 36 " + // Pulse 54 
			"20 13 02 25 21 46 01 00 " + 
			"00 02 " + 
			"00 0A " + 
			"00 32 " + // Pulse 50 
			"20 13 02 25 21 47 31 00 " + 
			"00 02 " + 
			"00 0A " + 
			"00 35 " + // Pulse 53 
			"20 13 02 25 22 27 48 00 " + 
			"00 02 " + 
			"00 0A " + 
			"00 37 " + // Pulse 55 
			"20 13 02 25 22 34 25 00 " +
			"00 02 " + 
			"00 0A " + 
			"00 38 " + // Pulse 56 
			"20 13 02 25 22 30 38 00 "; 
	
	static String confirmedMeasurementWithBloodPressureAndPulse = "00 86 " + // OCTET STRING.length (doesn't match this example, but that's OK...)
			"10 00 " + // invoke-id
			"01 01 " + // Choice
			"00 80 " + // Choice length (128) (doesn't match this example, but that's OK...)
			"00 00 " + // Obj-handle (MDS object)
			"FF FF FF FF " + // Event-time (RelativeTime is not supported)
			"0D 1D " + // Event-type (MDC _NOTI_SCA N_REPORT_FIXED )
			"00 76 " + // event-info length = 112 (doesn't match this example, but that's OK...)
			"F0 00 " + // ScanReportInfoFixed.data-req-id = 0xF000 
			"00 00 " + // ScanReportInfoFixed.scan-report-no = 0
			"00 02 " + // ScanReportInfoFixed.obs-scan-fixed.count = 2
			"00 6E " + // ScanReportInfoFixed.obs-scan-fixed.length = 110 (doesn't match this example, but that's OK...)
			"00 01 " + // ScanReportInfoFixed.obs-scan-fixed.value[0].obj-handle = 1
			"00 12 " + // ScanReportInfoFixed.obs-scan-fixed.value[0].obs-val-data.length = 18 
			"00 03 " + // Compound Object count (3 entries)
			"00 06 " + // Compound Object length (6 bytes)
			"00 7E " + // Systolic = 126
			"00 47 " + // Diastolic = 71 
			"00 56 " + // MAP = 86 
			"20 13 02 25 21 46 01 00 " + // Absolute-Time-Stamp = 2013-02-25 21:46:01.00
			"00 02 " + // ScanReportInfoFixed.obs-scan-fixed.value[0].obj-handle = 2
			"00 0A " + // ScanReportInfoFixed.obs-scan-fixed.value[0].obs-val-data.length = 10
			"00 36 " + // Pulse (54) 
			"20 13 02 25 21 46 01 00"; // Absolute-Time-Stamp = 2013-02-25 21:46:01.00
	
	@Test
	public void canParseBloodPressure() throws Exception {
		AndBloodPressureConfirmedMeasurementDataPacket packet = new AndBloodPressureConfirmedMeasurementDataPacket(PrettyByteParser.parse(confirmedMeasurementWithBloodPressure));
		
		assertTrue(packet.hasBloodPressure());
		assertFalse(packet.hasPulse());
		
		assertEquals(114, packet.getSystolicBloodPressure());
		assertEquals(67, packet.getDiastolicBloodPressure());
		assertEquals(90, packet.getMeanArterialPressure());
	}
	
	@Test
	public void canParsePulseData() throws Exception {
		AndBloodPressureConfirmedMeasurementDataPacket packet = new AndBloodPressureConfirmedMeasurementDataPacket(PrettyByteParser.parse(confirmedMeasurementWithPulse));

		assertFalse(packet.hasBloodPressure());
		assertTrue(packet.hasPulse());
		
		assertEquals(55, packet.getPulse());
	}
	
	@Test
	public void canParseBloodPressureAndPulse() throws Exception {
		AndBloodPressureConfirmedMeasurementDataPacket packet = new AndBloodPressureConfirmedMeasurementDataPacket(PrettyByteParser.parse(confirmedMeasurementWithBloodPressureAndPulse));

		assertTrue(packet.hasBloodPressure());
		assertTrue(packet.hasPulse());

		assertEquals(126, packet.getSystolicBloodPressure());
		assertEquals(71, packet.getDiastolicBloodPressure());
		assertEquals(86, packet.getMeanArterialPressure());
		assertEquals(54, packet.getPulse());
	}
}
