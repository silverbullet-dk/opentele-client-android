package dk.silverbullet.telemed.device.nonin.packet.states;

import dk.silverbullet.telemed.device.nonin.packet.NoninPacketCollector;

public abstract class ReceiverState {
    // Debugging
    @SuppressWarnings("unused")
    private static final String TAG = "ReceiverState";

    public static final byte STX = 0x2;
    public static final byte ETX = 0x3;
    public static final byte ACK = 0x6;


    protected final NoninPacketCollector stateController;

    public abstract boolean receive(int in);

    // Function called when the state is entered
    public abstract void entering();

    public ReceiverState(NoninPacketCollector stateController) {
        this.stateController = stateController;
    }
}
