package dk.silverbullet.telemed.rest.bean;

import java.io.Serializable;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class ChangePasswordError implements Serializable {

    private static final long serialVersionUID = 6633629885678810257L;

    public static final String FIELD_CURRENTPASSWORD = "currentPassword";
    public static final String FIELD_PASSWORD = "password";
    public static final String FIELD_PASSWORDREPEAT = "passwordRepeat";

    private String field;
    private String error;
}
