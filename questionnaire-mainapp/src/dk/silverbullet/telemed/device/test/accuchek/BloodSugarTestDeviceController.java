package dk.silverbullet.telemed.device.test.accuchek;

import dk.silverbullet.telemed.device.accuchek.BloodSugarDeviceListener;
import dk.silverbullet.telemed.device.accuchek.BloodSugarMeasurement;
import dk.silverbullet.telemed.device.accuchek.BloodSugarMeasurements;
import dk.silverbullet.telemed.device.continua.android.SingleShotTimer;
import dk.silverbullet.telemed.device.continua.android.StopwatchListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class BloodSugarTestDeviceController implements StopwatchListener {
    // Is public, so the tests can set it low
    public static long INTERVAL_BETWEEN_CALLBACKS_MS = 3000;

    private enum State {
        INITIALIZING, CONNECTED, FETCHING_DIARY, DONE
    }

    private BloodSugarDeviceListener listener;
    private State currentState = State.INITIALIZING;

    public BloodSugarTestDeviceController(BloodSugarDeviceListener listener) {
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
            currentState = State.FETCHING_DIARY;
            listener.fetchingDiary();
            restartTimer();
            break;
        case FETCHING_DIARY:
            currentState = State.DONE;
            listener.measurementsParsed(createMeasurements());
            break;
        case DONE:
            // Nothing to do
            break;
        }
    }

    public synchronized void close() {
        listener = null;
    }

    private void restartTimer() {
        new SingleShotTimer(INTERVAL_BETWEEN_CALLBACKS_MS, this);
    }

    private BloodSugarMeasurements createMeasurements() {
        BloodSugarMeasurements result = new BloodSugarMeasurements();
        result.measurements = createListOfMeasurements();
        result.serialNumber = "12345";
        result.transferTime = new Date();
        return result;
    }

    private List<BloodSugarMeasurement> createListOfMeasurements() {
        List<BloodSugarMeasurement> result = new ArrayList<BloodSugarMeasurement>();
        result.add(createSingleMeasurement(-60, 5.2, true, false));
        result.add(createSingleMeasurement(-50, 5.3, false, true));
        result.add(createSingleMeasurement(-40, 5.0, false, false));
        result.add(createSingleMeasurement(-5, 6.4, false, false));
        return result;
    }

    private BloodSugarMeasurement createSingleMeasurement(int minutesAgo, double value, boolean beforeMeal,
            boolean afterMeal) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MINUTE, -minutesAgo);

        BloodSugarMeasurement result = new BloodSugarMeasurement();
        result.timeOfMeasurement = calendar.getTime();
        result.result = value;
        result.isBeforeMeal = beforeMeal;
        result.isAfterMeal = afterMeal;
        return result;
    }
}