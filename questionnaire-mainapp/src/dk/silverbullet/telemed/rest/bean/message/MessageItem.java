package dk.silverbullet.telemed.rest.bean.message;

import lombok.Data;

@Data
public class MessageItem {

    private String result;

    private Integer unread;

    private Long id;
    private String title;
    private String text;
    private MessagePerson to;
    private MessagePerson from;
    private boolean isRead;
    private String sendDate;
    private String readDate;
}
