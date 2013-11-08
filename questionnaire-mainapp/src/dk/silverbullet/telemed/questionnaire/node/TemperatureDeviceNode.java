package dk.silverbullet.telemed.questionnaire.node;

import com.google.gson.annotations.Expose;
import dk.silverbullet.telemed.questionnaire.Questionnaire;
import dk.silverbullet.telemed.questionnaire.element.EditTextElement;
import dk.silverbullet.telemed.questionnaire.element.TextViewElement;
import dk.silverbullet.telemed.questionnaire.element.TwoButtonElement;
import dk.silverbullet.telemed.questionnaire.expression.Variable;
import dk.silverbullet.telemed.questionnaire.expression.VariableLinkFailedException;
import dk.silverbullet.telemed.utils.Util;

import java.util.Map;

public class TemperatureDeviceNode extends DeviceNode {

    @SuppressWarnings("unused")
    private static final String TAG = Util.getTag(TemperatureDeviceNode.class);

    @Expose
    private Variable<Float> temperature;

    @Expose
    String text;

    public TemperatureDeviceNode(Questionnaire questionnaire, String nodeName) {
        super(questionnaire, nodeName);
    }

    @Override
    public void enter() {
        clearElements();
        addElement(new TextViewElement(this, text));
        TextViewElement tve = new TextViewElement(this);
        tve.setText("Indtast temperatur");
        addElement(tve);

        EditTextElement ete = new EditTextElement(this);
        ete.setOutputVariable(temperature);
        ete.setDecimals(1);
        addElement(ete);

        TwoButtonElement be = new TwoButtonElement(this);
        be.setLeftNextNode(getNextFailNode());
        be.setLeftSkipValidation(true);
        be.setLeftText("Undlad");
        be.setRightNextNode(getNextNode());
        be.setRightText("OK");
        addElement(be);

        super.enter();
    }

    @Override
    public void linkVariables(Map<String, Variable<?>> variablePool) throws VariableLinkFailedException {
        super.linkVariables(variablePool);
        temperature = Util.linkVariable(variablePool, temperature);
    }

    @Override
    public void deviceLeave() {
    }

    public void setTemperature(Variable<Float> temperature) {
        this.temperature = temperature;
    }
}
