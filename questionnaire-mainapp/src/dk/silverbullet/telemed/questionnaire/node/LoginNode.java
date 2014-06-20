package dk.silverbullet.telemed.questionnaire.node;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import dk.silverbullet.telemed.MainActivity;
import dk.silverbullet.telemed.questionnaire.Questionnaire;
import dk.silverbullet.telemed.questionnaire.R;
import dk.silverbullet.telemed.rest.tasks.FillOutQuestionnaireWithUserDetailsTask;
import dk.silverbullet.telemed.rest.listener.LoginListener;
import dk.silverbullet.telemed.utils.Util;

public class LoginNode extends IONode implements LoginListener {
    private static final String SHARED_PREFERENCES_LAST_USERNAME = "PREF_LAST_USERNAME";

    private Node next;
    private Node changePasswordNode;
    private View form;
    private EditText passwordInput;
    private EditText usernameInput;
    private TextView errorTextView;
    private TextView usernameTextView;
    private TextView passwordTextView;
    private Button loginButton;
    private TextView loginInProgressTextView;

    public LoginNode(Questionnaire questionnaire, String nodeName) {
        super(questionnaire, nodeName);
    }

    @Override
    public void enter() {
        setView();
        questionnaire.clearStack();
        super.enter();
    }

    private void setView() {
        View loginView = inflateView();

        loginInProgressTextView = (TextView) loginView.findViewById(R.id.login_login_in_progress_text);
        form = loginView.findViewById(R.id.login_form);
        usernameInput = (EditText) loginView.findViewById(R.id.login_username_input);
        passwordInput = (EditText) loginView.findViewById(R.id.login_password_input);
        errorTextView = (TextView) loginView.findViewById(R.id.login_error_text);
        usernameTextView = (TextView) loginView.findViewById(R.id.login_username_view);
        passwordTextView = (TextView) loginView.findViewById(R.id.login_password_view);
        loginButton = (Button) loginView.findViewById(R.id.login_button);

        showKeyboard(usernameInput);

        TextWatcher textChangedListener = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                errorTextView.setText("");
                errorTextView.setVisibility(View.GONE);
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        };

        usernameInput.addTextChangedListener(textChangedListener);
        passwordInput.addTextChangedListener(textChangedListener);
        if (Util.shouldHidePasswordText(questionnaire)) {
            passwordInput.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        }

        setLoginButtonListener();
        showUpdatedNeededInfo();
        setDebugInfo(loginView);

        prefillUsername();
    }

    private void prefillUsername() {
        String lastUsedUsername = getSavedUsername();
        if(lastUsedUsername != null) {
            usernameInput.setText(lastUsedUsername);
            passwordInput.requestFocus();
        }
    }

    private void setDebugInfo(View loginView) {
        TextView debugInfo = (TextView) loginView.findViewById(R.id.login_debug_info);
        MainActivity mainActivity = (MainActivity) questionnaire.getActivity();
        String clientVersion = mainActivity.getResources().getString(R.string.client_version);
        String serverUrl = mainActivity.getResources().getString(R.string.server_url);
        boolean videoEnabled = mainActivity.clientIsVideoEnabled();

        String debugText = String.format("Ver.: %s - url: %s - Video enabled: %s", clientVersion, serverUrl, videoEnabled);
        debugInfo.setText(debugText);

    }

    private void setLoginButtonListener() {
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                doLogin();
            }
        });
    }

    private void hideLoginForm() {
        form.setVisibility(View.GONE);
        loginInProgressTextView.setVisibility(View.VISIBLE);

        // Removing the keyboard
        InputMethodManager imm = (InputMethodManager) getQuestionnaire().getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
    }

    private void showLoginForm() {
        form.setVisibility(View.VISIBLE);
        loginInProgressTextView.setVisibility(View.GONE);

        boolean isTryingWithPreviousUsername = getSavedUsername() != null && getSavedUsername().equalsIgnoreCase(usernameInput.getText().toString());
        if (isTryingWithPreviousUsername) {
            // If the user is logging in with the same user name as previously, but has entered the wrong password,
            // then probably we want focus on the password input field
            passwordInput.requestFocus();
        }
    }

    private View inflateView() {
        ViewGroup rootLayout = questionnaire.getRootLayout();
        LayoutInflater inflater = (LayoutInflater) questionnaire.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View loginView = inflater.inflate(R.layout.login, rootLayout, false);
        rootLayout.addView(loginView);
        return loginView;
    }

    private void showUpdatedNeededInfo() {
        Boolean clientSupported = (Boolean) questionnaire.getValuePool().get(Util.VARIABLE_CLIENT_SUPPORTED).getExpressionValue().getValue();

        if(clientSupported != null && !clientSupported) {
            showError(Util.getString(R.string.login_client_must_be_upgraded, questionnaire));
            loginButton.setVisibility(View.INVISIBLE);
            passwordInput.setVisibility(View.INVISIBLE);
            usernameInput.setVisibility(View.INVISIBLE);
            usernameTextView.setVisibility(View.INVISIBLE);
            passwordTextView.setVisibility(View.INVISIBLE);
        }
    }

    private void doLogin() {
        hideLoginForm();
        new FillOutQuestionnaireWithUserDetailsTask(questionnaire, this).execute(usernameInput.getText().toString(), passwordInput.getText().toString());
    }

    private void showError(String errorText) {
        errorTextView.setVisibility(View.VISIBLE);
        errorTextView.setText(errorText);
    }

    @Override
    public void loggedIn() {
        saveUsername();
        getQuestionnaire().setCurrentNode(next);
    }

    @Override
    public void loginFailed() {
        clearPasswordText();
        showLoginForm();
        showError(Util.getString(R.string.login_wrong_username_password, questionnaire));
    }

    @Override
    public void changePassword() {
        saveUsername();
        questionnaire.setCurrentNode(changePasswordNode);
    }

    @Override
    public void accountLocked() {
        clearPasswordText();
        showLoginForm();
        showError(Util.getString(R.string.login_account_locked, questionnaire));
    }

    @Override
    public void sendError() {
        showLoginForm();
        showError(Util.getString(R.string.default_server_communication_error, questionnaire));
    }

    private void clearPasswordText() {
        passwordInput.setText("");
    }

    @Override
    public void leave() {
        super.leave();
        hideKeyboard(passwordInput);
    }

    private String getSavedUsername() {
        if (Util.shouldClearUserNameOnLogin(questionnaire)) {
            return null;
        }

        Context context = questionnaire.getContext();
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getString(SHARED_PREFERENCES_LAST_USERNAME, null);
    }

    private void saveUsername() {
        Context context = questionnaire.getContext();
        String username = Util.getStringVariableValue(questionnaire, Util.VARIABLE_USERNAME);

        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
        if (Util.shouldClearUserNameOnLogin(questionnaire)) {
            editor.remove(SHARED_PREFERENCES_LAST_USERNAME);
        } else {
            editor.putString(SHARED_PREFERENCES_LAST_USERNAME, username);
        }
        editor.commit();
    }

    public void setNext(Node next) {
        this.next = next;
    }

    public void setChangePasswordNode(Node changePasswordNode) {
        this.changePasswordNode = changePasswordNode;
    }
}
