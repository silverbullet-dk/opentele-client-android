package dk.silverbullet.telemed.rest.bean.message;

import java.io.Serializable;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class MessageRecipient implements Serializable {

    private static final long serialVersionUID = -2614579596738901036L;

    private Long id;
    private String name;
}
