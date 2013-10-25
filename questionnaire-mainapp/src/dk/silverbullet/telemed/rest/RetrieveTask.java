package dk.silverbullet.telemed.rest;

import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.auth.BasicScheme;

import android.os.AsyncTask;
import dk.silverbullet.telemed.questionnaire.Questionnaire;
import dk.silverbullet.telemed.questionnaire.R;
import dk.silverbullet.telemed.utils.Util;

public abstract class RetrieveTask extends AsyncTask<String, String, String> {
    protected Questionnaire questionnaire;

    public void setHeaders(HttpRequestBase requestBase) {
        requestBase.setHeader("Content-type", "application/json");
        requestBase.setHeader("Accept", "application/json");
        requestBase.setHeader("X-Requested-With", "json");
        requestBase.setHeader("Client-version", questionnaire.getActivity().getString(R.string.client_version));

        UsernamePasswordCredentials creds = new UsernamePasswordCredentials(Util.getStringVariableValue(questionnaire,
                Util.VARIABLE_USERNAME), Util.getStringVariableValue(questionnaire, Util.VARIABLE_PASSWORD));
        requestBase.setHeader(BasicScheme.authenticate(creds, "UTF-8", false));
    }
}
