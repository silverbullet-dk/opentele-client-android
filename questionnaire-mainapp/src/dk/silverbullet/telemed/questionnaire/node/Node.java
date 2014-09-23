package dk.silverbullet.telemed.questionnaire.node;

import com.google.gson.annotations.Expose;
import dk.silverbullet.telemed.questionnaire.Questionnaire;
import dk.silverbullet.telemed.questionnaire.expression.Variable;
import dk.silverbullet.telemed.questionnaire.expression.VariableLinkFailedException;

import java.util.Map;

public abstract class Node {
    @Expose
    private String nodeName;
    protected Questionnaire questionnaire;

    public Node(Questionnaire questionnaire, String nodeName) {
        this.questionnaire = questionnaire;
        this.nodeName = nodeName;
    }

    public abstract void enter();

    public abstract void leave();

    public abstract void linkNodes(Map<String, Node> map) throws UnknownNodeException;

    public abstract void linkVariables(Map<String, Variable<?>> map) throws VariableLinkFailedException;

    public String toString() {
        return "Node..:" + nodeName;
    }

    public String getNodeName() {
        return nodeName;
    }

    public Questionnaire getQuestionnaire() {
        return questionnaire;
    }

    public void setQuestionnaire(Questionnaire questionnaire) {
        this.questionnaire = questionnaire;
    }


}
