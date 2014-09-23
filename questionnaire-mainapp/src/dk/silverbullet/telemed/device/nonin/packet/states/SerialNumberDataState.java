package dk.silverbullet.telemed.device.nonin.packet.states;

import android.util.Log;
import dk.silverbullet.telemed.device.nonin.packet.NoninPacketCollector;
import dk.silverbullet.telemed.device.nonin.packet.NoninPacketFactory;

import java.io.IOException;

public class SerialNumberDataState extends ReceiverState {

    public SerialNumberDataState(NoninPacketCollector noninPacketCollector) {
        super(noninPacketCollector);
    }

    @Override
    public void receive(int in) {
        stateController.addInt(in);

        //We're at the end of a message
        if (in == ETX) {
            try {
                Integer[] ints = stateController.getRead();

                if(!NoninPacketFactory.isSerialNumberPacket(ints)) {
                    //Not a serial number packet.
                    Log.d(stateController.TAG, "Not a serial number packet");
                    stateController.clearBuffer();
                    stateController.setState(stateController.WAIT_FOR_SERIAL_NUMBER_STATE);
                    return;
                }

                stateController.setSerialNumberPacket(NoninPacketFactory.serialNumberPacket(ints));
                stateController.clearBuffer();
            } catch (IOException e) {
                stateController.error(e);
            }
        }
    }
}
