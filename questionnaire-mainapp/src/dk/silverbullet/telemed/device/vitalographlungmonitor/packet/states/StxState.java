package dk.silverbullet.telemed.device.vitalographlungmonitor.packet.states;

import java.util.Date;

import dk.silverbullet.telemed.device.vitalographlungmonitor.LungMonitorPacketCollector;

public class StxState extends ReceiverState {
    public StxState(LungMonitorPacketCollector stateController) {
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
