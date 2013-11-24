package dk.silverbullet.telemed.rest;

import android.os.AsyncTask;
import android.util.Log;
import dk.silverbullet.telemed.questionnaire.Questionnaire;
import dk.silverbullet.telemed.utils.Json;
import dk.silverbullet.telemed.utils.Util;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.IOException;
import java.net.URL;

public class MarkMessagesAsReadTask extends AsyncTask<Long, Void, Void> {
    private static final String TAG = Util.getTag(MarkMessagesAsReadTask.class);
    private static final String URL_PREFIX_MARK_MESSAGES_AS_READ = "rest/message/markAsRead/";
    private final Questionnaire questionnaire;

    public MarkMessagesAsReadTask(Questionnaire questionnaire) {
        this.questionnaire = questionnaire;
    }

    @Override
    protected Void doInBackground(Long... ids) {
        if (ids.length == 0) {
            return null;
        }

        try {
            DefaultHttpClient httpClient = new DefaultHttpClient();
            URL url = new URL(Util.getServerUrl(questionnaire));
            HttpPost httpPost = new HttpPost(new URL(url, URL_PREFIX_MARK_MESSAGES_AS_READ).toExternalForm());
            Util.setHeaders(httpPost, questionnaire);

            httpPost.setEntity(new StringEntity(jsonList(ids), "UTF-8"));

            httpClient.execute(httpPost, new BasicResponseHandler());
        } catch (IOException e) {
            Log.e(TAG, "Could not mark messages as read", e);
        }
        return null;
    }

    private String jsonList(Long[] ids) {
        return Json.print(ids);
    }
}
