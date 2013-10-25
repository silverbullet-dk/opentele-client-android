package dk.silverbullet.telemed.device.monica.packet;

import java.util.Date;

public class FetalHeightAndSignalToNoise extends MonicaMessage {

    private final int fetalHeight;
    private final int signalToNoiseRatio;

    public FetalHeightAndSignalToNoise(Date readTime, String input) {
        super(readTime, input);
        // N02ANShhhhssss
        String premple = "N02ANS";
        if (!input.startsWith(premple))
            throw new IllegalArgumentException("Bad FetalHeightAndSignalToNoise package");
        int start = premple.length();
        fetalHeight = Integer.parseInt(input.substring(start, start + 4), 16);
        start += 4;
        signalToNoiseRatio = Integer.parseInt(input.substring(start, start + 4), 16);
    }

    @Override
    public String toString() {
        return "FetalHeightAndSignalToNoise [fetalHeight=" + fetalHeight + ", signalToNoiseRatio=" + signalToNoiseRatio
                + "]";
    }

    public int getFetalHeight() {
        return fetalHeight;
    }

    public int getSignalToNoiseRatio() {
        return signalToNoiseRatio;
    }

}
