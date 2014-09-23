package dk.silverbullet.telemed.device.nonin.packet.states;

import dk.silverbullet.telemed.device.nonin.packet.NoninPacketCollector;
import dk.silverbullet.telemed.utils.Util;

public class WaitForSerialNumberState extends ReceiverState {
    private static final String TAG = Util.getTag(WaitForSerialNumberState.class);
    public WaitForSerialNumberState(NoninPacketCollector stateController) {
        super(stateController);
    }

    @Override
    public void receive(int in) {
        if (in == STX) {
            stateController.clearBuffer();
            stateController.addInt(in);
            stateController.setState(stateController.SERIAL_NUMBER_DATA_STATE);
        }
    }
}
