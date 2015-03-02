package dk.silverbullet.telemed.questionnaire.node.monica.realtime;

import java.util.Date;

public class SampleMessage implements RealTimeCTGMessage {
    final float[] mhr;
    final float[] fhr;
    final int[] qfhr;
    final float[] toco;
    final Date readTime;
    final int sampleCount;

    public SampleMessage(float[] mhr, float[] fhr, int[] qfhr, float[] toco, int sampleSequenceNumber, Date readTime) {
        super();
        this.sampleCount = sampleSequenceNumber;
        this.readTime = readTime;
        this.mhr = mhr;
        this.fhr = fhr;
        this.qfhr = qfhr;
        this.toco = toco;
    }
}
