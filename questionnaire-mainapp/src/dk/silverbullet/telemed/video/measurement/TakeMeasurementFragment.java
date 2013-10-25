package dk.silverbullet.telemed.video.measurement;

import android.app.Fragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import dk.silverbullet.telemed.device.DeviceInitialisationException;
import dk.silverbullet.telemed.device.vitalographlungmonitor.LungMeasurement;
import dk.silverbullet.telemed.device.vitalographlungmonitor.LungMonitorController;
import dk.silverbullet.telemed.device.vitalographlungmonitor.LungMonitorListener;
import dk.silverbullet.telemed.device.vitalographlungmonitor.VitalographLungMonitorController;
import dk.silverbullet.telemed.questionnaire.R;
import dk.silverbullet.telemed.video.VideoActivity;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

public class TakeMeasurementFragment extends Fragment implements LungMonitorListener {
    private TextView statusText;
    private LungMonitorController controller;
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
        switch (pendingMeasurement.type) {
            case LUNG_FUNCTION:
                reveal();
                handleLungFunctionMeasurement();
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
            controller = createController();
        } catch (DeviceInitialisationException e) {
            setStatusText("Kunne ikke forbinde til lungefunktionsmåler.");
        }
    }

    private LungMonitorController createController() throws DeviceInitialisationException {
        return VitalographLungMonitorController.create(this);
    }

    @Override
    public void measurementReceived(String systemId, LungMeasurement measurement) {
        if (measurement.isGoodTest()) {
            setStatusText("Måling modtaget.");
            controller.close();
            new SubmitMeasurementAndRestartPollingTask().execute(measurement);
        } else {
            setStatusText("Dårlig måling modtaget. Prøv igen.");
        }
    }

    private void restartPendingMeasurementPolling() {
        pendingMeasurementPoller = new PendingMeasurementPoller(this);
        pendingMeasurementPoller.start();
    }

    @Override
    public void connected() {
        setStatusText("Udfør lungefunktionstest.");
    }

    @Override
    public void permanentProblem() {
        setStatusText("Der kan ikke skabes forbindelse.");
    }

    @Override
    public void temporaryProblem() {
        setStatusText("Kunne ikke hente data. Prøv evt. igen.");
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

    private class SubmitMeasurementAndRestartPollingTask extends AsyncTask<LungMeasurement, Void, Void> {

        @Override
        protected Void doInBackground(LungMeasurement... lungMeasurements) {
            VideoActivity activity = (VideoActivity)getActivity();
            LungMeasurement measurement = lungMeasurements[0];
            String measurementJson = new MeasurementResult(measurement).toJson();

            submitLungFunctionMeasurement(activity, measurementJson);

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            restartPendingMeasurementPolling();
            hide();
        }


        private void submitLungFunctionMeasurement(VideoActivity activity, String measurementJson) {
            DefaultHttpClient httpClient = new DefaultHttpClient();
            URL url;
            try {

                String serverUrl = activity.getServerURL();

                url = new URL(serverUrl);
                HttpPost httpPost = new HttpPost(new URL(url, "rest/conference/measurementFromPatient").toExternalForm());

                httpPost.setEntity(new StringEntity(measurementJson, "UTF-8"));
                setHeaders(httpPost, activity);

                httpClient.execute(httpPost, new BasicResponseHandler());

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        private void setHeaders(HttpRequestBase requestBase, VideoActivity activity) {
            requestBase.setHeader("Content-type", "application/json");
            requestBase.setHeader("Accept", "application/json");
            requestBase.setHeader("X-Requested-With", "json");
            requestBase.setHeader("Client-version", activity.getString(R.string.client_version));

            UsernamePasswordCredentials creds = new UsernamePasswordCredentials(activity.getUsername(), activity.getPassword());
            requestBase.setHeader(BasicScheme.authenticate(creds, "UTF-8", false));
        }
    }
}
