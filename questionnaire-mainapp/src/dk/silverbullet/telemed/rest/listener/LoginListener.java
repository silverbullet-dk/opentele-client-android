package dk.silverbullet.telemed.rest.listener;

public interface LoginListener extends Listener {
    void loggedIn();
    void loginFailed();
    void changePassword();
    void accountLocked();
}
