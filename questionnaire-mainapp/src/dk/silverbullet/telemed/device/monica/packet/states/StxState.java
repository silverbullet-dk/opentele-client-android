package dk.silverbullet.telemed.device.monica.packet.states;

import java.util.Date;

import dk.silverbullet.telemed.device.monica.packet.MonicaPacketCollector;

public class StxState extends ReceiverState {
    public StxState(MonicaPacketCollector stateController) {
        super(stateController);
    }

    @Override
    public void receive(byte in) {
        if (in == STX) {
            stateController.setReadTime(new Date());
            stateController.addByte(in);
            stateController.setState(stateController.DATA_STATE);
        } else {
            stateController.setState(stateController.IDLE_STATE);
        }
    }

}
