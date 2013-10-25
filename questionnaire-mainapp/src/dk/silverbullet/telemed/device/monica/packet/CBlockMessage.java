package dk.silverbullet.telemed.device.monica.packet;

import static dk.silverbullet.telemed.utils.Util.getUnsignedIntBits;

import java.util.Date;

import dk.silverbullet.telemed.utils.Util;

public class CBlockMessage extends MonicaMessage {

    private final byte[] data;

    public CBlockMessage(Date readTime, String input) {
        super(readTime, input);
        data = new byte[input.length() - 1];
        for (int i = 1; i < data.length; i++) {
            data[i - 1] = (byte) input.charAt(i);
        }
    }

    public float[] getFHR1() {

        return getFloats(0.25F, new float[4], 2, 5, 11);
    }

    public int[] getQFHR1() {
        int[] q = new int[4];
        for (int i = 0; i < q.length; i++) {
            q[i] = getUnsignedIntBits(data, 8 * (i * 2 + 2) + 1, 2);
        }
        return q;
    }

    int[] getFMP1() {

        int[] q = new int[4];
        for (int i = 0; i < q.length; i++) {
            q[i] = getUnsignedIntBits(data, 8 * (i * 2 + 2) + 3, 2);
        }
        return q;
    }

    public float[] getFHR2() {

        return getFloats(0.25F, new float[4], 10, 5, 11);
    }

    public float[] getMHR() {

        return getFloats(0.25F, new float[4], 18, 5, 11);
    }

    public float[] getTOCO() {

        return getFloats(0.5F, new float[4], 26, 0, 8);
    }

    private float[] getFloats(float scale, float[] out, int byteStart, int bit, int bits) {

        for (int i = 0; i < out.length; i++) {
            out[i] = scale * getUnsignedIntBits(data, bit + 8 * (byteStart + i * ((bits + 7) / 8)), bits);
        }
        return out;
    }

    @Override
    public String toString() {

        return "C (\nFHR1:" + Util.toString(getFHR1()) + "\nFMP1:" + Util.toString(getFMP1()) + "\nQFHR1:"
                + Util.toString(getQFHR1()) + "\nMHR:" + Util.toString(getMHR()) + "\nTOCO:" + Util.toString(getTOCO())
                + " )";
    }

}
