package dk.silverbullet.telemed.questionnaire.node;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import dk.silverbullet.telemed.questionnaire.Questionnaire;
import dk.silverbullet.telemed.questionnaire.R;
import dk.silverbullet.telemed.questionnaire.expression.Variable;
import dk.silverbullet.telemed.questionnaire.skema.*;
import dk.silverbullet.telemed.rest.ReminderTask;
import dk.silverbullet.telemed.rest.RetrieveMessageListTask;
import dk.silverbullet.telemed.rest.RetrieveRecipientsTask;
import dk.silverbullet.telemed.rest.bean.message.MessageRecipient;
import dk.silverbullet.telemed.rest.bean.message.Messages;
import dk.silverbullet.telemed.rest.listener.MessageListListener;
import dk.silverbullet.telemed.rest.listener.MessageWriteListener;
import dk.silverbullet.telemed.utils.Json;
import dk.silverbullet.telemed.utils.Util;

import java.text.MessageFormat;

public class PatientMenuNode extends MenuNode implements MessageListListener, MessageWriteListener {

    private static final String TAG = Util.getTag(PatientMenuNode.class);

    private Node nextNode;
    private ProgressDialog dialog;

    private int unreadMessages = -1;
    private boolean showMessagesMenuItem = false;

    public PatientMenuNode(Questionnaire questionnaire, String nodeName) {
        super(questionnaire, nodeName);
    }

    @Override
    public void enter() {

        hideMenuButton();
        hideBackButton();

        getQuestionnaire().clearStack();

        Variable<?> user = questionnaire.getValuePool().get(Util.VARIABLE_USERNAME);
        if (null != user && null != user.getExpressionValue() && null != user.getExpressionValue().toString()
                && !Util.ADMINUSER_NAME.equalsIgnoreCase(user.getExpressionValue().toString())) {
            dialog = ProgressDialog.show(questionnaire.getActivity(), Util.getString(R.string.default_working, questionnaire), Util.getString(R.string.default_please_wait, questionnaire), true);
            new ReminderTask(questionnaire).execute();
            new RetrieveRecipientsTask(questionnaire, this).execute();
            new RetrieveMessageListTask(questionnaire, this).execute();
        } else {
            super.enter();
        }
    }

    @Override
    public String toString() {
        return "PatientMenuNode(\"" + getNodeName() + "\") -> \"" + nextNode.getNodeName() + "\"";
    }

    @Override
    public void sendError() {
        dialog.dismiss();
    }

    @Override
    public void end(String result) {
        Log.d(TAG, result);
        Messages messages = Json.parse(result, Messages.class);
        int numberOfUnreadMessages = messages.unread;

        boolean refreshGui = numberOfUnreadMessages != unreadMessages;
        if (refreshGui) {
            unreadMessages = numberOfUnreadMessages;
            setMessageMenuText(questionnaire.getRootLayout());
        }
        dialog.dismiss();
    }
    

    @Override
    protected void createView() {
        Activity activity = questionnaire.getActivity();
        LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        ViewGroup rootLayout = questionnaire.getRootLayout();
        rootLayout.removeAllViews();

        inflater.inflate(R.layout.patient_menu, rootLayout, true);

        linkTopPanel(rootLayout);

        rootLayout.findViewById(R.id.patient_menu_questionnaire).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startQuestionnaire();
            }
        });


        if(showMessagesMenuItem) {
            setMessagesMenuItem(rootLayout);
            setAcknowledgementsMenuItem(rootLayout);
        }

        rootLayout.findViewById(R.id.patient_menu_my_measurements).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPatientMeasurements();
            }
        });

        rootLayout.findViewById(R.id.patient_menu_change_password).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changePassword();
            }
        });


    }

    private void setAcknowledgementsMenuItem(ViewGroup rootLayout) {
        View acknowledgementsMenuItem = rootLayout.findViewById(R.id.patient_menu_acknowledements);
        acknowledgementsMenuItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAcknowledgements();
            }
        });

        acknowledgementsMenuItem.setVisibility(View.VISIBLE);
    }

    private void setMessagesMenuItem(ViewGroup rootLayout) {
        TextView messages = (TextView) rootLayout.findViewById(R.id.patient_menu_messages);
        messages.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showMessages();
            }
        });

        setMessageMenuText(rootLayout);
        messages.setVisibility(View.VISIBLE);
    }

    private void setMessageMenuText(ViewGroup rootLayout) {
        TextView messages = (TextView) rootLayout.findViewById(R.id.patient_menu_messages);
        MessageFormat fmt = new MessageFormat(Util.getString(R.string.menu_messages, questionnaire));
        String messagesItemText = fmt.format(new Integer[] { unreadMessages });
        messages.setText(messagesItemText);
    }

    private void changePassword() {
        setupAndRunSkema(new ChangePasswordSkema());
    }

    private void showPatientMeasurements() {
        setupAndRunSkema(new PatientMeasurementSkema());
    }

    private void showAcknowledgements() {
        setupAndRunSkema(new AcknowledgementsSkema());
    }

    private void showMessages() {
        setupAndRunSkema(new MessageSkema());
    }

    private void startQuestionnaire() {
        setupAndRunSkema(new RunSkema());
    }


    @Override
    public void setRecipients(String result) {
        if (null != result && !"".equals(result)) {
            MessageRecipient[] messageRecipients = Json.parse(result, MessageRecipient[].class);
            showMessagesMenuItem = messageRecipients != null && messageRecipients.length > 0;

            super.enter();

            if (showMessagesMenuItem) {
                new RetrieveMessageListTask(questionnaire, this).execute();
            } else {
                dialog.dismiss();
            }
        }
    }

    public void setNextNode(Node nextNode) {
        this.nextNode = nextNode;
    }

}
