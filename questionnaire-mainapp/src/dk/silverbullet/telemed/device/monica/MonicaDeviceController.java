package dk.silverbullet.telemed.device.monica;

import android.content.Context;
import android.util.Log;
import dk.silverbullet.telemed.device.DeviceInitialisationException;
import dk.silverbullet.telemed.device.DeviceNotFoundException;
import dk.silverbullet.telemed.device.monica.packet.*;
import dk.silverbullet.telemed.device.monica.packet.PatientStatusMessage.Status;
import dk.silverbullet.telemed.questionnaire.R;
import dk.silverbullet.telemed.questionnaire.node.monica.DeviceState;
import dk.silverbullet.telemed.questionnaire.node.monica.MonicaDeviceCallback;
import dk.silverbullet.telemed.utils.DataLogger;
import dk.silverbullet.telemed.utils.Util;

import java.io.IOException;
import java.util.Date;

public class MonicaDeviceController extends Thread implements MonicaDevice {
    private static final String TAG = "MonicaDeviceController";
    private final MonicaDeviceCallback monicaCallback;
    private Context context;
    private MonicaBluetoothIOController btio;
    private boolean orange;
    private boolean white;
    private boolean green;
    private boolean black;
    private boolean yellow;

    public MonicaDeviceController(MonicaDeviceCallback monicaCallback, Context context) throws DeviceInitialisationException {
        this.monicaCallback = monicaCallback;
        this.context = context;
        try {
            monicaCallback.setState(DeviceState.WAITING_FOR_CONNECTION);
            btio = new MonicaBluetoothIOController();
            start();
            Log.d(TAG, "All is well!");
        } catch (IOException e) {
            btio = null;
            throw new DeviceInitialisationException("Could not initialize connection to device", e);
        } catch (InterruptedException e) {
            btio = null;
            throw new DeviceInitialisationException("Could not initialize connection to device", e);
        }
    }

    @Override
    public void run() {
        try {
            btio.checkDevice();
            monicaCallback.setDeviceIdString(btio.getMacAddress());
            monicaCallback.setState(DeviceState.CHECKING_STARTING_CONDITION);
            checkStartState();
            monicaCallback.setState(DeviceState.WAITING_FOR_DATA);
            collectData();
        } catch (DeviceNotFoundException dne) {
            Log.w(TAG, dne);
            monicaCallback.abort(Util.getString(R.string.monica_device_not_found, context));
        } catch (BatteryTooLowException btl) {
            Log.w(TAG, btl);
            monicaCallback.done(Util.getString(R.string.monica_battery_low, context));
        } catch (DeviceSwitchedOffException off) {
            Log.w(TAG, off);
            monicaCallback.done(Util.getString(R.string.monica_switched_off, context));
        } catch (UnknownFirmwareVersionException e) {
            Log.w(TAG, e);
            monicaCallback.abort(Util.getString(R.string.monica_unknown_firmware, context, e.getVersion()));
        } catch (DeviceInitialisationException e) {
            Log.w(TAG, e);
            monicaCallback.abort(Util.getString(R.string.monica_device_initialisation_error, context));
        } catch (IOException e) {
            Log.w(TAG, e);
            monicaCallback.done(Util.getString(R.string.monica_communication_error, context));
        } catch (InterruptedException e) {
            Log.w(TAG, e);
            monicaCallback.done(Util.getString(R.string.monica_reading_interrupted, context));
        } catch (MonicaSamplesMissingException e) {
            Log.w(TAG, e);
            monicaCallback.done(Util.getString(R.string.monica_missing_sample, context));
        } finally {
            monicaCallback.setState(DeviceState.CLOSING);
            try {
                checkEndState();
            } catch (IOException ex) {
                Log.w("IOException while shutting down!", ex);
            }
            monicaCallback.setState(DeviceState.PROCESSING_DATA);
            monicaCallback.done();
            close();
            Log.d(TAG, "BTIO closed!");
        }
    }

