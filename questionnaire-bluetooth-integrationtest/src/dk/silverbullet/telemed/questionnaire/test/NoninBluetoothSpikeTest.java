package dk.silverbullet.telemed.questionnaire.test;

import android.test.ActivityInstrumentationTestCase2;
import dk.silverbullet.telemed.MainActivity;
import dk.silverbullet.telemed.device.continua.ContinuaDeviceController;
import dk.silverbullet.telemed.device.continua.ContinuaListener;
import dk.silverbullet.telemed.device.continua.android.AndroidHdpController;
import dk.silverbullet.telemed.device.nonin.NoninController;
import dk.silverbullet.telemed.device.nonin.SaturationAndPulse;

public class NoninBluetoothSpikeTest extends ActivityInstrumentationTestCase2<MainActivity> {
	ContinuaDeviceController controller;
    boolean connectionEstablished;
    boolean measurementReceived;
    boolean problem;
    boolean disconnected;

    ContinuaListener<SaturationAndPulse> listener = new ContinuaListener<SaturationAndPulse>() {
    	@Override
        public void connected() {
    		System.out.println("Connection established");
            connectionEstablished = true;
        }

		@Override
		public void measurementReceived(String systemId, SaturationAndPulse measurement) {
			System.out.println("System ID: " + systemId);
			System.out.println("Measurement: " + measurement);
			measurementReceived = true;
		}

		@Override
		public void disconnected() {
			System.out.println("Disconnected");
			disconnected = true;
		}

		@Override
		public void permanentProblem() {
			System.out.println("Permanent problem");
			problem = true;
		}

		@Override
		public void temporaryProblem() {
			System.out.println("Temporary problem");
			problem = true;
		}
    };
	
    public NoninBluetoothSpikeTest() {
        super(MainActivity.class);
    }
    
    @Override
    protected void tearDown() throws Exception {
		controller.close();
        Thread.sleep(5000);

    	super.tearDown();
    }

    public void testCanEstablishConnection() throws Exception {
    	AndroidHdpController bluetoothController = new AndroidHdpController(getActivity());
    	controller = NoninController.create(listener, bluetoothController);

        assertEstablishesConnection();
        assertMeasurementReceived();
    }

    private void assertEstablishesConnection() throws Exception {
        for (int i = 0; i < 300; i++) {
            if (connectionEstablished) {
                return;
            }
            Thread.sleep(100);
        }

        fail("Connection never established");
    }

    private void assertMeasurementReceived() throws Exception {
        for (int i = 0; i < 300; i++) {
            if (measurementReceived) {
                return;
            }
            Thread.sleep(100);
        }

        fail("Measurements never received");
    }
}
