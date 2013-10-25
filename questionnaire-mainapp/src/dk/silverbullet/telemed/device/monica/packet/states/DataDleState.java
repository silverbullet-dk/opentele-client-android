package dk.silverbullet.telemed.device.monica.packet.states;

import dk.silverbullet.telemed.device.monica.packet.MonicaPacketCollector;

public class DataDleState extends ReceiverState {

    public DataDleState(MonicaPacketCollector stateController) {
        super(stateController);
    }

    @Override
    public void receive(byte in) {
        if (in == STX) {
            stateController.clearBuffer();
            stateController.addByte(DLE);
            stateController.addByte(STX);
            stateController.setState(stateController.DATA_STATE);
        } else if (in == ETX) {
            stateController.addByte(DLE);
            stateController.addByte(ETX);
            stateController.setState(stateController.CRC1_STATE);
        } else {
            stateController.addByte(DLE);
            stateController.addByte(in);
            stateController.setState(stateController.DATA_STATE);
        }
    }

}
