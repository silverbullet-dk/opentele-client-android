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
    public void receive(int in) {
        stateController.addInt(in);
        Log.d(noninPacketCollector.TAG, in + "");
        try {
            if(noninPacketCollector.getRead().length == 4) {  //Each measurement is 4 bytes long.
                NoninMeasurementPacket measurementPacket = NoninPacketFactory.measurementPacket(noninPacketCollector.getRead());
                noninPacketCollector.clearBuffer();
                noninPacketCollector.addMeasurement(measurementPacket);
            }
        } catch (IOException e) {
            Log.e(TAG, "Could not parse measurement", e);
        }
    }
}
