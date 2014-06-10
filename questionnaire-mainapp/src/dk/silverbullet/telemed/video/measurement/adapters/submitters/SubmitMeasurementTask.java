package dk.silverbullet.telemed.video.measurement.adapters.submitters;

import android.os.AsyncTask;
import android.util.Log;
import dk.silverbullet.telemed.rest.client.RestClient;
import dk.silverbullet.telemed.rest.client.RestException;
import dk.silverbullet.telemed.utils.Util;
import dk.silverbullet.telemed.video.measurement.MeasurementInformer;
import dk.silverbullet.telemed.video.measurement.MeasurementResult;
import dk.silverbullet.telemed.video.measurement.adapters.DeviceIdAndMeasurement;

public abstract class SubmitMeasurementTask<T> extends AsyncTask<DeviceIdAndMeasurement<T>, Void, Void> {
    private final String TAG = Util.getTag(SubmitMeasurementTask.class);
    private final MeasurementInformer informer;

    protected abstract MeasurementResult createMeasurementResult(DeviceIdAndMeasurement<T> measurement);

    public SubmitMeasurementTask(MeasurementInformer informer) {
        this.informer = informer;
    }

    @Override
    protected Void doInBackground(DeviceIdAndMeasurement<T>... measurements) {
        DeviceIdAndMeasurement<T> measurement = measurements[0];
        MeasurementResult measurementResult = createMeasurementResult(measurement);

        try {
            RestClient.postJson(informer, "rest/conference/measurementFromPatient", measurementResult);
        } catch (RestException e) {
            Log.e(TAG, "Could not submit measurement", e);
        }

        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        informer.hide();
    }
}
