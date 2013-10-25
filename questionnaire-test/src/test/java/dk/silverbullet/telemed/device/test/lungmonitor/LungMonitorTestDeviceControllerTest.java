package dk.silverbullet.telemed.device.test.lungmonitor;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import dk.silverbullet.telemed.device.vitalographlungmonitor.LungMeasurement;
import dk.silverbullet.telemed.device.vitalographlungmonitor.LungMonitorListener;

@RunWith(MockitoJUnitRunner.class)
public class LungMonitorTestDeviceControllerTest {
	@Mock LungMonitorListener listener;
	
	@Test
	public void goesThroughWholeCallbackSequence() throws Exception {
		LungMonitorTestDeviceController.INTERVAL_BETWEEN_CALLBACKS_MS = 1;
		new LungMonitorTestDeviceController(listener);
		
		Thread.sleep(1000);
		
		InOrder inOrder = inOrder(listener);
		inOrder.verify(listener).connected();
		inOrder.verify(listener).measurementReceived(any(String.class), any(LungMeasurement.class));
		verifyNoMoreInteractions(listener);

	}
}
