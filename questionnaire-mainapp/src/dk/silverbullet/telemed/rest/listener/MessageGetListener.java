package dk.silverbullet.telemed.rest.listener;

public interface MessageGetListener extends Listener {

    void end(String result);

    void sendError();
}
