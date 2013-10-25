package dk.silverbullet.telemed.device.test.lungmonitor;

import dk.silverbullet.telemed.device.continua.android.SingleShotTimer;
import dk.silverbullet.telemed.device.continua.android.StopwatchListener;
import dk.silverbullet.telemed.device.vitalographlungmonitor.LungMeasurement;
import dk.silverbullet.telemed.device.vitalographlungmonitor.LungMonitorController;
import dk.silverbullet.telemed.device.vitalographlungmonitor.LungMonitorListener;

public class LungMonitorTestDeviceController implements LungMonitorController, StopwatchListener {
    // Is public, so the tests can set it low
    public static long INTERVAL_BETWEEN_CALLBACKS_MS = 3000;

    private enum State {
        INITIALIZING, CONNECTED, DONE
    }

    private LungMonitorListener listener;
    private State currentState = State.INITIALIZING;

    public LungMonitorTestDeviceController(LungMonitorListener listener) {
        this.listener = listener;
        restartTimer();
    }

    @Override
    public synchronized void timeout() {
        if (listener == null) {
            return;
        }

        switch (currentState) {
        case INITIALIZING:
            currentState = State.CONNECTED;
            listener.connected();
            restartTimer();
            break;
        case CONNECTED:
            currentState = State.DONE;
            listener.measurementReceived("1234567890", createMeasurement());
            break;
        case DONE:
            // Nothing to do
            break;
        }
    }

    private LungMeasurement createMeasurement() {
        return new LungMeasurement(3.8f, 4.2f, 0.91f, 3.95f, true, 933);
    }

    @Override
    public void close() {
        listener = null;
    }

    private void restartTimer() {
        new SingleShotTimer(INTERVAL_BETWEEN_CALLBACKS_MS, this);
    }
}
