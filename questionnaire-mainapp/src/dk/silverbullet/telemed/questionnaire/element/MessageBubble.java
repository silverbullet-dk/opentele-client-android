package dk.silverbullet.telemed.questionnaire.element;

import java.util.Map;

import android.text.Html;
import android.widget.LinearLayout;
import android.widget.TextView;
import dk.silverbullet.telemed.questionnaire.expression.Variable;
import dk.silverbullet.telemed.questionnaire.expression.VariableLinkFailedException;
import dk.silverbullet.telemed.questionnaire.node.IONode;
import dk.silverbullet.telemed.questionnaire.node.Node;
import dk.silverbullet.telemed.questionnaire.node.UnknownNodeException;
import dk.silverbullet.telemed.rest.bean.message.MessageItem;
import dk.silverbullet.telemed.utils.Util;

abstract class MessageBubble extends Element {
    protected MessageItem messageItem;

    static final String TAG = Util.getTag(MessageBubble.class);

    public MessageBubble(IONode node, MessageItem messageItem) {
        super(node);
        this.messageItem = messageItem;
    }

    protected void formatMessageBubble(LinearLayout messageBubbleLayout, int unreadId, String statusUnread, int readId,
            String statusRead, String received) {
        TextView messageBubble = (TextView) messageBubbleLayout.getChildAt(0);
        String status;
        if (messageItem.isRead()) {
            status = statusRead;
            messageBubble.setBackgroundResource(readId);
        } else {
            status = statusUnread;
            messageBubble.setBackgroundResource(unreadId);
        }

        String messageTime;

        if (messageItem.getSendDate() != null) {
            messageTime = " - " + received + " "
                    + Util.formatTime(messageItem.getSendDate());
        } else {
            messageTime = "";
        }
        messageBubble.setText(Html.fromHtml(status + " <b>" + Util.escapeHtml(messageItem.getTitle()) + "</b>"
                + messageTime + "<br>" + Util.escapeHtml(messageItem.getText())));
    }

    @Override
    public void leave() {
        // TODO Auto-generated method stub

    }

    @Override
    public void linkNodes(Map<String, Node> map) throws UnknownNodeException {
        // TODO Auto-generated method stub

    }

    @Override
    public void linkVariables(Map<String, Variable<?>> map) throws VariableLinkFailedException {
        // TODO Auto-generated method stub

    }

    @Override
    public boolean validates() {
        return true;
    }

}