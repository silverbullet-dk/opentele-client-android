package dk.silverbullet.telemed.rest;

import java.io.IOException;

import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;

import android.util.Log;
import dk.silverbullet.telemed.questionnaire.Questionnaire;
import dk.silverbullet.telemed.questionnaire.expression.Variable;
import dk.silverbullet.telemed.rest.listener.SkemaListener;
import dk.silverbullet.telemed.utils.Util;

public class RetrieveSchemaTask extends RetrieveTask {

    private final SkemaListener skemaListener;
    private static final String TAG = Util.getTag(RetrieveSchemaTask.class);
    private static final String GET_URL_PREFIX = "rest/questionnaire/download/";

    public RetrieveSchemaTask(Questionnaire questionnaire, SkemaListener skemaListener) {
        this.questionnaire = questionnaire;
        this.skemaListener = skemaListener;
    }

    @Override
    protected String doInBackground(String... params) {

        Log.d(TAG, "getSkema...");
        DefaultHttpClient httpclient = new DefaultHttpClient();
        Variable<?> id = questionnaire.getValuePool().get(Util.VARIABLE_ID);
        HttpGet httppost = new HttpGet(Util.getServerUrl(questionnaire) + GET_URL_PREFIX
                + id.getExpressionValue().getValue());

        httppost.setHeader("Content-type", "application/json");
        httppost.setHeader("Accept", "application/json");
        httppost.setHeader("X-Requested-With", "json");

        Variable<?> username = questionnaire.getValuePool().get(Util.VARIABLE_USERNAME);
        Variable<?> password = questionnaire.getValuePool().get(Util.VARIABLE_PASSWORD);

        UsernamePasswordCredentials creds = new UsernamePasswordCredentials(username.getExpressionValue().toString(),
                password.evaluate().toString());
        httppost.setHeader(BasicScheme.authenticate(creds, "UTF-8", false));

        try {
            String schemaResponse = httpclient.execute(httppost, new BasicResponseHandler());
            return schemaResponse;
        } catch (IOException e) {
            Log.e(TAG, e.getMessage());
            skemaListener.sendError();

            return "";
        }
    }

    @Override
    protected void onPostExecute(String result) {
        skemaListener.setJson(result);
    }
}
