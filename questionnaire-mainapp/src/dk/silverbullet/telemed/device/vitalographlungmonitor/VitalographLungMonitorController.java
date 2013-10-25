package dk.silverbullet.telemed.device.vitalographlungmonitor;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.util.Log;
import dk.silverbullet.telemed.device.DeviceInitialisationException;
import dk.silverbullet.telemed.device.bluetooth.BluetoothConnector;
import dk.silverbullet.telemed.device.vitalographlungmonitor.packet.PacketReceiver;
import dk.silverbullet.telemed.device.vitalographlungmonitor.packet.VitalographPacket;
import dk.silverbullet.telemed.utils.Util;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;
import java.util.regex.Pattern;

public class VitalographLungMonitorController extends Thread implements PacketReceiver, LungMonitorController {
    private static final String TAG = Util.getTag(VitalographLungMonitorController.class);
    private static final UUID SERIAL_SERVICE_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private final BluetoothConnector connector = new BluetoothConnector();
    private final BluetoothDevice device;
    private final Object writeSemaphore = new Object();
    private volatile boolean running;
    private BluetoothSocket socket;
    private InputStream inputStream;
    private OutputStream outputStream;
    private LungMonitorPacketCollector packetCollector;
    private LungMonitorListener listener;

    public static LungMonitorController create(LungMonitorListener listener) throws DeviceInitialisationException {
        return new VitalographLungMonitorController(listener);
    }

    public VitalographLungMonitorController(LungMonitorListener listener) throws DeviceInitialisationException {
        this.listener = listener;
        connector.initiate();
        device = connector.getDevice(Pattern.compile("LUNG_.*"), "10:00:E8:");
        start();
    }

    @Override
    public void close() {
        running = false;
        interrupt();
        closeBluetoothSocket();
    }

    @Override
    public void run() {
        int retries = 0;
        running = true;
        packetCollector = new LungMonitorPacketCollector();
        packetCollector.setListener(this);

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
                    listener.connected();
                    Log.d(TAG, "Read is working, now open output!");
                    synchronized (writeSemaphore) {
                        outputStream = socket.getOutputStream();
                        writeSemaphore.notify();
                    }

                    Log.d(TAG, "Output opened, now start working!");
                    int read = inputStream.read();
                    packetCollector.reset();
                    retries = 0;
                    while (read >= 0 && running) {
                        packetCollector.receive((byte) read);
                        read = inputStream.read();
                    }
                } catch (IOException ioe) {
                    Log.d(TAG, "Reader exception: " + ioe);
                } finally {
                    closeBluetoothSocket();
                }
            }
        } catch (InterruptedException e) {
            Log.d(TAG, "Reader thread was interrupted!");
        } finally {
            Log.d(TAG, "Reader thread stopped!");
            running = false;
        }
    }

    private synchronized void closeBluetoothSocket() {
        Log.i(TAG, "Closing Bluetooth socket");
        try {
            if (socket != null) {
                socket.close();
            }
        } catch (IOException e) {
            Log.i(TAG, "Could not close Bluetooth socket", e);
        } finally {
            socket = null;
        }
    }

    @Override
    public void receive(VitalographPacket packet) {
        if (packet instanceof FevMeasurementPacket) {
            FevMeasurementPacket fevMeasurement = (FevMeasurementPacket) packet;
            LungMeasurement measurement = new LungMeasurement(fevMeasurement.getFev1(), fevMeasurement.getFev6(),
                    fevMeasurement.getFev1Fev6Ratio(), fevMeasurement.getFef2575(), fevMeasurement.isGoodTest(),
                    fevMeasurement.getSoftwareVersion());
            listener.measurementReceived(fevMeasurement.getDeviceId(), measurement);
        }
    }

    @Override
    public void error(IOException e) {
        listener.temporaryProblem();
    }

    @Override
    public void sendByte(byte b) throws IOException {
        synchronized (writeSemaphore) {
            if (outputStream == null) {
                throw new IllegalStateException("No outputstream");
            }
            outputStream.write(b);
        }
    }
}
