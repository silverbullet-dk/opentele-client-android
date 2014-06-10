package dk.silverbullet.telemed.rest.listener;

public interface LoginListener {
    void sendError();
    void loggedIn();
    void loginFailed();
    void changePassword();
    void accountLocked();
}
