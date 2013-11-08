package dk.silverbullet.telemed.questionnaire.node;

import com.google.gson.Gson;
import dk.silverbullet.telemed.questionnaire.Questionnaire;
import dk.silverbullet.telemed.questionnaire.element.ButtonElement;
import dk.silverbullet.telemed.questionnaire.element.TextViewElement;
import dk.silverbullet.telemed.questionnaire.expression.Variable;
import dk.silverbullet.telemed.questionnaire.expression.VariableLinkFailedException;
import dk.silverbullet.telemed.rest.PostMessageTask;
import dk.silverbullet.telemed.rest.RetrieveTask;
import dk.silverbullet.telemed.rest.bean.message.MessageWrite;
import dk.silverbullet.telemed.rest.listener.MessageWriteListener;
import dk.silverbullet.telemed.utils.Util;

import java.util.Map;

public class MessageSendNode extends IONode implements MessageWriteListener {
    private Node next;
    private String screenText = "Indsender besvarelser - vent venligst...";

    private Variable<Long> departmentId;
    private Variable<String> title;
    private Variable<String> text;
    private boolean enabled;

    public MessageSendNode(Questionnaire questionnaire, String nodeName) {
        super(questionnaire, nodeName);
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

        RetrieveTask retrieveFeedTask = new PostMessageTask(questionnaire, this);
        retrieveFeedTask.execute(new Gson().toJson(messageWrite));

        super.enter();
    }

    public void setView() {
        clearElements();

        TextViewElement tve = new TextViewElement(this);
        tve.setText(screenText);
        addElement(tve);

        ButtonElement be = new ButtonElement(this);
        be.setText("OK");
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
    public void sendError() {
        screenText = "Fejl ved kommunikation med serveren";

        enabled = true;
        setView();
        createView();
    }

    @Override
    public void setRecipients(String result) {
        // Not used here...
    }

    @Override
    public void end(String result) {
        if ("200".equals(result)) {
            screenText = "Beskeden er nu afsendt.";
            // departmentId.setValue((Long)null); // TODO ??
            title.setValue("");
            text.setValue("");
        } else
            screenText = "Fejl ved afsendelse af besked!";

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
