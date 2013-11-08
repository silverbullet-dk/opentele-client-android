package dk.silverbullet.telemed.rest;

import java.io.IOException;
import java.net.URL;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;

import android.util.Log;
import dk.silverbullet.telemed.questionnaire.Questionnaire;
import dk.silverbullet.telemed.rest.listener.MessageListListener;
import dk.silverbullet.telemed.utils.Util;

public class RetrieveMessageListTask extends RetrieveTask {
    private static final String TAG = Util.getTag(RetrieveMessageListTask.class);
    public static final String URL_PREFIX_LIST = "rest/message/list/";
    private final MessageListListener messageListListener;

    public RetrieveMessageListTask(Questionnaire questionnaire, MessageListListener messageListListener) {
        this.questionnaire = questionnaire;
        this.messageListListener = messageListListener;
    }

    @Override
    protected String doInBackground(String... params) {
        try {
            Log.d(TAG, "list...");
            DefaultHttpClient httpClient = new DefaultHttpClient();
            URL url = new URL(Util.getServerUrl(questionnaire));
            HttpGet httpGet = new HttpGet(new URL(url, URL_PREFIX_LIST).toExternalForm());
            setHeaders(httpGet);

            return httpClient.execute(httpGet, new BasicResponseHandler());
        } catch (IOException ioe) {
            messageListListener.sendError();
            return "";
        }
    }

    protected void onPostExecute(String result) {
        messageListListener.end(result);
    }
}
