package dk.silverbullet.telemed.rest.tasks;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;
import dk.silverbullet.telemed.OpenTeleApplication;
import dk.silverbullet.telemed.questionnaire.Questionnaire;
import dk.silverbullet.telemed.questionnaire.QuestionnaireFragment;
import dk.silverbullet.telemed.questionnaire.R;
import dk.silverbullet.telemed.rest.client.RestClient;
import dk.silverbullet.telemed.rest.client.RestException;
import dk.silverbullet.telemed.utils.Util;

public class RetrieveServerInformationTask extends AsyncTask<Void, Void, String> {
    private static final String TAG = Util.getTag(RetrieveServerInformationTask.class);
    private static final String SERVER_VERSION_PATH = "currentVersion";
    private final QuestionnaireFragment fragment;
    private final Questionnaire questionnaire;
    private ProgressDialog progress;

    public RetrieveServerInformationTask(QuestionnaireFragment fragment, Questionnaire questionnaire) {
        this.fragment = fragment;
        this.questionnaire = questionnaire;
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
        try {
            Log.d(TAG, "Getting server version...");
            return RestClient.getString(questionnaire, SERVER_VERSION_PATH);
        } catch (RestException e) {
            Log.e(TAG, "Failed to fetch version information", e);
            OpenTeleApplication.instance().logException(e);
            return null;
        }
    }
}
