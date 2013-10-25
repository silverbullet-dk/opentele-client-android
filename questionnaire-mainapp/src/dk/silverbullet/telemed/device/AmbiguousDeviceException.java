package dk.silverbullet.telemed.device;

public class AmbiguousDeviceException extends DeviceInitialisationException {
    private static final long serialVersionUID = 3844299737919967942L;

    public AmbiguousDeviceException() {
        super("More than one device match");
    }
}
