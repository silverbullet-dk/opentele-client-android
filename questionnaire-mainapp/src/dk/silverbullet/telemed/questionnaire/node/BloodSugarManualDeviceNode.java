package dk.silverbullet.telemed.questionnaire.node;

import com.google.gson.annotations.Expose;
import dk.silverbullet.telemed.device.accuchek.BloodSugarMeasurement;
import dk.silverbullet.telemed.device.accuchek.BloodSugarMeasurements;
import dk.silverbullet.telemed.questionnaire.Questionnaire;
import dk.silverbullet.telemed.questionnaire.R;
import dk.silverbullet.telemed.questionnaire.element.CheckBoxElement;
import dk.silverbullet.telemed.questionnaire.element.EditTextElement;
import dk.silverbullet.telemed.questionnaire.element.TextViewElement;
import dk.silverbullet.telemed.questionnaire.element.TwoButtonElement;
import dk.silverbullet.telemed.questionnaire.expression.Variable;
import dk.silverbullet.telemed.questionnaire.expression.VariableLinkFailedException;
import dk.silverbullet.telemed.utils.Util;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class BloodSugarManualDeviceNode extends DeviceNode {
    private EditTextElement measurementElement;
    private CheckBoxElement beforeMealElement;
    private CheckBoxElement afterMealElement;
    private TwoButtonElement be;
    @Expose
    private Variable<BloodSugarMeasurements> bloodSugarMeasurements;
    private Variable<Double> measurementVariable;
    private Variable<Boolean> beforeMealVariable;
    private Variable<Boolean> afterMealVariable;
    @Expose
    String text;

    public BloodSugarManualDeviceNode(Questionnaire questionnaire, String nodeName) {
        super(questionnaire, nodeName);
    }

    @Override
    public void enter() {
        clearElements();

        measurementVariable = new Variable<Double>("Measurement", Double.class);
        beforeMealVariable = new Variable<Boolean>("BeforeMeal", Boolean.class);
        beforeMealVariable.setValue(false);
        afterMealVariable = new Variable<Boolean>("AfterMeal", Boolean.class);
        afterMealVariable.setValue(false);

        addElement(new TextViewElement(this, text));

        measurementElement = new EditTextElement(this);
        measurementElement.setDecimals(2);
        measurementElement.setOutputVariable(measurementVariable);
        addElement(measurementElement);

        beforeMealElement = new CheckBoxElement(this);
        beforeMealElement.setText(Util.getString(R.string.bloodsugar_before_meal, questionnaire));
        beforeMealElement.setOutputVariable(beforeMealVariable);
        addElement(beforeMealElement);

        afterMealElement = new CheckBoxElement(this);
        afterMealElement.setText(Util.getString(R.string.bloodsugar_after_meal, questionnaire));
        afterMealElement.setOutputVariable(afterMealVariable);
        addElement(afterMealElement);

        be = new TwoButtonElement(this);
        be.setLeftNextNode(getNextFailNode());
        be.setLeftText(Util.getString(R.string.default_omit, questionnaire));
        be.setLeftSkipValidation(true);
        be.setRightNextNode(getNextNode());
        be.setRightText(Util.getString(R.string.default_next, questionnaire));
        addElement(be);

        super.enter();
    }

    @Override
    public void deviceLeave() {
        Date now = new Date();

        BloodSugarMeasurement measurement = new BloodSugarMeasurement();
        measurement.timeOfMeasurement = now;
        measurement.result = measurementVariable.evaluate();
        measurement.isBeforeMeal = beforeMealVariable.evaluate();
        measurement.isAfterMeal = afterMealVariable.evaluate();

        List<BloodSugarMeasurement> listOfMeasurements = new ArrayList<BloodSugarMeasurement>();
        listOfMeasurements.add(measurement);

        BloodSugarMeasurements measurements = new BloodSugarMeasurements();
        measurements.transferTime = now;
        measurements.measurements = listOfMeasurements;

        bloodSugarMeasurements.setValue(measurements);
    }

    @Override
    public void linkVariables(Map<String, Variable<?>> variablePool) throws VariableLinkFailedException {
        super.linkVariables(variablePool);
        bloodSugarMeasurements = Util.linkVariable(variablePool, bloodSugarMeasurements);
    }
}
