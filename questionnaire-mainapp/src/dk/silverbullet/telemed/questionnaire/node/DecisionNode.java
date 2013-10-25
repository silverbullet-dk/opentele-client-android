package dk.silverbullet.telemed.questionnaire.node;

import java.util.Map;

import lombok.Data;
import lombok.EqualsAndHashCode;
import android.util.Log;

import com.google.gson.annotations.Expose;

import dk.silverbullet.telemed.questionnaire.Questionnaire;
import dk.silverbullet.telemed.questionnaire.expression.Expression;
import dk.silverbullet.telemed.questionnaire.expression.UnknownVariableException;
import dk.silverbullet.telemed.questionnaire.expression.Variable;
import dk.silverbullet.telemed.questionnaire.expression.VariableLinkFailedException;
import dk.silverbullet.telemed.utils.Util;

@Data
@EqualsAndHashCode(callSuper = false)
public class DecisionNode extends Node {

    private static final String TAG = Util.getTag(DecisionNode.class);

    @Expose
    private String next;
    private Node nextNode;

    @Expose
    private Expression<Boolean> expression;

    @Expose
    private String nextFalse;
    private Node nextFalseNode;

    public DecisionNode(Questionnaire questionnaire, String nodeName, Expression<Boolean> expression) {
        super(questionnaire, nodeName);
        this.expression = expression;
    }

    public DecisionNode(Questionnaire questionnaire, String nodeName, Expression<Boolean> expression, Node nextTrue,
            Node nextFalse) {

        this(questionnaire, nodeName, expression);
        setNextNode(nextTrue);
        setNextFalseNode(nextFalse);
    }

    public void enter() {
        Log.d(TAG, "nodeName...:" + getNodeName());
        if (expression.evaluate()) {
            questionnaire.setCurrentNode(getNextNode());
        } else {
            questionnaire.setCurrentNode(nextFalseNode);
        }
    }

    @Override
    public void leave() {
    }

    @Override
    public void linkNodes(Map<String, Node> map) {
        nextNode = map.get(next);
        nextFalseNode = map.get(nextFalse);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void linkVariables(Map<String, Variable<?>> variablePool) throws VariableLinkFailedException {
        if (expression instanceof Variable) {
            String name = ((Variable<Boolean>) expression).getName();
            if (variablePool.containsKey(name))
                expression = (Expression<Boolean>) variablePool.get(name);
            else
                throw new UnknownVariableException(name);
        } else
            expression.link(variablePool);
    }

    @Override
    public String toString() {
        return "DecisionNode(\"" + getNodeName() + "\") T->\"" + getNextNode().getNodeName() + "\" F->\""
                + getNextFalseNode().getNodeName() + "\"";
    }
}
