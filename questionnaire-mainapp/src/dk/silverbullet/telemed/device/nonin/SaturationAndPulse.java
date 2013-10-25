package dk.silverbullet.telemed.device.nonin;

public class SaturationAndPulse {
    private final int saturation;
    private final int pulse;

    public SaturationAndPulse(int saturation, int pulse) {
        this.saturation = saturation;
        this.pulse = pulse;
    }

    public int getSaturation() {
        return saturation;
    }

    public int getPulse() {
        return pulse;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + pulse;
        result = prime * result + saturation;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        SaturationAndPulse other = (SaturationAndPulse) obj;
        if (pulse != other.pulse)
            return false;
        if (saturation != other.saturation)
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "SaturationAndPulse [saturation=" + saturation + ", pulse=" + pulse + "]";
    }
}
