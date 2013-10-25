package dk.silverbullet.telemed.rest;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;

import android.util.Log;
import dk.silverbullet.telemed.questionnaire.Questionnaire;
import dk.silverbullet.telemed.rest.listener.UploadListener;
import dk.silverbullet.telemed.utils.Util;

public class PostQuestionnaireTask extends RetrieveTask {
    private final UploadListener uploadListener;
    private static final String TAG = Util.getTag(PostQuestionnaireTask.class);
    private static final String UPLOAD_URL_PREFIX = "rest/questionnaire/listing";

    public PostQuestionnaireTask(Questionnaire questionnaire, UploadListener uploadListener) {
        this.questionnaire = questionnaire;
        this.uploadListener = uploadListener;
    }

    @Override
    protected String doInBackground(String... params) {
        String jsonObject = params[0];
        Log.d(TAG, "upload...");
        DefaultHttpClient httpclient = new DefaultHttpClient();
        HttpPost httppost = new HttpPost(Util.getServerUrl(questionnaire) + UPLOAD_URL_PREFIX);
        setHeaders(httppost);

        try {
            httppost.setEntity(new StringEntity(jsonObject, "UTF-8"));
            return httpclient.execute(httppost, new BasicResponseHandler());
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            Log.w(TAG, e.getMessage());

            uploadListener.sendError();
            return "";
        } catch (IOException e) {
            e.printStackTrace();
            Log.w(TAG, e.getMessage());

            uploadListener.sendError();
            return "";
        }
    }

    @Override
    protected void onPostExecute(String result) {
        uploadListener.end(result.contains("success"));
    }
}
