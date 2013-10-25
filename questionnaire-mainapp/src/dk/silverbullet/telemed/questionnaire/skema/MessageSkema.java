package dk.silverbullet.telemed.questionnaire.skema;

import java.util.Map;

import android.util.Log;
import dk.silverbullet.telemed.questionnaire.Questionnaire;
import dk.silverbullet.telemed.questionnaire.expression.Variable;
import dk.silverbullet.telemed.questionnaire.node.EndNode;
import dk.silverbullet.telemed.questionnaire.node.MessageDialogNode;
import dk.silverbullet.telemed.questionnaire.node.MessageListNode;
import dk.silverbullet.telemed.questionnaire.node.MessageSendNode;
import dk.silverbullet.telemed.questionnaire.node.MessageWriteNode;
import dk.silverbullet.telemed.questionnaire.node.UnknownNodeException;
import dk.silverbullet.telemed.questionnaire.output.OutputSkema;
import dk.silverbullet.telemed.utils.Util;

public class MessageSkema implements SkemaDef {

    private static final String TAG = Util.getTag(MessageSkema.class);

    @Override
    public Skema getSkema(Questionnaire questionnaire) {

        Variable<String> readMessageId = new Variable<String>("readMessageId", String.class);
        Variable<String> userId = new Variable<String>("userId", String.class);
        Variable<Long> departmentId = new Variable<Long>("departmentId", Long.class);
        @SuppressWarnings("rawtypes")
        Variable<Map> departmentNameMap = new Variable<Map>("departmentNameMap", Map.class);
        Variable<String> title = new Variable<String>("title", String.class);
        Variable<String> text = new Variable<String>("text", String.class);

        OutputSkema outputSkema = new OutputSkema();
        outputSkema.addVariable(readMessageId);
        outputSkema.addVariable(departmentId);
        outputSkema.addVariable(departmentNameMap);

        for (Variable<?> output : outputSkema.getOutput()) {
            questionnaire.addVariable(output);
        }

        // ////////////////////////////////////////////////////////////////////////////////

        EndNode end = new EndNode(questionnaire, "End");

        MessageSendNode messageSendNode = new MessageSendNode(questionnaire, "messageSendNode");
        messageSendNode.setDepartmentId(departmentId);
        messageSendNode.setTitle(title);
        messageSendNode.setText(text);
        messageSendNode.setHideTopPanel(true);

        MessageWriteNode messageWriteNode = new MessageWriteNode(questionnaire, "messageWriteNode");
        messageWriteNode.setUserId(userId);
        messageWriteNode.setDepartmentId(departmentId);
        messageWriteNode.setTitle(title);
        messageWriteNode.setText(text);
        messageWriteNode.setWriteMessageNode(messageSendNode);

        MessageDialogNode messageDialogNode = new MessageDialogNode(questionnaire, "MessageDialogNode");
        messageDialogNode.setDepartmentId(departmentId);
        messageDialogNode.setDepartmentNameMap(departmentNameMap);
        messageDialogNode.setNewMessageNode(messageWriteNode);

        MessageListNode messageListNode = new MessageListNode(questionnaire, "MessageListNode");
        messageListNode.setDepartmentId(departmentId);
        messageListNode.setDepartmentNameMap2(departmentNameMap);
        messageListNode.setReadMessagesNode(messageDialogNode);
        messageSendNode.setNext(messageListNode);

        Skema skema = new Skema();
        skema.setCron(null);
        skema.setEndNode(end.getNodeName());
        skema.setName("MESSAGE");
        skema.setStartNode(messageListNode.getNodeName());
        skema.setVersion("1.0");

        skema.addNode(end);
        skema.addNode(messageListNode);

        try {
            skema.link();
        } catch (UnknownNodeException e) {
            Log.e(TAG, "Got Exception", e);
        }

        return skema;
    }
}
