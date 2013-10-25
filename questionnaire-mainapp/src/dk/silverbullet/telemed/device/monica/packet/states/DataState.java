package dk.silverbullet.telemed.device.monica.packet.states;

import dk.silverbullet.telemed.device.monica.packet.MonicaPacketCollector;

public class DataState extends ReceiverState {
    public DataState(MonicaPacketCollector stateController) {
        super(stateController);
    }

    @Override
    public void receive(byte in) {
        if (in == DLE) {
            stateController.setState(stateController.DATA_DLE);
        } else {
            stateController.addByte(in);
        }
    }

}
