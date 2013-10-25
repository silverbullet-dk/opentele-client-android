package dk.silverbullet.telemed.questionnaire.node;

import java.util.Map;

import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import android.util.Log;
import dk.silverbullet.telemed.questionnaire.Questionnaire;
import dk.silverbullet.telemed.questionnaire.expression.UnknownVariableException;
import dk.silverbullet.telemed.questionnaire.expression.Variable;
import dk.silverbullet.telemed.questionnaire.expression.VariableLinkFailedException;
import dk.silverbullet.telemed.questionnaire.skema.Skema;
import dk.silverbullet.telemed.questionnaire.skema.SkemaDef;
import dk.silverbullet.telemed.utils.Util;

@Data
@EqualsAndHashCode(callSuper = false)
public class IOMenuNode2 extends Node {

    private static final String TAG = Util.getTag(IOMenuNode2.class);

    private Node nextNode;
    private Variable<String> menu;

    @Getter(AccessLevel.PRIVATE)
    @Setter(AccessLevel.PRIVATE)
    private Skema skema;

    public IOMenuNode2(Questionnaire questionnaire, String nodeName) {
        super(questionnaire, nodeName);
    }

    @Override
    public void enter() {
        Log.d(TAG, "Enter....");
        Log.d(TAG, "menu...:" + menu);
        questionnaire.cleanSkemaValuePool();

        try {
            getLocal(menu);
            setup();
        } catch (Exception e) {
            Log.e(TAG, "", e);
            ErrorNode errorNode = new ErrorNode(questionnaire, "errorNode");
            errorNode.setNextNode(nextNode);
            questionnaire.setCurrentNode(errorNode);
        }
    }

    public void getLocal(Variable<?> variable) throws ClassNotFoundException, InstantiationException,
            IllegalAccessException, UnknownNodeException, VariableLinkFailedException {
        Class<?> c = null;
        c = Class.forName(variable.evaluate().toString());
        SkemaDef ts = (SkemaDef) c.newInstance();
        skema = ts.getSkema(questionnaire);
    }

    public void setup() throws UnknownNodeException, VariableLinkFailedException {
        setupSkema();
        skema.getEndNodeNode().setNextNode(nextNode);
        questionnaire.setCurrentNode(skema.getStartNodeNode());
    }

    public void setupSkema() throws UnknownNodeException, VariableLinkFailedException {
        skema.setQuestionnaire(questionnaire);
        skema.link();
        for (Variable<?> output : skema.getOutput())
            questionnaire.addSkemaVariable(output);

        for (Node node : skema.getNodes()) {
            node.linkVariables(questionnaire.getValuePool());
        }
        questionnaire.setStartNode(skema.getStartNodeNode());
    }

    @Override
    public void leave() {
        // Nothing to do
    }

    @Override
    public void linkNodes(Map<String, Node> map) {
        // Nothing to do
    }

    @Override
    public void linkVariables(Map<String, Variable<?>> map) throws UnknownVariableException {
        // Nothing to do
    }
}
