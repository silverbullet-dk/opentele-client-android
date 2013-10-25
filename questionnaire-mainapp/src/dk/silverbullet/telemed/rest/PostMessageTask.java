package dk.silverbullet.telemed.rest;

import java.io.IOException;
import java.net.URL;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import android.util.Log;
import dk.silverbullet.telemed.questionnaire.Questionnaire;
import dk.silverbullet.telemed.rest.listener.MessageWriteListener;
import dk.silverbullet.telemed.utils.Util;

public class PostMessageTask extends RetrieveTask {
    private static final String TAG = Util.getTag(PostMessageTask.class);
    private static final String URL_PREFIX_WRITE = "rest/message/list/";
    private final MessageWriteListener messageWriteListener;

    public PostMessageTask(Questionnaire questionnaire, MessageWriteListener messageWriteListener) {
        this.questionnaire = questionnaire;
        this.messageWriteListener = messageWriteListener;
    }

    @Override
    protected String doInBackground(String... params) {
        String message = params[0];

        try {
            Log.d(TAG, "postMessage...");
            DefaultHttpClient httpclient = new DefaultHttpClient();
            URL url = new URL(Util.getServerUrl(questionnaire));
            HttpPost httpPost = new HttpPost(new URL(url, URL_PREFIX_WRITE).toExternalForm());
            setHeaders(httpPost);

            httpPost.setEntity(new StringEntity(message, "UTF-8"));

            HttpResponse response = httpclient.execute(httpPost);
            return "" + response.getStatusLine().getStatusCode();
        } catch (IOException ioe) {
            messageWriteListener.sendError();
            return "";
        }
    }

    @Override
    protected void onPostExecute(String result) {
        messageWriteListener.end(result);
    }
}
