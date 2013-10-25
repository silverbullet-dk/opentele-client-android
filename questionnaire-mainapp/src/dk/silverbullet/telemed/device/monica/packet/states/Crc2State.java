package dk.silverbullet.telemed.device.monica.packet.states;

import android.util.Log;
import dk.silverbullet.telemed.device.monica.packet.MonicaPacketCollector;
import dk.silverbullet.telemed.utils.Util;

public class Crc2State extends ReceiverState {
    // Debugging
    private static final String TAG = "Crc2State";

    public Crc2State(MonicaPacketCollector stateController) {
        super(stateController);
    }

    @Override
    public void receive(byte in) {
        stateController.addByte(in);
        byte[] bytes = stateController.getBytes();
        short crc = Util.calcCRC16(bytes);
        if (crc == 0) { // Correctly received!
            StringBuffer sb = new StringBuffer();
            // Copy all data to a string buffer, except for header (DLE, STX) and tail (DLE, ETX, CRC1, CRC2)
            boolean dle = false;
            for (int i = 2; i < bytes.length - 4; i++) {
                if (dle) {
                    sb.append((char) (bytes[i] & 0xff));
                    dle = false;
                } else if (bytes[i] == DLE) {
                    dle = true;
                } else
                    sb.append((char) (bytes[i] & 0xff));
            }
            stateController.handleMessage(stateController.getReadTime(), sb.toString());
        } else {
            Log.d(TAG, "Bad CRC: " + crc);
        }
        stateController.setState(stateController.IDLE_STATE);
    }

}
