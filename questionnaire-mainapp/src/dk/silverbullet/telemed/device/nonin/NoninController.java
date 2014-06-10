package dk.silverbullet.telemed.device.nonin;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.util.Log;
import dk.silverbullet.telemed.device.DeviceInitialisationException;
import dk.silverbullet.telemed.device.bluetooth.BluetoothConnector;
import dk.silverbullet.telemed.device.nonin.packet.*;
import dk.silverbullet.telemed.utils.Util;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;
import java.util.regex.Pattern;

public class NoninController extends Thread implements PacketReceiver, SaturationController {
    private static final String TAG = Util.getTag(NoninController.class);
    private static final Pattern DEVICE_NAME_PATTERN = Pattern.compile("Nonin_Medical_Inc._\\d{6}");
    private static final String MAC_ADDRESS_FOR_NONIN_MEDICAL_INC = "00:1C:05:";
    private static final UUID SERIAL_SERVICE_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    private final BluetoothConnector connector = new BluetoothConnector();
    private final BluetoothDevice device;
    private final Object writeSemaphore = new Object();
    private volatile boolean running;
    private BluetoothSocket socket;
    private InputStream inputStream;
    private OutputStream outputStream;
    private NoninPacketCollector packetCollector;
    private SaturationPulseListener listener;

    private String serialNumber;
    private SaturationAndPulse measurement;
    private boolean measurementRecieved;

    public static SaturationController create(SaturationPulseListener listener) throws DeviceInitialisationException {
        return new NoninController(listener);
    }

    public NoninController(SaturationPulseListener listener) throws DeviceInitialisationException {
        this.listener = listener;
        connector.initiate();
        device = connector.getDevice(DEVICE_NAME_PATTERN, MAC_ADDRESS_FOR_NONIN_MEDICAL_INC);
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
        running = true;
        packetCollector = new NoninPacketCollector();
        packetCollector.setListener(this);

        try {
            while (running) {
                sleep(3000);
                Log.d(TAG, "Connecting...");

                try {
                    socket = device.createInsecureRfcommSocketToServiceRecord(SERIAL_SERVICE_UUID);
                    if (socket == null) {
                        throw new IOException("NullSocket!");
                    }
                    socket.connect();
                    inputStream = socket.getInputStream();
                    listener.connected();
                    Log.d(TAG, device.getName());

                    Log.d(TAG, "Read is working, now open output!");
                    synchronized (writeSemaphore) {
                        outputStream = socket.getOutputStream();
                        sendGetSerialNumberCommand();
                        writeSemaphore.notify();
                    }

                    Log.d(TAG, "Output opened, now start working!");
                    int read = inputStream.read();
                    packetCollector.reset();
                    measurementRecieved = false;

                    while (read >= 0 && running) {
                        packetCollector.receive(read);
                        read = inputStream.read();
                    }
                } catch (IOException ioe) { //The Nonin device has closed the connection.
                    if(measurementAndSerialNumberRecieved()) {
                        Log.d(TAG, "Measurement and Serial number received. ");
                        running = false;
                        if(measurement == null) {
                            //We have recieved at least one measurement. But either the checksum check failed or it was from memory.
                            //So we cant use it.
                            listener.temporaryProblem();
                        } else {
                            listener.measurementReceived(serialNumber, measurement);
                        }
                    }
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

    private boolean measurementAndSerialNumberRecieved() {
        return serialNumber != null && measurementRecieved;
    }

    private void sendGetSerialNumberCommand() {
        Log.d(TAG, "sending get serial number command");
        byte[] command = new byte[6];
        command[0] = Byte.parseByte("02", 16);
        command[1] = Byte.parseByte("74", 16);
        command[2] = Byte.parseByte("02", 16);
        command[3] = Byte.parseByte("02", 16);
        command[4] = Byte.parseByte("02", 16);
        command[5] = Byte.parseByte("03", 16);

        try {
            outputStream.write(command);
            outputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
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
    public void setSerialNumber(NoninSerialNumberPacket packet) {
        this.serialNumber = packet.serial;
    }

    @Override
    public void addMeasurement(NoninMeasurementPacket packet) {
        this.measurementRecieved = true;
        if(packet.isDataMissing) {
            Log.i(TAG, "Got measurement with no data");
            return;
        }

        if(packet.isFromMemory) {
            Log.i(TAG, "Got measurement from memory");
            return;
        }
        this.measurement = new SaturationAndPulse(packet.sp02, packet.pulse);
    }

    @Override
    public void error(IOException e) {
        listener.temporaryProblem();
    }
}
