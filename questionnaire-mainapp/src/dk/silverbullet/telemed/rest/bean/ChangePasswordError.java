package dk.silverbullet.telemed.rest.bean;

import com.google.gson.annotations.Expose;

import java.io.Serializable;

public class ChangePasswordError implements Serializable {

    private static final long serialVersionUID = 6633629885678810257L;

    public static final String FIELD_CURRENTPASSWORD = "currentPassword";
    public static final String FIELD_PASSWORD = "password";
    public static final String FIELD_PASSWORDREPEAT = "passwordRepeat";

    @Expose private String field;
    @Expose private String error;

    public String getError() {
        return error;
    }
}
