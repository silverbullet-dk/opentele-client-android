package dk.silverbullet.telemed.device.vitalographlungmonitor.packet.states;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import dk.silverbullet.telemed.device.vitalographlungmonitor.LungMonitorPacketCollector;

public class ChecksumState extends ReceiverState {
    public ChecksumState(LungMonitorPacketCollector stateController) {
        super(stateController);
    }

    @Override
    public void receive(byte receivedChecksum) {
        byte[] receivedBytes = stateController.getBytes();
        byte calculatedChecksum = calculateChecksum(receivedBytes);
        try {
            if (receivedChecksum == calculatedChecksum) {
                stateController.sendByte(ACK);
                String data = createDataString(receivedBytes);
                stateController.handleMessage(data);
            } else {
                stateController.sendByte(NAK);
                stateController.error(new IOException("Invalid checksum. Got " + receivedChecksum + ", expected "
                        + calculatedChecksum));
            }
        } catch (IOException e) {
            stateController.error(e);
        }

        stateController.setState(stateController.IDLE_STATE);
    }

    private byte calculateChecksum(byte[] receivedBytes) {
        byte checksum = 0;
        for (byte b : receivedBytes) {
            checksum ^= b;
        }
        return checksum;
    }

    private String createDataString(byte[] receivedBytes) {
        try {
            return new String(receivedBytes, 1, receivedBytes.length - 2, "US-ASCII");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("Could not create data string", e);
        }
    }
}
