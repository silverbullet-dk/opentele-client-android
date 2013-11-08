package dk.silverbullet.telemed.questionnaire.node;

import com.google.gson.annotations.Expose;
import dk.silverbullet.telemed.device.DeviceInitialisationException;
import dk.silverbullet.telemed.device.continua.ContinuaDeviceController;
import dk.silverbullet.telemed.device.continua.ContinuaListener;
import dk.silverbullet.telemed.device.continua.HdpController;
import dk.silverbullet.telemed.device.continua.android.AndroidHdpController;
import dk.silverbullet.telemed.device.nonin.NoninController;
import dk.silverbullet.telemed.device.nonin.SaturationAndPulse;
import dk.silverbullet.telemed.questionnaire.Questionnaire;
import dk.silverbullet.telemed.questionnaire.element.TextViewElement;
import dk.silverbullet.telemed.questionnaire.element.TwoButtonElement;
import dk.silverbullet.telemed.questionnaire.expression.Variable;
import dk.silverbullet.telemed.questionnaire.expression.VariableLinkFailedException;
import dk.silverbullet.telemed.utils.Util;

import java.util.Map;

public class SaturationDeviceNode extends DeviceNode implements ContinuaListener<SaturationAndPulse> {
    @Expose
    private Variable<Integer> saturation;

    @Expose
    private Variable<Integer> pulse;
    @Expose
    String text;
    private TextViewElement statusElement;
    private TwoButtonElement be;
    private ContinuaDeviceController noninController;

    public SaturationDeviceNode(Questionnaire questionnaire, String nodeName) {
        super(questionnaire, nodeName);
    }

    @Override
    public void enter() {
        clearElements();
        addElement(new TextViewElement(this, text));

        statusElement = new TextViewElement(this, "Forbinder til enhed...");
        addElement(statusElement);

        be = new TwoButtonElement(this);
        be.setLeftNextNode(getNextFailNode());
        be.setLeftText("Undlad");
        be.setRightNextNode(this);
        be.setRightText("Prøv igen");
        be.hideRightButton();
        addElement(be);

        super.enter();

        if (noninController == null) {
            try {
                HdpController bluetoothController = new AndroidHdpController(questionnaire.getActivity());
                noninController = NoninController.create(this, bluetoothController);
            } catch (DeviceInitialisationException e) {
                statusElement.setText("Kunne ikke forbinde til måler.");
            }
        }
    }

    @Override
    public void linkVariables(Map<String, Variable<?>> variablePool) throws VariableLinkFailedException {
        super.linkVariables(variablePool);
        saturation = Util.linkVariable(variablePool, saturation);
        pulse = Util.linkVariable(variablePool, pulse);
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
                statusElement.setText("Venter på måling. Hold dig i ro.");
            }
        });
    }

    @Override
    public void measurementReceived(String systemId, final SaturationAndPulse measurement) {
        saturation.setValue(measurement.getSaturation());
        pulse.setValue(measurement.getPulse());
        getDeviceId().setValue(systemId);

        questionnaire.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                noninController.close();

                statusElement.setText("Måling modtaget\n" + "Iltmætning: " + measurement.getSaturation() + "\n"
                        + "Puls: " + measurement.getPulse());

                be.setRightText("Næste");
                be.showRightButton();
                be.setRightNextNode(getNextNode());
            }
        });
    }

    @Override
    public void disconnected() {
        setStatusText("Forbindelse afbrudt");
    }

    @Override
    public void permanentProblem() {
        setStatusText("Der kan ikke skabes forbindelse");
    }

    @Override
    public void temporaryProblem() {
        setStatusText("Kunne ikke hente data. Sluk og tænd evt. oxymeteret.");
    }

    private void setStatusText(final String statusText) {
        questionnaire.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                statusElement.setText(statusText);
            }
        });
    }

    public void setSaturation(Variable<Integer> saturation) {
        this.saturation = saturation;
    }

    public void setPulse(Variable<Integer> pulse) {
        this.pulse = pulse;
    }
}
