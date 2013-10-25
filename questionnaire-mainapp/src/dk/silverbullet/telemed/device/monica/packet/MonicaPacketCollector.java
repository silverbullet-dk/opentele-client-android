package dk.silverbullet.telemed.device.monica.packet;

import java.io.ByteArrayOutputStream;
import java.util.Date;

import dk.silverbullet.telemed.device.monica.packet.states.Crc1State;
import dk.silverbullet.telemed.device.monica.packet.states.Crc2State;
import dk.silverbullet.telemed.device.monica.packet.states.DataDleState;
import dk.silverbullet.telemed.device.monica.packet.states.DataState;
import dk.silverbullet.telemed.device.monica.packet.states.IdleState;
import dk.silverbullet.telemed.device.monica.packet.states.ReceiverState;
import dk.silverbullet.telemed.device.monica.packet.states.StxState;
import dk.silverbullet.telemed.utils.DataLogger;

public class MonicaPacketCollector {
    public final ReceiverState IDLE_STATE = new IdleState(this);
    public final ReceiverState CRC1_STATE = new Crc1State(this);
    public final ReceiverState CRC2_STATE = new Crc2State(this);
    public final ReceiverState DATA_STATE = new DataState(this);
    public final ReceiverState DATA_DLE = new DataDleState(this);
    public final ReceiverState STX_STATE = new StxState(this);

    protected ReceiverState currentState = IDLE_STATE;
    private ByteArrayOutputStream bytes = new ByteArrayOutputStream(512);
    private PacketReceiver listener;

    private Date readTime;

    public void setListener(PacketReceiver listener) {
        this.listener = listener;
    }

    public void receive(byte b) {
        currentState.receive(b);
    }

    public void reset() {
        setState(IDLE_STATE);
    }

    public void clearBuffer() {
        bytes.reset();
    }

    public void addByte(byte in) {
        bytes.write(in);
    }

    public void setState(ReceiverState newState) {
        // Log.d(TAG, newState.getClass().getName());
        currentState = newState;
    }

    public byte[] getBytes() {
        return bytes.toByteArray();
    }

    public void handleMessage(Date readTime, String input) {
        DataLogger.logInput(readTime, input);
        if (input.startsWith("C")) {
            listener.receive(new CBlockMessage(readTime, input));
        } else if (input.startsWith("F")) {
            listener.receive(new FBlockMessage(readTime, input));
        } else if (input.startsWith("MM")) {
            listener.receive(new MmMessage(readTime, input));
        } else if (input.startsWith("N")) {
            listener.receive(NBlock.parse(readTime, input));
        } else if (input.startsWith("I")) {
            listener.receive(new IBlockMessage(readTime, input));
        } else {
            listener.receive(new UnknownMessage(readTime, input));
        }
    }

    public void setReadTime(Date readTime) {
        this.readTime = readTime;
    }

    public Date getReadTime() {
        return readTime;
    }
}
