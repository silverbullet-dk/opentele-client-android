package dk.silverbullet.telemed.device.vitalographlungmonitor;

import com.google.gson.annotations.Expose;

public class LungMeasurement {
    @Expose private final float fev1;
    @Expose private final float fev6;
    @Expose private final float fev1Fev6Ratio;
    @Expose private final float fef2575;
    @Expose private final boolean goodTest;
    @Expose private final int softwareVersion;

    public LungMeasurement(float fev1, float fev6, float fev1Fev6Ratio, float fef2575, boolean goodTest,
            int softwareVersion) {
        this.fev1 = fev1;
        this.fev6 = fev6;
        this.fev1Fev6Ratio = fev1Fev6Ratio;
        this.fef2575 = fef2575;
        this.goodTest = goodTest;
        this.softwareVersion = softwareVersion;
    }

    public float getFev1() {
        return fev1;
    }

    public float getFev6() {
        return fev6;
    }

    public float getFev1Fev6Ratio() {
        return fev1Fev6Ratio;
    }

    public float getFef2575() {
        return fef2575;
    }

    public boolean isGoodTest() {
        return goodTest;
    }

    public int getSoftwareVersion() {
        return softwareVersion;
    }

    @Override
    public String toString() {
        return "LungMeasurement [fev1=" + fev1 + ", fev6=" + fev6 + ", fev1Fev6Ratio=" + fev1Fev6Ratio + ", fef2575="
                + fef2575 + ", softwareVersion=" + softwareVersion + "]";
    }
}
