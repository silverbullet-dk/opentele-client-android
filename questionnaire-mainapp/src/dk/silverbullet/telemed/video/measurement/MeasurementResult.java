package dk.silverbullet.telemed.video.measurement;

import com.google.gson.Gson;
import dk.silverbullet.telemed.device.andbloodpressure.BloodPressureAndPulse;
import dk.silverbullet.telemed.device.nonin.SaturationAndPulse;
import dk.silverbullet.telemed.device.vitalographlungmonitor.LungMeasurement;

public class MeasurementResult {
    MeasurementType type;
    String deviceId;
    Object measurement;

    public MeasurementResult(String deviceId, LungMeasurement lungMeasurement) {
        this.type = MeasurementType.LUNG_FUNCTION;
        this.deviceId = deviceId;
        this.measurement = lungMeasurement;
    }

    public MeasurementResult(String deviceId, BloodPressureAndPulse bloodPressureAndPulse) {
        this.type = MeasurementType.BLOOD_PRESSURE;
        this.deviceId = deviceId;
        this.measurement = bloodPressureAndPulse;
    }

    public MeasurementResult(String deviceId, SaturationAndPulse saturationAndPulse) {
        this.type = MeasurementType.SATURATION;
        this.deviceId = deviceId;
        this.measurement = saturationAndPulse;
    }

    public String toJson() {
        return new Gson().toJson(this);
    }
}
