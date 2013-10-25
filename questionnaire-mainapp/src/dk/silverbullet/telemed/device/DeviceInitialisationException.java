package dk.silverbullet.telemed.device;

public class DeviceInitialisationException extends Exception {
    private static final long serialVersionUID = -3585496042502595745L;

    public DeviceInitialisationException(String message) {
        super(message);
    }

    public DeviceInitialisationException(String message, Exception cause) {
        super(message, cause);
    }
}
