package dk.silverbullet.telemed.rest.listener;

public interface LoginListener extends Listener {
    void login(String login);

    void accountLocked();
}
