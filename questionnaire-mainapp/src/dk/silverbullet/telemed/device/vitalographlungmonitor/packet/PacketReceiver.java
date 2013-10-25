package dk.silverbullet.telemed.device.vitalographlungmonitor.packet;

import java.io.IOException;

public interface PacketReceiver {
    void receive(VitalographPacket message);

    void error(IOException e);

    void sendByte(byte b) throws IOException;
}
