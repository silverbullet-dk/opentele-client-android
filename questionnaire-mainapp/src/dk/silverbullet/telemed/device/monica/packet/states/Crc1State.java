package dk.silverbullet.telemed.device.monica.packet.states;

import dk.silverbullet.telemed.device.monica.packet.MonicaPacketCollector;

public class Crc1State extends ReceiverState {

    public Crc1State(MonicaPacketCollector stateController) {
        super(stateController);
    }

    @Override
    public void receive(byte in) {
        stateController.addByte(in);
        stateController.setState(stateController.CRC2_STATE);
    }
}
