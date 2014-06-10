package dk.silverbullet.telemed.rest.tasks;

import android.os.AsyncTask;
import android.util.Log;
import dk.silverbullet.telemed.questionnaire.Questionnaire;
import dk.silverbullet.telemed.rest.client.RestClient;
import dk.silverbullet.telemed.rest.client.RestException;
import dk.silverbullet.telemed.rest.listener.RetrieveEntityListener;
import dk.silverbullet.telemed.utils.Util;

public class RetrieveEntityTask<T>  extends AsyncTask<String, String, T> {
    private static final String TAG = Util.getTag(RetrieveEntityTask.class);
    private final String path;
    private final Questionnaire questionnaire;
    private final RetrieveEntityListener<T> listener;
    private final Class<T> clazz;

    public RetrieveEntityTask(String path, Questionnaire questionnaire, RetrieveEntityListener<T> listener, Class<T> clazz) {
        this.path = path;
        this.questionnaire = questionnaire;
        this.listener = listener;
        this.clazz = clazz;
    }

    @Override
    protected T doInBackground(String... params) {
        try {
            return RestClient.getJson(questionnaire, path, clazz);
        } catch (RestException e) {
            Log.w(TAG, "Could not retrieve acknowledgement list", e);
        }
        return null;
    }

    @Override
    protected void onPostExecute(T result) {
        if (result != null) {
            listener.retrieved(result);
        } else {
            listener.retrieveError();
        }
    }
}
