package dk.silverbullet.telemed.rest;

import java.io.IOException;

import android.util.Log;
import dk.silverbullet.telemed.questionnaire.Questionnaire;
import dk.silverbullet.telemed.rest.listener.ListListener;
import dk.silverbullet.telemed.rest.httpclient.HttpClientFactory;
import dk.silverbullet.telemed.utils.Util;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;

public class RetrieveQuestionnaireListTask extends RetrieveTask {

    private final ListListener listListener;
    private static final String TAG = Util.getTag(RetrieveQuestionnaireListTask.class);
    private static final String LIST_URL_PREFIX = "rest/questionnaire/listing";

    public RetrieveQuestionnaireListTask(Questionnaire questionnaire, ListListener listListener) {
        this.questionnaire = questionnaire;
        this.listListener = listListener;
    }

    @Override
    protected String doInBackground(String... params) {
        Log.d(TAG, "list...");
        HttpClient httpClient = HttpClientFactory.createHttpClient(questionnaire.getActivity());
        HttpGet httpGet = new HttpGet(Util.getServerUrl(questionnaire) + LIST_URL_PREFIX);
        setHeaders(httpGet);

        try {
            return httpClient.execute(httpGet, new BasicResponseHandler());
        } catch (IOException e) {
            Log.w(TAG, e.getMessage());
            e.printStackTrace();
            listListener.sendError();

            return "";
        }
    }

    @Override
    protected void onPostExecute(String result) {
        listListener.setJson(result);
    }
}
