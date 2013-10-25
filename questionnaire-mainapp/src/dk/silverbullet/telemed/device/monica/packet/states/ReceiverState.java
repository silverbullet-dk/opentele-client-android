package dk.silverbullet.telemed.device.monica.packet.states;

import dk.silverbullet.telemed.device.monica.packet.MonicaPacketCollector;

public abstract class ReceiverState {
    // Debugging
    @SuppressWarnings("unused")
    private static final String TAG = "ReceiverState";

    public static final byte DLE = 16;
    public static final byte STX = 2;
    public static final byte ETX = 3;

    protected final MonicaPacketCollector stateController;

    public abstract void receive(byte in);

    public ReceiverState(MonicaPacketCollector stateController) {

        this.stateController = stateController;
    }
}
