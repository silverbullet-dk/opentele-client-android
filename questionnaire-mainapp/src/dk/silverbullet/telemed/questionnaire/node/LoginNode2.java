package dk.silverbullet.telemed.questionnaire.node;

import java.util.Map;

import lombok.Data;
import lombok.EqualsAndHashCode;
import dk.silverbullet.telemed.questionnaire.Questionnaire;
import dk.silverbullet.telemed.questionnaire.element.TextViewElement;
import dk.silverbullet.telemed.questionnaire.expression.Constant;
import dk.silverbullet.telemed.questionnaire.expression.Variable;
import dk.silverbullet.telemed.questionnaire.expression.VariableLinkFailedException;
import dk.silverbullet.telemed.rest.FillOutQuestionnaireWithUserDetailsTask;
import dk.silverbullet.telemed.rest.listener.LoginListener;
import dk.silverbullet.telemed.utils.Util;

@Data
@EqualsAndHashCode(callSuper = false)
public class LoginNode2 extends IONode implements LoginListener {

    @SuppressWarnings("unused")
    private static final String TAG = Util.getTag(LoginNode2.class);

    private Node next;
    private Node nextFail;
    private Node changePassword;
    private Variable<String> errorText;
    private Variable<String> password; // Password used on login
    private Variable<String> currentPassword; // Password used when changing password

    public LoginNode2(Questionnaire questionnaire, String nodeName) {
        super(questionnaire, nodeName);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void enter() {
        clearElements();
        setView();

        ((Variable<String>) getQuestionnaire().getValuePool().get(Util.VARIABLE_MESSAGE_TEXT)).setValue("");

        Boolean clientSupported = (Boolean) questionnaire.getValuePool().get(Util.VARIABLE_CLIENT_SUPPORTED)
                .getExpressionValue().getValue();
        boolean isTryingAdminLogin = Util.getStringVariableValue(questionnaire, Util.VARIABLE_USERNAME).equals(
                Util.ADMINUSER_NAME);
        super.enter();
        if ((clientSupported == null || !clientSupported) && !isTryingAdminLogin) {
            getQuestionnaire().setCurrentNode(nextFail);
        } else {
            new FillOutQuestionnaireWithUserDetailsTask(questionnaire, this).execute();
        }
    }

    public void setView() {
        TextViewElement tve = new TextViewElement(this);
        tve.setText("Logger ind. Vent venligst...");
        addElement(tve);
    }

    @Override
    public void leave() {
        super.leave();
    }

    @Override
    public void linkNodes(Map<String, Node> map) throws UnknownNodeException {
        super.linkNodes(map);
    }

    @Override
    public void linkVariables(Map<String, Variable<?>> variablePool) throws VariableLinkFailedException {
        super.linkVariables(variablePool);
        errorText = Util.linkVariable(variablePool, errorText);
        password = Util.linkVariable(variablePool, password);
        currentPassword = Util.linkVariable(variablePool, currentPassword);
    }

    @Override
    public String toString() {
        return "LoginNode";
    }

    @Override
    public void login(String login) {
        Variable<?> chpass = questionnaire.getValuePool().get(Util.VARIABLE_CHANGE_PASSWORD);
        if (null != login && "true".equalsIgnoreCase(login)) {
            currentPassword.setValue(password.getExpressionValue());

            errorText.setValue(new Constant<String>(""));
            if (null != chpass.getExpressionValue().getValue() && (Boolean) chpass.getExpressionValue().getValue())
                getQuestionnaire().setCurrentNode(changePassword);
            else
                getQuestionnaire().setCurrentNode(next);
        } else if (null != login && "false".equalsIgnoreCase(login)) {
            errorText.setValue(new Constant<String>("Forkert brugernavn eller adgangskode."));
            getQuestionnaire().setCurrentNode(nextFail);
        } else {
            errorText.setValue(new Constant<String>("Fejl ved kommunikation med serveren."));
            getQuestionnaire().setCurrentNode(nextFail);
        }
    }

    @Override
    public void sendError() {
        errorText.setValue(new Constant<String>("Fejl ved kommunikation med serveren."));
        getQuestionnaire().setCurrentNode(nextFail);
    }

    @Override
    public void accountLocked() {
        errorText.setValue(new Constant<String>(
                "Din konto er blevet låst. Henvend dig til din kontaktperson."));
        getQuestionnaire().setCurrentNode(nextFail);
    }
}
