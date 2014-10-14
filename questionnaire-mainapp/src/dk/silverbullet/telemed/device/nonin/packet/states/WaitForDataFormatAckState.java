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
    public boolean receive(int in) {
        if(in == ACK || 0x15 == in) {
            if(0x15 == in) Log.d(stateController.TAG, "!!! We got an 'unkown data format' response, but are continuing");
            noninPacketCollector.clearBuffer();
            noninPacketCollector.receivedDataFormatChanged();
            return true;
        } else {
            Log.d(stateController.TAG, "Expected ACK but got:" + Integer.toHexString(in));
        }


        return false;
    }

    @Override
    public void entering()
    {

    }

}
