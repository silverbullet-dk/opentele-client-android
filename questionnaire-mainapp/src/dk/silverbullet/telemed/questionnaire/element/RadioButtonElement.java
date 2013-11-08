package dk.silverbullet.telemed.questionnaire.element;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import com.google.gson.annotations.Expose;
import dk.silverbullet.telemed.questionnaire.R;
import dk.silverbullet.telemed.questionnaire.expression.Expression;
import dk.silverbullet.telemed.questionnaire.expression.UnknownVariableException;
import dk.silverbullet.telemed.questionnaire.expression.Variable;
import dk.silverbullet.telemed.questionnaire.expression.VariableLinkFailedException;
import dk.silverbullet.telemed.questionnaire.node.IONode;
import dk.silverbullet.telemed.questionnaire.node.Node;
import dk.silverbullet.telemed.utils.Util;

import java.util.Map;

import static dk.silverbullet.telemed.utils.Util.linkVariable;

public class RadioButtonElement<T> extends Element {

    @SuppressWarnings("unused")
    private static final String TAG = Util.getTag(RadioButtonElement.class);

    @Expose
    private Variable<T> outputVariable;

    @Expose
    private ValueChoice<T>[] choices;

    private RadioButton[] radioButton;

    private RadioGroup radioGroup;

    public RadioButtonElement(final IONode node) {
        super(node);
    }

    @Override
    public View getView() {
        if (null == radioGroup) {
            Activity activity = getQuestionnaire().getActivity();
            LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            radioGroup = (RadioGroup) inflater.inflate(R.layout.radiogroup, null);
            radioButton = new RadioButton[choices.length];
            for (int i = 0; i < choices.length; i++) {
                ValueChoice<T> choice = choices[i];
                radioButton[i] = (RadioButton) inflater.inflate(R.layout.radiobutton, null);
                radioButton[i].setText(choice.getText());
                radioButton[i].setId(i);
                radioGroup.addView(radioButton[i]);
            }
        }

        if (outputVariable != null && outputVariable.evaluate() != null) {
            for (int i = 0; i < choices.length; i++) {
                if (outputVariable.evaluate().equals(choices[i].getValue().evaluate())) {
                    radioButton[i].setChecked(true);
                }
            }
        }

        return radioGroup;
    }

    @Override
    public void leave() {

        if (outputVariable != null) {
            for (int i = 0; i < choices.length; i++) {
                if (radioButton[i].isChecked()) {
                    outputVariable.setValue(choices[i].getValue().evaluate());
                    break;
                }
            }
        }

        InputMethodManager imm = (InputMethodManager) getQuestionnaire().getActivity().getApplicationContext()
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(radioGroup.getWindowToken(), 0);
    }

    @Override
    public void linkNodes(Map<String, Node> map) {
        // Done
    }

    @SuppressWarnings("unchecked")
    @Override
    public void linkVariables(Map<String, Variable<?>> variablePool) throws VariableLinkFailedException {
        outputVariable = linkVariable(variablePool, outputVariable);
        for (ValueChoice<T> choice : choices) {
            if (choice.value instanceof Variable) {
                String name = ((Variable<T>) choice.value).getName();
                if (variablePool.containsKey(name)) {
                    choice.value = (Expression<T>) variablePool.get(name);
                } else
                    throw new UnknownVariableException(name);
            } else
                choice.value.link(variablePool);
        }
    }

    @Override
    public boolean validates() {
        for (RadioButton r : radioButton) {
            if (r.isChecked())
                return true;
        }
        return false;
    }
    public void setOutputVariable(Variable<T> outputVariable) {
        this.outputVariable = outputVariable;
    }

    public void setChoices(ValueChoice<T>[] choices) {
        this.choices = choices;
    }

}
