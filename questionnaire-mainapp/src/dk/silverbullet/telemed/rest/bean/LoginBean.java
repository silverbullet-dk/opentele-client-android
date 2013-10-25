package dk.silverbullet.telemed.rest.bean;

import java.io.Serializable;

import lombok.Data;
import lombok.ToString;
import dk.silverbullet.telemed.rest.bean.message.MessagePerson;

@Data
@ToString
public class LoginBean implements Serializable {

    private static final long serialVersionUID = 188331560289897695L;

    private long id;
    private String firstName;
    private String lastName;
    private MessagePerson user;

    public String getFullName() {
        return firstName + " " + lastName;
    }
}
