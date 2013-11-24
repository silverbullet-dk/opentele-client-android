package dk.silverbullet.telemed.video;

import android.util.Log;
import com.google.gson.annotations.Expose;
import dk.silverbullet.telemed.MainActivity;
import dk.silverbullet.telemed.questionnaire.Questionnaire;
import dk.silverbullet.telemed.utils.Json;
import dk.silverbullet.telemed.utils.Util;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import java.io.IOException;
import java.net.URL;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import static java.util.concurrent.TimeUnit.SECONDS;

public class JoinConferencePoller {
    private static final String TAG = "ConferenceHandler";
    private static final int FIVE_SECONDS_IN_MILLIS = 5000;
    private final MainActivity mainActivity;
    private final Questionnaire mainQuestionnaire;
    private volatile boolean stopped;
    private volatile HttpGet httpGet;

    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private Runnable checkForConferenceRunnable;

    public JoinConferencePoller(final MainActivity mainActivity, Questionnaire mainQuestionnaire) {
        this.mainActivity = mainActivity;
        this.mainQuestionnaire = mainQuestionnaire;

        checkForConferenceRunnable = new Runnable() {
            @Override
            public void run() {
                PendingConferenceResponse checkForConferenceResponse = checkForConference();
                if (!checkForConferenceResponse.roomKey.isEmpty()) {
                    Log.d(TAG, "Got roomkey" + checkForConferenceResponse.roomKey + "and service url:" + checkForConferenceResponse.serviceUrl);
                    mainActivity.startVideoConference(checkForConferenceResponse.roomKey, checkForConferenceResponse.serviceUrl);

                    scheduler.shutdown();
                }
            }
        };
    }

    public void start() {
        scheduler.scheduleWithFixedDelay(checkForConferenceRunnable, 0, 2, SECONDS);
    }

    public void stop() {
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

    private PendingConferenceResponse checkForConference() {
        DefaultHttpClient httpClient = new DefaultHttpClient();

        HttpParams httpParameters = httpClient.getParams();
        HttpConnectionParams.setConnectionTimeout(httpParameters, FIVE_SECONDS_IN_MILLIS);
        HttpConnectionParams.setSoTimeout(httpParameters, 0);

        URL url;
        try {
            url = new URL(mainActivity.getServerURL());

            httpGet = new HttpGet(new URL(url, "rest/conference/patientHasPendingConference").toExternalForm());
            Util.setHeaders(httpGet, mainQuestionnaire);

            String result = httpClient.execute(httpGet, new BasicResponseHandler());

            if (!stopped && !result.isEmpty()) {
                return Json.parse(result, PendingConferenceResponse.class);
            }
        } catch (IOException e) {
            // If we're trying to stop this poller thread by aborting the GET request, we get an IOException. In
            // that case, we don't log anything.
            if (!stopped) {
                Log.e(TAG, "Could not check for pending conference", e);
            }
        }
        return new PendingConferenceResponse();
    }

    class PendingConferenceResponse {
        @Expose String roomKey = "";
        @Expose String serviceUrl = "";
    }
}
