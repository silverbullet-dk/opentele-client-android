package dk.silverbullet.telemed.rest;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;
import dk.silverbullet.telemed.questionnaire.Questionnaire;
import dk.silverbullet.telemed.questionnaire.QuestionnaireFragment;
import dk.silverbullet.telemed.questionnaire.R;
import dk.silverbullet.telemed.rest.httpclient.HttpClientFactory;
import dk.silverbullet.telemed.utils.Util;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import java.io.IOException;

public class RetrieveServerInformationTask extends AsyncTask<Void, Void, String> {
    private static final String TAG = Util.getTag(RetrieveServerInformationTask.class);
    private static final String SERVER_VERSION_URL = "currentVersion";
    private final String serverUrl;
    private ProgressDialog progress;
    private final QuestionnaireFragment fragment;
    private Questionnaire questionnaire;

    public RetrieveServerInformationTask(QuestionnaireFragment fragment, Questionnaire questionnaire) {
        this.fragment = fragment;
        this.questionnaire = questionnaire;
        this.serverUrl = Util.getServerUrl(questionnaire);
    }

    @Override
    protected void onPreExecute() {
        // show progress dialog
        progress = new ProgressDialog(fragment.getActivity());
        progress.setTitle(fragment.getActivity().getString(R.string.checking_version_headling));
        progress.setMessage(fragment.getActivity().getString(R.string.checking_version_detail));
        progress.setCancelable(false);

        progress.show();
    }

    @Override
    protected void onPostExecute(String result) {
        if (progress != null && progress.isShowing()) {
            progress.dismiss();
        }
        fragment.fetchServerVersionFinished(result);
    }

    @Override
    protected String doInBackground(Void... params) {
        Log.d(TAG, "Getting server version...");

        HttpClient httpClient = httpClientWithTimeoutOf5Seconds();

        HttpGet httppost = new HttpGet(serverUrl + SERVER_VERSION_URL);

        httppost.setHeader("Content-type", "application/json");
        httppost.setHeader("Accept", "application/json");
        httppost.setHeader("X-Requested-With", "json");
        try {
            String response = httpClient.execute(httppost, new BasicResponseHandler());
            return response;
        } catch (IOException e) {
            Log.e(TAG, "Faild to fetch version information from: " + SERVER_VERSION_URL);
            e.printStackTrace();
            return null;
        }
    }

    private HttpClient httpClientWithTimeoutOf5Seconds() {
        HttpClient httpClient = HttpClientFactory.createHttpClient(questionnaire.getActivity());

        HttpParams httpParameters = httpClient.getParams();
        HttpConnectionParams.setConnectionTimeout(httpParameters, 5000);
        HttpConnectionParams.setSoTimeout(httpParameters, 5000);

        return httpClient;
    }
}
