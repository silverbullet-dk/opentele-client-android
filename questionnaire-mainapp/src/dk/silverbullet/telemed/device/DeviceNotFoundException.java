package dk.silverbullet.telemed.device;

public class DeviceNotFoundException extends DeviceInitialisationException {
    private static final long serialVersionUID = 610000703324426626L;

    public DeviceNotFoundException() {
        super("Did not find relevant device in list of paired devices");
    }
}
