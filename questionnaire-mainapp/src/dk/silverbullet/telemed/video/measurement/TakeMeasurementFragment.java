package dk.silverbullet.telemed.video.measurement;

import android.app.Fragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import dk.silverbullet.telemed.device.DeviceController;
import dk.silverbullet.telemed.device.DeviceInitialisationException;
import dk.silverbullet.telemed.device.andbloodpressure.AndBloodPressureController;
import dk.silverbullet.telemed.device.andbloodpressure.BloodPressureAndPulse;
import dk.silverbullet.telemed.device.continua.ContinuaListener;
import dk.silverbullet.telemed.device.continua.android.AndroidHdpController;
import dk.silverbullet.telemed.device.vitalographlungmonitor.LungMeasurement;
import dk.silverbullet.telemed.device.vitalographlungmonitor.LungMonitorListener;
import dk.silverbullet.telemed.device.vitalographlungmonitor.VitalographLungMonitorController;
import dk.silverbullet.telemed.questionnaire.R;
import dk.silverbullet.telemed.utils.Util;
import dk.silverbullet.telemed.video.VideoActivity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.IOException;
import java.net.URL;

public class TakeMeasurementFragment extends Fragment implements LungMonitorListener, ContinuaListener<BloodPressureAndPulse> {
    private TextView statusText;
    private DeviceController controller;
    private TextView measurementTypeText;
    private PendingMeasurementPoller pendingMeasurementPoller;
    private ViewGroup takeMeasurementViewGroup;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View takeMeasurementView = inflater.inflate(R.layout.video_take_measurement, container, false);

        takeMeasurementViewGroup = (ViewGroup) takeMeasurementView.findViewById(R.id.take_measurement_parent);
        measurementTypeText = (TextView) takeMeasurementView.findViewById(R.id.measurement_type);
        statusText = (TextView) takeMeasurementView.findViewById(R.id.status_text);

        return takeMeasurementView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        pendingMeasurementPoller = new PendingMeasurementPoller(this);
        pendingMeasurementPoller.start();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if(controller != null) {
            controller.close();
        }

