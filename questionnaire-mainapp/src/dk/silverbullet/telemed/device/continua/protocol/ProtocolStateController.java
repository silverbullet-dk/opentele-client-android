package dk.silverbullet.telemed.device.continua.protocol;

import java.io.IOException;

import android.util.Log;
import dk.silverbullet.telemed.device.continua.ContinuaPacketTag;
import dk.silverbullet.telemed.device.continua.PacketParser;
import dk.silverbullet.telemed.device.continua.packet.SystemId;
import dk.silverbullet.telemed.device.continua.packet.input.AbortCommunicationPacket;
import dk.silverbullet.telemed.device.continua.packet.input.AssociationReleaseRequestPacket;
import dk.silverbullet.telemed.device.continua.packet.input.AssociationRequestPacket;
import dk.silverbullet.telemed.device.continua.packet.input.ConfirmedMeasurementDataPacket;
import dk.silverbullet.telemed.device.continua.packet.output.AssociationReleaseResponsePacket;
import dk.silverbullet.telemed.device.continua.packet.output.AssociationResponsePacket;
import dk.silverbullet.telemed.device.continua.packet.output.ConfirmedMeasurementResponsePacket;
import dk.silverbullet.telemed.utils.Util;

public abstract class ProtocolStateController<MeasurementType, ConfirmedMeasurementsType extends ConfirmedMeasurementDataPacket>
        implements PacketParser {
    private static final String TAG = Util.getTag(ProtocolStateController.class);
    private static final SystemId DUMMY_TABLET_SYSTEM_ID = new SystemId("12");
    private static final int MAX_RESET_COUNT = 5;
    private int resetCount = 0;

    public static enum State {
        UNASSOCIATED, ASSOCIATED, MEASUREMENT_RECEIVED, DONE
    };

    protected final ProtocolStateListener<MeasurementType> listener;
    protected State currentState = State.UNASSOCIATED;
    protected SystemId systemId;

    public ProtocolStateController(ProtocolStateListener<MeasurementType> listener) {
        this.listener = listener;
    }

    protected abstract ConfirmedMeasurementsType createConfirmedMeasurementsType(byte[] contents) throws IOException;

    protected abstract void handleConfirmedMeasurements(ConfirmedMeasurementsType confirmedMeasurements);

    protected abstract void handleAssociationReleaseRequest(
            AssociationReleaseRequestPacket associationReleaseRequestPacket);

    public void receive(AssociationRequestPacket associationRequest) {
        Log.d(TAG, "Received AssociationRequestPacket: " + associationRequest);

        if (currentState == State.DONE) {
            Log.w(TAG, "Ignored - in state done!");
        } else if (currentState == State.UNASSOCIATED) {
            systemId = associationRequest.getSystemId();
            try {
                listener.sendPacket(new AssociationResponsePacket(DUMMY_TABLET_SYSTEM_ID));
                currentState = State.ASSOCIATED;
            } catch (IOException ex) {
                // Ignore! We're ins state UNASSOCIATED anyway!
            }
        } else {
            Log.e(TAG, "Unexpected protocol state (" + currentState + ") - resetting!");
            resetProtocol();
        }
    }

    public void receive(ConfirmedMeasurementsType confirmedMeasurements) {
        Log.d(TAG, "Received ConfirmedMeasurementData: " + confirmedMeasurements);

        if (currentState == State.DONE) {
            Log.w(TAG, "Ignored - in state done!");
        } else if (currentState == State.ASSOCIATED) {
            handleConfirmedMeasurements(confirmedMeasurements);

            try {
                listener.sendPacket(new ConfirmedMeasurementResponsePacket(confirmedMeasurements.getInvokeId(),
                        confirmedMeasurements.getEventType()));
            } catch (IOException ex) {
                currentState = State.UNASSOCIATED;
            }
        } else {
            Log.e(TAG, "Unexpected protocol state (" + currentState + ") - resetting!");
            resetProtocol();
        }
    }

    public void receive(AssociationReleaseRequestPacket associationReleaseRequestPacket) {
        Log.d(TAG, "Received AssociationReleaseRequestPacket: " + associationReleaseRequestPacket);

        if (currentState == State.DONE) {
            Log.w(TAG, "Ignored - in state done!");
        } else if (currentState == State.ASSOCIATED || currentState == State.MEASUREMENT_RECEIVED) {
            handleAssociationReleaseRequest(associationReleaseRequestPacket);

            try {
                listener.sendPacket(new AssociationReleaseResponsePacket());
            } catch (IOException e) {
                Log.w(TAG, "Could not send association release response", e);
            }

            currentState = State.DONE;
            listener.finish();
        } else {
            Log.e(TAG, "Unexpected protocol state (" + currentState + ") - resetting!");
            resetProtocol();
        }
    }

    public void receive(AbortCommunicationPacket abortCommunication) {
        Log.d(TAG, "Received AbortCommunication: " + abortCommunication);
        resetProtocol();
    }

    @Override
    public void errorReceived(IOException ex) {
        Log.e(TAG, "ProtocolStateController received an error", ex);
        resetProtocol();
    }

    private void resetProtocol() {
        if (currentState == State.DONE) {
            return;
        }

        resetCount++;
        Log.w(TAG, "Protocol reset, count=" + resetCount);
        if (resetCount >= MAX_RESET_COUNT) {
            listener.tooManyRetries();
            currentState = State.DONE;
        } else {
            currentState = State.UNASSOCIATED;
        }
    }

    @Override
    public void reset() {
        Log.d(TAG, "Received reset!");
        currentState = State.UNASSOCIATED;
        resetCount = 0;
    }

    @Override
    public void handle(ContinuaPacketTag tag, byte[] contents) throws IOException {
        switch (tag) {
        case AARQ_APDU:
            receive(new AssociationRequestPacket(contents));
            break;
        case PRST_APDU:
            receive(createConfirmedMeasurementsType(contents));
            break;
        case ABRT_APDU:
            receive(new AbortCommunicationPacket(contents));
            break;
        case RLRQ_APDU:
            receive(new AssociationReleaseRequestPacket(contents));
            break;
        default:
            throw new IllegalStateException("Uknonwn packet tag: '" + tag + "'");
        }
    }
}
