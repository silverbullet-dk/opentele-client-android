package dk.silverbullet.telemed.rest;

import dk.silverbullet.telemed.rest.client.RestClient;
import dk.silverbullet.telemed.rest.client.RestException;

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
        Variable<?> id = questionnaire.getValuePool().get(Util.VARIABLE_ID);
        String path = GET_URL_PREFIX + id.getExpressionValue().getValue();

        try {
            String schemaResponse = RestClient.get(questionnaire, path);
            return schemaResponse;
        } catch (RestException e) {
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
