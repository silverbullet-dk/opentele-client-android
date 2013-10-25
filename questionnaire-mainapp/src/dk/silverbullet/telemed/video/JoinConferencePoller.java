package dk.silverbullet.telemed.video;

import android.util.Log;
import com.google.gson.Gson;
import dk.silverbullet.telemed.MainActivity;
import dk.silverbullet.telemed.questionnaire.R;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.auth.BasicScheme;
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
    private final String username;
    private final String password;
    private volatile boolean stopped;
    private volatile HttpGet httpGet;

    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private Runnable checkForConferenceRunnable;

    public JoinConferencePoller(final MainActivity mainActivity, String username, String password) {
        this.mainActivity = mainActivity;
        this.username = username;
        this.password = password;

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
            setHeaders(httpGet);

            String result = httpClient.execute(httpGet, new BasicResponseHandler());

            if (!stopped && !result.isEmpty()) {
                return new Gson().fromJson(result, PendingConferenceResponse.class);
            }
        } catch (IOException e) {
            // We shut down the thread by closing the socket, which looks worse than it is. Therefore, this is just
            // an "info" log.
            Log.i(TAG, "Could not check for pending conference, probably because user logged out", e);
        }
        return new PendingConferenceResponse();
    }

    class PendingConferenceResponse {
        String roomKey = "";
        String serviceUrl = "";
    }

    private void setHeaders(HttpRequestBase requestBase) {
        requestBase.setHeader("Content-type", "application/json");
        requestBase.setHeader("Accept", "application/json");
        requestBase.setHeader("X-Requested-With", "json");
        requestBase.setHeader("Client-version", mainActivity.getString(R.string.client_version));

        UsernamePasswordCredentials credentials = new UsernamePasswordCredentials(username, password);
        requestBase.setHeader(BasicScheme.authenticate(credentials, "UTF-8", false));
    }
}
