package dk.silverbullet.telemed.device.monica;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;
import java.util.LinkedList;
import java.util.Queue;
import java.util.UUID;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.util.Log;
import dk.silverbullet.telemed.device.AmbiguousDeviceException;
import dk.silverbullet.telemed.device.BluetoothDisabledException;
import dk.silverbullet.telemed.device.BluetoothNotAvailableException;
import dk.silverbullet.telemed.device.DeviceInitialisationException;
import dk.silverbullet.telemed.device.DeviceNotFoundException;
import dk.silverbullet.telemed.device.MessageTimeout;
import dk.silverbullet.telemed.device.monica.packet.IBlockMessage;
import dk.silverbullet.telemed.device.monica.packet.MonicaMessage;
import dk.silverbullet.telemed.device.monica.packet.MonicaPacketCollector;
import dk.silverbullet.telemed.device.monica.packet.PacketReceiver;
import dk.silverbullet.telemed.device.monica.packet.states.ReceiverState;
import dk.silverbullet.telemed.utils.DataLogger;
import dk.silverbullet.telemed.utils.Util;

public class MonicaBluetoothIOController extends Thread implements PacketReceiver {
    private static final String TAG = Util.getTag(MonicaBluetoothIOController.class);
    private static final UUID SERIAL_SERVICE_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    private static final int MESSAGE_TIMEOUT = 1000;
    private static final long READWRITE_DELAY = 100; // Number of ms to wait between read until write will be allowed.

    private final MonicaPacketCollector packetCollector = new MonicaPacketCollector();
    private final Queue<MonicaMessage> messageQueue = new LinkedList<MonicaMessage>();

    private final BluetoothDevice device;
    private final BluetoothAdapter btAdapter;
    private BluetoothSocket socket;
    private InputStream inputStream;
    private OutputStream outputStream;

    private final Object writeSemaphore = new Object();
    private boolean running = true;
    private long readTime;

    public MonicaBluetoothIOController() throws IOException, InterruptedException, DeviceInitialisationException {
        packetCollector.setListener(this);

        btAdapter = BluetoothAdapter.getDefaultAdapter();

        // If the adapter is null, then Bluetooth is not supported
        if (btAdapter == null) {
            throw new BluetoothNotAvailableException();
        }

        device = getDevice();

        // Increase the thread priority in order to minimize timing issues.
        setPriority((NORM_PRIORITY + MAX_PRIORITY) / 2);
        start();
    }

    @Override
    public void run() {
        int retries = 0;

        try {
            while (running) {
                sleep(Math.min(100 + retries * 500, 3000));
                retries++;

                Log.d(TAG, "Connecting...");
                try {
                    socket = device.createInsecureRfcommSocketToServiceRecord(SERIAL_SERVICE_UUID);
                    if (socket == null) {
                        throw new IOException("NullSocket!");
                    }
                    socket.connect();
                    inputStream = socket.getInputStream();
                    Log.d(TAG, "Read is working, now open output!");
                    synchronized (writeSemaphore) {
                        readTime = System.currentTimeMillis();
                        outputStream = socket.getOutputStream();
                        writeSemaphore.notify();
                    }

                    Log.d(TAG, "Output opened, now start working!");
                    int read = inputStream.read();
                    packetCollector.reset();
                    synchronized (writeSemaphore) {
                        readTime = System.currentTimeMillis();
                    }
                    retries = 0;
                    while (read >= 0) {
                        if (!running) {
                            throw new IOException("Reader thread requested to stop!");
                        }
                        packetCollector.receive((byte) read);
                        long processingTime = System.currentTimeMillis() - readTime;
                        if (processingTime > 10) {
                            Log.d(TAG, "Byte processing time too high: " + processingTime);
                        }
                        read = inputStream.read();
                        synchronized (writeSemaphore) {
                            readTime = System.currentTimeMillis();
                        }
                    }
                    throw new IOException("EOF from stream!");
                } catch (IOException ioe) {
                    Log.d(TAG, "Reader exception: " + ioe);
                    if (socket != null) {
                        closeSocket(socket);
                    }
                    socket = null;
                }
            }
        } catch (InterruptedException e1) {
            Log.d(TAG, "Reader thread was interrupted!");
        } finally {
            Log.d(TAG, "Reader thread stopped!");
            running = false;
        }
    }

    @Override
    public void receive(MonicaMessage packet) {
        synchronized (messageQueue) {
            messageQueue.add(packet);
            messageQueue.notifyAll();
        }
    }

    public String getMacAddress() {
        return device.getAddress();
    }

    public int messagesWaiting() {
        synchronized (messageQueue) {
            return messageQueue.size();
        }
    }

    public MonicaMessage readMessage(long timeout) throws IOException, MessageTimeout {
        synchronized (messageQueue) {
            long now = System.currentTimeMillis();
            long deadline = now + timeout;
            while (messageQueue.isEmpty() && now < deadline) {
                try {
                    messageQueue.wait(deadline - now);
                } catch (InterruptedException e) {
                    // Ignore
                }
                now = System.currentTimeMillis();
            }
            if (messageQueue.size() > 0) {
                return messageQueue.remove();
            } else
                throw new MessageTimeout();
        }
    }

