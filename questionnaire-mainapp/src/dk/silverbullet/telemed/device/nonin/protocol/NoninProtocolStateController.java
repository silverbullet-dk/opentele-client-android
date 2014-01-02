package dk.silverbullet.telemed.device.nonin.protocol;

import java.io.IOException;

import android.util.Log;
import dk.silverbullet.telemed.device.continua.packet.input.AssociationReleaseRequestPacket;
import dk.silverbullet.telemed.device.continua.protocol.ProtocolStateController;
import dk.silverbullet.telemed.device.continua.protocol.ProtocolStateListener;
import dk.silverbullet.telemed.device.nonin.SaturationAndPulse;
import dk.silverbullet.telemed.device.nonin.packet.input.NoninConfirmedMeasurementDataPacket;
import dk.silverbullet.telemed.utils.Util;

/**
 * Implementation of the protocol specific for the Nonin device.
 */
public class NoninProtocolStateController extends
        ProtocolStateController<SaturationAndPulse, NoninConfirmedMeasurementDataPacket> {
    private static final String TAG = Util.getTag(NoninProtocolStateController.class);
    private final ProtocolStateListener<SaturationAndPulse> listener;

    private int pulse;
    private int saturation;

    public NoninProtocolStateController(ProtocolStateListener<SaturationAndPulse> listener) {
        super(listener);
        this.listener = listener;
    }

    @Override
    protected NoninConfirmedMeasurementDataPacket createConfirmedMeasurementsType(byte[] contents) throws IOException {
        return new NoninConfirmedMeasurementDataPacket(contents);
    }

    @Override
    public void handleConfirmedMeasurements(NoninConfirmedMeasurementDataPacket confirmedMeasurement) {
        pulse = confirmedMeasurement.getPulse();
        saturation = confirmedMeasurement.getSaturation();

        listener.measurementReceived(systemId, new SaturationAndPulse(saturation, pulse));
    }

    @Override
    protected void handleAssociationReleaseRequest(AssociationReleaseRequestPacket associationReleaseRequestPacket) {
        // Does not occur for Nonin devices
        Log.i(TAG, "Association release request received for Nonin device");
    }
}
