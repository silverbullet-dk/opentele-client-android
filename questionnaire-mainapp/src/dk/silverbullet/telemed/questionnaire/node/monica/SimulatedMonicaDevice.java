package dk.silverbullet.telemed.questionnaire.node.monica;

import android.util.Log;
import dk.silverbullet.telemed.device.monica.MonicaDevice;

import java.util.Date;
import java.util.Random;

public class SimulatedMonicaDevice implements Runnable, MonicaDevice {

    Random r = new Random(17); // World's most random number!

    private static final int SIMULATED_TIME = 45000; // milliseconds

    private final MonicaDeviceCallback monicaDeviceCallback;

    private Thread thread;

    public SimulatedMonicaDevice(MonicaDeviceCallback monicaDeviceCallback) {
        this.monicaDeviceCallback = monicaDeviceCallback;
        thread = new Thread(this);
        thread.start();
    }

    @Override
    public void run() {

        try {
            monicaDeviceCallback.setDeviceIdString("SIMULATED");
            monicaDeviceCallback.setState(DeviceState.WAITING_FOR_CONNECTION);
            Thread.sleep(2000);
            monicaDeviceCallback.setState(DeviceState.CHECKING_STARTING_CONDITION);
            Thread.sleep(500);
            // Dazzle...
            monicaDeviceCallback.setProbeState(true, false, false, false, false);
            Thread.sleep(500);
            monicaDeviceCallback.setProbeState(true, true, false, true, false);
            Thread.sleep(500);
            monicaDeviceCallback.setProbeState(true, true, false, true, false);
            Thread.sleep(500);
            monicaDeviceCallback.setProbeState(true, false, true, false, true);
            Thread.sleep(500);
            monicaDeviceCallback.setProbeState(true, true, false, false, true);
            Thread.sleep(500);
            monicaDeviceCallback.setProbeState(true, true, true, true, true);
            Thread.sleep(500);
            monicaDeviceCallback.setState(DeviceState.WAITING_FOR_DATA);
        } catch (InterruptedException ie) {
            return;
        }

        long start = System.currentTimeMillis();
        monicaDeviceCallback.setStartTimeValue(new Date(start));
        monicaDeviceCallback.addSignal(new Date(start + 10 * 60 * 1000));
        monicaDeviceCallback.addSignal(new Date(start + 15 * 60 * 1000));
        monicaDeviceCallback.addSignal(new Date(start + 20 * 60 * 1000));
        monicaDeviceCallback.setStartVoltage(3.1416F);
        monicaDeviceCallback.setEndVoltage(2.7183F);
        int samples = monicaDeviceCallback.getSampleTimeMinutes() * 60;
        int sampleCount = 0;
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            return;
        }
        for (int i = 0; i < samples; i++) {
            if(thread.isInterrupted()) {
                Log.d("SIMULATED", "Was interrupted" );
                return;
            }
            // CBlockMessage cblk= new CBlockMessage("");
            // float[] fhr = cblk.getFHR1();
            // int[] qfhr = cblk.getQFHR1();
            // int[] fmp = cblk.getFMP1();
            // float[] mhr = cblk.getMHR();
            // float[] toco = cblk.getTOCO();

            float[] fhr = makeFloats(4, 120, 140);
            int[] qfhr = makeInts(4, 3, 3);
            // int[] fmp = makeInts(4, 3, 3);
            float[] mhr = makeFloats(4, 60, 70);
            float[] toco = makeFloats(4, 10, 30);

            monicaDeviceCallback.addSamples(mhr, fhr, qfhr, toco);
            if (i % 20 == 0) {
                monicaDeviceCallback.addFetalHeight(r.nextInt(65536));
                monicaDeviceCallback.addSignalToNoise(r.nextInt(65536));
            }
            monicaDeviceCallback.updateProgress(i, samples);
            long sync = start + (SIMULATED_TIME * i + samples / 2) / samples;
            long wait = sync - System.currentTimeMillis();
            if (wait > 50 && !Thread.interrupted()) {
                try {
                    Thread.sleep(wait);
                } catch (InterruptedException e) {
                    return;
                }
            }
            sampleCount++;
            samples = monicaDeviceCallback.getSampleTimeMinutes() * 60;
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                return;
            }
        }
        monicaDeviceCallback.setEndTimeValue(new Date(start + sampleCount * 1000));
        monicaDeviceCallback.done();
    }

    private int[] makeInts(int count, int from, int to) {
        int[] ii = new int[count];
        for (int i = 0; i < count; i++) {
            ii[i] = from + r.nextInt(to - from + 1);
        }
        return ii;
    }

    private float[] makeFloats(int count, float from, float to) {
        float[] f = new float[count];
        int iRange = (int) (4 * (to - from + 1));
        for (int i = 0; i < count; i++) {
            f[i] = ((int) (from * 4) + r.nextInt(iRange)) / 4F;
        }
        return f;
    }

    @Override
    public void close() {
        thread.interrupt();
    }

    public String getDeviceName() {
        return "Simulated-AN24";
    }

}