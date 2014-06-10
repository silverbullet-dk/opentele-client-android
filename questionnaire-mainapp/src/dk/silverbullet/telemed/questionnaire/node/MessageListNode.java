package dk.silverbullet.telemed.questionnaire.node;

import android.app.ProgressDialog;
import android.util.Log;
import dk.silverbullet.telemed.questionnaire.Questionnaire;
import dk.silverbullet.telemed.questionnaire.R;
import dk.silverbullet.telemed.questionnaire.element.ListViewElement;
import dk.silverbullet.telemed.questionnaire.element.TextViewElement;
import dk.silverbullet.telemed.questionnaire.expression.Variable;
import dk.silverbullet.telemed.questionnaire.expression.VariableLinkFailedException;
import dk.silverbullet.telemed.rest.Resources;
import dk.silverbullet.telemed.rest.bean.message.MessageItem;
import dk.silverbullet.telemed.rest.bean.message.MessagePerson;
import dk.silverbullet.telemed.rest.bean.message.MessageRecipient;
import dk.silverbullet.telemed.rest.bean.message.Messages;
import dk.silverbullet.telemed.rest.listener.RetrieveEntityListener;
import dk.silverbullet.telemed.utils.Util;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;

public class MessageListNode extends IONode {

    private static final String TAG = Util.getTag(MessageListNode.class);
    private Node readMessagesNode;
    private Map<Long, String> departmentNameMap;
    private Map<Long, Integer> departmentMessageCountMap;
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
        dialog = ProgressDialog.show(questionnaire.getContext(), Util.getString(R.string.message_fetching, questionnaire), Util.getString(R.string.default_please_wait, questionnaire), true);

        Resources.getMessageRecipients(questionnaire, new RetrieveEntityListener<MessageRecipient[]>() {
            @Override
            public void retrieveError() {
                dialog.dismiss();
            }

            @Override
            public void retrieved(MessageRecipient[] result) {
                setRecipients(result);
            }
        });

        super.enter();
    }

    public void setView() {
        clearElements();

        TextViewElement tve = new TextViewElement(this, Util.getString(R.string.message_messages, questionnaire));
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

    public void setMessages(Messages messageResult) {
        for (MessageItem msg : messageResult.messages) {
            MessagePerson from = msg.getFrom();
            boolean fromDepartment = from.getType().equals("Department");
            if (fromDepartment && !msg.isRead()) {
                Long key = from.getId();
                if (departmentMessageCountMap.containsKey(key)) {
                    departmentMessageCountMap.put(key, departmentMessageCountMap.get(key) + 1);
                }
            }
        }

        setView();
        createView();
        dialog.dismiss();
    }

    private void setRecipients(MessageRecipient[] messageRecipients) {
        departmentNameMap = new LinkedHashMap<Long, String>();
        departmentMessageCountMap = new HashMap<Long, Integer>();

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
            Resources.getMessages(questionnaire, new RetrieveEntityListener<Messages>() {
                @Override
                public void retrieveError() {
                    dialog.dismiss();
                }

                @Override
                public void retrieved(Messages result) {
                    setMessages(result);
                }
            });
        }

        departmentNameMap2.setValue(departmentNameMap);
    }

    public void setDepartmentId(Variable<Long> departmentId) {
        this.departmentId = departmentId;
    }

    public void setDepartmentNameMap2(Variable<Map> departmentNameMap2) {
        this.departmentNameMap2 = departmentNameMap2;
    }

    public void setReadMessagesNode(Node readMessagesNode) {
        this.readMessagesNode = readMessagesNode;
    }
}
