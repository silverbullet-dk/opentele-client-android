package dk.silverbullet.telemed.device.continua.protocol;

import java.io.IOException;

import dk.silverbullet.telemed.device.continua.packet.SystemId;
import dk.silverbullet.telemed.device.continua.packet.output.OutputPacket;

public interface ProtocolStateListener<MeasurementType> {
    void sendPacket(OutputPacket packet) throws IOException;

    void tooManyRetries();

    void finish();

    void finishNow();

    void measurementReceived(SystemId systemId, MeasurementType measurement);

    void noMeasurementsReceived();
}
