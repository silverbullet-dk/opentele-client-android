package dk.silverbullet.telemed.device.nonin.packet;

import android.util.Log;
import dk.silverbullet.telemed.device.nonin.packet.states.DataState;
import dk.silverbullet.telemed.device.nonin.packet.states.IdleState;
import dk.silverbullet.telemed.device.nonin.packet.states.ReceiverState;
import dk.silverbullet.telemed.utils.Util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

public class NoninPacketCollector {
    private static final String TAG = Util.getTag(NoninPacketCollector.class);

    public final ReceiverState IDLE_STATE = new IdleState(this);
    public final ReceiverState DATA_STATE = new DataState(this);

    protected ReceiverState currentState = IDLE_STATE;
    private ArrayList<Integer> read = new ArrayList<Integer>(512);
    private PacketReceiver listener;

    private Date readTime;
    public void setListener(PacketReceiver listener) {
        this.listener = listener;
    }

    public void receive(int i) {
        currentState.receive(i);
    }

    public void reset() {
        setState(IDLE_STATE);
    }

    public void clearBuffer() {
        read.clear();
    }

    public void addInt(int in) {
        read.add(in);
    }

    public void setState(ReceiverState newState) {
        // Log.d(TAG, newState.getClass().getName());
        currentState = newState;
    }

    public Integer[] getRead() {
        return read.toArray(new Integer[0]);
    }

    public void setReadTime(Date readTime) {
        this.readTime = readTime;
    }

    public Date getReadTime() {
        return readTime;
    }

    public void error(IOException e) {
        listener.error(e);
    }

    public void sendPacket(NoninPacket packet) {
        if(packet instanceof NoninSerialNumberPacket) {
            listener.setSerialNumber((NoninSerialNumberPacket) packet);
        } else if(packet instanceof NoninMeasurementPacket) {
            listener.addMeasurement((NoninMeasurementPacket) packet);
        } else {
            Log.e(TAG, "Unknown packet type:" + packet.getClass().getName());
        }
    }
}
