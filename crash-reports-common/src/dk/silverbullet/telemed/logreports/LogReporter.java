package dk.silverbullet.telemed.logreports;

import android.app.Application;

import java.net.URL;

/**
 * Common interface for sending logs and crashes to a central server
 */
public interface LogReporter {
    /**
     *
     * @param reportUrl The url to send logs and crashes to
     * @param application Application context to be used by logging framework.
     * @param environmentName Environment name is picked up from string resources.
     *                        But resources of the application are not available from the library project so we need to supply it at initialization
     */
    public void initialize(URL reportUrl, Application application, String environmentName);

    /**
     * Send log of exception to central server
     * @param exception Exception to be logged
     */
    public void logException(Exception exception);

    /**
     * Send log of message to central server
     * @param message Messsage to be logged
     */
    public void logMessage(String message);
}
