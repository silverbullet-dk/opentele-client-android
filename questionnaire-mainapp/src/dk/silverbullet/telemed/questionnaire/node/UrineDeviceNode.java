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
public class UrineDeviceNode extends DeviceNode {

    @SuppressWarnings("unused")
    private static final String TAG = Util.getTag(UrineDeviceNode.class);

    @Expose
    private Variable<Integer> urine;

    @Expose
    String text;

    public UrineDeviceNode(Questionnaire questionnaire, String nodeName) {
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
        addElement(new TextViewElement(this, "Angiv urin proteintal"));

        RadioButtonElement<Integer> select = new RadioButtonElement<Integer>(this);
        {
            @SuppressWarnings("unchecked")
            // Arrays and generics don't mix.
            ValueChoice<Integer>[] choices = new ValueChoice[6];
            int c = 0;
            choices[c++] = new ValueChoice<Integer>(0, "negativ");
            choices[c++] = new ValueChoice<Integer>(1, "+/-");
            choices[c++] = new ValueChoice<Integer>(2, "+1");
            choices[c++] = new ValueChoice<Integer>(3, "+2");
            choices[c++] = new ValueChoice<Integer>(4, "+3");
            choices[c++] = new ValueChoice<Integer>(5, "+4");
            assert c == choices.length;
            select.setChoices(choices);
        }

        select.setOutputVariable(urine);

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
        urine = Util.linkVariable(variablePool, urine);
    }

    @Override
    public void deviceLeave() {
    }
}
