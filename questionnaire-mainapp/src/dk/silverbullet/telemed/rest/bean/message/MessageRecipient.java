package dk.silverbullet.telemed.rest.bean.message;

import java.io.Serializable;

public class MessageRecipient implements Serializable {
    private static final long serialVersionUID = -2614579596738901036L;
    private Long id;
    private String name;

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
