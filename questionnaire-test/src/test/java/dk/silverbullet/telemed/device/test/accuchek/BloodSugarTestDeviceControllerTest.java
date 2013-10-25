package dk.silverbullet.telemed.device.test.accuchek;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Mockito.*;

import dk.silverbullet.telemed.device.accuchek.AccuChekListener;
import dk.silverbullet.telemed.device.accuchek.BloodSugarMeasurements;

@RunWith(MockitoJUnitRunner.class)
public class BloodSugarTestDeviceControllerTest {
	@Mock AccuChekListener listener;
	
	@Test
	public void goesThroughWholeCallbackSequence() throws Exception {
		BloodSugarTestDeviceController.INTERVAL_BETWEEN_CALLBACKS_MS = 1;
		new BloodSugarTestDeviceController(listener);
		
		Thread.sleep(1000);
		
		InOrder inOrder = inOrder(listener);
		inOrder.verify(listener).connected();
		inOrder.verify(listener).fetchingDiary();
		inOrder.verify(listener).measurementsParsed(any(BloodSugarMeasurements.class));
		verifyNoMoreInteractions(listener);
	}
}
