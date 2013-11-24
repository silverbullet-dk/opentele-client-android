package dk.silverbullet.telemed.rest.bean;

import com.google.gson.annotations.Expose;

import java.io.Serializable;

public class ChangePasswordBean implements Serializable {
    private static final long serialVersionUID = 188331560289897695L;

    @Expose private String currentPassword;
    @Expose private String password;
    @Expose private String passwordRepeat;

    public void setCurrentPassword(String currentPassword) {
        this.currentPassword = currentPassword;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setPasswordRepeat(String passwordRepeat) {
        this.passwordRepeat = passwordRepeat;
    }
}