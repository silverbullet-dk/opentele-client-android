package dk.silverbullet.telemed.questionnaire.node;

import dk.silverbullet.telemed.questionnaire.Questionnaire;
import dk.silverbullet.telemed.questionnaire.R;
import dk.silverbullet.telemed.questionnaire.element.ButtonElement;
import dk.silverbullet.telemed.questionnaire.element.CheckBoxElement;
import dk.silverbullet.telemed.questionnaire.element.TextViewElement;
import dk.silverbullet.telemed.questionnaire.expression.Variable;
import dk.silverbullet.telemed.questionnaire.expression.VariableLinkFailedException;
import dk.silverbullet.telemed.utils.Util;

import java.util.Map;

public class SetShowUploadDebugNode extends IONode {
    private Variable<Boolean> showUploadDebugNode;
    private Node nextNode;

    public SetShowUploadDebugNode(Questionnaire questionnaire, String nodeName) {
        super(questionnaire, nodeName);
    }

    @Override
    public void enter() {
        clearElements();

        TextViewElement tve = new TextViewElement(this);
        tve.setText(Util.getString(R.string.set_show_upload_debug_show_debug, questionnaire));
        addElement(tve);

        CheckBoxElement ete = new CheckBoxElement(this);
        ete.setOutputVariable(showUploadDebugNode);
        ete.setText(Util.getString(R.string.set_show_upload_debug_show_data, questionnaire));
        addElement(ete);

        ButtonElement be = new ButtonElement(this);
        be.setNextNode(nextNode);
        be.setText(Util.getString(R.string.default_ok, questionnaire));
        addElement(be);

        super.enter();
    }

    @Override
    public void leave() {
        super.leave();
        Util.saveVariables(questionnaire);
    }

    @Override
    public void linkNodes(Map<String, Node> map) {
    }

    @Override
    public void linkVariables(Map<String, Variable<?>> map) throws VariableLinkFailedException {
        showUploadDebugNode = Util.linkVariable(map, showUploadDebugNode);

        super.linkVariables(map);
    }

    @Override
    public String toString() {
        return "SetShowUploadDebugNode";
    }

    public void setShowUploadDebugNode(Variable<Boolean> showUploadDebugNode) {
        this.showUploadDebugNode = showUploadDebugNode;
    }

    public void setNextNode(Node nextNode) {
        this.nextNode = nextNode;
    }
}
