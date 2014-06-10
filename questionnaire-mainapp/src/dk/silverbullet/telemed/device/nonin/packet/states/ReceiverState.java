package dk.silverbullet.telemed.device.nonin.packet.states;

import dk.silverbullet.telemed.device.nonin.packet.NoninPacketCollector;

public abstract class ReceiverState {
    // Debugging
    @SuppressWarnings("unused")
    private static final String TAG = "ReceiverState";

    public static final byte STX = 0x2;
    public static final byte ETX = 0x3;
    public static final int NULL_START_SYNC = 0x00;


    protected final NoninPacketCollector stateController;

    public abstract void receive(int in);

    public ReceiverState(NoninPacketCollector stateController) {
        this.stateController = stateController;
    }
}
