package dk.silverbullet.telemed.questionnaire.node;

import java.util.Map;

import lombok.Data;
import lombok.EqualsAndHashCode;
import dk.silverbullet.telemed.questionnaire.Questionnaire;
import dk.silverbullet.telemed.questionnaire.element.ButtonElement;
import dk.silverbullet.telemed.questionnaire.element.EditTextElement;
import dk.silverbullet.telemed.questionnaire.element.TextViewElement;
import dk.silverbullet.telemed.questionnaire.expression.Variable;
import dk.silverbullet.telemed.questionnaire.expression.VariableLinkFailedException;
import dk.silverbullet.telemed.utils.Util;

@Data
@EqualsAndHashCode(callSuper = false)
public class SetServerIpNode extends IONode {
    private Variable<String> serverIP;
    private Node nextNode;

    public SetServerIpNode(Questionnaire questionnaire, String nodeName) {
        super(questionnaire, nodeName);
    }

    @Override
    public void enter() {
        clearElements();

        TextViewElement tve = new TextViewElement(this);
        tve.setText("Set server-ip");
        addElement(tve);

        EditTextElement ete = new EditTextElement(this);
        ete.setOutputVariable(serverIP);
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
        serverIP = Util.linkVariable(map, serverIP);

        super.linkVariables(map);
    }

    @Override
    public String toString() {
        return "SetServerIpNode";
    }
}
