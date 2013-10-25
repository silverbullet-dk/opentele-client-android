package dk.silverbullet.telemed.device;

public class BluetoothDisabledException extends DeviceInitialisationException {
    private static final long serialVersionUID = 2891236021447001533L;

    public BluetoothDisabledException() {
        super("Bluetooth was not enabled on your device");
    }
}
