package dk.silverbullet.telemed.device.continua.packet;

import java.io.IOException;

public class MeasurementOutOfLimitsException extends IOException {

    private static final long serialVersionUID = -2849386663361887073L;

    public MeasurementOutOfLimitsException(String message) {
        super(message);
    }
}
