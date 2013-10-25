package dk.silverbullet.telemed.device.continua.packet.output;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class OrderedByteWriter {
    private final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

    public void writeByte(int b) {
        outputStream.write(b);
    }

    public void writeShort(int i) {
        writeByte(i >> 8);
        writeByte(i & 0xFF);
    }

    public void writeInt(int i) {
        writeShort(i >> 16);
        writeShort(i & 0xFFFF);
    }

    public void writeLong(long l) {
        writeInt((int) (l >> 32));
        writeInt((int) (l & 0xFFFFFFFF));
    }

    public byte[] getBytes() {
        try {
            outputStream.close();
            return outputStream.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException("Could not close underlying stream", e);
        }
    }
}
