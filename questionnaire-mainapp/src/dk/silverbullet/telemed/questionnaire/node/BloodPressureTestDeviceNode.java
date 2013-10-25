package dk.silverbullet.telemed.questionnaire.node;

import java.util.Map;

import lombok.Data;
import lombok.EqualsAndHashCode;

import com.google.gson.annotations.Expose;

import dk.silverbullet.telemed.questionnaire.Questionnaire;
import dk.silverbullet.telemed.questionnaire.expression.Constant;
import dk.silverbullet.telemed.questionnaire.expression.Variable;
import dk.silverbullet.telemed.questionnaire.expression.VariableLinkFailedException;
import dk.silverbullet.telemed.utils.Util;

@Data
@EqualsAndHashCode(callSuper = false)
public class BloodPressureTestDeviceNode extends DeviceNode {

    @SuppressWarnings("unused")
    private static final String TAG = Util.getTag(BloodPressureTestDeviceNode.class);

    @Expose
    private Variable<Float> dia;
    @Expose
    private Variable<Float> sys;
    @Expose
    private Variable<Float> puls;

    public BloodPressureTestDeviceNode(Questionnaire questionnaire, String nodeName) {
        super(questionnaire, nodeName);
    }

    @Override
    public void enter() {
        super.enter();
        questionnaire.setCurrentNode(getNextNode());
    }

    @Override
    public void linkNodes(Map<String, Node> map) throws UnknownNodeException {
        super.linkNodes(map);
    }

    @Override
    public void linkVariables(Map<String, Variable<?>> variablePool) throws VariableLinkFailedException {
        super.linkVariables(variablePool);
        dia = Util.linkVariable(variablePool, dia);
        sys = Util.linkVariable(variablePool, sys);
        puls = Util.linkVariable(variablePool, puls);
    }

    @Override
    public void deviceLeave() {
        dia.setValue(new Constant<Float>(Util.random(110, 170)));
        sys.setValue(new Constant<Float>(Util.random(50, 90)));
        puls.setValue(new Constant<Float>(Util.random(40, 170)));
    }
}
