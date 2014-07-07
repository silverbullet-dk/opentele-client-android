package dk.silverbullet.telemed.rest.bean;

import com.google.gson.annotations.Expose;
import dk.silverbullet.telemed.rest.bean.message.MessagePerson;

import java.io.Serializable;

public class LoginBean implements Serializable {
    private static final long serialVersionUID = 188331560289897695L;
    @Expose private long id;
    @Expose private String firstName;
    @Expose private String lastName;
    @Expose private MessagePerson user;
    @Expose private Boolean showRealtimeCTG;

    public String getFullName() {
        return firstName + " " + lastName;
    }

    public long getId() {
        return id;
    }

    public MessagePerson getUser() {
        return user;
    }

    public Boolean getShowRealtimeCTG() {
        return showRealtimeCTG;
    }
}
