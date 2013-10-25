package dk.silverbullet.telemed.device.continua.android;

import java.io.IOException;
import java.util.regex.Pattern;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothHealth;
import android.bluetooth.BluetoothHealthAppConfiguration;
import android.bluetooth.BluetoothHealthCallback;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.os.ParcelFileDescriptor;
import android.util.Log;
import dk.silverbullet.telemed.device.DeviceInitialisationException;
import dk.silverbullet.telemed.device.bluetooth.BluetoothConnector;
import dk.silverbullet.telemed.device.continua.HdpController;
import dk.silverbullet.telemed.device.continua.HdpListener;
import dk.silverbullet.telemed.device.continua.HdpProfile;
import dk.silverbullet.telemed.device.continua.PacketCollector;
import dk.silverbullet.telemed.utils.Util;

/**
 * Handles all the Bluetooth-specific communication for communicating with an HDP device.
 */
public class AndroidHdpController implements HdpController, BluetoothProfile.ServiceListener {
    private static final String TAG = Util.getTag(AndroidHdpController.class);

    private static enum State {
        SERVICE_NOT_CONNECTED, SERVICE_CONNECTED, APPLICATION_REGISTERED, DEVICE_CONNECTING, DEVICE_CONNECTED, DEVICE_DISCONNECTING, CLOSED
    }

    private BluetoothHealthAppConfiguration healthApplicationConfiguration;
    private BluetoothHealth bluetoothHealth;
    private BluetoothDevice device;
    private int channelId;

    private final BluetoothConnector connector = new BluetoothConnector();
    private final Context context;
    private final Object stateSemaphore = new Object();
    private final BluetoothStreamer streamer;
    private final Stopwatch stopwatch = new Stopwatch();

    private HdpProfile hdpProfile;
    private HdpListener listener;
    private boolean pollForConnection;
    private State currentState = State.SERVICE_NOT_CONNECTED;
    private boolean shuttingDown;
    private boolean applicationUnregistrationScheduled;
    private long applicationShutdownTime;

    /**
     * @param context
     *            Is unfortunately required for setting up Bluetooth Health Profile
     */
    public AndroidHdpController(Context context) {
        this.context = context;
        this.streamer = new BluetoothStreamer();
    }

    @Override
    public void setHdpProfile(HdpProfile hdpProfile) {
        this.hdpProfile = hdpProfile;
    }

    @Override
    public void setPacketCollector(PacketCollector packetCollector) {
        streamer.setPacketCollector(packetCollector);
    }

    @Override
    public void setBluetoothListener(HdpListener listener) {
        this.listener = listener;
    }

    public void setPollForConnection(boolean pollForConnection) {
        this.pollForConnection = pollForConnection;
    }

    @Override
    public void initiate(Pattern deviceNamePattern, String deviceMacAddressPrefix) throws DeviceInitialisationException {
        connector.initiate();
        device = connector.getDevice(deviceNamePattern, deviceMacAddressPrefix);
        connector.openHdp(context, this);
    }

    @Override
    public void terminate() {
        synchronized (stateSemaphore) {
            if (shuttingDown) {
                Log.d(TAG, "Already closing");
            } else {
                Log.d(TAG, "Closing. Current state: " + currentState);
                shuttingDown = true;
                stopwatch.cancel();

                if (currentState == State.DEVICE_CONNECTED) {
                    streamer.stopReading();
                    disconnectChannel();
                } else if (currentState == State.APPLICATION_REGISTERED) {
                    scheduleApplicationUnregistration();
                }
            }

            waitForState(State.CLOSED);
        }
    }

    @Override
    public void send(byte[] contents) throws IOException {
        streamer.write(contents);
    }

    @Override
    public void onServiceConnected(int profile, BluetoothProfile proxy) {
        if (profile != BluetoothProfile.HEALTH) {
            Log.d(TAG, "onServiceConnected profile==" + profile);
            return;
        }

        Log.d(TAG, "onServiceConnected");
        synchronized (stateSemaphore) {
            bluetoothHealth = (BluetoothHealth) proxy;
            goFromStateToState(State.SERVICE_NOT_CONNECTED, State.SERVICE_CONNECTED);
            Log.d(TAG, "onServiceConnected to profile: " + profile);

            if (shuttingDown) {
                closeProfileProxy();
            } else {
                registerApplication();
            }
        }
    }

    @Override
    public void onServiceDisconnected(int profile) {
        if (profile != BluetoothProfile.HEALTH) {
            Log.d(TAG, "onServiceDisconnected profile==" + profile);
            return;
        }

        Log.d(TAG, "onServiceDisconnected");
        synchronized (stateSemaphore) {
            bluetoothHealth = null;
            goFromStateToState(State.SERVICE_CONNECTED, State.CLOSED);
            listener.serviceConnectionFailed();
        }
    }