        pendingMeasurementPoller.stop();
    }

    public void takeMeasurement(PendingMeasurement pendingMeasurement) {
        pendingMeasurementPoller.stop();
        reveal();

        switch (pendingMeasurement.type) {
            case LUNG_FUNCTION:
                handleLungFunctionMeasurement();
                break;
            case BLOOD_PRESSURE:
                handleBloodPressureMeasurement();
                break;
            default:
                throw new IllegalArgumentException("Unknown measurement type: '" + pendingMeasurement.type + "'");
        }
    }

    private void reveal() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                takeMeasurementViewGroup.setVisibility(View.VISIBLE);
            }
        });
    }

    private void hide() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                takeMeasurementViewGroup.setVisibility(View.GONE);
            }
        });
    }

    private void handleLungFunctionMeasurement() {
        try {
            setMeasurementTypeText("Lungefunktion");
            setStatusText("Tænd for apparatet og udfør lungefunktionstest.");
            controller = createLungMonitorController();
        } catch (DeviceInitialisationException e) {
            setStatusText("Kunne ikke forbinde til lungefunktionsmåler.");
        }
    }

    private void handleBloodPressureMeasurement() {
        try {
            setMeasurementTypeText("Blodtryk og puls");
            setStatusText("Tænd for apparatet og udfør blodtryksmåling");
            controller = createBloodPressureController();
        } catch (DeviceInitialisationException e) {
            setStatusText("Kunne ikke forbinde til blodtryksmåler.");
        }
    }

    private DeviceController createLungMonitorController() throws DeviceInitialisationException {
        return VitalographLungMonitorController.create(this);
    }

    private DeviceController createBloodPressureController() throws DeviceInitialisationException {
        return AndBloodPressureController.create(this, new AndroidHdpController(getActivity()));
    }

    @Override
    public void measurementReceived(String deviceId, LungMeasurement measurement) {
        if (measurement.isGoodTest()) {
            setStatusText("Måling modtaget.");
            controller.close();

            DeviceIdAndMeasurement<LungMeasurement> deviceIdAndMeasurement = new DeviceIdAndMeasurement<LungMeasurement>(deviceId, measurement);
            new SubmitLungMeasurementAndRestartPollingTask().execute(deviceIdAndMeasurement);
        } else {
            setStatusText("Dårlig måling modtaget. Prøv igen.");
        }
    }

    @Override
    public void measurementReceived(String deviceId, BloodPressureAndPulse measurement) {
        setStatusText("Måling modtaget.");
        controller.close();

        DeviceIdAndMeasurement<BloodPressureAndPulse> deviceIdAndMeasurement = new DeviceIdAndMeasurement<BloodPressureAndPulse>(deviceId, measurement);
        new SubmitBloodPressureMeasurementAndRestartPollingTask().execute(deviceIdAndMeasurement);
    }

    private void restartPendingMeasurementPolling() {
        pendingMeasurementPoller = new PendingMeasurementPoller(this);
        pendingMeasurementPoller.start();
    }

    @Override
    public void connected() {
        setStatusText("Udfør måling.");
    }

    @Override
    public void disconnected() {
        setStatusText("Afkoblet.");
    }

    @Override
    public void permanentProblem() {
        setStatusText("Der kan ikke skabes forbindelse.");
    }

    @Override
    public void temporaryProblem() {
        setStatusText("Kunne ikke modtage data. Prøv evt. igen.");
    }

    private void setStatusText(final String text) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                statusText.setText(text);
            }
        });
    }

    private void setMeasurementTypeText(final String text) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                measurementTypeText.setText(text);
            }
        });
    }

    private class DeviceIdAndMeasurement<T> {
        private final String deviceId;
        private final T measurement;

        public DeviceIdAndMeasurement(String deviceId, T measurement) {
            this.deviceId = deviceId;
            this.measurement = measurement;
        }

        public String getDeviceId() {
            return deviceId;
        }

        public T getMeasurement() {
            return measurement;
        }
    }

    private abstract class SubmitMeasurementAndRestartPollingTask<T> extends AsyncTask<DeviceIdAndMeasurement<T>, Void, Void> {
        private final String TAG = Util.getTag(SubmitMeasurementAndRestartPollingTask.class);

        protected abstract String createJson(DeviceIdAndMeasurement<T> measurement);

        @Override
        protected Void doInBackground(DeviceIdAndMeasurement<T>... measurements) {
            VideoActivity activity = (VideoActivity)getActivity();
            DeviceIdAndMeasurement<T> measurement = measurements[0];
            String measurementJson = createJson(measurement);
            submitMeasurement(activity, measurementJson);

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            restartPendingMeasurementPolling();
            hide();
        }

        private void submitMeasurement(VideoActivity activity, String measurementJson) {
            DefaultHttpClient httpClient = new DefaultHttpClient();
            URL url;
            try {
                String serverUrl = activity.getServerURL();

                url = new URL(serverUrl);
                HttpPost httpPost = new HttpPost(new URL(url, "rest/conference/measurementFromPatient").toExternalForm());

                httpPost.setEntity(new StringEntity(measurementJson, "UTF-8"));
                setHeaders(httpPost, activity);

                httpClient.execute(httpPost, new BasicResponseHandler());
            } catch (IOException e) {
                Log.e(TAG, "Could not submit measurement", e);
            }
        }

        private void setHeaders(HttpRequestBase request, VideoActivity activity) {
            String clientVersion = activity.getString(R.string.client_version);
            String userName = activity.getUsername();
            String password = activity.getPassword();

            Util.setHeaders(request, clientVersion, userName, password);
        }
    }

    private class SubmitLungMeasurementAndRestartPollingTask extends SubmitMeasurementAndRestartPollingTask<LungMeasurement> {
        @Override
        protected String createJson(DeviceIdAndMeasurement<LungMeasurement> measurement) {
            return new MeasurementResult(measurement.getDeviceId(), measurement.getMeasurement()).toJson();
        }
    }

    private class SubmitBloodPressureMeasurementAndRestartPollingTask extends SubmitMeasurementAndRestartPollingTask<BloodPressureAndPulse> {
        @Override
        protected String createJson(DeviceIdAndMeasurement<BloodPressureAndPulse> measurement) {
            return new MeasurementResult(measurement.getDeviceId(), measurement.getMeasurement()).toJson();
        }
    }
}
