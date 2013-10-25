package dk.silverbullet.telemed.questionnaire;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.FrameLayout;
import dk.silverbullet.telemed.questionnaire.expression.Variable;
import dk.silverbullet.telemed.rest.RetrieveServerInformationTask;
import dk.silverbullet.telemed.utils.Util;
import dk.silverbullet.telemed.utils.VersionMatcher;

public class QuestionnaireFragment extends Fragment {

    private static final String TAG = "QuestionnaireFragment";
    private MainQuestionnaire questionnaire;
    private SharedPreferences preferences;
    private static final String SERVER_VERSION_UNKNOWN = "Ukendt";
    private FrameLayout mainContent;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        questionnaire = new MainQuestionnaire(this);
        setQuestionnaireOnActivity(questionnaire);

        new RetrieveServerInformationTask(this, questionnaire).execute();

        mainContent = new FrameLayout(getActivity());
        mainContent.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));

        return mainContent;
    }

    public void fetchServerVersionFinished(String serverVersionJson) {

        String clientVersion = getString(R.string.client_version);
        String serverVersion = parseServerMinumumRequiredVersion(serverVersionJson);
        addServerEnvironmentVariable(serverVersionJson);

        Log.d(TAG, "Client version: " + clientVersion);
        Log.d(TAG, "Server version: " + serverVersion);
        Boolean clientSupported = serverVersion.equals(SERVER_VERSION_UNKNOWN) ? null : VersionMatcher
                .isClientVersionSupported(clientVersion, serverVersion);

        Variable<Boolean> isClientSupported = new Variable<Boolean>(Util.VARIABLE_CLIENT_SUPPORTED, Boolean.class);

        if (shouldSkipVersionCheck()) {
            isClientSupported.setValue(true);
        } else if (couldNotConnectToServerWhileServerUrlCannotBeChanged(clientSupported)) {
            showConnectionErrorAndExit();
        } else {
            isClientSupported.setValue(clientSupported);
        }

        isClientSupported.setValue(true);
        questionnaire.addVariable(isClientSupported);

        Variable<Boolean> showUploadDebugNode = new Variable<Boolean>(Util.VARIABLE_SHOW_UPLOAD_DEBUG, Boolean.class);
        showUploadDebugNode.setValue(preferences.getBoolean(Util.PREF_SHOW_UPLOAD_DEBUG, false));
        questionnaire.addVariable(showUploadDebugNode);

        Variable<String> serverIP = new Variable<String>(Util.VARIABLE_SERVER_IP, String.class);
        serverIP.setValue(preferences.getString(Util.PREF_SERVER_IP, Util.getServerUrl(questionnaire)));
        questionnaire.addVariable(serverIP);

        questionnaire.start();

    }

    @Override
    public void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
    }

    private boolean shouldSkipVersionCheck() {
        return true;// getString(R.string.skip_version_check).equals("true");
    }

    private boolean couldNotConnectToServerWhileServerUrlCannotBeChanged(Boolean clientSupported) {
        return clientSupported == null && Util.isServerUrlLocked(questionnaire);
    }

    private void showConnectionErrorAndExit() {
        new AlertDialog.Builder(getActivity())
                .setTitle(R.string.client_server_version_connection_problem_title)
                .setMessage(R.string.client_server_version_connection_problem_body)
                .setPositiveButton(R.string.client_server_version_connection_problem_button,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                System.exit(0);
                            }
                        }).setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        System.exit(0);
                    }
                }).create().show();
    }

    private void addServerEnvironmentVariable(String serverVersionJson) {
        String serverEnvironment = parseServerEnvironment(serverVersionJson);
        Variable<String> serverEnvironmentVariable = new Variable<String>(Util.SERVER_ENVIRONMENT, String.class);
        serverEnvironmentVariable.setValue(serverEnvironment);
        questionnaire.addVariable(serverEnvironmentVariable);
    }

    private String parseServerMinumumRequiredVersion(String serverVersionJson) {
        if (serverVersionJson == null) {
            return SERVER_VERSION_UNKNOWN;
        }
        try {
            JSONObject parsedServerVersion = new JSONObject(serverVersionJson);
            return parsedServerVersion.getString("minimumRequiredClientVersion");
        } catch (JSONException e) {
            Log.e(TAG, "Could not parse version response from server:" + serverVersionJson);
        }
        return SERVER_VERSION_UNKNOWN;
    }

    private String parseServerEnvironment(String serverVersionJson) {
        if (serverVersionJson == null) {
            return SERVER_VERSION_UNKNOWN;
        }
        try {
            JSONObject parsedServerVersion = new JSONObject(serverVersionJson);
            return parsedServerVersion.getString("serverEnvironment");
        } catch (JSONException e) {
            Log.e(TAG, "Could not parse version response from server:" + serverVersionJson);
        }
        return SERVER_VERSION_UNKNOWN;
    }

    public ViewGroup getRootLayout() {
        return mainContent;
    }

    private void setQuestionnaireOnActivity(MainQuestionnaire questionnaire) {
        ((QuestionnaireFragmentContainer) getActivity()).questionnaireCreated(questionnaire);
    }

}
