package dk.silverbullet.telemed.questionnaire.node;

import dk.silverbullet.telemed.questionnaire.Questionnaire;
import dk.silverbullet.telemed.questionnaire.R;
import dk.silverbullet.telemed.questionnaire.element.ButtonElement;
import dk.silverbullet.telemed.questionnaire.element.TextViewElement;
import dk.silverbullet.telemed.questionnaire.expression.Variable;
import dk.silverbullet.telemed.questionnaire.expression.VariableLinkFailedException;
import dk.silverbullet.telemed.rest.Resources;
import dk.silverbullet.telemed.rest.bean.message.MessageWrite;
import dk.silverbullet.telemed.rest.listener.PostEntityListener;
import dk.silverbullet.telemed.utils.Util;

import java.util.Map;

public class MessageSendNode extends IONode implements PostEntityListener {
    private Node next;
    private String screenText;

    private Variable<Long> departmentId;
    private Variable<String> title;
    private Variable<String> text;
    private boolean enabled;

    public MessageSendNode(Questionnaire questionnaire, String nodeName) {
        super(questionnaire, nodeName);
        screenText = Util.getString(R.string.message_sending, questionnaire);
    }

    @Override
    public void enter() {
        setView();

        MessageWrite messageWrite = new MessageWrite();
        Long userId = (Long) questionnaire.getValuePool().get(Util.VARIABLE_USER_ID).getExpressionValue().getValue();

        messageWrite.setUserId(userId);
        messageWrite.setDepartmentId(departmentId.getExpressionValue().getValue());
        messageWrite.setTitle(title.getExpressionValue().getValue());
        messageWrite.setText(text.getExpressionValue().getValue());

        Resources.postMessage(messageWrite, questionnaire, this);

        super.enter();
    }

    public void setView() {
        clearElements();

        TextViewElement tve = new TextViewElement(this);
        tve.setText(screenText);
        addElement(tve);

        ButtonElement be = new ButtonElement(this);
        be.setText(Util.getString(R.string.default_ok, questionnaire));
        be.setNextNode(next);
        if (enabled) {
            addElement(be);
        }
    }

    @Override
    public void leave() {
        super.leave();
    }

    @Override
    public void linkNodes(Map<String, Node> map) throws UnknownNodeException {
        super.linkNodes(map);
    }

    @Override
    public void linkVariables(Map<String, Variable<?>> variablePool) throws VariableLinkFailedException {
        super.linkVariables(variablePool);
        departmentId = Util.linkVariable(variablePool, departmentId);
        title = Util.linkVariable(variablePool, title);
        text = Util.linkVariable(variablePool, text);
    }

    @Override
    public String toString() {
        return "LoginNode";
    }

    @Override
    public void postError() {
        screenText = Util.getString(R.string.default_server_communication_error, questionnaire);

        enabled = true;
        setView();
        createView();
    }

    @Override
    public void posted() {
        screenText = Util.getString(R.string.message_message_sent, questionnaire);
        // departmentId.setValue((Long)null); // TODO ??
        title.setValue("");
        text.setValue("");

        enabled = true;
        setView();
        createView();
    }

    public void setNext(Node next) {
        this.next = next;
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
}
