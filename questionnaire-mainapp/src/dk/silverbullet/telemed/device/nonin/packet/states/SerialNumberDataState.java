package dk.silverbullet.telemed.device.nonin.packet.states;

import android.util.Log;
import dk.silverbullet.telemed.device.nonin.packet.NoninPacketCollector;
import dk.silverbullet.telemed.device.nonin.packet.NoninPacketFactory;

import java.io.IOException;

public class SerialNumberDataState extends ReceiverState {

    private int numberOfBytesRead;

    public SerialNumberDataState(NoninPacketCollector noninPacketCollector) {
        super(noninPacketCollector);
    }

    @Override
    public boolean receive(int in) {
        stateController.addInt(in);
        numberOfBytesRead++;

        //We're at the end of a message
        if (15 <= numberOfBytesRead) {
            try {
                Integer[] ints = stateController.getRead();

                if(!NoninPacketFactory.isSerialNumberPacket(ints, numberOfBytesRead)) {
                    //Not a serial number packet.
                    Log.d(stateController.TAG, "Not a serial number packet");
                    stateController.clearBuffer();
                    stateController.setState(stateController.WAIT_FOR_SERIAL_NUMBER_STATE);
                    return false;
                }
                stateController.setSerialNumberPacket(NoninPacketFactory.serialNumberPacket(ints));
                stateController.clearBuffer();
                return true;
            } catch (IOException e) {
                stateController.error(e);
            }
            return false;
        }
        return true;
    }

    @Override
    public void entering()
    {
        // Set our count to 0
        numberOfBytesRead = 0;
        // Clear the buffer
        stateController.clearBuffer();
    }
}
