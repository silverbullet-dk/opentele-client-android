package dk.silverbullet.telemed.questionnaire.node;

import java.util.Map;

import lombok.Data;
import lombok.EqualsAndHashCode;
import dk.silverbullet.telemed.questionnaire.Questionnaire;
import dk.silverbullet.telemed.questionnaire.element.ButtonElement;
import dk.silverbullet.telemed.questionnaire.element.CheckBoxElement;
import dk.silverbullet.telemed.questionnaire.element.TextViewElement;
import dk.silverbullet.telemed.questionnaire.expression.Variable;
import dk.silverbullet.telemed.questionnaire.expression.VariableLinkFailedException;
import dk.silverbullet.telemed.utils.Util;

@Data
@EqualsAndHashCode(callSuper = false)
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
        tve.setText("Skal upload-debug-noden vises?");
        addElement(tve);

        CheckBoxElement ete = new CheckBoxElement(this);
        ete.setOutputVariable(showUploadDebugNode);
        ete.setText("Vis data");
        addElement(ete);

        ButtonElement be = new ButtonElement(this);
        be.setNextNode(nextNode);
        be.setText("OK");
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
}
