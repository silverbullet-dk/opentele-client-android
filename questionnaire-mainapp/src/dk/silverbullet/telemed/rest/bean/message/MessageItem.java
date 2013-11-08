package dk.silverbullet.telemed.rest.bean.message;

public class MessageItem {
    private Long id;
    private String title;
    private String text;
    private MessagePerson to;
    private MessagePerson from;
    private boolean isRead;
    private String sendDate;
    private String readDate;


    public String getSendDate() {
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
