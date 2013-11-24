package dk.silverbullet.telemed.rest.bean.message;

import com.google.gson.annotations.Expose;

import java.io.Serializable;

public class MessageRecipient implements Serializable {
    private static final long serialVersionUID = -2614579596738901036L;
    @Expose private Long id;
    @Expose private String name;

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