    public void clearReadQueue() {
        synchronized (messageQueue) {
            messageQueue.clear();
        }
    }

    public void close() {
        running = false;
        BluetoothSocket theSocket = socket;
        socket = null;

        if (theSocket != null) {
            closeSocket(theSocket);
        }
    }

    public void writeMessage(byte[] bytes) throws IOException {
        writeMessage(bytes, 30, 60000); // Randomly chosen timeout value
    }

    public void writeMessage(byte[] bytes, int retries, long timeout) throws IOException {
        // Note: Should now handle embedded ETX codes!
        ByteArrayOutputStream buffer2 = new ByteArrayOutputStream(25);
        buffer2.write(ReceiverState.DLE);
        buffer2.write(ReceiverState.STX);
        for (int i = 0; i < bytes.length; i++) {
            byte b = bytes[i];
            if (b == ReceiverState.DLE)
                buffer2.write(ReceiverState.DLE);
            buffer2.write(b);
        }
        buffer2.write(ReceiverState.DLE);
        buffer2.write(ReceiverState.ETX);
        short crc = Util.calcCRC16(buffer2.toByteArray());
        buffer2.write((crc >> 8) & 0xFF);
        buffer2.write(crc & 0xFF);

        Log.d(TAG, "CRC check: " + Util.calcCRC16(buffer2.toByteArray()));

        long now = System.currentTimeMillis();
        long deadline = now + timeout;
        for (int tries = 0; tries < retries && running && now < deadline; tries++) {
            try {
                synchronized (writeSemaphore) {
                    long timeToWait = readTime + READWRITE_DELAY - System.currentTimeMillis();
                    while (timeToWait > 0) {
                        writeSemaphore.wait(timeToWait);
                        timeToWait = readTime + READWRITE_DELAY - System.currentTimeMillis();
                    }
                    try {
                        if (outputStream != null) {
                            Date writeTime = new Date();
                            outputStream.write(buffer2.toByteArray());
                            DataLogger.logOutput(writeTime, bytes);
                            return; // Success!
                        }
                    } catch (IOException ioe) {
                        Log.d(TAG, "Writing exception: " + ioe);
                    }
                    Log.d(TAG, "Writer waiting for outputStream...");
                    writeSemaphore.wait(2000); // Consider semaphore signal instead of polling
                }
            } catch (InterruptedException e) {
                throw new IOException("Writing was interrupted!");
            }
            now = System.currentTimeMillis();
        }
        if (!running)
            throw new IOException("Writing: Reader stopped!");
        else
            throw new MessageTimeout("Timeout writing message!");
    }

    public <T> T writeAndRead(byte[] message, Class<T> type) throws IOException {
        return writeAndRead(message, type, 1, MESSAGE_TIMEOUT);
    }

    @SuppressWarnings("unchecked")
    public <T> T writeAndRead(byte[] message, Class<T> type, int retries, int timeout) throws IOException {
        for (int tries = 0; tries < retries; tries++) {
            if (message != null)
                writeMessage(message);
            long now = System.currentTimeMillis();
            long deadline = now + timeout;
            while (now < deadline) {
                MonicaMessage msg = readMessage(deadline - now);
                if (type.isAssignableFrom(msg.getClass())) {
                    return (T) msg; // Yes, GOT IT! :)
                }
                now = System.currentTimeMillis();
            }
        }
        throw new MessageTimeout(Util.getTag(type) + "-message type not received!");
    }

    void checkDevice() throws IOException, DeviceNotFoundException, UnknownFirmwareVersionException {
        // Test the device ID and version

        Log.d(TAG, "Check device...");

        IBlockMessage info = writeAndRead(MessageFactory.getInfoMessage(), IBlockMessage.class, 5, 3000);

        Log.d(TAG, "Info: " + info);

        if (!"AN24V1A30A".equals(info.getDeviceType())) {
            Log.d(TAG, "Unknown device type: " + info.getDeviceType());
            throw new DeviceNotFoundException();
        }

        if (!"000000005900".equals(info.getDeviceVersion())) {
            Log.d(TAG, "Bad/unknown device firmware version: " + info);
            throw new UnknownFirmwareVersionException(info.getDeviceVersion());
        }
    }

    private BluetoothDevice getDevice() throws IOException, InterruptedException, DeviceInitialisationException {
        if (!btAdapter.isEnabled()) {
            Log.d(TAG, "Sorry, Bluetooth is not enabled!");
            throw new BluetoothDisabledException();
        }

        BluetoothDevice result = null;
        for (BluetoothDevice potentialDevice : btAdapter.getBondedDevices()) {
            String address = potentialDevice.getAddress();
            String name = potentialDevice.getName();
            Log.d(TAG, name + ": " + address);

            // MONICA
            if (address.startsWith("00:80:98")) {
                if (result != null)
                    throw new AmbiguousDeviceException();
                result = potentialDevice;
            }
        }

        if (result == null) {
            throw new DeviceNotFoundException();
        }

        return result;
    }

    private void closeSocket(BluetoothSocket socketToClose) {
        try {
            socketToClose.close();
        } catch (IOException e) {
            // Ignore!
        }
    }
}
