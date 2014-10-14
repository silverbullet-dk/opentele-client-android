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
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

public class NoninController extends Thread implements PacketReceiver, SaturationController {
    private static final String TAG = Util.getTag(NoninController.class);
    private static final Pattern DEVICE_NAME_PATTERN = Pattern.compile("Nonin_Medical_Inc._\\d{6}");
    private static final String MAC_ADDRESS_FOR_NONIN_MEDICAL_INC = "00:1C:05:";
    private static final UUID SERIAL_SERVICE_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private static final int FIRST_TRY_TIMEOUT_IN_SECONDS = 45;
    private static final int SECOND_TRY_TIMEOUT_IN_SECONDS = 55; //The device seems to take around 10 seconds to turn off
    private static final int MS_MAX_DATA_WAIT_TIME = 5000; // Max time in 1/1000'th seconds to wait before giving up listening for an answer

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
    private SaturationAndPulse lastMeasurement;

    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private ScheduledFuture<?> firstTimeout;
    private ScheduledFuture<?> lastTimeout;
    private boolean timeoutStarted;

    private static long timeOfLastDisconnectedSocket=0;
    private static boolean shouldWaitWhenReconnecting=false;

    // Must be called when we have connected on the socket interface to the nonin
    private void socketConnected() {
        // Remember we were connected to the Nonin
        shouldWaitWhenReconnecting = true;
    }

    // Should be called when we detect a disconnected socket
    private void socketDisconnected() {
        if(shouldWaitWhenReconnecting) {
            // Remember when we were disconnected from the Nonin
            timeOfLastDisconnectedSocket = System.currentTimeMillis();
        }
    }

    // Must be called before trying to connect to the Nonin, it will
    // ensure that minimum 15s has passed since the connection was lost
    // if socketDisconnected hasn't been called, it will just wait
    // 15s
    //
    // NOTE: The function will max wait 15s since "socketConnected" was called
    private void ensure15sSocketDelay() throws InterruptedException {
        // Bail if we don't care about waiting
        if(!shouldWaitWhenReconnecting) return;

        // We have a default wait time of 15s
        long waitTimeMS = 15000;

        // Check that we have a valid timestamp (we might have been connected,
        // but socketDisconnected wasn't called
        if(0 != timeOfLastDisconnectedSocket) {
            // The wait time, is the waitTimeMS minus the time already passed,
            // note that the result might be negative
            waitTimeMS -= System.currentTimeMillis() - timeOfLastDisconnectedSocket;
        }

        // Wait waitTimeMS if waitTime is positive
        if(0<waitTimeMS) {
            sleep(waitTimeMS);
        }

        // We are good as new, so reset everything
        timeOfLastDisconnectedSocket = 0;
        shouldWaitWhenReconnecting = false;
    }

    public static SaturationController create(SaturationPulseListener listener) throws DeviceInitialisationException {
        //Log.d(TAG, "Creating new noninController object!");
        return new NoninController(listener);

    }

