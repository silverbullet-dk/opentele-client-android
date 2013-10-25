package dk.silverbullet.telemed.device.vitalographlungmonitor;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Date;

import android.util.Log;
import dk.silverbullet.telemed.device.vitalographlungmonitor.packet.PacketReceiver;
import dk.silverbullet.telemed.device.vitalographlungmonitor.packet.states.ChecksumState;
import dk.silverbullet.telemed.device.vitalographlungmonitor.packet.states.DataState;
import dk.silverbullet.telemed.device.vitalographlungmonitor.packet.states.IdleState;
import dk.silverbullet.telemed.device.vitalographlungmonitor.packet.states.ReceiverState;
import dk.silverbullet.telemed.utils.Util;

public class LungMonitorPacketCollector {
    private static final String TAG = Util.getTag(LungMonitorPacketCollector.class);

    public final ReceiverState IDLE_STATE = new IdleState(this);
    public final ReceiverState CHECKSUM_STATE = new ChecksumState(this);
    public final ReceiverState DATA_STATE = new DataState(this);

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

    public void handleMessage(String input) {
        Log.i(TAG, "Got message: '" + input + "'");
        try {
            FevMeasurementPacket measurement = new FevMeasurementPacket(input);
            listener.receive(measurement);
        } catch (IOException e) {
            Log.e(TAG, "Could not handle message", e);
            listener.error(e);
        }
    }

    public void setReadTime(Date readTime) {
        this.readTime = readTime;
    }

    public Date getReadTime() {
        return readTime;
    }

    public void sendByte(byte b) throws IOException {
        listener.sendByte(b);
    }

    public void error(IOException e) {
        listener.error(e);
    }
}
