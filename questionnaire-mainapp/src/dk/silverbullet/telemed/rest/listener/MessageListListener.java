package dk.silverbullet.telemed.rest.listener;

public interface MessageListListener extends Listener {

    void end(String result);

    void sendError();
}
