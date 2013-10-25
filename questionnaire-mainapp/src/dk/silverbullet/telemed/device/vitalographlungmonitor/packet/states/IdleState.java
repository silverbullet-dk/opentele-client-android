package dk.silverbullet.telemed.device.vitalographlungmonitor.packet.states;

import dk.silverbullet.telemed.device.vitalographlungmonitor.LungMonitorPacketCollector;

public class IdleState extends ReceiverState {
    public IdleState(LungMonitorPacketCollector stateController) {
        super(stateController);
    }

    @Override
    public void receive(byte in) {
        if (in == STX) {
            stateController.clearBuffer();
            stateController.addByte(in);
            stateController.setState(stateController.DATA_STATE);
        }
    }
}
