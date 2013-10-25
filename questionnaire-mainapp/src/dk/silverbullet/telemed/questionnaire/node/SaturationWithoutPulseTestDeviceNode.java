package dk.silverbullet.telemed.questionnaire.node;

import com.google.gson.annotations.Expose;
import dk.silverbullet.telemed.questionnaire.Questionnaire;
import dk.silverbullet.telemed.questionnaire.element.EditTextElement;
import dk.silverbullet.telemed.questionnaire.element.TextViewElement;
import dk.silverbullet.telemed.questionnaire.element.TwoButtonElement;
import dk.silverbullet.telemed.questionnaire.expression.Variable;
import dk.silverbullet.telemed.questionnaire.expression.VariableLinkFailedException;
import dk.silverbullet.telemed.utils.Util;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Map;

@Data
@EqualsAndHashCode(callSuper = false)
public class SaturationWithoutPulseTestDeviceNode extends DeviceNode {

    @SuppressWarnings("unused")
    private static final String TAG = Util.getTag(SaturationWithoutPulseTestDeviceNode.class);

    @Expose
    private Variable<Integer> saturation;

    public SaturationWithoutPulseTestDeviceNode(Questionnaire questionnaire, String nodeName) {
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
    }

    @Override
    public void deviceLeave() {
    }
}
