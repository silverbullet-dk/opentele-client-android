package dk.silverbullet.telemed.questionnaire.node;

import com.google.gson.annotations.Expose;
import dk.silverbullet.telemed.device.DeviceInitialisationException;
import dk.silverbullet.telemed.device.andweightscale.AndWeightScaleController;
import dk.silverbullet.telemed.device.andweightscale.Weight;
import dk.silverbullet.telemed.device.andweightscale.Weight.Unit;
import dk.silverbullet.telemed.device.continua.ContinuaDeviceController;
import dk.silverbullet.telemed.device.continua.ContinuaListener;
import dk.silverbullet.telemed.device.continua.android.AndroidHdpController;
import dk.silverbullet.telemed.questionnaire.Questionnaire;
import dk.silverbullet.telemed.questionnaire.element.TextViewElement;
import dk.silverbullet.telemed.questionnaire.element.TwoButtonElement;
import dk.silverbullet.telemed.questionnaire.expression.Constant;
import dk.silverbullet.telemed.questionnaire.expression.Variable;
import dk.silverbullet.telemed.questionnaire.expression.VariableLinkFailedException;
import dk.silverbullet.telemed.utils.Util;

import java.util.Map;

public class WeightDeviceNode extends DeviceNode implements ContinuaListener<Weight> {
    @Expose
    private Variable<Float> weight;

    @Expose
    String text;

    private TextViewElement statusText;
    private TwoButtonElement be;

    private ContinuaDeviceController weightDeviceController;

    public WeightDeviceNode(Questionnaire questionnaire, String nodeName) {
        super(questionnaire, nodeName);
    }

    @Override
    public void enter() {
        clearElements();

        addElement(new TextViewElement(this, text));

        statusText = new TextViewElement(this);
        setStatusText("Tænd for vægten og afvent ny besked i skærmbillede.");
        addElement(statusText);

        be = new TwoButtonElement(this);
        be.setLeftNextNode(getNextFailNode());
        be.setLeftText("Undlad");
        be.setRightNextNode(this);
        be.setRightText("Prøv igen");
        addElement(be);

        super.enter();

        try {
            weightDeviceController = AndWeightScaleController.create(this, new AndroidHdpController(questionnaire
                    .getActivity().getApplicationContext()));
        } catch (DeviceInitialisationException e) {
            setStatusText("Kunne ikke forbinde til vægt.");
        }
    }

    @Override
    public void linkVariables(Map<String, Variable<?>> variablePool) throws VariableLinkFailedException {
        super.linkVariables(variablePool);
        weight = Util.linkVariable(variablePool, weight);
    }

    @Override
    public void deviceLeave() {
        if (weightDeviceController != null) {
            weightDeviceController.close();
        }
    }

    @Override
    public void connected() {
        setStatusText("Træd op på vægten og hold dig i ro.");
    }

    @Override
    public void disconnected() {
        setStatusText("Forbindelsen til vægten blev afbrudt. Prøv evt. igen.");
    }

    @Override
    public void permanentProblem() {
        setStatusText("Der kan ikke skabes forbindelse.");
    }

    @Override
    public void temporaryProblem() {
        setStatusText("Kunne ikke hente data. Prøv evt. igen.");
    }

    @Override
    public void measurementReceived(final String systemId, final Weight weight) {
        questionnaire.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (weight.getUnit() == Unit.KG) {
                    setDeviceIdString(systemId);
                    WeightDeviceNode.this.weight.setValue(new Constant<Float>(weight.getWeight()));
                    statusText.setText("Vægt: " + weight.getWeight() + " " + weight.getUnit().getName());
                    be.setRightText("OK");
                    be.setRightNextNode(getNextNode());
                } else {
                    statusText.setText("Indstil din vægt til at måle i kg og prøv igen. Vægt: " + weight.getWeight()
                            + " " + weight.getUnit().getName());
                }
            }
        });
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
