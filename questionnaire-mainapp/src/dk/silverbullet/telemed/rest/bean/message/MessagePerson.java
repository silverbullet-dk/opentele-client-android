package dk.silverbullet.telemed.rest.bean.message;

import com.google.gson.annotations.Expose;

public class MessagePerson {
    @Expose private String type;
    @Expose private Long id;
    @Expose private String name;
    @Expose private boolean changePassword;

    public Long getId() {
        return id;
    }

    public String getType() {
        return type;
    }

    public boolean isChangePassword() {
        return changePassword;
    }
}
