package dk.silverbullet.telemed.device.continua.packet.input;

import java.io.IOException;

import dk.silverbullet.telemed.device.UnknownPacketException;
import dk.silverbullet.telemed.device.continua.ContinuaPacketTag;

public class AbortCommunicationPacket extends InputPacket {
    private int reason;

    public AbortCommunicationPacket(byte[] contents) throws IOException {
        super(ContinuaPacketTag.ABRT_APDU, contents);
        checkContents();
    }

    private void checkContents() throws IOException {
        if (getContents().length != 2) {
            throw new UnknownPacketException();
        }
        OrderedByteReader in = new OrderedByteReader(super.getContents());
        reason = in.readShort();
    }

    public int getReason() {
        return reason;
    }

    @Override
    public String toString() {
        return "AbortCommunicationPacket [reason=" + reason + "]";
    }
}
