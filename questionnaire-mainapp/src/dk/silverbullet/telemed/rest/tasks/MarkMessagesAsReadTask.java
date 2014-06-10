package dk.silverbullet.telemed.rest.tasks;

import android.os.AsyncTask;
import android.util.Log;
import dk.silverbullet.telemed.questionnaire.Questionnaire;
import dk.silverbullet.telemed.rest.client.RestClient;
import dk.silverbullet.telemed.rest.client.RestException;
import dk.silverbullet.telemed.utils.Util;

public class MarkMessagesAsReadTask extends AsyncTask<Long, Void, Void> {
    private static final String TAG = Util.getTag(MarkMessagesAsReadTask.class);
    private static final String MARK_MESSAGES_AS_READ_PATH = "rest/message/markAsRead/";
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
            RestClient.postJson(questionnaire, MARK_MESSAGES_AS_READ_PATH, ids);
        } catch (RestException e) {
            Log.e(TAG, "Could not mark messages as read", e);
        }
        return null;
    }
}
