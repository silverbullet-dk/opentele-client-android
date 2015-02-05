package dk.silverbullet.telemed.questionnaire.node;

import com.google.gson.annotations.Expose;
import dk.silverbullet.telemed.questionnaire.Questionnaire;
import dk.silverbullet.telemed.questionnaire.R;
import dk.silverbullet.telemed.questionnaire.element.EditTextElement;
import dk.silverbullet.telemed.questionnaire.element.HelpTextElement;
import dk.silverbullet.telemed.questionnaire.element.TextViewElement;
import dk.silverbullet.telemed.questionnaire.element.TwoButtonElement;
import dk.silverbullet.telemed.questionnaire.expression.Variable;
import dk.silverbullet.telemed.questionnaire.expression.VariableLinkFailedException;
import dk.silverbullet.telemed.utils.Util;

import java.util.Map;

public class HaemoglobinDeviceNode extends DeviceNode {

    @SuppressWarnings("unused")
    private static final String TAG = Util.getTag(HaemoglobinDeviceNode.class);

    @Expose
    private Variable<Float> haemoglobinValue;

    @Expose
    String text;

    public HaemoglobinDeviceNode(Questionnaire questionnaire, String nodeName) {
        super(questionnaire, nodeName);
    }

    @Override
    public void enter() {
        clearElements();
        addElement(new TextViewElement(this, text));
        if (hasHelp()) {
            addElement(new HelpTextElement(this, getHelpText(), getHelpImage()));
        }

        TextViewElement tve = new TextViewElement(this);
        tve.setText(Util.getString(R.string.haemoglobin_enter_value, questionnaire));
        addElement(tve);

        EditTextElement ete = new EditTextElement(this);
        ete.setOutputVariable(haemoglobinValue);
        ete.setDecimals(1);
        addElement(ete);

        TwoButtonElement be = new TwoButtonElement(this);
        be.setLeftNextNode(getNextFailNode());
        be.setLeftSkipValidation(true);
        be.setLeftText(Util.getString(R.string.default_omit, questionnaire));
        be.setRightNextNode(getNextNode());
        be.setRightText(Util.getString(R.string.default_ok, questionnaire));
        addElement(be);

        super.enter();
    }

    @Override
    public void linkVariables(Map<String, Variable<?>> variablePool) throws VariableLinkFailedException {
        super.linkVariables(variablePool);
        haemoglobinValue = Util.linkVariable(variablePool, haemoglobinValue);
    }

    @Override
    public void deviceLeave() {
    }
}
