package dk.silverbullet.telemed.rest.client;

import android.content.Context;

/**
 * Defines what is required to communicate with the back-end server. It may seem odd that a Context is required, but
 * this is Android...
 */
public interface ServerInformation {
    String getServerUrl();
    String getUserName();
    String getPassword();
    Context getContext();
}
