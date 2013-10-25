package dk.silverbullet.telemed.questionnaire.node;

import java.util.Map;

import lombok.Data;
import lombok.EqualsAndHashCode;

import com.google.gson.annotations.Expose;

import dk.silverbullet.telemed.questionnaire.Questionnaire;
import dk.silverbullet.telemed.questionnaire.element.EditTextElement;
import dk.silverbullet.telemed.questionnaire.element.TextViewElement;
import dk.silverbullet.telemed.questionnaire.element.TwoButtonElement;
import dk.silverbullet.telemed.questionnaire.expression.Variable;
import dk.silverbullet.telemed.questionnaire.expression.VariableLinkFailedException;
import dk.silverbullet.telemed.utils.Util;

@Data
@EqualsAndHashCode(callSuper = false)
public class SaturationTestDeviceNode extends DeviceNode {

    @SuppressWarnings("unused")
    private static final String TAG = Util.getTag(SaturationTestDeviceNode.class);

    @Expose
    private Variable<Integer> saturation;
    @Expose
    private Variable<Integer> pulse;

    public SaturationTestDeviceNode(Questionnaire questionnaire, String nodeName) {
        super(questionnaire, nodeName);
    }

    @Override
    public void enter() {
        clearElements();
        addElement(new TextViewElement(this, "Saturation"));

        addElement(new TextViewElement(this, "Angiv saturation:"));
        EditTextElement saturationElement = new EditTextElement(this);
        saturationElement.setOutputVariable(saturation);
        saturationElement.setDecimals(0);
        addElement(saturationElement);

        addElement(new TextViewElement(this, "Angiv puls:"));

        EditTextElement pulseElement = new EditTextElement(this);
        pulseElement.setOutputVariable(pulse);
        pulseElement.setDecimals(0);
        addElement(pulseElement);

        TwoButtonElement be = new TwoButtonElement(this);
        be.setLeftNextNode(getNextFailNode());
        be.setLeftText("Undlad");
        be.setRightNextNode(getNextNode());
        be.setRightText("OK");
        addElement(be);

        super.enter();
    }

    @Override
    public void linkVariables(Map<String, Variable<?>> variablePool) throws VariableLinkFailedException {
        super.linkVariables(variablePool);
        saturation = Util.linkVariable(variablePool, saturation);
        pulse = Util.linkVariable(variablePool, pulse);
    }

    @Override
    public void deviceLeave() {
    }
}
