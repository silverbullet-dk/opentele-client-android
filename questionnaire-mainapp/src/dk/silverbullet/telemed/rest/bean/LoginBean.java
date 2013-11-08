package dk.silverbullet.telemed.rest.bean;

import dk.silverbullet.telemed.rest.bean.message.MessagePerson;

import java.io.Serializable;

public class LoginBean implements Serializable {

    private static final long serialVersionUID = 188331560289897695L;
    private long id;
    private String firstName;
    private String lastName;
    private MessagePerson user;

    public String getFullName() {
        return firstName + " " + lastName;
    }

    public long getId() {
        return id;
    }

    public MessagePerson getUser() {
        return user;
    }
}
