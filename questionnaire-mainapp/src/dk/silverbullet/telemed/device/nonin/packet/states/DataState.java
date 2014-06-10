package dk.silverbullet.telemed.device.nonin.packet.states;

import dk.silverbullet.telemed.device.nonin.packet.NoninPacketCollector;
import dk.silverbullet.telemed.device.nonin.packet.NoninPacketFactory;

import java.io.IOException;

public class DataState extends ReceiverState {
    public DataState(NoninPacketCollector stateController) {
        super(stateController);
    }

    @Override
    public void receive(int in) {
        stateController.addInt(in);
        //We're at the end of a message, validate checksum
        if (in == ETX) {
            try {
                Integer[] ints = stateController.getRead();
                stateController.sendPacket(NoninPacketFactory.packetFromInts(ints));
            } catch (IOException e) {
                stateController.error(e);
            }
            stateController.reset();
        }
    }
}
