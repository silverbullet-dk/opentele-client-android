package dk.silverbullet.telemed.rest.bean.message;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class MessagePerson {

    private Long id;
    private String name;
    private boolean changePassword;
}
