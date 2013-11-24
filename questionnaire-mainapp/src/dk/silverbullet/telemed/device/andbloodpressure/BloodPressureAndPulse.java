package dk.silverbullet.telemed.device.andbloodpressure;

import com.google.gson.annotations.Expose;

public class BloodPressureAndPulse {
    @Expose private final int systolic;
    @Expose private final int diastolic;
    @Expose private final int meanArterialPressure;
    @Expose private final int pulse;

    public BloodPressureAndPulse(int systolic, int diastolic, int meanArterialPressure, int pulse) {
        this.systolic = systolic;
        this.diastolic = diastolic;
        this.meanArterialPressure = meanArterialPressure;
        this.pulse = pulse;
    }

    public int getSystolic() {
        return systolic;
    }

    public int getDiastolic() {
        return diastolic;
    }

    public int getMeanArterialPressure() {
        return meanArterialPressure;
    }

    public int getPulse() {
        return pulse;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + diastolic;
        result = prime * result + meanArterialPressure;
        result = prime * result + pulse;
        result = prime * result + systolic;
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
        BloodPressureAndPulse other = (BloodPressureAndPulse) obj;
        if (diastolic != other.diastolic)
            return false;
        if (meanArterialPressure != other.meanArterialPressure)
            return false;
        if (pulse != other.pulse)
            return false;
        if (systolic != other.systolic)
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "BloodPressureAndPulse [systolic=" + systolic + ", diastolic=" + diastolic + ", meanArterialPressure="
                + meanArterialPressure + ", pulse=" + pulse + "]";
    }
}
