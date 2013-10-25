package dk.silverbullet.telemed.questionnaire.node;

import java.util.Map;

import lombok.Data;
import lombok.EqualsAndHashCode;
import android.util.Log;
import dk.silverbullet.telemed.questionnaire.Questionnaire;
import dk.silverbullet.telemed.questionnaire.expression.UnknownVariableException;
import dk.silverbullet.telemed.questionnaire.expression.Variable;
import dk.silverbullet.telemed.utils.Util;

@Data
@EqualsAndHashCode(callSuper = false)
public class EndNode extends Node {

    private static final String TAG = Util.getTag(EndNode.class);

    private Node nextNode;

    public EndNode(Questionnaire questionnaire, String nodeName) {
        super(questionnaire, nodeName);
    }

    @Override
    public void enter() {
        Log.d(TAG, "The End!");
        questionnaire.setCurrentNode(getNextNode());
    }

    @Override
    public void leave() {
    }

    @Override
    public void linkNodes(Map<String, Node> map) {
    }

    @Override
    public void linkVariables(Map<String, Variable<?>> variablePool) throws UnknownVariableException {
        // Done1
    }

    @Override
    public String toString() {
        return "EndNode(\"" + getNodeName() + "\")";
    }
}
