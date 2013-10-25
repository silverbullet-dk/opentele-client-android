package dk.silverbullet.telemed.rest;

import java.io.IOException;
import java.net.URL;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;

import android.util.Log;
import dk.silverbullet.telemed.questionnaire.Questionnaire;
import dk.silverbullet.telemed.rest.listener.MessageWriteListener;
import dk.silverbullet.telemed.utils.Util;

public class RetrieveRecipientsTask extends RetrieveTask {
    private static final String TAG = Util.getTag(RetrieveRecipientsTask.class);
    public static final String URL_PREFIX_CLINICIANS = "rest/message/recipients/";
    private final MessageWriteListener messageWriteListener;

    public RetrieveRecipientsTask(Questionnaire questionnaire, MessageWriteListener messageWriteListener) {
        this.questionnaire = questionnaire;
        this.messageWriteListener = messageWriteListener;
    }

    @Override
    protected String doInBackground(String... params) {
        try {
            Log.d(TAG, "get recipients...");
            DefaultHttpClient httpclient = new DefaultHttpClient();
            URL url = new URL(Util.getServerUrl(questionnaire));
            HttpGet httpGet = new HttpGet(new URL(url, URL_PREFIX_CLINICIANS).toExternalForm());
            setHeaders(httpGet);

            return httpclient.execute(httpGet, new BasicResponseHandler());
        } catch (IOException ioe) {
            messageWriteListener.sendError();
            return "";
        }
    }

    @Override
    protected void onPostExecute(String result) {
        messageWriteListener.setRecipients(result);
    }
}
