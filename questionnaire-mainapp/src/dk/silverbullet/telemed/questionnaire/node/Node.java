package dk.silverbullet.telemed.questionnaire.node;

import java.util.Map;

import lombok.Data;
import lombok.Setter;

import com.google.gson.annotations.Expose;

import dk.silverbullet.telemed.questionnaire.Questionnaire;
import dk.silverbullet.telemed.questionnaire.expression.Variable;
import dk.silverbullet.telemed.questionnaire.expression.VariableLinkFailedException;

@Data
public abstract class Node {

    @Expose
    private String nodeName;

    @Setter
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
}
