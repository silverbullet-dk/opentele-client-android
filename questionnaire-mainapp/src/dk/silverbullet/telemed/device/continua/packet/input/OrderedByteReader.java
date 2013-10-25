package dk.silverbullet.telemed.device.continua.packet.input;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import dk.silverbullet.telemed.device.continua.EndOfFileException;

public class OrderedByteReader {
    private final ByteArrayInputStream inputStream;

    public OrderedByteReader(byte[] data) {
        this.inputStream = new ByteArrayInputStream(data);
    }

    public int readByte() throws EndOfFileException {
        int value = inputStream.read();
        if (value < 0) {
            throw new EndOfFileException();
        }
        return value;
    }

    public int readShort() throws EndOfFileException {
        int a = readByte();
        int b = readByte();
        return (a << 8) | b;
    }

    public int readInt() throws EndOfFileException {
        int a = readShort();
        int b = readShort();
        return (a << 16) | b;
    }

    public long readLong() throws EndOfFileException {
        long a = readInt();
        long b = readInt();
        return (a << 32) | (b & 0xffffffffL);
    }

    public void close() {
        try {
            inputStream.close();
        } catch (IOException e) {
            throw new RuntimeException("Could not close underlying stream", e);
        }
    }

    public int available() {
        return inputStream.available();
    }
}
