package dk.silverbullet.telemed.rest;

import java.io.IOException;
import java.net.URL;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;

import android.util.Log;
import dk.silverbullet.telemed.questionnaire.Questionnaire;
import dk.silverbullet.telemed.rest.listener.MessageGetListener;
import dk.silverbullet.telemed.utils.Util;

public class RetrieveMessageTask extends RetrieveTask {
    private static final String TAG = Util.getTag(RetrieveMessageTask.class);
    private static final String URL_PREFIX_GET = "rest/message/element/";

    private final MessageGetListener messageGetListener;

    public RetrieveMessageTask(Questionnaire questionnaire, MessageGetListener messageGetListener) {
        this.questionnaire = questionnaire;
        this.messageGetListener = messageGetListener;
    }

    @Override
    protected String doInBackground(String... params) {
        try {
            String messageId = params[0];

            Log.d(TAG, "getMessage...");
            DefaultHttpClient httpclient = new DefaultHttpClient();
            URL url = new URL(Util.getServerUrl(questionnaire));
            HttpGet httpGet = new HttpGet(new URL(url, URL_PREFIX_GET + messageId).toExternalForm());
            setHeaders(httpGet);

            return httpclient.execute(httpGet, new BasicResponseHandler());
        } catch (IOException ioe) {
            messageGetListener.sendError();
            return "";
        }
    }

    @Override
    protected void onPostExecute(String result) {
        Log.d(TAG, "onPostExecute...:" + result);

        messageGetListener.end(result);
    }
}
