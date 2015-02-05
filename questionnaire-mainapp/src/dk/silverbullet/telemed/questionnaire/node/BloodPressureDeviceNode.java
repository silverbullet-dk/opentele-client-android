package dk.silverbullet.telemed.questionnaire.node;

import android.util.Log;
import com.google.gson.annotations.Expose;
import dk.silverbullet.telemed.OpenTeleApplication;
import dk.silverbullet.telemed.device.DeviceInitialisationException;
import dk.silverbullet.telemed.device.andbloodpressure.AndBloodPressureController;
import dk.silverbullet.telemed.device.andbloodpressure.BloodPressureAndPulse;
import dk.silverbullet.telemed.device.continua.ContinuaDeviceController;
import dk.silverbullet.telemed.device.continua.ContinuaListener;
import dk.silverbullet.telemed.device.continua.android.AndroidHdpController;
import dk.silverbullet.telemed.questionnaire.Questionnaire;
import dk.silverbullet.telemed.questionnaire.R;
import dk.silverbullet.telemed.questionnaire.element.HelpTextElement;
import dk.silverbullet.telemed.questionnaire.element.TextViewElement;
import dk.silverbullet.telemed.questionnaire.element.TwoButtonElement;
import dk.silverbullet.telemed.questionnaire.expression.Variable;
import dk.silverbullet.telemed.questionnaire.expression.VariableLinkFailedException;
import dk.silverbullet.telemed.utils.Util;

import java.util.Map;

public class BloodPressureDeviceNode extends DeviceNode implements ContinuaListener<BloodPressureAndPulse> {
    private static final String TAG = Util.getTag(BloodPressureDeviceNode.class);

    @Expose
    private Variable<Integer> diastolic;
    @Expose
    private Variable<Integer> systolic;
    @Expose
    private Variable<Integer> meanArterialPressure;
    @Expose
    private Variable<Integer> pulse;
    @Expose
    String text;

    private TextViewElement statusText;
    private TextViewElement systolicBloodPressureDisplayText;
    private TextViewElement diastolicBloodPressureDisplayText;
    private TextViewElement pulseDisplayText;
    private TwoButtonElement be;

    private ContinuaDeviceController controller;

    public BloodPressureDeviceNode(Questionnaire questionnaire, String nodeName) {
        super(questionnaire, nodeName);
    }

    @Override
    public void enter() {
        clearElements();
        addElement(new TextViewElement(this, text));
        if (hasHelp()) {
            addElement(new HelpTextElement(this, getHelpText(), getHelpImage()));
        }

        statusText = new TextViewElement(this, Util.getString(R.string.bloodpressure_press_start, questionnaire));
        addElement(statusText);

        systolicBloodPressureDisplayText = new TextViewElement(this);
        addElement(systolicBloodPressureDisplayText);

        diastolicBloodPressureDisplayText = new TextViewElement(this);
        addElement(diastolicBloodPressureDisplayText);

        pulseDisplayText = new TextViewElement(this);
        addElement(pulseDisplayText);

        be = new TwoButtonElement(this);
        be.setLeftNextNode(getNextFailNode());
        be.setLeftText(Util.getString(R.string.default_omit, questionnaire));
        be.setRightNextNode(this);
        be.setRightText(Util.getString(R.string.default_retry, questionnaire));
        addElement(be);

        super.enter();

        try {
            controller = AndBloodPressureController.create(this, new AndroidHdpController(questionnaire.getContext()));
        } catch (DeviceInitialisationException e) {
            OpenTeleApplication.instance().logException(e);
            setStatusText(Util.getString(R.string.bloodpressure_could_not_connect, questionnaire));
        }
    }

    @Override
    public void linkVariables(Map<String, Variable<?>> variablePool) throws VariableLinkFailedException {
        super.linkVariables(variablePool);
        diastolic = Util.linkVariable(variablePool, diastolic);
        systolic = Util.linkVariable(variablePool, systolic);
        meanArterialPressure = Util.linkVariable(variablePool, meanArterialPressure, true);
        pulse = Util.linkVariable(variablePool, pulse);
    }

    @Override
    public void measurementReceived(String systemId, BloodPressureAndPulse measurement) {
        systolic.setValue(measurement.getSystolic());
        diastolic.setValue(measurement.getDiastolic());
        if (meanArterialPressure != null) {
            meanArterialPressure.setValue(measurement.getMeanArterialPressure());
        }
        pulse.setValue(measurement.getPulse());
        setDeviceIdString(systemId);

        questionnaire.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                statusText.setText(Util.getString(R.string.bloodpressure_your_bloodpressure_and_pulse, questionnaire));
                systolicBloodPressureDisplayText.setText(Util.getString(R.string.bloodpressure_systolic, questionnaire) + systolic.getExpressionValue());
                diastolicBloodPressureDisplayText.setText(Util.getString(R.string.bloodpressure_diastolic, questionnaire) + diastolic.getExpressionValue());
                pulseDisplayText.setText(Util.getString(R.string.bloodpressure_pulse, questionnaire) + pulse.getExpressionValue());

                be.showRightButton();
                be.setRightText(Util.getString(R.string.default_ok, questionnaire));
                be.setRightNextNode(getNextNode());
            }
        });
    }

    @Override
    public void deviceLeave() {
        Log.d(TAG, "deviceLeave...");
        if (controller != null) {
            controller.close();
        }
    }

    @Override
    public void connected() {
        setStatusText(Util.getString(R.string.bloodpressure_waiting_for_measurement, questionnaire));
    }

    @Override
    public void disconnected() {
        setStatusText(Util.getString(R.string.bloodpressure_disconnected, questionnaire));
    }

    @Override
    public void permanentProblem() {
        setStatusText(Util.getString(R.string.bloodpressure_permanent_problem, questionnaire));
    }

    @Override
    public void temporaryProblem() {
        setStatusText(Util.getString(R.string.bloodpressure_temporary_problem, questionnaire));
    }

    private void setStatusText(final String text) {
        questionnaire.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                statusText.setText(text);
            }
        });
    }

    public void setDiastolic(Variable<Integer> diastolic) {
        this.diastolic = diastolic;
    }
    public void setSystolic(Variable<Integer> systolic) {
        this.systolic = systolic;
    }

    public void setMeanArterialPressure(Variable<Integer> meanArterialPressure) {
        this.meanArterialPressure = meanArterialPressure;
    }
    public void setPulse(Variable<Integer> pulse) {
        this.pulse = pulse;
    }
}
