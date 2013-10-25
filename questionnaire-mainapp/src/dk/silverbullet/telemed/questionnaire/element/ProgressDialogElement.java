package dk.silverbullet.telemed.questionnaire.element;

import java.util.Map;

import android.view.View;
import dk.silverbullet.telemed.questionnaire.expression.Variable;
import dk.silverbullet.telemed.questionnaire.expression.VariableLinkFailedException;
import dk.silverbullet.telemed.questionnaire.node.IONode;
import dk.silverbullet.telemed.questionnaire.node.Node;
import dk.silverbullet.telemed.questionnaire.node.UnknownNodeException;

public class ProgressDialogElement extends Element {
    public ProgressDialogElement(IONode node) {
        super(node);
    }

    @Override
    public View getView() {
        return null;
    }

    @Override
    public void leave() {
        // Nothing to do
    }

    @Override
    public void linkNodes(Map<String, Node> map) throws UnknownNodeException {
        // Nothing to do
    }

    @Override
    public void linkVariables(Map<String, Variable<?>> map) throws VariableLinkFailedException {
        // Nothing to do
    }

    @Override
    public boolean validates() {
        return false;
    }
}
