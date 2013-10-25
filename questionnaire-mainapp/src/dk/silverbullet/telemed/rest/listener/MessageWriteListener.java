package dk.silverbullet.telemed.rest.listener;

public interface MessageWriteListener extends Listener {

    void setRecipients(String result);

    void end(String result);

    void sendError();
}
