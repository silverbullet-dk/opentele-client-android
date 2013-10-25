package dk.silverbullet.telemed.questionnaire.element;

import java.util.Map;

import android.view.View;
import dk.silverbullet.telemed.questionnaire.Questionnaire;
import dk.silverbullet.telemed.questionnaire.expression.Variable;
import dk.silverbullet.telemed.questionnaire.expression.VariableLinkFailedException;
import dk.silverbullet.telemed.questionnaire.node.IONode;
import dk.silverbullet.telemed.questionnaire.node.Node;
import dk.silverbullet.telemed.questionnaire.node.UnknownNodeException;

public abstract class Element {

    public static int MARGIN = 15;
    public static int TEXTSIZE = 36;

    protected IONode node;

    public Element(IONode node) {
        setNode(node);
    }

    public void setNode(IONode node) {
        this.node = node;
    }

    public Questionnaire getQuestionnaire() {
        return node.getQuestionnaire();
    }

    public abstract View getView();

    public abstract void leave();

    public abstract void linkNodes(Map<String, Node> map) throws UnknownNodeException;

    public abstract void linkVariables(Map<String, Variable<?>> map) throws VariableLinkFailedException;

    public abstract boolean validates();
}
