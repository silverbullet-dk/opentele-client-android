package dk.silverbullet.telemed.device.continua;

import java.io.IOException;

import android.util.Log;
import dk.silverbullet.telemed.device.continua.android.SingleShotTimer;
import dk.silverbullet.telemed.device.continua.android.StopwatchListener;
import dk.silverbullet.telemed.device.continua.packet.SystemId;
import dk.silverbullet.telemed.device.continua.packet.output.OutputPacket;
import dk.silverbullet.telemed.device.continua.protocol.ProtocolStateListener;
import dk.silverbullet.telemed.utils.Util;

/**
 * Superclass with all the common functionality of the actual Continua controllers.
 */
public abstract class DeviceController<MeasurementType> implements ContinuaDeviceController, HdpListener,
        ProtocolStateListener<MeasurementType> {
    private static final String TAG = Util.getTag(DeviceController.class);
    private final ContinuaListener<MeasurementType> listener;
    private final HdpController hdpController;

    protected DeviceController(ContinuaListener<MeasurementType> listener, HdpController hdpController,
            HdpProfile hdpProfile) {
        this.listener = listener;
        this.hdpController = hdpController;

        hdpController.setHdpProfile(hdpProfile);
        hdpController.setBluetoothListener(this);
    }

    @Override
    public void close() {
        hdpController.terminate();
    }

    @Override
    public void applicationConfigurationRegistrationFailed() {
        listener.permanentProblem();
    }

    @Override
    public void applicationConfigurationRegistered() {
        Log.d(TAG, "applicationConfigurationRegistered");
    }

    @Override
    public void applicationConfigurationUnregistered() {
        Log.d(TAG, "applicationConfigurationUnregistered");
    }

    @Override
    public void applicationConfigurationUnregistrationFailed() {
        Log.d(TAG, "applicationConfigurationUnregistrationFailed");
    }

    @Override
    public void serviceConnectionFailed() {
        listener.permanentProblem();
    }

    @Override
    public void connectionEstablished() {
        listener.connected();
    }

    @Override
    public void disconnected() {
        listener.disconnected();
    }

    @Override
    public void measurementReceived(SystemId systemId, MeasurementType measurement) {
        listener.measurementReceived(systemId.asString(), measurement);
    }

    @Override
    public void noMeasurementsReceived() {
        listener.temporaryProblem();
    }

    @Override
    public void sendPacket(OutputPacket packet) throws IOException {
        Log.d(TAG, "Sending " + packet);
        hdpController.send(packet.getContents());
    }

    @Override
    public void tooManyRetries() {
        listener.permanentProblem();
    }

    @Override
    public void finishNow() {
        close();
    }

    @Override
    public void finish() {
        new SingleShotTimer(1000, new StopwatchListener() {
            @Override
            public void timeout() {
                finishNow();
            }
        });
    }
}
