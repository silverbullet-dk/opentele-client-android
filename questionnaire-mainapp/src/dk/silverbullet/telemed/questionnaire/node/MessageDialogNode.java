package dk.silverbullet.telemed.questionnaire.node;

import android.app.ProgressDialog;
import android.util.Log;
import dk.silverbullet.telemed.questionnaire.Questionnaire;
import dk.silverbullet.telemed.questionnaire.R;
import dk.silverbullet.telemed.questionnaire.element.ButtonElement;
import dk.silverbullet.telemed.questionnaire.element.ClinicMessageBubbleElement;
import dk.silverbullet.telemed.questionnaire.element.PatientMessageBubbleElement;
import dk.silverbullet.telemed.questionnaire.element.TextViewElement;
import dk.silverbullet.telemed.questionnaire.expression.Variable;
import dk.silverbullet.telemed.questionnaire.expression.VariableLinkFailedException;
import dk.silverbullet.telemed.rest.Resources;
import dk.silverbullet.telemed.rest.listener.RetrieveEntityListener;
import dk.silverbullet.telemed.rest.tasks.MarkMessagesAsReadTask;
import dk.silverbullet.telemed.rest.bean.message.MessageItem;
import dk.silverbullet.telemed.rest.bean.message.Messages;
import dk.silverbullet.telemed.utils.Util;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class MessageDialogNode extends IONode implements RetrieveEntityListener<Messages> {

    private static final String TAG = Util.getTag(MessageDialogNode.class);

    private Node newMessageNode;

    private Variable<Long> departmentId;
    @SuppressWarnings("rawtypes")
    private Variable<Map> departmentNameMap;
    private LinkedList<MessageItem> messages;
    private ProgressDialog dialog;

    public MessageDialogNode(Questionnaire questionnaire, String nodeName) {
        super(questionnaire, nodeName);
    }

    @Override
    public void enter() {
        if (null != messages) {
            messages.clear();
            clearElements();
        }

        dialog = ProgressDialog.show(questionnaire.getContext(), Util.getString(R.string.message_fetching, questionnaire), Util.getString(R.string.default_please_wait, questionnaire), true);

        Resources.getMessages(questionnaire, this);

        super.enter();
    }

    public void setView() {
        clearElements();
        String departmentName = (String) departmentNameMap.getExpressionValue().getValue()
                .get(departmentId.getExpressionValue().getValue());
        Log.d(TAG, "questionnaire.getUserId(): " + questionnaire.getUserId());
        Log.d(TAG, "questionnaire.getFullName(): " + questionnaire.getFullName());

        if (messages != null && messages.size() > 0) {
            addElement(new TextViewElement(this, departmentName, false));
            for (MessageItem msg : messages) {
                boolean isFromPatient = msg.getFrom().getType().equals("Patient");

                if (isFromPatient) {
                    Log.d(TAG, "Adding PatientMessageBubbleElement");
                    addElement(new PatientMessageBubbleElement(this, msg));
                } else {
                    Log.d(TAG, "Adding ClinicMessageBubbleElement");
                    addElement(new ClinicMessageBubbleElement(this, msg));
                }
            }
        } else {
            addElement(new TextViewElement(this, departmentName, false));
            addElement(new TextViewElement(this, Util.getString(R.string.message_read_write, questionnaire)));
            addElement(new TextViewElement(this, Util.getString(R.string.message_write_instructions, questionnaire)));
        }

        ButtonElement be = new ButtonElement(this);
        be.setNextNode(newMessageNode);
        be.setText(Util.getString(R.string.message_new, questionnaire));
        addElement(be);
    }

    @Override
    public void leave() {
        super.leave();
        Util.saveVariables(questionnaire);
    }

    @Override
    public void linkVariables(Map<String, Variable<?>> map) throws VariableLinkFailedException {
        super.linkVariables(map);
        Util.linkVariable(map, departmentId);
        Util.linkVariable(map, departmentNameMap);
    }

    @Override
    public String toString() {
        return "MessageListNode";
    }

    @Override
    public void retrieveError() {
        dialog.dismiss();
    }

    @Override
    public void retrieved(Messages result) {
        messages = new LinkedList<MessageItem>();
        final List<Long> messagesToRead = new LinkedList<Long>();

        long departmentId = this.departmentId.getExpressionValue().getValue();
        for (MessageItem message : result.messages) {
            boolean isFromDepartment = message.getFrom().getType().equals("Department") && message.getFrom().getId() == departmentId;
            boolean isToDepartment = message.getTo().getType().equals("Department") && message.getTo().getId() == departmentId;

            if (isFromDepartment || isToDepartment) {
                messages.add(0, message);
            }
            if (isFromDepartment && !message.isRead()) {
                messagesToRead.add(message.getId());
            }
        }

        new MarkMessagesAsReadTask(getQuestionnaire()).execute(messagesToRead.toArray(new Long[0]));

        setView();
        createView();
        dialog.dismiss();
    }

    public void setNewMessageNode(Node newMessageNode) {
        this.newMessageNode = newMessageNode;
    }

    public void setDepartmentId(Variable<Long> departmentId) {
        this.departmentId = departmentId;
    }

    public void setDepartmentNameMap(Variable<Map> departmentNameMap) {
        this.departmentNameMap = departmentNameMap;
    }
}
