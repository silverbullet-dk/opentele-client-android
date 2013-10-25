package dk.silverbullet.telemed.device.monica;

import dk.silverbullet.telemed.device.DeviceInitialisationException;

public class BatteryTooLowException extends DeviceInitialisationException {
    private static final long serialVersionUID = 311388916019114213L;

    public BatteryTooLowException() {
        super("Not enough power on batteries");
    }
}
