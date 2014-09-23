package dk.silverbullet.telemed.device.nonin.packet;

import java.io.IOException;

public interface PacketReceiver {
    void setSerialNumber(NoninSerialNumberPacket packet);
    void addMeasurement(NoninMeasurementPacket packet);
    void error(IOException e);

    void sendChangeDataFormatCommand();
}