    private final BluetoothHealthCallback mHealthCallback = new BluetoothHealthCallback() {
        // Callback to handle application registration and unregistration
        // events. The service passes the status back to the UI client.
        @Override
        public void onHealthAppConfigurationStatusChange(BluetoothHealthAppConfiguration config, int status) {
            Log.d(TAG, "onHealthAppConfigurationStatusChange config=" + config + " status=" + status);
            if (healthApplicationConfiguration != null && !healthApplicationConfiguration.equals(config)) {
                Log.d(TAG, "Ignoring status change because config was unexpected");
                return;
            }

            synchronized (stateSemaphore) {
                stopwatch.cancel();

                switch (status) {
                case BluetoothHealth.APP_CONFIG_REGISTRATION_SUCCESS:
                    healthApplicationConfiguration = config;
                    goFromStateToState(State.SERVICE_CONNECTED, State.APPLICATION_REGISTERED);
                    listener.applicationConfigurationRegistered();
                    if (shuttingDown) {
                        scheduleApplicationUnregistration();
                    } else {
                        streamer.startReading();
                        startStopwatch();
                    }
                    break;
                case BluetoothHealth.APP_CONFIG_REGISTRATION_FAILURE:
                    listener.applicationConfigurationRegistrationFailed();
                    goFromStateToState(State.SERVICE_CONNECTED, State.SERVICE_CONNECTED);
                    closeProfileProxy();
                    break;
                case BluetoothHealth.APP_CONFIG_UNREGISTRATION_SUCCESS:
                    healthApplicationConfiguration = null;
                    streamer.stopReading();
                    listener.applicationConfigurationUnregistered();
                    goFromStateToState(State.APPLICATION_REGISTERED, State.SERVICE_CONNECTED);
                    closeProfileProxy();
                    break;
                case BluetoothHealth.APP_CONFIG_UNREGISTRATION_FAILURE:
                    healthApplicationConfiguration = null;
                    streamer.stopReading();
                    listener.applicationConfigurationUnregistrationFailed();
                    // Logically wrong state, but there's nothing else we can do, except getting stuck
                    goFromStateToState(State.APPLICATION_REGISTERED, State.SERVICE_CONNECTED);
                    Log.w(TAG, "Application unregistration failed");
                    closeProfileProxy();
                    break;
                }
            }
        }

        // Callback to handle channel connection state changes.
        // Note that the logic of the state machine may need to be modified
        // based on the HDP device.
        // When the HDP device is connected, the received file descriptor is
        // passed to the ReadThread to read the content.
        @Override
        public void onHealthChannelStateChange(BluetoothHealthAppConfiguration config, BluetoothDevice device,
                int prevState, int newState, ParcelFileDescriptor parcelFileDescriptor, int channelId) {
            Log.d(TAG, String.format("prevState\t%d ----------> newState\t%d", prevState, newState));

            if (prevState == newState || !config.equals(healthApplicationConfiguration)) {
                Log.d(TAG, "onHealthChannelStateChange was ignored!");
                Log.d(TAG, "config: " + config);
                Log.d(TAG, "mHealthAppConfig: " + healthApplicationConfiguration);
                return;
            }

            synchronized (stateSemaphore) {
                if (newState == BluetoothHealth.STATE_CHANNEL_CONNECTED) {
                    if (currentState == State.DEVICE_CONNECTED) {
                        return;
                    }

                    goFromStateToState(State.DEVICE_CONNECTING, State.DEVICE_CONNECTED);
                    AndroidHdpController.this.channelId = channelId;
                    stopwatch.cancel();
                    listener.connectionEstablished();
                    if (shuttingDown) {
                        disconnectChannel();
                    } else {
                        streamer.replugConnection(parcelFileDescriptor);
                    }
                } else if (newState == BluetoothHealth.STATE_CHANNEL_DISCONNECTED) {
                    if (currentState == State.APPLICATION_REGISTERED) {
                        return;
                    }
                    if (shuttingDown) {
                        goFromStateToState(State.DEVICE_CONNECTING, State.APPLICATION_REGISTERED);
                        scheduleApplicationUnregistration();
                    } else {
                        if (currentState == State.DEVICE_CONNECTED || currentState == State.DEVICE_DISCONNECTING) {
                            listener.disconnected();
                        }
                        goFromStateToState(currentState, State.APPLICATION_REGISTERED);
                        streamer.unplugConnection();
                        startStopwatch();
                    }
                } else if (newState == BluetoothHealth.STATE_CHANNEL_CONNECTING) {
                    goFromStateToState(State.APPLICATION_REGISTERED, State.DEVICE_CONNECTING);
                } else if (newState == BluetoothHealth.STATE_CHANNEL_DISCONNECTING) {
                    goFromStateToState(State.DEVICE_CONNECTED, State.DEVICE_DISCONNECTING);
                }
            }
        }
    };

