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
import dk.silverbullet.telemed.questionnaire.R;
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
        setStatusText(Util.getString(R.string.weight_turn_on_and_wait, questionnaire));
        addElement(statusText);

        be = new TwoButtonElement(this);
        be.setLeftNextNode(getNextFailNode());
        be.setLeftText(Util.getString(R.string.default_omit, questionnaire));
        be.setRightNextNode(this);
        be.setRightText(Util.getString(R.string.default_retry, questionnaire));
        addElement(be);

        super.enter();

        try {
            weightDeviceController = AndWeightScaleController.create(this, new AndroidHdpController(questionnaire
                    .getContext().getApplicationContext()));
        } catch (DeviceInitialisationException e) {
            setStatusText(Util.getString(R.string.weight_could_not_connect, questionnaire));
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
        setStatusText(Util.getString(R.string.weight_connected, questionnaire));
    }

    @Override
    public void disconnected() {
        setStatusText(Util.getString(R.string.weight_disconnected, questionnaire));
    }

    @Override
    public void permanentProblem() {
        setStatusText(Util.getString(R.string.weight_permanent_problem, questionnaire));
    }

    @Override
    public void temporaryProblem() {
        setStatusText(Util.getString(R.string.weight_temporary_problem, questionnaire));
    }

    @Override
    public void measurementReceived(final String systemId, final Weight weight) {
        questionnaire.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (weight.getUnit() == Unit.KG) {
                    setDeviceIdString(systemId);
                    WeightDeviceNode.this.weight.setValue(new Constant<Float>(weight.getWeight()));
                    statusText.setText(Util.getString(R.string.weight_weight, questionnaire) + weight.getWeight() + " " + weight.getUnit().getName());
                    be.setRightText(Util.getString(R.string.default_ok, questionnaire));
                    be.setRightNextNode(getNextNode());
                } else {
                    statusText.setText(Util.getString(R.string.weight_set_weight_to_kg, questionnaire) + weight.getWeight()
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
