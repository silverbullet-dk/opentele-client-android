package dk.silverbullet.telemed.rest.listener;

public interface ChangePasswordListener extends Listener {

    void response(String response);

    String getCurrentPassword();

    String getPassword();

    String getPasswordRepeat();
}
