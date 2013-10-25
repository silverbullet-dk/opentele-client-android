package dk.silverbullet.telemed.device.monica.packet.states;

import dk.silverbullet.telemed.device.monica.packet.MonicaPacketCollector;

public class IdleState extends ReceiverState {
    public IdleState(MonicaPacketCollector stateController) {
        super(stateController);
    }

    @Override
    public void receive(byte in) {
        if (in == DLE) {
            stateController.clearBuffer();
            stateController.addByte(in);
            stateController.setState(stateController.STX_STATE);
        }
    }
}
