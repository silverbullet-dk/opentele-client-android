package dk.silverbullet.telemed.rest.bean.message;

import com.google.gson.annotations.Expose;

import java.util.Date;

public class MessageItem {
    @Expose private Long id;
    @Expose private String title;
    @Expose private String text;
    @Expose private MessagePerson to;
    @Expose private MessagePerson from;
    @Expose private boolean isRead;
    @Expose private Date sendDate;
    @Expose private Date readDate;

    public Date getSendDate() {
        return sendDate;
    }

    public boolean isRead() {
        return isRead;
    }

    public String getTitle() {
        return title;
    }

    public String getText() {
        return text;
    }

    public MessagePerson getTo() {
        return to;
    }

    public MessagePerson getFrom() {
        return from;
    }

    public Long getId() {
        return id;
    }

}
