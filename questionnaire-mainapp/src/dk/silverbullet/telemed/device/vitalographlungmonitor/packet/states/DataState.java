package dk.silverbullet.telemed.device.vitalographlungmonitor.packet.states;

import dk.silverbullet.telemed.device.vitalographlungmonitor.LungMonitorPacketCollector;

public class DataState extends ReceiverState {
    public DataState(LungMonitorPacketCollector stateController) {
        super(stateController);
    }

    @Override
    public void receive(byte in) {
        stateController.addByte(in);
        if (in == ETX) {
            stateController.setState(stateController.CHECKSUM_STATE);
        }
    }
}
