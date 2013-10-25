package dk.silverbullet.telemed.device;

public interface DeviceController {
    /**
     * Closes all currently pending communication and terminates the session.
     */
    void close();
}
