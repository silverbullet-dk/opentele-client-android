package dk.silverbullet.telemed.questionnaire.test;

import android.test.ActivityInstrumentationTestCase2;
import dk.silverbullet.telemed.MainActivity;
import dk.silverbullet.telemed.device.vitalographlungmonitor.LungMeasurement;
import dk.silverbullet.telemed.device.vitalographlungmonitor.LungMonitorController;
import dk.silverbullet.telemed.device.vitalographlungmonitor.VitalographLungMonitorController;
import dk.silverbullet.telemed.device.vitalographlungmonitor.LungMonitorListener;

public class VitalographLungMonitorSpikeTest extends ActivityInstrumentationTestCase2<MainActivity> {
	LungMonitorController controller;
	boolean connectionEstablished;
	boolean measurementReceived;
	LungMonitorListener listener = new LungMonitorListener() {
		@Override
		public void connected() {
			connectionEstablished = true;
		}

		@Override
		public void permanentProblem() {
			System.out.println("Permanent problem");
		}

		@Override
		public void temporaryProblem() {
			System.out.println("Temporary problem");
		}

		@Override
		public void measurementReceived(String systemId, LungMeasurement measurement) {
			System.out.println("Measurement: " + measurement);
			measurementReceived = true;
		}
	};

	public VitalographLungMonitorSpikeTest() {
		super(MainActivity.class);
	}
    
    @Override
    protected void tearDown() throws Exception {
		controller.close();
        Thread.sleep(5000);

    	super.tearDown();
    }

    public void testCanEstablishConnection() throws Exception {
    	controller = VitalographLungMonitorController.create(listener);

        assertEstablishesConnection();
        assertMeasurementReceived();
    }

    private void assertEstablishesConnection() throws Exception {
        for (int i = 0; i < 600; i++) {
            if (connectionEstablished) {
                return;
            }
            Thread.sleep(100);
        }

        fail("Connection never established");
    }

    private void assertMeasurementReceived() throws Exception {
        for (int i = 0; i < 600; i++) {
            if (measurementReceived) {
                return;
            }
            Thread.sleep(100);
        }

        fail("Measurements never received");
    }
}
