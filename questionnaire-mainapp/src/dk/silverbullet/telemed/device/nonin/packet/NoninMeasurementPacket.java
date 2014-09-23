package dk.silverbullet.telemed.device.nonin.packet;

import android.util.Log;
import dk.silverbullet.telemed.utils.Util;

import java.io.IOException;

public class NoninMeasurementPacket extends NoninPacket {
    private static final String TAG = Util.getTag(NoninMeasurementPacket.class);




    private static final int STATUS_1_POSITON = 0;
    private static final int PULSE_POSITION = 1;
    private static final int SPO2_POSITION = 2;
    private static final int STATUS_2_POSITON = 3;

    private static final int smartPointBitMask = 0x20;
    private static final int outOfTrackBitMask= 0x20;
    private static final int lowPerfusionBitMask = 0x10;
    private static final int marginalPerfusionBitMask = 0x08;
    private static final int fingerRemovedBitMask = 0x08;
    private static final int lowBatteryBitMask = 0x01;
    private static final int artifactBitMask = 0x04;

    private Integer[] data;
    public int sp02;
    public int pulse;
    public boolean artifact, outOfTrack, lowPerfusion, marginalPerfusion, fingerRemoved, highQuality, lowBattery, measurementMissing;


    @SuppressWarnings("unused")
    public NoninMeasurementPacket(Integer[] data) throws IOException {
        this.data = data;
        if(data == null) {
            throw new IOException("Got null data");
        }
        Log.d(TAG, "Building measurement packet");

        setFlags();
        this.sp02 = getSpO2();
        this.pulse = getPulse();
        this.measurementMissing = isMeasurementMissing();
    }

    private void setFlags() {
        outOfTrack        = isFlagSet(data[STATUS_1_POSITON], outOfTrackBitMask);
        lowPerfusion      = isFlagSet(data[STATUS_1_POSITON], lowPerfusionBitMask);
        marginalPerfusion = isFlagSet(data[STATUS_1_POSITON], marginalPerfusionBitMask);
        artifact          = isFlagSet(data[STATUS_1_POSITON], artifactBitMask);

        highQuality       = isFlagSet(data[STATUS_2_POSITON], smartPointBitMask);
        fingerRemoved     = isFlagSet(data[STATUS_2_POSITON], fingerRemovedBitMask);
        lowBattery        = isFlagSet(data[STATUS_2_POSITON], lowBatteryBitMask);
    }

    private boolean isFlagSet(int data, int flag) {
        return (data & flag) == flag;
    }

    private boolean isMeasurementMissing() {

        if(this.getPulse() == 511) {
            return true;
        }

        if (this.getSpO2() == 127) {
            return true;
        }

        return false;
    }


    private int getSpO2() {
        return data[SPO2_POSITION];
    }

    private int getPulse() {
        int overflowedBits = (data[STATUS_1_POSITON] & 0x01) << 8;
        return overflowedBits + data[PULSE_POSITION];
    }
}
