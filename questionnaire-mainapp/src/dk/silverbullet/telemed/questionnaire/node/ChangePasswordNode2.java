package dk.silverbullet.telemed.questionnaire.node;

import java.util.Map;

import lombok.Data;
import lombok.EqualsAndHashCode;
import android.graphics.Color;
import android.util.Log;

import com.google.gson.Gson;

import dk.silverbullet.telemed.questionnaire.Questionnaire;
import dk.silverbullet.telemed.questionnaire.element.TextViewElement;
import dk.silverbullet.telemed.questionnaire.expression.Constant;
import dk.silverbullet.telemed.questionnaire.expression.Variable;
import dk.silverbullet.telemed.questionnaire.expression.VariableLinkFailedException;
import dk.silverbullet.telemed.rest.ChangePasswordTask;
import dk.silverbullet.telemed.rest.bean.ChangePasswordError;
import dk.silverbullet.telemed.rest.bean.ChangePasswordResponse;
import dk.silverbullet.telemed.rest.listener.ChangePasswordListener;
import dk.silverbullet.telemed.utils.Util;

@Data
@EqualsAndHashCode(callSuper = false)
public class ChangePasswordNode2 extends IONode implements ChangePasswordListener {

    private static final String TAG = Util.getTag(ChangePasswordNode2.class);

    private Node next;
    private Node nextFail;
    private Variable<String> currentPassword;
    private Variable<String> password;
    private Variable<String> passwordRepeat;
    private Variable<String> currentPasswordErrorText;
    private Variable<String> passwordErrorText;
    private Variable<String> passwordRepeatErrorText;

    public ChangePasswordNode2(Questionnaire questionnaire, String nodeName) {
        super(questionnaire, nodeName);
    }

    @Override
    public void enter() {
        clearElements();
        hideBackButton();
        hideMenuButton();
        setView();

        Boolean clientSupported = (Boolean) questionnaire.getValuePool().get(Util.VARIABLE_CLIENT_SUPPORTED)
                .getExpressionValue().getValue();
        boolean isTryingAdminLogin = Util.getStringVariableValue(questionnaire, Util.VARIABLE_USERNAME).equals(
                Util.ADMINUSER_NAME);
        super.enter();
        if ((clientSupported == null || !clientSupported) && !isTryingAdminLogin) {
            getQuestionnaire().setCurrentNode(nextFail);
        } else {
            new ChangePasswordTask(questionnaire, this).execute();
        }
    }

    public void setView() {
        TextViewElement tve = new TextViewElement(this);
        tve.setColor(Color.LTGRAY);
        tve.setText("Skift adgangskode. Vent venligst...");
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
    public String toString() {
        return "LoginNode";
    }

    @SuppressWarnings("unchecked")
    @Override
    public void response(String response) {
        Log.d(TAG, "Got response...:" + response);

        ChangePasswordResponse responseBean = new Gson().fromJson(response, ChangePasswordResponse.class);
        if (responseBean.isError() && null != responseBean.getErrors()) {
            for (ChangePasswordError error : responseBean.getErrors()) {
                if (error.getField().equals(ChangePasswordError.FIELD_CURRENTPASSWORD))
                    currentPasswordErrorText.setValue(new Constant<String>(error.getError()));
                if (error.getField().equals(ChangePasswordError.FIELD_PASSWORD))
                    passwordErrorText.setValue(new Constant<String>(error.getError()));
                if (error.getField().equals(ChangePasswordError.FIELD_PASSWORDREPEAT))
                    passwordRepeatErrorText.setValue(new Constant<String>(error.getError()));
            }

            getQuestionnaire().setCurrentNode(nextFail);
        } else {
            currentPassword.setValue("");
            password.setValue("");
            passwordRepeat.setValue("");
            currentPasswordErrorText.setValue("");
            passwordErrorText.setValue("");
            passwordRepeatErrorText.setValue("");

            ((Variable<String>) getQuestionnaire().getValuePool().get(Util.VARIABLE_MESSAGE_TEXT))
                    .setValue("Adgangskode er nu rettet. Log ind igen!");

            getQuestionnaire().logout();
        }
    }

    @Override
    public void sendError() {
        currentPasswordErrorText.setValue(new Constant<String>("Fejl ved kommunikation med serveren"));
        getQuestionnaire().setCurrentNode(nextFail);
    }

    @Override
    public String getCurrentPassword() {
        return currentPassword.getExpressionValue().getValue();
    }

    @Override
    public String getPassword() {
        return password.getExpressionValue().getValue();
    }

    @Override
    public String getPasswordRepeat() {
        return passwordRepeat.getExpressionValue().getValue();
    }

    @Override
    public void linkVariables(Map<String, Variable<?>> map) throws VariableLinkFailedException {
        currentPassword = Util.linkVariable(map, currentPassword);
        currentPasswordErrorText = Util.linkVariable(map, currentPasswordErrorText);
        password = Util.linkVariable(map, password);
        passwordErrorText = Util.linkVariable(map, passwordErrorText);
        passwordRepeat = Util.linkVariable(map, passwordRepeat);
        passwordRepeatErrorText = Util.linkVariable(map, passwordRepeatErrorText);

        super.linkVariables(map);
    }
}
