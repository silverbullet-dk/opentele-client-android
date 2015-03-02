package dk.silverbullet.telemed.video.measurement;

import android.util.Log;
import dk.silverbullet.telemed.questionnaire.R;
import dk.silverbullet.telemed.rest.client.lowlevel.HttpHeaderBuilder;
import dk.silverbullet.telemed.rest.httpclient.HttpClientFactory;
import dk.silverbullet.telemed.utils.Json;
import dk.silverbullet.telemed.video.VideoActivity;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.client.BasicResponseHandler;

import java.io.IOException;
import java.net.URL;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import static java.util.concurrent.TimeUnit.SECONDS;

class PendingMeasurementPoller {
    private static final String TAG = PendingMeasurement.class.getName();

    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private Runnable checkForPendingMeasurementRunnable;
    private TakeMeasurementFragment parentFragment;
    private volatile boolean stopped;
    private volatile HttpGet httpGet;

    PendingMeasurementPoller(TakeMeasurementFragment fragment) {
        parentFragment = fragment;

        checkForPendingMeasurementRunnable = new Runnable() {
            @Override
            public void run() {
                PendingMeasurement pendingMeasurement = checkForPendingMeasurement();
                parentFragment.takeMeasurement(pendingMeasurement);
            }
        };
    }

    void start() {
        scheduler.scheduleWithFixedDelay(checkForPendingMeasurementRunnable, 0, 3, SECONDS);
    }

    void stop() {
        stopped = true;
        final HttpGet pendingHttpGet = httpGet;
        if (pendingHttpGet != null) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    pendingHttpGet.abort();
                }
            }).start();
        }
        scheduler.shutdownNow();
    }

    private PendingMeasurement checkForPendingMeasurement() {
        try {
            // We don't use the otherwise nice and simple RestClient here, since we want to hold on to our HttpGet
            // object since we might like to abort it.

            VideoActivity videoActivity = (VideoActivity) parentFragment.getActivity();

            URL url = new URL(videoActivity.getServerURL());
            httpGet = new HttpGet(new URL(url, "rest/conference/patientHasPendingMeasurement").toExternalForm());
            setHeaders(httpGet);

            HttpClient httpClient = HttpClientFactory.createHttpClient(videoActivity);
            String result = httpClient.execute(httpGet, new BasicResponseHandler());

            if (!stopped && !result.isEmpty()) {
                return Json.parse(result, PendingMeasurement.class);
            }
        } catch (IOException e) {
            Log.e(TAG, "Could not check for pending measurement", e);
        }

        return null;
    }

    private void setHeaders(HttpRequestBase requestBase) {
        new HttpHeaderBuilder(requestBase, parentFragment)
                .withAcceptTypeJSON()
                .withAuthentication();
    }
}
