package dk.silverbullet.telemed.device.andweightscale.protocol;

import java.io.IOException;

import dk.silverbullet.telemed.device.andweightscale.Weight;
import dk.silverbullet.telemed.device.andweightscale.packet.input.AndWeightScaleConfirmedMeasurementDataPacket;
import dk.silverbullet.telemed.device.continua.packet.input.AssociationReleaseRequestPacket;
import dk.silverbullet.telemed.device.continua.protocol.ProtocolStateController;
import dk.silverbullet.telemed.device.continua.protocol.ProtocolStateListener;

/**
 * Implementation of the protocol specific for the A&amp;D weight scale.
 */
public class AndWeightScaleProtocolStateController extends
        ProtocolStateController<Weight, AndWeightScaleConfirmedMeasurementDataPacket> {
    private static final int NO_TIME = -1;
    private long timestamp = NO_TIME;
    private Weight weight;

    public AndWeightScaleProtocolStateController(ProtocolStateListener<Weight> listener) {
        super(listener);
    }

    @Override
    protected AndWeightScaleConfirmedMeasurementDataPacket createConfirmedMeasurementsType(byte[] contents)
            throws IOException {
        return new AndWeightScaleConfirmedMeasurementDataPacket(contents);
    }

    @Override
    protected void handleConfirmedMeasurements(AndWeightScaleConfirmedMeasurementDataPacket confirmedMeasurement) {
        if (confirmedMeasurement.getTimestamp() > timestamp) {
            weight = confirmedMeasurement.getWeight();
            timestamp = confirmedMeasurement.getTimestamp();
        }
    }

    @Override
    protected void handleAssociationReleaseRequest(AssociationReleaseRequestPacket associationReleaseRequestPacket) {
        if (weight != null) {
            listener.measurementReceived(systemId, weight);
        } else {
            listener.noMeasurementsReceived();
        }
    }
}
