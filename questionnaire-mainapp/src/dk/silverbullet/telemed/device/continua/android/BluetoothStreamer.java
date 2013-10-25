package dk.silverbullet.telemed.device.continua.android;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import android.os.ParcelFileDescriptor;
import android.util.Log;
import dk.silverbullet.telemed.device.continua.EndOfFileException;
import dk.silverbullet.telemed.device.continua.PacketCollector;
import dk.silverbullet.telemed.utils.Util;

/**
 * Listens on the input stream from the connected Bluetooth device, and allows sending bytes to the device. Is
 * completely agnostic as to what is sent back and forth.
 * 
 * Received bytes are simply piped to the given {@see PacketCollector}.
 */
public class BluetoothStreamer extends Thread {
    private static final String TAG = Util.getTag(BluetoothStreamer.class);

    private final Object parcelFileDescriptorSemaphore = new Object();
    private PacketCollector packetCollector;
    private ParcelFileDescriptor parcelFileDescriptor;

    private static final long RESET_TIME = 100;
    private long resetTime;
    private boolean resetCollector;

    public void setPacketCollector(PacketCollector packetCollector) {
        this.packetCollector = packetCollector;
    }

    public void startReading() {
        Log.d(TAG, "startReading");
        if (!isAlive()) {
            start();
        }
    }

    public void stopReading() {
        Log.d(TAG, "stopReading");

        synchronized (parcelFileDescriptorSemaphore) {
            if (isAlive()) {
                unplugConnection();
                interrupt();
            }
        }
    }

    public void unplugConnection() {
        Log.d(TAG, "unplugConnection");

        synchronized (parcelFileDescriptorSemaphore) {
            ParcelFileDescriptor oldFileDescriptor = this.parcelFileDescriptor;
            this.parcelFileDescriptor = null;

            if (oldFileDescriptor != null) {
                try {
                    oldFileDescriptor.close();
                } catch (IOException ex) {
                    // Ignore!
                }
            }

            parcelFileDescriptorSemaphore.notifyAll();
        }
    }

    public void replugConnection(ParcelFileDescriptor newFd) {
        Log.d(TAG, "ParcelFileDescriptor");
        if (this.parcelFileDescriptor != null) {
            unplugConnection();
        }
        synchronized (parcelFileDescriptorSemaphore) {
            this.parcelFileDescriptor = newFd;
            parcelFileDescriptorSemaphore.notifyAll();
        }
    }

    @Override
    public void run() {
        Log.d(TAG, "Reader thread was started.");
        try {
            for (;;) { // Forever: We'll be interrupted when we're supposed to stop!
                try {
                    BlockingInputStream inputStream = waitForInputStream();
                    Log.d(TAG, "Reader thread has a connection. Reading...");

                    ensureCollectorIsBeingReset();
                    int read = inputStream.read();
                    while (read >= 0) {
                        sendByteToCollector(read);
                        read = inputStream.read();
                    }
                    Log.d(TAG, "Reader thread got an EOF!");
                    packetCollector.error(new EndOfFileException());
                    inputStream.close();
                    unplugConnection();
                } catch (IOException ioe) {
                    Log.d(TAG, "Reader thread got an IOException: " + ioe);
                    packetCollector.error(ioe);
                }
            }
        } catch (InterruptedException ie) {
            Log.d(TAG, "Reader thread was interupted");
        } finally {
            Log.d(TAG, "Reader thread terminating!");
        }
    }

    public void write(byte[] bytes) throws IOException {
        synchronized (parcelFileDescriptorSemaphore) {
            if (parcelFileDescriptor == null) {
                throw new IOException("Missing parcelFileDescriptor - Bluetooth communication has been unplugged");
            }
            OutputStream outputStream = new FileOutputStream(parcelFileDescriptor.getFileDescriptor());
            try {
                outputStream.write(bytes);
            } finally {
                outputStream.close();
            }
        }
    }

    private void ensureCollectorIsBeingReset() {
        resetCollector = true;
    }

    private void sendByteToCollector(int read) {
        long now = System.currentTimeMillis();
        boolean withinTimeoutPeriod = now - resetTime < RESET_TIME;
        if (withinTimeoutPeriod) {
            resetTime = now;
        } else {
            if (resetCollector) {
                packetCollector.reset();
                resetCollector = false;
            }
            packetCollector.receive((byte) read);
        }
    }

    private BlockingInputStream waitForInputStream() throws InterruptedException {
        synchronized (parcelFileDescriptorSemaphore) {
            Log.d(TAG, "Reader thread waiting for a connection.");
            while (parcelFileDescriptor == null) {
                parcelFileDescriptorSemaphore.wait();
            }
            return new BlockingInputStream(new FileInputStream(parcelFileDescriptor.getFileDescriptor()));
        }
    }
}
