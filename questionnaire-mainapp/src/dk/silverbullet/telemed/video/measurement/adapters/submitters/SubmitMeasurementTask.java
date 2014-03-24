package dk.silverbullet.telemed.video.measurement.adapters.submitters;

import android.os.AsyncTask;
import android.util.Log;
import dk.silverbullet.telemed.rest.httpclient.HttpClientFactory;
import dk.silverbullet.telemed.utils.Util;
import dk.silverbullet.telemed.video.measurement.MeasurementInformer;
import dk.silverbullet.telemed.video.measurement.adapters.DeviceIdAndMeasurement;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicResponseHandler;

import java.io.IOException;
import java.net.URL;

public abstract class SubmitMeasurementTask<T> extends AsyncTask<DeviceIdAndMeasurement<T>, Void, Void> {
    private final String TAG = Util.getTag(SubmitMeasurementTask.class);
    private final MeasurementInformer informer;

    protected abstract String createJson(DeviceIdAndMeasurement<T> measurement);

    public SubmitMeasurementTask(MeasurementInformer informer) {
        this.informer = informer;
    }

    @Override
    protected Void doInBackground(DeviceIdAndMeasurement<T>... measurements) {
        DeviceIdAndMeasurement<T> measurement = measurements[0];
        String measurementJson = createJson(measurement);
        submitMeasurement(measurementJson);

        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        informer.hide();
    }

    private void submitMeasurement(String measurementJson) {
        HttpClient httpClient = HttpClientFactory.createHttpClient(informer.getContext());
        URL url;
        try {
            String serverUrl = informer.getServerUrl();

            url = new URL(serverUrl);
            HttpPost httpPost = new HttpPost(new URL(url, "rest/conference/measurementFromPatient").toExternalForm());

            httpPost.setEntity(new StringEntity(measurementJson, "UTF-8"));
            setHeaders(httpPost);

            httpClient.execute(httpPost, new BasicResponseHandler());
        } catch (IOException e) {
            Log.e(TAG, "Could not submit measurement", e);
        }
    }

    private void setHeaders(HttpRequestBase request) {
        String clientVersion = informer.getClientVersion();
        String userName = informer.getUsername();
        String password = informer.getPassword();

        Util.setHeaders(request, clientVersion, userName, password);
    }
}
