package dk.silverbullet.telemed.device;

public class BluetoothNotAvailableException extends DeviceInitialisationException {
    private static final long serialVersionUID = 9143749052078385153L;

    public BluetoothNotAvailableException() {
        super("Bluetooth not available on your device");
    }
}
