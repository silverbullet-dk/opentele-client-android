package dk.silverbullet.telemed.questionnaire.node;

import java.util.Calendar;

public class SampleMessage extends RealTimeCTGMessage {
    final float[] mhr;
    final float[] fhr;
    final int[] qfhr;
    final float[] toco;
    final Calendar timeStamp;
    final int sampleCount;

    public SampleMessage(float[] mhr, float[] fhr, int[] qfhr, float[] toco, int sampleCount) {
        super();
        this.sampleCount = sampleCount;
        this.timeStamp = Calendar.getInstance();
        this.mhr = mhr;
        this.fhr = fhr;
        this.qfhr = qfhr;
        this.toco = toco;
    }
}
