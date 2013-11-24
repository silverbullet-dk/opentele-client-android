package dk.silverbullet.telemed.questionnaire.node;

import com.google.gson.annotations.Expose;
import dk.silverbullet.telemed.questionnaire.Questionnaire;
import dk.silverbullet.telemed.questionnaire.R;
import dk.silverbullet.telemed.questionnaire.element.RadioButtonElement;
import dk.silverbullet.telemed.questionnaire.element.TextViewElement;
import dk.silverbullet.telemed.questionnaire.element.TwoButtonElement;
import dk.silverbullet.telemed.questionnaire.element.ValueChoice;
import dk.silverbullet.telemed.questionnaire.expression.Variable;
import dk.silverbullet.telemed.questionnaire.expression.VariableLinkFailedException;
import dk.silverbullet.telemed.utils.Util;

import java.util.Map;

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
        addElement(new TextViewElement(this, Util.getString(R.string.urineglocose_enter_glucose_number, questionnaire)));

        RadioButtonElement<Integer> select = new RadioButtonElement<Integer>(this);
        {
            @SuppressWarnings("unchecked")
            // Arrays and generics don't mix.
            ValueChoice<Integer>[] choices = new ValueChoice[5];
            int c = 0;
            choices[c++] = new ValueChoice<Integer>(0, Util.getString(R.string.urineglocose_value_negative, questionnaire));
            choices[c++] = new ValueChoice<Integer>(1, Util.getString(R.string.urineglocose_value_plus_one, questionnaire));
            choices[c++] = new ValueChoice<Integer>(2, Util.getString(R.string.urineglocose_value_plus_two, questionnaire));
            choices[c++] = new ValueChoice<Integer>(3, Util.getString(R.string.urineglocose_value_plus_three, questionnaire));
            choices[c++] = new ValueChoice<Integer>(4, Util.getString(R.string.urineglocose_value_plus_four, questionnaire));
            assert c == choices.length;
            select.setChoices(choices);
        }

        select.setOutputVariable(glucoseUrine);

        addElement(select);

        TwoButtonElement be = new TwoButtonElement(this, Util.getString(R.string.default_omit, questionnaire), Util.getString(R.string.default_next, questionnaire));
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
