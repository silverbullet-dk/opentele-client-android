package dk.silverbullet.telemed.questionnaire.node;

import java.util.Map;

import lombok.Data;
import lombok.EqualsAndHashCode;

import com.google.gson.annotations.Expose;

import dk.silverbullet.telemed.questionnaire.Questionnaire;
import dk.silverbullet.telemed.questionnaire.element.RadioButtonElement;
import dk.silverbullet.telemed.questionnaire.element.TextViewElement;
import dk.silverbullet.telemed.questionnaire.element.TwoButtonElement;
import dk.silverbullet.telemed.questionnaire.element.ValueChoice;
import dk.silverbullet.telemed.questionnaire.expression.Variable;
import dk.silverbullet.telemed.questionnaire.expression.VariableLinkFailedException;
import dk.silverbullet.telemed.utils.Util;

@Data
@EqualsAndHashCode(callSuper = false)
public class GlucoseUrineDeviceNode extends DeviceNode {

    @SuppressWarnings("unused")
    private static final String TAG = Util.getTag(GlucoseUrineDeviceNode.class);

    @Expose
    private Variable<Integer> glucoseUrine;
    @Expose
    String text;

    public GlucoseUrineDeviceNode(Questionnaire questionnaire, String nodeName) {
        super(questionnaire, nodeName);
    }

    @Override
    public void enter() {
        setView();
        super.enter();
    }

    public void setView() {
        clearElements();
        addElement(new TextViewElement(this, text));
        addElement(new TextViewElement(this, "Angiv urin glukosetal"));

        RadioButtonElement<Integer> select = new RadioButtonElement<Integer>(this);
        {
            @SuppressWarnings("unchecked")
            // Arrays and generics don't mix.
            ValueChoice<Integer>[] choices = new ValueChoice[5];
            int c = 0;
            choices[c++] = new ValueChoice<Integer>(0, "negativ");
            choices[c++] = new ValueChoice<Integer>(1, "+1");
            choices[c++] = new ValueChoice<Integer>(2, "+2");
            choices[c++] = new ValueChoice<Integer>(3, "+3");
            choices[c++] = new ValueChoice<Integer>(4, "+4");
            assert c == choices.length;
            select.setChoices(choices);
        }

        select.setOutputVariable(glucoseUrine);

        addElement(select);

        TwoButtonElement be = new TwoButtonElement(this, "Undlad", "Næste");
        be.setLeftNextNode(getNextFailNode());
        be.setLeftSkipValidation(true);
        be.setRightNextNode(getNextNode());
        addElement(be);
    }

    @Override
    public void linkVariables(Map<String, Variable<?>> variablePool) throws VariableLinkFailedException {
        super.linkVariables(variablePool);
        glucoseUrine = Util.linkVariable(variablePool, glucoseUrine);
    }

    @Override
    public void deviceLeave() {
    }
}
