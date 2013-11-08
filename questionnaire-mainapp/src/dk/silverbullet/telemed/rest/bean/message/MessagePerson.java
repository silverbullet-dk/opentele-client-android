package dk.silverbullet.telemed.rest.bean.message;

public class MessagePerson {
    private String type;
    private Long id;
    private String name;
    private boolean changePassword;

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
