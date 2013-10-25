package dk.silverbullet.telemed.device.andbloodpressure.protocol;

import java.io.IOException;

import dk.silverbullet.telemed.device.andbloodpressure.BloodPressureAndPulse;
import dk.silverbullet.telemed.device.andbloodpressure.packet.input.AndBloodPressureConfirmedMeasurementDataPacket;
import dk.silverbullet.telemed.device.continua.packet.input.AssociationReleaseRequestPacket;
import dk.silverbullet.telemed.device.continua.protocol.ProtocolStateController;
import dk.silverbullet.telemed.device.continua.protocol.ProtocolStateListener;

/**
 * Implementation of the protocol specific for the A&amp;D blood pressure device.
 */
public class AndBloodPressureProtocolStateController extends
        ProtocolStateController<BloodPressureAndPulse, AndBloodPressureConfirmedMeasurementDataPacket> {
    private static final int NO_TIME = -1;

    private long timestampForBloodPressure = NO_TIME;
    private int systolicBloodPressure;
    private int diastolicBloodPressure;
    private int meanArterialPressure;

    private long timestampForPulse = NO_TIME;
    private int pulse;

    public AndBloodPressureProtocolStateController(ProtocolStateListener<BloodPressureAndPulse> listener) {
        super(listener);
    }

    @Override
    protected AndBloodPressureConfirmedMeasurementDataPacket createConfirmedMeasurementsType(byte[] contents)
            throws IOException {
        return new AndBloodPressureConfirmedMeasurementDataPacket(contents);
    }

    @Override
    protected void handleConfirmedMeasurements(AndBloodPressureConfirmedMeasurementDataPacket confirmedMeasurement) {
        if (confirmedMeasurement.hasBloodPressure()
                && confirmedMeasurement.getBloodPressureTimestamp() > timestampForBloodPressure) {
            systolicBloodPressure = confirmedMeasurement.getSystolicBloodPressure();
            diastolicBloodPressure = confirmedMeasurement.getDiastolicBloodPressure();
            meanArterialPressure = confirmedMeasurement.getMeanArterialPressure();
            timestampForBloodPressure = confirmedMeasurement.getBloodPressureTimestamp();
        }
        if (confirmedMeasurement.hasPulse() && confirmedMeasurement.getPulseTimestamp() > timestampForPulse) {
            pulse = confirmedMeasurement.getPulse();
            timestampForPulse = confirmedMeasurement.getPulseTimestamp();
        }
    }

    @Override
    protected void handleAssociationReleaseRequest(AssociationReleaseRequestPacket associationReleaseRequestPacket) {
        if (timestampForBloodPressure == timestampForPulse && timestampForBloodPressure != NO_TIME) {
            listener.measurementReceived(systemId, new BloodPressureAndPulse(systolicBloodPressure,
                    diastolicBloodPressure, meanArterialPressure, pulse));
        } else {
            listener.noMeasurementsReceived();
        }
    }
}
