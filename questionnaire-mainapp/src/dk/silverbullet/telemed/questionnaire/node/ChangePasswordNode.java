package dk.silverbullet.telemed.questionnaire.node;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import dk.silverbullet.telemed.questionnaire.Questionnaire;
import dk.silverbullet.telemed.questionnaire.R;
import dk.silverbullet.telemed.rest.ChangePasswordTask;
import dk.silverbullet.telemed.rest.listener.ChangePasswordListener;
import dk.silverbullet.telemed.utils.Util;

import java.util.List;

public class ChangePasswordNode extends IONode implements ChangePasswordListener {
    private Node next;

    private View inProgressText;
    private View form;
    private EditText passwordInput;
    private EditText passwordRepeatInput;
    private TextView errorTextView;

    public ChangePasswordNode(Questionnaire questionnaire, String nodeName) {
        super(questionnaire, nodeName);
    }

    public void setNext(Node next) {
        this.next = next;
    }

    @Override
    public void enter() {
        setView();
        super.enter();
    }

    private void setView() {
        ViewGroup rootLayout = questionnaire.getRootLayout();
        LayoutInflater inflater = (LayoutInflater) questionnaire.getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View changePasswordView = inflater.inflate(R.layout.change_password, rootLayout, false);
        rootLayout.addView(changePasswordView);

        linkTopPanel(changePasswordView);

        inProgressText = changePasswordView.findViewById(R.id.change_password_in_progress_text);
        form = changePasswordView.findViewById(R.id.change_password_form);
        passwordInput = (EditText) changePasswordView.findViewById(R.id.password_input);
        passwordRepeatInput = (EditText) changePasswordView.findViewById(R.id.password_repeat_input);
        errorTextView = (TextView) changePasswordView.findViewById(R.id.change_password_error_text);

        changePasswordOnButtonClick(changePasswordView);
        clearErrorTextWhenFieldsAreChanged();
        showKeyboard(passwordInput);
    }

    @Override
    public void leave() {
        super.leave();
        hideKeyboard(passwordInput);
    }

    @Override
    public void changePasswordFailed(List<String> errorTexts) {
        errorTextView.setText(Util.join(errorTexts, "\n"));
        showForm();
    }

    @Override
    public void changePasswordSucceeded() {
        getQuestionnaire().setCurrentNode(next);
    }

    @Override
    public void communicationError() {
        errorTextView.setText("Fejl ved kommunikation med serveren");
        showForm();
    }

    private void showProgressText() {
        form.setVisibility(View.GONE);
        inProgressText.setVisibility(View.VISIBLE);
    }

    private void showForm() {
        form.setVisibility(View.VISIBLE);
        inProgressText.setVisibility(View.GONE);
    }

    private void changePasswordOnButtonClick(View changePasswordView) {
        Button changePasswordButton = (Button) changePasswordView.findViewById(R.id.change_password_button);
        changePasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String password = passwordInput.getText().toString();
                String passwordRepeat = passwordRepeatInput.getText().toString();

                showProgressText();
                new ChangePasswordTask(questionnaire, ChangePasswordNode.this, password, passwordRepeat).execute();
            }
        });
    }

    private void clearErrorTextWhenFieldsAreChanged() {
        TextWatcher passwordAlteredListener = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                // Nothing to do
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                errorTextView.setText("");
            }

            @Override
            public void afterTextChanged(Editable editable) {
                // Nothing to do
            }
        };
        passwordInput.addTextChangedListener(passwordAlteredListener);
        passwordRepeatInput.addTextChangedListener(passwordAlteredListener);
    }
}
