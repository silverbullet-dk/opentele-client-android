package dk.silverbullet.telemed.questionnaire.node;

import com.google.gson.annotations.Expose;
import dk.silverbullet.telemed.OpenTeleApplication;
import dk.silverbullet.telemed.device.DeviceInitialisationException;
import dk.silverbullet.telemed.device.nonin.NoninController;
import dk.silverbullet.telemed.device.nonin.SaturationAndPulse;
import dk.silverbullet.telemed.device.nonin.SaturationController;
import dk.silverbullet.telemed.device.nonin.SaturationPulseListener;
import dk.silverbullet.telemed.questionnaire.Questionnaire;
import dk.silverbullet.telemed.questionnaire.R;
import dk.silverbullet.telemed.questionnaire.element.TextViewElement;
import dk.silverbullet.telemed.questionnaire.element.TwoButtonElement;
import dk.silverbullet.telemed.questionnaire.expression.Variable;
import dk.silverbullet.telemed.questionnaire.expression.VariableLinkFailedException;
import dk.silverbullet.telemed.utils.Util;

import java.util.Map;

public class SaturationWithoutPulseDeviceNode extends DeviceNode implements SaturationPulseListener {
    @Expose
    private Variable<Integer> saturation;

    @Expose
    String text;
    private TextViewElement statusElement;
    private TwoButtonElement be;
    private SaturationController noninController;

    public SaturationWithoutPulseDeviceNode(Questionnaire questionnaire, String nodeName) {
        super(questionnaire, nodeName);
    }

    @Override
    public void enter() {
        clearElements();
        addElement(new TextViewElement(this, text));

        statusElement = new TextViewElement(this, Util.getString(R.string.saturation_connecting, questionnaire));
        addElement(statusElement);

        be = new TwoButtonElement(this);
        be.setLeftNextNode(getNextFailNode());
        be.setLeftText(Util.getString(R.string.default_omit, questionnaire));
        be.setRightNextNode(this);
        be.setRightText(Util.getString(R.string.default_retry, questionnaire));
        be.hideRightButton();
        addElement(be);

        super.enter();

        if (noninController == null) {
            try {
                noninController = NoninController.create(this);
            } catch (DeviceInitialisationException e) {
                OpenTeleApplication.instance().logException(e);
                statusElement.setText(Util.getString(R.string.saturation_could_not_connect, questionnaire));
            }
        }
    }

    @Override
    public void linkVariables(Map<String, Variable<?>> variablePool) throws VariableLinkFailedException {
        super.linkVariables(variablePool);
        saturation = Util.linkVariable(variablePool, saturation);
    }

    @Override
    public void deviceLeave() {
        if (noninController != null) {
            noninController.close();
            noninController = null;
        }
    }

    @Override
    public void connected() {
        questionnaire.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                statusElement.setText(Util.getString(R.string.saturation__waiting_for_measurement, questionnaire));
            }
        });
    }

    @Override
    public void measurementReceived(String systemId, final SaturationAndPulse measurement) {
        saturation.setValue(measurement.getSaturation());
        getDeviceId().setValue(systemId);

        questionnaire.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                noninController.close();

                statusElement.setText(Util.getString(R.string.saturation_measuremen_without_pulse_received, questionnaire, measurement.getSaturation()));

                be.setRightText(Util.getString(R.string.default_next, questionnaire));
                be.showRightButton();
                be.setRightNextNode(getNextNode());
            }
        });
    }

    @Override
    public void firstTimeOut() {
        setStatusText(Util.getString(R.string.saturation_first_timeout, questionnaire));
    }

    @Override
    public void finalTimeOut(String systemId, SaturationAndPulse measurement) {
        //TODO: Eventually we'll have to send extra information to the server indicating that this was low quality measurement.
        if(measurement == null) {
            questionnaire.getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    noninController.close();
                    setStatusText(Util.getString(R.string.saturation_could_not_collect_measurement, questionnaire));
                    be.setRightNextNode(SaturationWithoutPulseDeviceNode.this);
                    be.setRightText(Util.getString(R.string.default_retry, questionnaire));
                    be.showRightButton();
                }});
        } else {
            measurementReceived(systemId, measurement);
        }
    }

    @Override
    public void temporaryProblem() {
        setStatusText(Util.getString(R.string.saturation_temporary_problem, questionnaire));
    }

    private void setStatusText(final String statusText) {
        questionnaire.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                statusElement.setText(statusText);
            }
        });
    }
}
