package dk.silverbullet.telemed.device.vitalographlungmonitor.packet.states;

import dk.silverbullet.telemed.device.vitalographlungmonitor.LungMonitorPacketCollector;

public abstract class ReceiverState {
    // Debugging
    @SuppressWarnings("unused")
    private static final String TAG = "ReceiverState";

    public static final byte DLE = 16;
    public static final byte STX = 2;
    public static final byte ETX = 3;
    public static final byte ACK = 6;
    public static final byte NAK = 21;

    protected final LungMonitorPacketCollector stateController;

    public abstract void receive(byte in);

    public ReceiverState(LungMonitorPacketCollector stateController) {
        this.stateController = stateController;
    }
}
