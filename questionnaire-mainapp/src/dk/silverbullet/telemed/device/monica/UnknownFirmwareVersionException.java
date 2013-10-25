package dk.silverbullet.telemed.device.monica;

import dk.silverbullet.telemed.device.DeviceInitialisationException;

public class UnknownFirmwareVersionException extends DeviceInitialisationException {
    private static final long serialVersionUID = -851800684418483954L;
    private final String version;

    UnknownFirmwareVersionException(String version) {
        super("Unknown firmware");
        this.version = version;
    }

    public String getVersion() {
        return version;
    }
}
