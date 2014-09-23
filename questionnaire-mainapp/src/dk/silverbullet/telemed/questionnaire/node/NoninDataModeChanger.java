package dk.silverbullet.telemed.questionnaire.node;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.util.Log;
import dk.silverbullet.telemed.device.DeviceInitialisationException;
import dk.silverbullet.telemed.device.bluetooth.BluetoothConnector;
import dk.silverbullet.telemed.utils.Util;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;
import java.util.regex.Pattern;

public class NoninDataModeChanger extends Thread {


    private static final Pattern DEVICE_NAME_PATTERN = Pattern.compile("Nonin_Medical_Inc._\\d{6}");
    private static final String MAC_ADDRESS_FOR_NONIN_MEDICAL_INC = "00:1C:05:";
    private static final UUID SERIAL_SERVICE_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private static final String TAG = Util.getTag(NoninDataModeChanger.class);

    private final BluetoothConnector connector = new BluetoothConnector();
    private final BluetoothDevice device;
    private final Object writeSemaphore = new Object();
    private final SetNoninDataModeNode parent;
    private final boolean resetToDataformat13;
    private volatile boolean running;
    private BluetoothSocket socket;
    private InputStream inputStream;
    private OutputStream outputStream;

    public NoninDataModeChanger(SetNoninDataModeNode parent, boolean resetToDataformat13) throws DeviceInitialisationException {
        this.parent = parent;
        this.resetToDataformat13 = resetToDataformat13;
        connector.initiate();
        device = connector.getDevice(DEVICE_NAME_PATTERN, MAC_ADDRESS_FOR_NONIN_MEDICAL_INC);
        start();
    }

    @Override
    public void run() {
        running = true;

        try {
            while (running) {
                sleep(1000);

                try {
                    socket = device.createInsecureRfcommSocketToServiceRecord(SERIAL_SERVICE_UUID);
                    if (socket == null) {
                        throw new IOException("NullSocket!");
                    }
                    socket.connect();
                    inputStream = socket.getInputStream();

                    synchronized (writeSemaphore) {
                        outputStream = socket.getOutputStream();
                        writeSemaphore.notify();
                    }

                    if(resetToDataformat13) {
                        resetDataformatToDefault();
                    } else {
                        sendChangeDataFormatCommand();
                    }

                    int read = inputStream.read();

                    if(read == 6) {
                        parent.dataformatChanged();
                        break;
                    }

                } catch (IOException ioe) { //Could not connect or the Nonin device has closed the connection.
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


    public void sendChangeDataFormatCommand() {
        Log.d(TAG, "sending change data format command");
        byte[] command = new byte[6];
        command[0] = Byte.parseByte("02", 16);
        command[1] = Byte.parseByte("70", 16);
        command[2] = Byte.parseByte("02", 16);
        command[3] = Byte.parseByte("02", 16);
        command[4] = Byte.parseByte("08", 16);
        command[5] = Byte.parseByte("03", 16);

        try {
            outputStream.write(command);
            outputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void resetDataformatToDefault() {
        Log.d(TAG, "resetting data format command");
        byte[] command = new byte[6];
        command[0] = Byte.parseByte("02", 16);
        command[1] = Byte.parseByte("70", 16);
        command[2] = Byte.parseByte("02", 16);
        command[3] = Byte.parseByte("02", 16);
        command[4] = Byte.parseByte("0D", 16);
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
}