    private void collectData() throws InterruptedException, IOException, MonicaSamplesMissingException,
            DeviceSwitchedOffException {
        btio.clearReadQueue();
        btio.writeMessage(MessageFactory.selectContinuousMode());
        int count = 0;
        MessageProcessor mp = new MessageProcessor(monicaCallback);

        long lastMessageTime = System.currentTimeMillis();

        while (count < monicaCallback.getSampleTimeMinutes() * 60) {

            try {
                if (btio.messagesWaiting() == 0) {
                    if (count > 0 && System.currentTimeMillis() - lastMessageTime > 30000) {
                        Log.d(TAG, "Read timeout - no connection for " + (System.currentTimeMillis() - lastMessageTime)
                                + " ms");
                        mp.flushMessageBuffer();
                        break;
                    }

                    Date startTime = monicaCallback.getStartTimeValue();
                    long sampleTime = monicaCallback.getSampleTimeMinutes() * 60 * 1000;

                    if (startTime != null && System.currentTimeMillis() >= startTime.getTime() + sampleTime + 20000) {
                        Log.d(TAG, "Time is up - got all requested data (startTime: " + startTime + ")");
                        mp.flushMessageBuffer();
                        break;
                    }

                    sleep(500);
                }

                if (btio.messagesWaiting() == 0 && count == 0) {
                    Log.d(TAG, "*** Ping....");
                    btio.writeMessage(MessageFactory.downloadData(), 1, 3000);
                }

                MonicaMessage msg;
                if (count == 0)
                    msg = btio.readMessage(10000);
                else
                    msg = btio.readMessage(3000);

                if (msg instanceof CBlockMessage) {
                    count++;

                    if (msg.getReadTime() == null)
                        lastMessageTime = System.currentTimeMillis();
                    else
                        lastMessageTime = msg.getReadTime().getTime();

                    mp.process((CBlockMessage) msg);
                } else if (msg instanceof MmMessage) {
                    mp.process((MmMessage) msg);
                } else if (msg instanceof FetalHeightAndSignalToNoise) {
                    mp.process((FetalHeightAndSignalToNoise) msg);
                } else if (msg instanceof DeviceOffMessage) {
                    btio.close();
                    btio = null;
                    throw new DeviceSwitchedOffException();
                } else if (msg instanceof ImpedanceStatus) { // May never be received at this stage
                    updateImpedanceStatus((ImpedanceStatus) msg);
                } else
                    Log.i(TAG, "Unexpected message: " + msg);
            } catch (IOException ioe) {
                monicaCallback.setState(DeviceState.WAITING_FOR_CONNECTION);
                Log.d(TAG, "Got an IOException while reading!");
            }
        }
        monicaCallback.setEndTimeValue(new Date());
        if (btio != null)
            btio.writeMessage(MessageFactory.halt());

    }

    private void checkStartState() throws IOException, InterruptedException, BatteryTooLowException,
            DeviceInitialisationException {
        Log.d(TAG, "checkStartState");
        btio.clearReadQueue();
        BatteryVoltageMessage voltage = btio.writeAndRead(MessageFactory.requestBatteryLevel(),
                BatteryVoltageMessage.class);
        while (voltage.getVoltage() < 0.01) {
            Log.d(TAG, "Bad battery voltage message: " + voltage);
            sleep(500);
            voltage = btio.writeAndRead(MessageFactory.requestBatteryLevel(), BatteryVoltageMessage.class);
        }
        Log.d(TAG, "Battery voltage: " + voltage);
        monicaCallback.setStartVoltage(voltage.getVoltage());
        if (voltage.getVoltage() < 3.85) // Problems observed when voltage is below 3.85 V in firmware 2.1
            throw new BatteryTooLowException();

        PatientStatusMessage status = btio.writeAndRead(MessageFactory.setPatientStatus(Status.L_AND_D, true),
                PatientStatusMessage.class);

        if (status.getStatus() != Status.L_AND_D) {
            throw new UnexpectedPatientStatus(Status.L_AND_D, status);
        }

        // Check electrodes/data status...
        int okCount = 0;
        for (int i = 0; i < 60 && okCount < 5; i++) {

            MonicaMessage msg = btio.writeAndRead(MessageFactory.requestImpedanceStatus1(), MonicaMessage.class, 3,
                    20000);

            if (msg instanceof ImpedanceStatus) {
                ImpedanceStatus imp = (ImpedanceStatus) msg;
                Log.d(TAG, imp.toString());
                updateImpedanceStatus(imp);

                if (black && green && white && orange && yellow)
                    okCount++;
                else
                    okCount = 0;

                if (okCount < 5 && btio.messagesWaiting() == 0)
                    sleep(500);

            } else if (msg instanceof GotDataMessage) { // Sent without request..!!
                Log.d(TAG, "Attempting to delete unknown existing data...");
                btio.writeMessage(MessageFactory.deleteExistingData());
                sleep(500);
            } else {
                Log.d(TAG, "Ignoring: " + msg);
            }
        }

        if (okCount < 5) {
            throw new DeviceInitialisationException("");
        }
    }

    private void updateImpedanceStatus(ImpedanceStatus imp) {
        Log.d(TAG, imp.toString());
        orange = imp.isOrangeOK();
        white = imp.isWhiteOK();
        green = imp.isGreenOK();
        black = imp.isBlackOK();
        yellow = imp.isYellowOK();
        monicaCallback.setProbeState(orange, white, green, black, yellow);
    }

    private void checkEndState() throws IOException {
        Log.d(TAG, "checkEndState");
        if (btio != null) {
            btio.clearReadQueue();
            BatteryVoltageMessage voltage = btio.writeAndRead(MessageFactory.requestBatteryLevel(),
                    BatteryVoltageMessage.class);
            monicaCallback.setEndVoltage(voltage.getVoltage());
            btio.writeMessage(MessageFactory.deleteDataAndSwitchOff());
            btio.close();
        }
    }

    @Override
    public void close() {
        DataLogger.close();
        if (btio != null) {
            try {
                interrupt();
                join();
                btio.clearReadQueue();
                btio.writeMessage(MessageFactory.deleteDataAndSwitchOff());
                btio.close();
                btio = null;
            } catch (IOException ex) {
                // Ignore
            } catch (InterruptedException ex) {
                // Ignore
            }
        }
    }
}
