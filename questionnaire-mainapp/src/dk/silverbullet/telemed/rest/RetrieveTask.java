package dk.silverbullet.telemed.rest;

import android.os.AsyncTask;
import dk.silverbullet.telemed.questionnaire.Questionnaire;
import dk.silverbullet.telemed.utils.Util;
import org.apache.http.client.methods.HttpRequestBase;

public abstract class RetrieveTask extends AsyncTask<String, String, String> {
    protected Questionnaire questionnaire;
    protected void setHeaders(HttpRequestBase requestBase) {
        Util.setHeaders(requestBase, questionnaire);
    }
}
