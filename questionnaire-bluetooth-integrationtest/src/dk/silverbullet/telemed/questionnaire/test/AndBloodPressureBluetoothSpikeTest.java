package dk.silverbullet.telemed.questionnaire.test;

import android.test.ActivityInstrumentationTestCase2;
import dk.silverbullet.telemed.MainActivity;
import dk.silverbullet.telemed.device.andbloodpressure.AndBloodPressureController;
import dk.silverbullet.telemed.device.andbloodpressure.BloodPressureAndPulse;
import dk.silverbullet.telemed.device.continua.ContinuaDeviceController;
import dk.silverbullet.telemed.device.continua.ContinuaListener;
import dk.silverbullet.telemed.device.continua.android.AndroidHdpController;

public class AndBloodPressureBluetoothSpikeTest extends ActivityInstrumentationTestCase2<MainActivity> {
	ContinuaDeviceController controller;
	boolean connectionEstablished;
	boolean measurementReceived;
	ContinuaListener<BloodPressureAndPulse> listener = new ContinuaListener<BloodPressureAndPulse>() {
		@Override
		public void connected() {
			connectionEstablished = true;
		}

		@Override
		public void disconnected() {
			System.out.println("Disconnected");
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
		public void measurementReceived(String systemId, BloodPressureAndPulse measurement) {
			System.out.println("Measurement: " + measurement);
			measurementReceived = true;
		}
	};
	
	public AndBloodPressureBluetoothSpikeTest() {
		super(MainActivity.class);
	}

    @Override
    protected void tearDown() throws Exception {
        if (controller != null) {
			controller.close();
		}
        Thread.sleep(5000);

    	super.tearDown();
    }

    public void testCanEstablishConnection() throws Exception {
    	AndroidHdpController bluetoothController = new AndroidHdpController(getActivity());
    	controller = AndBloodPressureController.create(listener, bluetoothController);

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
