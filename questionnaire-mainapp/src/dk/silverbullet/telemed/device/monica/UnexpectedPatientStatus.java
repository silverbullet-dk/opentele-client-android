package dk.silverbullet.telemed.device.monica;

import dk.silverbullet.telemed.device.DeviceInitialisationException;
import dk.silverbullet.telemed.device.monica.packet.PatientStatusMessage;
import dk.silverbullet.telemed.device.monica.packet.PatientStatusMessage.Status;

public class UnexpectedPatientStatus extends DeviceInitialisationException {

    public UnexpectedPatientStatus(Status expectedStatus, PatientStatusMessage status) {
        super("Expected patient status " + expectedStatus + " got " + status);
    }

    private static final long serialVersionUID = 1L;

}
