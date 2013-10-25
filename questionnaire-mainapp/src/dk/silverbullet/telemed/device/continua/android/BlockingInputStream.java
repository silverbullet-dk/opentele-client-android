package dk.silverbullet.telemed.device.continua.android;

import java.io.IOException;
import java.io.InputStream;

public class BlockingInputStream extends InputStream {
    private final byte[] buffer = new byte[500];
    private final InputStream is;
    private int next = 0;
    private int count = 0;

    public BlockingInputStream(InputStream is) {
        this.is = is;
    }

    @Override
    public int read() throws IOException {
        if (hasBytes()) {
            return nextByte();
        }
        fillBuffer();
        return nextByte();
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        throw new UnsupportedOperationException();
    }

    @Override
    public int read(byte[] b) throws IOException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void close() throws IOException {
        super.close();
        is.close();
    }

    private boolean hasBytes() {
        return next < count;
    }

    private int nextByte() {
        return buffer[next++] & 0xFF;
    }

    private void fillBuffer() throws IOException {
        waitForData();
        count = is.read(buffer);
        next = 0;
    }

    private void waitForData() throws IOException {
        int wait = 100;
        boolean wasInterrupted = false;
        while (is.available() == 0) {
            try {
                Thread.sleep(wait);
                if (wait < 250)
                    wait += 25;
            } catch (InterruptedException ex) {
                wasInterrupted = true;
            }
        }

        if (wasInterrupted) {
            Thread.currentThread().interrupt();
        }
    }
}
