package dk.silverbullet.telemed.questionnaire.element;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import dk.silverbullet.telemed.questionnaire.R;
import dk.silverbullet.telemed.questionnaire.node.IONode;
import dk.silverbullet.telemed.rest.bean.message.MessageItem;
import dk.silverbullet.telemed.utils.Util;

public class PatientMessageBubbleElement extends MessageBubble {

    static final String TAG = Util.getTag(ClinicMessageBubbleElement.class);

    public PatientMessageBubbleElement(IONode node, MessageItem messageItem) {
        super(node, messageItem);
    }

    @Override
    public View getView() {

        Activity activity = getQuestionnaire().getActivity();
        LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        LinearLayout messageBubbleLayout = (LinearLayout) inflater.inflate(R.layout.patient_message_bubble, null);
        formatMessageBubble(messageBubbleLayout, R.drawable.patient_bubble_unread, "(Ulæst)",
                R.drawable.patient_bubble_read, "", "sendt");
        return messageBubbleLayout;
    }

}
