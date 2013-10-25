package dk.silverbullet.telemed.questionnaire.node;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;

import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import android.app.ProgressDialog;
import android.util.Log;

import com.google.gson.Gson;

import dk.silverbullet.telemed.questionnaire.Questionnaire;
import dk.silverbullet.telemed.questionnaire.element.ListViewElement;
import dk.silverbullet.telemed.questionnaire.element.TextViewElement;
import dk.silverbullet.telemed.questionnaire.expression.Variable;
import dk.silverbullet.telemed.questionnaire.expression.VariableLinkFailedException;
import dk.silverbullet.telemed.rest.RetrieveMessageListTask;
import dk.silverbullet.telemed.rest.RetrieveRecipientsTask;
import dk.silverbullet.telemed.rest.bean.message.MessageItem;
import dk.silverbullet.telemed.rest.bean.message.MessagePerson;
import dk.silverbullet.telemed.rest.bean.message.MessageRecipient;
import dk.silverbullet.telemed.rest.listener.MessageListListener;
import dk.silverbullet.telemed.rest.listener.MessageWriteListener;
import dk.silverbullet.telemed.utils.Util;

@Data
@EqualsAndHashCode(callSuper = false)
public class MessageListNode extends IONode implements MessageListListener, MessageWriteListener {

    private static final String TAG = Util.getTag(MessageListNode.class);

    @Getter(AccessLevel.PUBLIC)
    @Setter(AccessLevel.PUBLIC)
    private Node readMessagesNode;

    @Getter(AccessLevel.PRIVATE)
    @Setter(AccessLevel.PRIVATE)
    private Map<Long, String> departmentNameMap;

    @Getter(AccessLevel.PRIVATE)
    @Setter(AccessLevel.PRIVATE)
    private Map<Long, Integer> departmentMessageCountMap;

    @Getter(AccessLevel.PRIVATE)
    @Setter(AccessLevel.PRIVATE)
    private ProgressDialog dialog;

    private Variable<Long> departmentId;
    @SuppressWarnings("rawtypes")
    private Variable<Map> departmentNameMap2;

    public MessageListNode(Questionnaire questionnaire, String nodeName) {
        super(questionnaire, nodeName);
    }

    @Override
    public void enter() {
        hideBackButton();
        if (null != departmentMessageCountMap) {
            departmentMessageCountMap.clear();
        }
        if (null != departmentNameMap) {
            departmentNameMap.clear();
        }

        setView();
        dialog = ProgressDialog.show(questionnaire.getActivity(), "Henter beskeder", "Vent venligst...", true);

        new RetrieveRecipientsTask(questionnaire, this).execute();

        super.enter();
    }

    public void setView() {
        clearElements();

        TextViewElement tve = new TextViewElement(this, "Beskeder");
        addElement(tve);
        Log.d(TAG, "questionnaire.getUserId(): " + questionnaire.getUserId());
        Log.d(TAG, "questionnaire.getFullName(): " + questionnaire.getFullName());

        if (departmentNameMap != null) {
            ListViewElement<Long> lve = new ListViewElement<Long>(this);
            String[] menu = new String[departmentNameMap.size()];
            Long[] res = new Long[departmentNameMap.size()];
            LinkedList<String> highLight = new LinkedList<String>();
            int i = 0;
            for (Long departmentId : departmentNameMap.keySet()) {
                if (departmentMessageCountMap.get(departmentId) > 0) {
                    menu[i] = departmentNameMap.get(departmentId) + " (" + departmentMessageCountMap.get(departmentId)
                            + ")";
                    highLight.add(menu[i]);
                } else
                    menu[i] = departmentNameMap.get(departmentId);
                res[i] = departmentId;
                i++;

            }

            lve.setValues(menu);
            lve.setResults(res);
            lve.setValuesToHighlight(highLight.toArray(new String[0]));
            lve.setVariable(departmentId);
            lve.setNextNode(readMessagesNode);
            addElement(lve);
        }
    }

    @Override
    public void leave() {
        super.leave();
        Util.saveVariables(questionnaire);
    }

    @Override
    public void linkVariables(Map<String, Variable<?>> variablePool) throws VariableLinkFailedException {
        super.linkVariables(variablePool);
        Util.linkVariable(variablePool, departmentId);
        Util.linkVariable(variablePool, departmentNameMap2);
    }

    @Override
    public void sendError() {
        dialog.dismiss();
    }

    @Override
    public void end(String result) {
        if (null != result && !"".equals(result)) {
            Log.d(TAG, result);
            for (MessageItem msg : new Gson().fromJson(result, MessageItem[].class)) {
                if (null == msg.getResult() && null == msg.getUnread()) {
                    MessagePerson from = msg.getFrom();
                    if (questionnaire.getUserId() != from.getId() && !msg.isRead()) {
                        Long key = from.getId();
                        if (departmentMessageCountMap.containsKey(key)) {
                            departmentMessageCountMap.put(key, departmentMessageCountMap.get(key) + 1);
                        }
                    }
                }
            }
        }
        setView();
        createView();
        dialog.dismiss();
    }

    @Override
    public void setRecipients(String result) {
        if (null != result && !"".equals(result)) {

            departmentNameMap = new LinkedHashMap<Long, String>();
            departmentMessageCountMap = new HashMap<Long, Integer>();

            MessageRecipient[] messageRecipients = new Gson().fromJson(result, MessageRecipient[].class);

            for (MessageRecipient mc : messageRecipients) {
                departmentNameMap.put(mc.getId(), mc.getName());
                departmentMessageCountMap.put(mc.getId(), 0);
            }
            if (messageRecipients.length == 1) {
                departmentId.setValue(messageRecipients[0].getId());
                questionnaire.chainToNextIONode();
                questionnaire.setCurrentNode(readMessagesNode);
                dialog.dismiss();
            } else {
                new RetrieveMessageListTask(questionnaire, this).execute();
            }

            departmentNameMap2.setValue(departmentNameMap);
        }
    }
}
