package dk.silverbullet.telemed.questionnaire.node;

import java.util.Map;

import dk.silverbullet.telemed.questionnaire.Questionnaire;
import dk.silverbullet.telemed.questionnaire.expression.Constant;
import dk.silverbullet.telemed.questionnaire.expression.Variable;
import dk.silverbullet.telemed.questionnaire.expression.VariableLinkFailedException;
import dk.silverbullet.telemed.utils.Util;

public class MonicaTestDeviceNode extends MonicaDeviceNode {

    @SuppressWarnings("unused")
    private static final String TAG = Util.getTag(MonicaTestDeviceNode.class);

    public MonicaTestDeviceNode(Questionnaire questionnaire, String nodeName) {
        super(questionnaire, nodeName);
    }

    @Override
    public void enter() {
        super.setRunAsSimulator(new Constant<Boolean>(true));
        super.enter();
    }

    @Override
    public void linkVariables(Map<String, Variable<?>> variablePool) throws VariableLinkFailedException {
        super.linkVariables(variablePool);
    }
}
