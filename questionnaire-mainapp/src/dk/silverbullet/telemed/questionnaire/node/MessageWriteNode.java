package dk.silverbullet.telemed.questionnaire.node;

import android.app.ProgressDialog;
import dk.silverbullet.telemed.questionnaire.Questionnaire;
import dk.silverbullet.telemed.questionnaire.element.ButtonElement;
import dk.silverbullet.telemed.questionnaire.element.EditTextElement;
import dk.silverbullet.telemed.questionnaire.element.TextViewElement;
import dk.silverbullet.telemed.questionnaire.expression.Variable;
import dk.silverbullet.telemed.questionnaire.expression.VariableLinkFailedException;
import dk.silverbullet.telemed.utils.Util;

import java.util.Map;

public class MessageWriteNode extends IONode {
    private MessageSendNode writeMessageNode;

    private Variable<String> userId;
    private Variable<Long> departmentId;
    private Variable<String> title;
    private Variable<String> text;

    private ProgressDialog dialog;
    private EditTextElement ete;

    public MessageWriteNode(Questionnaire questionnaire, String nodeName) {
        super(questionnaire, nodeName);
    }

    @Override
    public void enter() {
        setView();
        super.enter();
    }

    public void setView() {
        clearElements();

        addElement(new TextViewElement(this, "Opret ny besked"));

        // addElement(new TextViewElement(this, "Til afdelingen")); // TODO indsæt

        addElement(new TextViewElement(this, "Overskrift"));

        EditTextElement ete2 = new EditTextElement(this);
        ete2.setOutputVariable(title);
        addElement(ete2);

        addElement(new TextViewElement(this, "Besked"));

        EditTextElement ete3 = new EditTextElement(this);
        ete3.setForMessageBody(true);
        ete3.setOutputVariable(text);
        addElement(ete3);

        addElement(new ButtonElement(this, "Send", writeMessageNode));
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
    public void linkVariables(Map<String, Variable<?>> variablePool) throws VariableLinkFailedException {
        super.linkVariables(variablePool);
        userId = Util.linkVariable(variablePool, userId);
        departmentId = Util.linkVariable(variablePool, departmentId);
        title = Util.linkVariable(variablePool, title);
        text = Util.linkVariable(variablePool, text);
    }

    public void setUserId(Variable<String> userId) {
        this.userId = userId;
    }

    public void setDepartmentId(Variable<Long> departmentId) {
        this.departmentId = departmentId;
    }

    public void setTitle(Variable<String> title) {
        this.title = title;
    }

    public void setText(Variable<String> text) {
        this.text = text;
    }

    public void setWriteMessageNode(MessageSendNode writeMessageNode) {
        this.writeMessageNode = writeMessageNode;
    }

}
