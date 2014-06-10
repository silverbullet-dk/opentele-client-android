package dk.silverbullet.telemed.device.nonin.packet.states;

import android.util.Log;
import dk.silverbullet.telemed.device.nonin.packet.NoninPacketCollector;
import dk.silverbullet.telemed.utils.Util;

public class IdleState extends ReceiverState {
    private static final String TAG = Util.getTag(IdleState.class);
    public IdleState(NoninPacketCollector stateController) {
        super(stateController);
    }

    @Override
    public void receive(int in) {
        if(in == NULL_START_SYNC) {
            Log.i(TAG, "Got NULL_START_SYNC. Ignoring");
            return;
        }

        if (in == STX) {
            stateController.clearBuffer();
            stateController.addInt(in);
            stateController.setState(stateController.DATA_STATE);
        } else {
            Log.e(TAG, "Expected STX:" + STX + " but got:" + in);
        }
    }
}
