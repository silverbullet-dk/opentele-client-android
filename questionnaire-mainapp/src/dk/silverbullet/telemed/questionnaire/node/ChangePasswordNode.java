package dk.silverbullet.telemed.questionnaire.node;

import java.util.Map;

import lombok.Data;
import lombok.EqualsAndHashCode;
import android.graphics.Color;
import dk.silverbullet.telemed.questionnaire.Questionnaire;
import dk.silverbullet.telemed.questionnaire.element.ButtonElement;
import dk.silverbullet.telemed.questionnaire.element.EditTextElement;
import dk.silverbullet.telemed.questionnaire.element.TextViewElement;
import dk.silverbullet.telemed.questionnaire.expression.Constant;
import dk.silverbullet.telemed.questionnaire.expression.Variable;
import dk.silverbullet.telemed.questionnaire.expression.VariableLinkFailedException;
import dk.silverbullet.telemed.utils.Util;

@Data
@EqualsAndHashCode(callSuper = false)
public class ChangePasswordNode extends IONode {

    private Node next;

    private Variable<String> currentPassword;
    private Variable<String> password;
    private Variable<String> passwordRepeat;
    private Variable<String> currentPasswordErrorText;
    private Variable<String> passwordErrorText;
    private Variable<String> passwordRepeatErrorText;

    public ChangePasswordNode(Questionnaire questionnaire, String nodeName) {
        super(questionnaire, nodeName);
    }

    @Override
    public void enter() {
        Variable<?> chpass = questionnaire.getValuePool().get(Util.VARIABLE_CHANGE_PASSWORD);
        if (null != chpass.getExpressionValue().getValue() && (Boolean) chpass.getExpressionValue().getValue())
            hideMenuButton();

        hideBackButton();
        setView();
        questionnaire.clearStack();
        super.enter();
    }

    public void setView() {
        clearElements();

        addElement(new TextViewElement(this, "Skift adgangskode"));

        addElement(new TextViewElement(this, "Aktuel adgangskode"));
        EditTextElement ete = new EditTextElement(this);
        ete.setOutputVariable(currentPassword);
        ete.setPassword(true);
        addElement(ete);

        if (null != currentPasswordErrorText && null != currentPasswordErrorText.getExpressionValue()
                && !"null".equals(currentPasswordErrorText.getExpressionValue().toString().trim())) {
            TextViewElement tve3 = new TextViewElement(this);
            tve3.setColor(Color.RED);
            tve3.setText(currentPasswordErrorText.getExpressionValue().toString());
            addElement(tve3);
        }

        addElement(new TextViewElement(this, "Ny adgangskode"));
        EditTextElement ete2 = new EditTextElement(this);
        ete2.setOutputVariable(password);
        ete2.setPassword(true);
        addElement(ete2);

        if (null != passwordErrorText && null != passwordErrorText.getExpressionValue()
                && !"null".equals(passwordErrorText.getExpressionValue().toString().trim())) {
            TextViewElement tve3 = new TextViewElement(this);
            tve3.setColor(Color.RED);
            tve3.setText(passwordErrorText.getExpressionValue().toString());
            addElement(tve3);
        }

        addElement(new TextViewElement(this, "Gentag ny adgangskode"));
        EditTextElement ete3 = new EditTextElement(this);
        ete3.setOutputVariable(passwordRepeat);
        ete3.setPassword(true);
        addElement(ete3);

        if (null != passwordRepeatErrorText && null != passwordRepeatErrorText.getExpressionValue()
                && !"null".equals(passwordRepeatErrorText.getExpressionValue().toString().trim())) {
            TextViewElement tve3 = new TextViewElement(this);
            tve3.setColor(Color.RED);
            tve3.setText(passwordRepeatErrorText.getExpressionValue().toString());
            addElement(tve3);
        }

        ButtonElement be = new ButtonElement(this);
        be.setText("Opdater");
        be.setNextNode(getNext());
        addElement(be);
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

    @Override
    public void leave() {
        currentPasswordErrorText.setValue(new Constant<String>(""));
        passwordErrorText.setValue(new Constant<String>(""));
        passwordRepeatErrorText.setValue(new Constant<String>(""));

        super.leave();
    }
}
