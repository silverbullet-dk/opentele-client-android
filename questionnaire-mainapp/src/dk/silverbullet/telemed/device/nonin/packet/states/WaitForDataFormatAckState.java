package dk.silverbullet.telemed.device.nonin.packet.states;

import android.util.Log;
import dk.silverbullet.telemed.device.nonin.packet.NoninPacketCollector;

public class WaitForDataFormatAckState extends ReceiverState {
    private NoninPacketCollector noninPacketCollector;

    public WaitForDataFormatAckState(NoninPacketCollector noninPacketCollector) {
        super(noninPacketCollector);
        this.noninPacketCollector = noninPacketCollector;
    }

    @Override
    public void receive(int in) {
        if(in == ACK) {
            noninPacketCollector.clearBuffer();
            noninPacketCollector.receivedDataFormatChanged();
        } else {
            Log.d(stateController.TAG, "Expected ACK but got:" + Integer.toHexString(in));
        }
    }

}
