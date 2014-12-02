package dk.silverbullet.telemed.questionnaire.node;

import com.google.gson.annotations.Expose;
import dk.silverbullet.telemed.bloodsugar.*;
import dk.silverbullet.telemed.questionnaire.Questionnaire;
import dk.silverbullet.telemed.questionnaire.R;
import dk.silverbullet.telemed.questionnaire.element.TextViewElement;
import dk.silverbullet.telemed.questionnaire.element.TwoButtonElement;
import dk.silverbullet.telemed.questionnaire.expression.Variable;
import dk.silverbullet.telemed.questionnaire.expression.VariableLinkFailedException;
import dk.silverbullet.telemed.utils.Util;

import java.util.Calendar;
import java.util.Date;
import java.util.Map;

public class ContinuousBloodSugarTestDeviceNode extends DeviceNode {
    private TextViewElement infoElement;
    @Expose
    private Variable<ContinuousBloodSugarEvents> events;
    @Expose
    String text;

    public ContinuousBloodSugarTestDeviceNode(Questionnaire questionnaire, String nodeName) {
        super(questionnaire, nodeName);
    }

    @Override
    public void enter() {
        clearElements();

        addElement(new TextViewElement(this, text));

        infoElement = new TextViewElement(this);
        updateInfoElement("Simuleret CGM");
        addElement(infoElement);

        TwoButtonElement be = new TwoButtonElement(this);
        be.setLeftNextNode(getNextFailNode());
        be.setLeftText(Util.getString(R.string.default_omit, questionnaire));
        be.setRightNextNode(getNextNode());
        be.setRightText(Util.getString(R.string.default_next, questionnaire));
        addElement(be);

        super.enter();
    }

    @Override
    public void linkVariables(Map<String, Variable<?>> variablePool) throws VariableLinkFailedException {
        super.linkVariables(variablePool);

        events = Util.linkVariable(variablePool, events);
    }

    public void deviceLeave() {

        ContinuousBloodSugarEvents measurements = new ContinuousBloodSugarEvents();
        measurements.deviceId = "123456";
        measurements.transferTime = new Date();
        for (int i=0; i<1000; i++) {
            ContinuousBloodSugarMeasurement measurement = new ContinuousBloodSugarMeasurement();
            measurement.recordId = i;
            measurement.eventTime = new Date(System.currentTimeMillis() - i*(1000 * 60 * 5));
            measurement.glucoseValueInmmolPerl = (5 + 50*Math.sin(i / 100d)) + "";
            measurements.events.add(measurement);
        }

        HypoAlarmEvent hypoAlarmEvent = new HypoAlarmEvent();
        hypoAlarmEvent.recordId = 2001;
        hypoAlarmEvent.eventTime = Calendar.getInstance().getTime();
        hypoAlarmEvent.glucoseValueInmmolPerl = "5.5";
        measurements.events.add(hypoAlarmEvent);

        HyperAlarmEvent hyperAlarmEvent = new HyperAlarmEvent();
        hyperAlarmEvent.recordId = 2002;
        hyperAlarmEvent.eventTime = Calendar.getInstance().getTime();
        hyperAlarmEvent.glucoseValueInmmolPerl = "5.5";
        measurements.events.add(hyperAlarmEvent);

        ImpendingHypoAlarmEvent impendingHypoAlarmEvent = new ImpendingHypoAlarmEvent();
        impendingHypoAlarmEvent.recordId = 2003;
        impendingHypoAlarmEvent.eventTime = Calendar.getInstance().getTime();
        impendingHypoAlarmEvent.glucoseValueInmmolPerl = "5.5";
        impendingHypoAlarmEvent.impendingNess = "2";
        measurements.events.add(impendingHypoAlarmEvent);

        ImpendingHyperAlarmEvent impendingHyperAlarmEvent = new ImpendingHyperAlarmEvent();
        impendingHyperAlarmEvent.recordId = 2004;
        impendingHyperAlarmEvent.eventTime = Calendar.getInstance().getTime();
        impendingHyperAlarmEvent.glucoseValueInmmolPerl = "5.5";
        impendingHyperAlarmEvent.impendingNess = "2";
        measurements.events.add(impendingHyperAlarmEvent);

        CoulometerReadingEvent coulometerReadingEvent = new CoulometerReadingEvent();
        coulometerReadingEvent.recordId = 2005;
        coulometerReadingEvent.eventTime = Calendar.getInstance().getTime();
        coulometerReadingEvent.glucoseValueInmmolPerl = "5.5";
        measurements.events.add(coulometerReadingEvent);

        InsulinEvent insulinEvent = new InsulinEvent();
        insulinEvent.recordId = 2006;
        insulinEvent.eventTime = Calendar.getInstance().getTime();
        insulinEvent.insulinType = InsulinEvent.InsulinType.INTERMEDIATE;
        insulinEvent.units = "1000";
        measurements.events.add(insulinEvent);

        MealEvent mealEvent = new MealEvent();
        mealEvent.recordId = 2007;
        mealEvent.eventTime = Calendar.getInstance().getTime();
        mealEvent.carboGrams = "120";
        mealEvent.foodType = MealEvent.FoodType.BREAKFAST;
        measurements.events.add(mealEvent);

        ExerciseEvent exerciseEvent = new ExerciseEvent();
        exerciseEvent.recordId = 2008;
        exerciseEvent.eventTime = Calendar.getInstance().getTime();
        exerciseEvent.durationInMinutes = "10";
        exerciseEvent.exerciseIntensity = ExerciseEvent.ExerciseIntensity.HIGH;
        exerciseEvent.exerciseType = ExerciseEvent.ExerciseType.AEROBICS;
        measurements.events.add(exerciseEvent);

        StateOfHealthEvent stateOfHealthEvent = new StateOfHealthEvent();
        stateOfHealthEvent.recordId = 2009;
        stateOfHealthEvent.eventTime = Calendar.getInstance().getTime();
        stateOfHealthEvent.stateOfHealth = StateOfHealthEvent.HealthState.ALLERGY;
        measurements.events.add(stateOfHealthEvent);

        events.setValue(measurements);
    }

    private void updateInfoElement(final String text) {
        questionnaire.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                infoElement.setText(text);
            }
        });
    }
}
