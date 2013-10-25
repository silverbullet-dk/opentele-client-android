package dk.silverbullet.telemed.device.continua;

public interface ContinuaDeviceController {
    /**
     * Closes all currently pending Bluetooth communication and terminates the session.
     */
    void close();
}
