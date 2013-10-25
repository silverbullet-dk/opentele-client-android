package dk.silverbullet.telemed.questionnaire.node;

import java.util.Map;

import lombok.Data;
import lombok.EqualsAndHashCode;
import android.util.Log;

import com.google.gson.annotations.Expose;

import dk.silverbullet.telemed.device.DeviceInitialisationException;
import dk.silverbullet.telemed.device.andbloodpressure.AndBloodPressureController;
import dk.silverbullet.telemed.device.andbloodpressure.BloodPressureAndPulse;
import dk.silverbullet.telemed.device.continua.ContinuaDeviceController;
import dk.silverbullet.telemed.device.continua.ContinuaListener;
import dk.silverbullet.telemed.device.continua.android.AndroidHdpController;
import dk.silverbullet.telemed.questionnaire.Questionnaire;
import dk.silverbullet.telemed.questionnaire.element.TextViewElement;
import dk.silverbullet.telemed.questionnaire.element.TwoButtonElement;
import dk.silverbullet.telemed.questionnaire.expression.Variable;
import dk.silverbullet.telemed.questionnaire.expression.VariableLinkFailedException;
import dk.silverbullet.telemed.utils.Util;

@Data
@EqualsAndHashCode(callSuper = false)
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
        statusText = new TextViewElement(this, "Tryk på START-knappen på blodtryksmåleren.");
        addElement(statusText);

        systolicBloodPressureDisplayText = new TextViewElement(this);
        addElement(systolicBloodPressureDisplayText);

        diastolicBloodPressureDisplayText = new TextViewElement(this);
        addElement(diastolicBloodPressureDisplayText);

        pulseDisplayText = new TextViewElement(this);
        addElement(pulseDisplayText);

        be = new TwoButtonElement(this);
        be.setLeftNextNode(getNextFailNode());
        be.setLeftText("Undlad");
        be.setRightNextNode(this);
        be.setRightText("Prøv igen");
        addElement(be);

        super.enter();

        try {
            controller = AndBloodPressureController.create(this, new AndroidHdpController(questionnaire.getActivity()));
        } catch (DeviceInitialisationException e) {
            setStatusText("Kunne ikke forbinde til måler.");
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
                statusText.setText("Dit blodtryk og din puls");
                systolicBloodPressureDisplayText.setText("Systolisk blodtryk: " + systolic.getExpressionValue());
                diastolicBloodPressureDisplayText.setText("Diastolisk blodtryk: " + diastolic.getExpressionValue());
                pulseDisplayText.setText("Puls: " + pulse.getExpressionValue());

                be.showRightButton();
                be.setRightText("OK");
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
        setStatusText("Venter på måling. Hold dig i ro.");
    }

    @Override
    public void disconnected() {
        setStatusText("Forbindelse afbrudt.");
    }

    @Override
    public void permanentProblem() {
        setStatusText("Der kan ikke skabes forbindelse.");
    }

    @Override
    public void temporaryProblem() {
        setStatusText("Kunne ikke hente data. Prøv evt. en ny blodtryksmåling.");
    }

    private void setStatusText(final String text) {
        questionnaire.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                statusText.setText(text);
            }
        });
    }
}
