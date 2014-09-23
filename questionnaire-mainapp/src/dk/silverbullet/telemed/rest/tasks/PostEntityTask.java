package dk.silverbullet.telemed.rest.tasks;

import android.os.AsyncTask;
import android.util.Log;
import dk.silverbullet.telemed.OpenTeleApplication;
import dk.silverbullet.telemed.questionnaire.Questionnaire;
import dk.silverbullet.telemed.rest.client.RestClient;
import dk.silverbullet.telemed.rest.client.RestException;
import dk.silverbullet.telemed.rest.listener.PostEntityListener;
import dk.silverbullet.telemed.utils.Util;

public class PostEntityTask<T> extends AsyncTask<String, String, Void> {
    private static final String TAG = Util.getTag(PostEntityTask.class);
    private final T entity;
    private final String path;
    private final Questionnaire questionnaire;
    private final PostEntityListener listener;
    private boolean success;

    public PostEntityTask(T entity, String path, Questionnaire questionnaire, PostEntityListener listener) {
        this.entity = entity;
        this.path = path;
        this.questionnaire = questionnaire;
        this.listener = listener;
    }

    @Override
    protected Void doInBackground(String... params) {
        try {
            RestClient.postJson(questionnaire, path, entity);
            success = true;
        } catch (RestException e) {
            OpenTeleApplication.instance().logException(e);
            Log.e(TAG, "Could not post", e);
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void result) {
        if (success) {
            listener.posted();
        } else {
            listener.postError();
        }
    }
}
