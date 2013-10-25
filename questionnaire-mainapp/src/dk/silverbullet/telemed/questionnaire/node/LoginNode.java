package dk.silverbullet.telemed.questionnaire.node;

import java.util.Map;

import lombok.Data;
import lombok.EqualsAndHashCode;
import android.graphics.Color;
import dk.silverbullet.telemed.questionnaire.Questionnaire;
import dk.silverbullet.telemed.questionnaire.element.ButtonElement;
import dk.silverbullet.telemed.questionnaire.element.EditTextChangedListener;
import dk.silverbullet.telemed.questionnaire.element.EditTextElement;
import dk.silverbullet.telemed.questionnaire.element.TextViewElement;
import dk.silverbullet.telemed.questionnaire.expression.Variable;
import dk.silverbullet.telemed.questionnaire.expression.VariableLinkFailedException;
import dk.silverbullet.telemed.utils.Util;

@Data
@EqualsAndHashCode(callSuper = false)
public class LoginNode extends IONode {
    private Node next;

    private Variable<String> userName;
    private Variable<String> password;
    private Variable<String> errorText;
    private TextViewElement errorTextViewElement;

    public LoginNode(Questionnaire questionnaire, String nodeName) {
        super(questionnaire, nodeName);
    }

    @Override
    public void enter() {
        setView();
        questionnaire.clearStack();
        super.enter();
    }

    public void setView() {
        clearElements();

        EditTextChangedListener listener = new EditTextChangedListener() {
            @Override
            public void textChanged() {
                if (errorTextViewElement != null) {
                    errorTextViewElement.setText("");
                }
            }
        };

        addElement(new TextViewElement(this, getQuestionnaire().getActivity().getString(
                dk.silverbullet.telemed.questionnaire.R.string.app_name)));

        addElement(new TextViewElement(this, "Brugernavn"));

        EditTextElement userNameEditTextElement = new EditTextElement(this);
        userNameEditTextElement.setOutputVariable(userName);
        userNameEditTextElement.addChangedListener(listener);
        addElement(userNameEditTextElement);

        addElement(new TextViewElement(this, "Adgangskode"));

        EditTextElement passwordEditTextElement = new EditTextElement(this);
        passwordEditTextElement.setOutputVariable(password);
        passwordEditTextElement.setPassword(true);
        passwordEditTextElement.addChangedListener(listener);
        addElement(passwordEditTextElement);

        if (null != errorText && null != errorText.getExpressionValue()
                && !"null".equals(errorText.getExpressionValue().toString().trim())) {
            errorTextViewElement = new TextViewElement(this);
            errorTextViewElement.setColor(Color.RED);
            errorTextViewElement.setText(errorText.getExpressionValue().toString());
            addElement(errorTextViewElement);
        }

        @SuppressWarnings("unchecked")
        Variable<String> messageText = (Variable<String>) getQuestionnaire().getValuePool().get(
                Util.VARIABLE_MESSAGE_TEXT);
        if (null != messageText && null != messageText.getExpressionValue()
                && !"null".equals(messageText.getExpressionValue().toString().trim())) {
            TextViewElement tve3 = new TextViewElement(this);
            tve3.setColor(Color.GREEN);
            tve3.setText(messageText.getExpressionValue().toString());
            addElement(tve3);
        }

        addVersionNumber();

        ButtonElement be = new ButtonElement(this);
        be.setText("Login");
        be.setNextNode(getNext());
        addElement(be);
    }

    @Override
    public void linkVariables(Map<String, Variable<?>> map) throws VariableLinkFailedException {
        errorText = Util.linkVariable(map, errorText);
        password = Util.linkVariable(map, password);
        userName = Util.linkVariable(map, userName);

        super.linkVariables(map);
    }
}
