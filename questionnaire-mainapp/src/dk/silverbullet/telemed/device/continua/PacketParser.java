package dk.silverbullet.telemed.device.continua;

import java.io.IOException;

public interface PacketParser {
    void errorReceived(IOException exception);

    void reset();

    void handle(ContinuaPacketTag tag, byte[] contents) throws IOException;
}
