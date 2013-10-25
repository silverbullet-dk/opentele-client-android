package dk.silverbullet.telemed.rest.bean.message;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class MessageRead {

    private Long id;
    private String createdBy;
    private String createdDate;
    private MessagePerson from;
    private boolean isRead;
    private String modifiedBy;
    private String readDate;
    private String sendDate;
    private String text;
    private String title;
    private MessagePerson to;
}
