package dk.silverbullet.telemed.device.nonin.packet.states;

import android.util.Log;
import dk.silverbullet.telemed.device.nonin.packet.NoninMeasurementPacket;
import dk.silverbullet.telemed.device.nonin.packet.NoninPacketCollector;
import dk.silverbullet.telemed.device.nonin.packet.NoninPacketFactory;
import dk.silverbullet.telemed.utils.Util;

import java.io.IOException;

public class MeasurementDataState extends ReceiverState {
    public static final String TAG = Util.getTag(MeasurementDataState.class);
    private NoninPacketCollector noninPacketCollector;

    public MeasurementDataState(NoninPacketCollector noninPacketCollector) {
        super(noninPacketCollector);
        this.noninPacketCollector = noninPacketCollector;
    }

    @Override
    public boolean receive(int in) {
        stateController.addInt(in);
        Log.d(noninPacketCollector.TAG, in + "");
        try {
            // TODO: Add some code that brings the read in sync, ie. MSB set to one for first packet
            int length = noninPacketCollector.getRead().length;
            // First byte must have MSB set, the reset must have it cleared
            if((1 == length &&  in < 0x80) || (1 < length && in >= 0x80)) {
                return false;
            }
            if(length == 4) {  //Each measurement is 4 bytes long.
                NoninMeasurementPacket measurementPacket = NoninPacketFactory.measurementPacket(noninPacketCollector.getRead());
                noninPacketCollector.clearBuffer();
                noninPacketCollector.addMeasurement(measurementPacket);
            }
            return true;
        } catch (IOException e) {
            Log.e(TAG, "Could not parse measurement", e);
        }
        return false;
    }

    @Override
    public void entering()
    {

    }
}
