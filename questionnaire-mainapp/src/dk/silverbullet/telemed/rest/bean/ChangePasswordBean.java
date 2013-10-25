package dk.silverbullet.telemed.rest.bean;

import java.io.Serializable;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class ChangePasswordBean implements Serializable {

    private static final long serialVersionUID = 188331560289897695L;

    private String currentPassword;
    private String password;
    private String passwordRepeat;
}