    private void startStopwatch() {
        if (pollForConnection) {
            stopwatch.start(3000, new StopwatchListener() {
                @Override
                public void timeout() {
                    synchronized (stateSemaphore) {
                        if (currentState != State.APPLICATION_REGISTERED) {
                            Log.d(TAG, "Stopwatch not acting, since state is not APPLICATION_REGISTERED: "
                                    + currentState);
                            return;
                        } else if (shuttingDown) {
                            Log.d(TAG, "Stopwatch not acting, since we're shutting down");
                            return;
                        }
                    }

                    // Outside of the "synchronized" block because of potential deadlock
                    Log.d(TAG, "Polling for connection....");
                    connectChannelToSource();
                }
            });
        }
    }

    private void goFromStateToState(State oldState, State newState) {
        synchronized (stateSemaphore) {
            if (currentState != oldState) {
                Log.e(TAG, "Unexpected state: " + oldState + " - actual state " + currentState);
            }

            Log.d(TAG, "Going from state " + currentState + " to " + newState);
            currentState = newState;
            stateSemaphore.notifyAll();
        }
    }

    private void waitForState(State state) {
        synchronized (stateSemaphore) {
            long startTime = System.currentTimeMillis();
            while (currentState != state) {
                try {
                    stateSemaphore.wait(500);
                    if (System.currentTimeMillis() > startTime + 40000) {
                        Log.d(TAG, "Timed out waiting for state " + state + ". Ending up in " + currentState);
                        return;
                    }
                } catch (InterruptedException e) {
                    Log.d(TAG, "Interrupted while waiting for state " + state);
                    return;
                }
            }
        }
    }

    private void registerApplication() {
        Log.d(TAG, "registerApplication()");
        runLater(new Runnable() {

            @Override
            public void run() {
                Log.d(TAG, "bluetoothHealth.registerApplication()");
                bluetoothHealth.registerSinkAppConfiguration(TAG, hdpProfile.getProfileId(), mHealthCallback);
            }

        });
    }

    private void connectChannelToSource() {
        Log.d(TAG, "connectChannelToSource()");
        bluetoothHealth.connectChannelToSource(device, healthApplicationConfiguration);
    }

    private void closeProfileProxy() {
        Log.d(TAG, "closeProfileProxy() bluetoothHealth='" + bluetoothHealth + "'");
        runLater(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "bluetoothAdapter.closeProfileProxy() bluetoothHealth='" + bluetoothHealth + "'");
                connector.closeHdp(bluetoothHealth);

                // In practice, our onServiceDisconnected never gets called by Android, so we need to
                // jump to the next state ourselves
                goFromStateToState(State.SERVICE_CONNECTED, State.CLOSED);
            }
        });
    }

    /**
     * Schedules an unregistration of the application. If called multiple times within 500ms, makes sure that the
     * application is shut down 500ms after the _last_ invocation.
     * 
     * Apparently, we cannot unregisterApplication() inside an Android Bluetooth callback - or, rather, Android never
     * sends a callback telling us that our application is unregistered. (Probably because of a deadlock in Android?) So
     * we use this method in order to make sure everything "settles" in a period of 500ms.
     */
    private void scheduleApplicationUnregistration() {
        Log.d(TAG, "scheduleApplicationUnregistration() healthApplicationConfiguration='"
                + healthApplicationConfiguration + "'");

        synchronized (stateSemaphore) {
            applicationShutdownTime = System.currentTimeMillis() + 500;
            if (applicationUnregistrationScheduled) {
                Log.d(TAG, "Application shutdown already scheduled - setting new shutdown time");
                return;
            }

            applicationUnregistrationScheduled = true;
            runLater(new Runnable() {
                @Override
                public void run() {
                    synchronized (stateSemaphore) {
                        while (!shouldShutdownApplicationNow()) {
                            try {
                                stateSemaphore.wait(timeUntilApplicationShutdown());
                            } catch (InterruptedException e) {
                                Log.d(TAG, "Interrupted while waiting for application shutdown", e);
                                Thread.currentThread().interrupt();
                            }
                        }

                        Log.d(TAG, "bluetoothHealth.unregisterAppConfiguration('" + healthApplicationConfiguration
                                + "')");
                        bluetoothHealth.unregisterAppConfiguration(healthApplicationConfiguration);
                    }
                }
            });
        }
    }

    private boolean shouldShutdownApplicationNow() {
        return timeUntilApplicationShutdown() == 0;
    }

    private long timeUntilApplicationShutdown() {
        return Math.max(applicationShutdownTime - System.currentTimeMillis(), 0);
    }

    private void disconnectChannel() {
        Log.d(TAG, "disconnectChannel()");
        runLater(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "Disconnecting channel");
                bluetoothHealth.disconnectChannel(device, healthApplicationConfiguration, channelId);
            }
        });
    }

    private void runLater(Runnable runnable) {
        new Thread(runnable).start();
    }
}
