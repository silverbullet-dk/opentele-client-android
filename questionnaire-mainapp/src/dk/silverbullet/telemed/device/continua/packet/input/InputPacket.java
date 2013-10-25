package dk.silverbullet.telemed.device.continua.packet.input;

import java.io.IOException;

import dk.silverbullet.telemed.device.UnexpectedPacketFormatException;
import dk.silverbullet.telemed.device.continua.ContinuaPacketTag;

public abstract class InputPacket {
    private final ContinuaPacketTag tag;
    final byte[] contents;

    protected InputPacket(ContinuaPacketTag tag, byte[] contents) {
        this.tag = tag;
        this.contents = contents;
    }

    public ContinuaPacketTag getTag() {
        return tag;
    }

    public byte[] getContents() {
        return contents;
    }

    public int length() {
        return contents.length;
    }

    protected void checkShort(OrderedByteReader in, String name, int expected) throws IOException,
            UnexpectedPacketFormatException {
        int value = in.readShort();
        if (value != expected) {
            throw new UnexpectedPacketFormatException("Unexpected " + name + "(" + value + " - expected " + expected
                    + ")");
        }
    }

    protected void checkByte(OrderedByteReader in, String name, int expected) throws IOException,
            UnexpectedPacketFormatException {
        int value = in.readByte();
        if (value != expected) {
            throw new UnexpectedPacketFormatException("Unexpected " + name + "(" + value + " - expected " + expected
                    + ")");
        }
    }

    protected void checkInt(OrderedByteReader in, String name, int expected) throws IOException,
            UnexpectedPacketFormatException {
        int value = in.readInt();
        if (value != expected) {
            throw new UnexpectedPacketFormatException("Unexpected " + name + "(" + value + " - expected " + expected
                    + ")");
        }
    }
}
