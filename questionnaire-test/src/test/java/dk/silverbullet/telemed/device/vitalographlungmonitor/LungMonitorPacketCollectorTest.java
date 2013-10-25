package dk.silverbullet.telemed.device.vitalographlungmonitor;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import dk.silverbullet.telemed.device.vitalographlungmonitor.packet.PacketReceiver;
import dk.silverbullet.telemed.device.vitalographlungmonitor.packet.states.ReceiverState;

@RunWith(MockitoJUnitRunner.class)
public class LungMonitorPacketCollectorTest {
	byte[] measurementBytes = new byte[] { (byte) 2, (byte) 70, (byte) 84,
			(byte) 68, (byte) 48, (byte) 48, (byte) 48, (byte) 48, (byte) 49,
			(byte) 50, (byte) 57, (byte) 52, (byte) 48, (byte) 54, (byte) 51,
			(byte) 54, (byte) 51, (byte) 51, (byte) 57, (byte) 57, (byte) 48,
			(byte) 57, (byte) 49, (byte) 52, (byte) 48, (byte) 52, (byte) 48,
			(byte) 48, (byte) 48, (byte) 48, (byte) 48, (byte) 48, (byte) 48,
			(byte) 57, (byte) 53, (byte) 48, (byte) 57, (byte) 48, (byte) 48,
			(byte) 48, (byte) 48, (byte) 49, (byte) 51, (byte) 48, (byte) 53,
			(byte) 50, (byte) 56, (byte) 49, (byte) 48, (byte) 52, (byte) 52,
			(byte) 49, (byte) 56, (byte) 48, (byte) 57, (byte) 50, (byte) 51,
			(byte) 3, (byte) 106 };
	byte[] measurementBytesWithWrongChecksum = new byte[] { (byte) 2, (byte) 70, (byte) 84,
			(byte) 68, (byte) 48, (byte) 48, (byte) 48, (byte) 48, (byte) 49,
			(byte) 50, (byte) 57, (byte) 52, (byte) 48, (byte) 54, (byte) 51,
			(byte) 54, (byte) 51, (byte) 51, (byte) 57, (byte) 57, (byte) 48,
			(byte) 57, (byte) 49, (byte) 52, (byte) 48, (byte) 52, (byte) 48,
			(byte) 48, (byte) 48, (byte) 48, (byte) 48, (byte) 48, (byte) 48,
			(byte) 57, (byte) 53, (byte) 48, (byte) 57, (byte) 48, (byte) 48,
			(byte) 48, (byte) 48, (byte) 49, (byte) 51, (byte) 48, (byte) 53,
			(byte) 50, (byte) 56, (byte) 49, (byte) 48, (byte) 52, (byte) 52,
			(byte) 49, (byte) 56, (byte) 48, (byte) 57, (byte) 50, (byte) 51,
			(byte) 3, (byte) 107 };
	@Mock PacketReceiver receiver;
	LungMonitorPacketCollector collector;
	
	@Before
	public void before() {
		collector = new LungMonitorPacketCollector();
		collector.setListener(receiver);
	}
	
	@Test
	public void canParseSingleMeasurement() throws Exception {
		receiveBytes(measurementBytes);
		
		ArgumentCaptor<FevMeasurementPacket> captor = ArgumentCaptor.forClass(FevMeasurementPacket.class);
		verify(receiver).receive(captor.capture());
		verify(receiver).sendByte(ReceiverState.ACK);
		
		FevMeasurementPacket measurement = captor.getValue();
		assertEquals("0000129406", measurement.getDeviceId());
		assertEquals(3.63, measurement.getFev1(), 0.005);
		assertEquals(3.99, measurement.getFev6(), 0.005);
		assertEquals(0.91, measurement.getFev1Fev6Ratio(), 0.005);
		assertEquals(4.04, measurement.getFef2575(), 0.005);
		assertTrue(measurement.isGoodTest());
		assertEquals(923, measurement.getSoftwareVersion());
	}
	
	@Test
	public void sendsErrorWhenChecksumDoesNotMatch() throws Exception {
		receiveBytes(measurementBytesWithWrongChecksum);
		
		ArgumentCaptor<IOException> captor = ArgumentCaptor.forClass(IOException.class);
		verify(receiver).error(captor.capture());
		verify(receiver).sendByte(ReceiverState.NAK);
		assertEquals("Invalid checksum. Got 107, expected 106", captor.getValue().getMessage());
	}
	
	@Test
	public void recoversFromBadMessageAndParsesCorrectMessageAfterwards() throws Exception {
		receiveBytes(measurementBytesWithWrongChecksum);
		receiveBytes(measurementBytes);

		InOrder inOrder = inOrder(receiver);
		inOrder.verify(receiver).sendByte(ReceiverState.NAK);
		inOrder.verify(receiver).error(any(IOException.class));
		inOrder.verify(receiver).sendByte(ReceiverState.ACK);
		inOrder.verify(receiver).receive(any(FevMeasurementPacket.class));
	}

	private void receiveBytes(byte[] bytes) {
		for (byte b : bytes) {
			collector.receive(b);
		}
	}
}
