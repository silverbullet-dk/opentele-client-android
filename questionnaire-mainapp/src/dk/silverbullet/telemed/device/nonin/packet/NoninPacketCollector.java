package dk.silverbullet.telemed.device.nonin.packet;

import android.util.Log;
import dk.silverbullet.telemed.device.nonin.packet.states.*;
import dk.silverbullet.telemed.utils.Util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

public class NoninPacketCollector {
    public static final String TAG = Util.getTag(NoninPacketCollector.class);

    public final ReceiverState WAIT_FOR_SERIAL_NUMBER_STATE = new WaitForSerialNumberState(this);
    public final ReceiverState WAIT_FOR_DATAFORMAT_CHANGE_ACK_STATE = new WaitForDataFormatAckState(this);
    public final ReceiverState SERIAL_NUMBER_DATA_STATE = new SerialNumberDataState(this);
    public final ReceiverState MEASUREMENT_DATA_STATE = new MeasurementDataState(this);

    /***
     * State flow diagram, items in parens are actions performed by either NoninController or the bluetooth device
     *
     *  WAIT_FOR_SERIAL_NUMBER_STATE
     *      ↓
     *  (NoninController sends get get serial number command to device. Device responds with: 0x02 (STX) and 0xF4 (the opcode for serial number reponse))
     *      ↓
     *  SERIAL_NUMBER_DATA_STATE
     *      ↓
     *  (NoninController sends change data format command to device)
     *      ↓
     *  WAIT_FOR_DATAFORMAT_CHANGE_ACK_STATE
     *      ↓
     *  (Device responds with 0x06 (ACK)
     *      ↓
     *  MEASUREMENT_DATA_STATE
     *      ↺
     * (Measurements are recieved once each second. We wait for the first measurement with the Smart Point Algorithm indicator set to true.)
     *      ↓
     *    end
     */



    protected ReceiverState currentState = WAIT_FOR_SERIAL_NUMBER_STATE;
    private ArrayList<Integer> read = new ArrayList<Integer>(512);
    private PacketReceiver listener;

    private Date readTime;

    public NoninPacketCollector()
    {
        reset();
    }

    public void setListener(PacketReceiver listener) {
        this.listener = listener;
    }

    public boolean receive(int i) {
        return currentState.receive(i);
    }

    public void reset() {
        clearBuffer();
        setState(WAIT_FOR_SERIAL_NUMBER_STATE);
    }

    public void clearBuffer() {
        read.clear();
    }

    public void addInt(int in) {
        read.add(in);
    }

    public void setState(ReceiverState newState) {
        Log.d(TAG, "Switch to state: " + newState.getClass().getName());
        currentState = newState;
        // Tell state that we are entering it
        currentState.entering();
        // Clear our own buffer (just in case)
        clearBuffer();
    }

    public Integer[] getRead() {
        return read.toArray(new Integer[read.size()]);
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

    public void setSerialNumberPacket(NoninSerialNumberPacket serialNumberPacket) {
        Log.d(TAG, "Got serial number");
        listener.setSerialNumber(serialNumberPacket);

        setState(WAIT_FOR_DATAFORMAT_CHANGE_ACK_STATE);
        listener.sendChangeDataFormatCommand();
    }

    public void trySendingNewDataFormatCommand()
    {
        //setState(WAIT_FOR_DATAFORMAT_CHANGE_ACK_STATE);
        listener.sendChangeDataFormatCommand2();
    }

    public void receivedDataFormatChanged() {
        setState(MEASUREMENT_DATA_STATE);
    }

    public void addMeasurement(NoninMeasurementPacket measurementPacket) {
        listener.addMeasurement(measurementPacket);
    }
}