    public NoninController(SaturationPulseListener listener) throws DeviceInitialisationException {
        this.listener = listener;
        connector.initiate();
        // Checks that the device has a pairing
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
                sleep(1000);
                Log.d(TAG, "Connecting...");
                pullDataFromDevice();
            }
        } catch (InterruptedException e) {
            Log.d(TAG, "Reader thread was interrupted!");
        } finally {
            Log.d(TAG, "Reader thread stopped!");
            cancelTimeouts();
        }
    }
     private void pullDataFromDevice() throws InterruptedException {
        try {
            setupSocket();
            setupStreams();

            packetCollector.reset();
            sendGetSerialNumberCommand();

            long timeOfLastCom = System.currentTimeMillis();

            while (running) {
                // Check if there are data available, otherwise wait,
                // but don't wait longer than max wait time
                while(0 == inputStream.available())
                {
                    long waitTime = System.currentTimeMillis() - timeOfLastCom;
                    if(waitTime > MS_MAX_DATA_WAIT_TIME) {
                        throw new IOException("Device not responding, waited " + waitTime + "ms");
                    }

                    sleep(100);
                }

                int read = inputStream.read();
                timeOfLastCom = System.currentTimeMillis();

                if( read < 0) throw new IOException("Nothing to read");

                // If the data wasn't as expected, try again
                if(!packetCollector.receive(read))
                {
                    Log.d(TAG, "Unexpected data, aborting...");
                    throw new IOException("Unexpected data received, resetting connection");
                }
            }
        } catch (IOException ioe) { //Could not connect or the Nonin device has closed the connection.
            Log.d(TAG, "Reader exception: " + ioe);
        } finally {
            // Close the BT socket
            closeBluetoothSocket();
        }
    }

    private void setupStreams() throws IOException {
        inputStream = socket.getInputStream();


        if(!timeoutStarted) {  //First time we actually connect to the device, start the timeout.
            listener.connected();
            scheduleFirstTimeout();
        }

        synchronized (writeSemaphore) {
            outputStream = socket.getOutputStream();
            writeSemaphore.notify();
        }
    }

    private void setupSocket() throws IOException, InterruptedException {
        socket = device.createInsecureRfcommSocketToServiceRecord(SERIAL_SERVICE_UUID);
        if (socket == null) {
            throw new IOException("NullSocket!");
        }

        // Make sure 15s has passed since a connection was lost
        ensure15sSocketDelay();

        // Try to connect to the socket, this will throw an exception if it fails
        socket.connect();

        // We got a path to the Nonin, so remember this event
        socketConnected();
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
            Log.e(TAG, "Error sending serial number command:" + e);
            e.printStackTrace();
        }
    }

    @Override
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

    public void sendChangeDataFormatCommand2() {
        Log.d(TAG, "sending change data format command2");
        byte[] command = new byte[8];
        command[0] = Byte.parseByte("02", 16);
        command[1] = Byte.parseByte("70", 16);
        command[2] = Byte.parseByte("04", 16);
        command[3] = Byte.parseByte("02", 16);
        command[4] = Byte.parseByte("08", 16);
        command[5] = Byte.parseByte("00", 16);
        command[6] = Byte.parseByte("7E", 16);
        command[7] = Byte.parseByte("03", 16);

        try {
            outputStream.write(command);
            outputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private synchronized void closeBluetoothSocket() {
        Log.i(TAG, "Closing Bluetooth socket");
        // Remember that we have closed/lost the BT connection
        socketDisconnected();
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


    private void scheduleFirstTimeout() {
        timeoutStarted = true;
        firstTimeout = scheduler.schedule(new Runnable() {
            @Override
            public void run() {
                listener.firstTimeOut();
                scheduleLastTimeout();
            }
        }, FIRST_TRY_TIMEOUT_IN_SECONDS, TimeUnit.SECONDS);
    }

    private void scheduleLastTimeout() {
        lastTimeout = scheduler.schedule(new Runnable() {
            @Override
            public void run() {
                listener.finalTimeOut(serialNumber, lastMeasurement);
            }
        }, SECOND_TRY_TIMEOUT_IN_SECONDS, TimeUnit.SECONDS);
    }

    private void cancelTimeouts() {
        running = false;
        if(lastTimeout != null) {
            lastTimeout.cancel(true);
        }

        if(firstTimeout != null) {
            firstTimeout.cancel(true);
        }
    }

    @Override
    public void setSerialNumber(NoninSerialNumberPacket packet) {
        this.serialNumber = packet.serial;
    }

    @Override
    public void addMeasurement(NoninMeasurementPacket packet) {
        if(packet.highQuality) {
            running = false;
            listener.measurementReceived(serialNumber, new SaturationAndPulse(packet.sp02, packet.pulse));
        } else if(!packet.measurementMissing) {
            this.lastMeasurement = new SaturationAndPulse(packet.sp02, packet.pulse);
        }
    }

    @Override
    public void error(IOException e) {
        listener.temporaryProblem();
    }
}
