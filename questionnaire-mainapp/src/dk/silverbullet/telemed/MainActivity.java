package dk.silverbullet.telemed;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import dk.silverbullet.telemed.questionnaire.Questionnaire;
import dk.silverbullet.telemed.questionnaire.QuestionnaireFragmentContainer;
import dk.silverbullet.telemed.questionnaire.R;
import dk.silverbullet.telemed.utils.Util;
import dk.silverbullet.telemed.video.JoinConferencePoller;
import dk.silverbullet.telemed.video.VideoActivity;

public class MainActivity extends Activity implements QuestionnaireFragmentContainer {
    public static boolean hasBeenCreated;
    private static final String TAG = Util.getTag(MainActivity.class);
    private static final int VIDEO_ACTIVITY_REQUEST_CODE = 501;
    private Questionnaire questionnaire;

    private JoinConferencePoller conferencePoller;
    private boolean isLoggedIn;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_view);
        hasBeenCreated = true;
    }

    @Override
    protected void onDestroy() {
        if (questionnaire != null) { // Might be null if Client/Server version check failed
            Util.saveVariables(questionnaire);
        }
        stopConferencePoller();

        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        Log.d(TAG, "onBackPressed...");
        questionnaire.back();
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopConferencePoller();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isLoggedIn) {
            restartConferencePoller();
        }
    }

    @Override
    public void questionnaireCreated(Questionnaire questionnaire) {
        this.questionnaire = questionnaire;
    }

    public String getServerURL() {
        return Util.getServerUrl(questionnaire);
    }

    @Override
    public void userLoggedIn() {
        isLoggedIn = true;
        restartConferencePoller();
    }

    @Override
    public void userLoggedOut() {
        isLoggedIn = false;
        stopConferencePoller();
    }

    private void stopConferencePoller() {
        if (conferencePoller != null) {
            conferencePoller.stop();
            conferencePoller = null;
        }
    }

    private void restartConferencePoller() {
        stopConferencePoller();
        startConferencePoller();
    }

    private void startConferencePoller() {
        if (clientIsVideoEnabled()) {
            conferencePoller = new JoinConferencePoller(this, questionnaire);
            conferencePoller.start();
        }
    }

    public void startVideoConference(final String roomKey, String serviceUrl) {
        stopConferencePoller();

        Intent startVideoConsultation = new Intent(getApplicationContext(), VideoActivity.class);

        String userName = Util.getStringVariableValue(questionnaire, Util.VARIABLE_USERNAME);
        String password = Util.getStringVariableValue(questionnaire, Util.VARIABLE_PASSWORD);

        startVideoConsultation.putExtra("guestName", userName);
        startVideoConsultation.putExtra("roomKey", roomKey);
        startVideoConsultation.putExtra("userPassword", password);
        startVideoConsultation.putExtra("serviceUrl", serviceUrl);
        startVideoConsultation.putExtra("serverUrl", Util.getServerUrl(questionnaire));

        startActivityForResult(startVideoConsultation, VIDEO_ACTIVITY_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == VIDEO_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK) {
            startConferencePoller(); // Start listening for incomming conferences again
        } else if (requestCode == VIDEO_ACTIVITY_REQUEST_CODE) {
            // Video activity did not finish normaly. Ensure patient is loged in before restarting handler. Otherwise
            // we'll get a whole bunch of "Unauthorized" errors
            if (userNameIsSet() && userPasswordIsSet()) {
                startConferencePoller();
            }
        }
    }

    private boolean userPasswordIsSet() {
        String password = Util.getStringVariableValue(questionnaire, Util.VARIABLE_PASSWORD);
        return password != null && !password.isEmpty();
    }

    private boolean userNameIsSet() {
        String userName = Util.getStringVariableValue(questionnaire, Util.VARIABLE_USERNAME);
        return userName != null && !userName.isEmpty();
    }

    @SuppressWarnings("rawtypes")
    public boolean clientIsVideoEnabled() {
        try {
            this.getClassLoader().loadClass("dk.silverbullet.telemed.video.VideoProvider");
            return true;
        } catch (ClassNotFoundException e) {
            Log.d(TAG, "Client not video enabled");
            return false;
        }
    }
}
