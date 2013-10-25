package dk.silverbullet.telemed.video.measurement;

import com.google.gson.Gson;
import dk.silverbullet.telemed.device.andbloodpressure.BloodPressureAndPulse;
import dk.silverbullet.telemed.device.vitalographlungmonitor.LungMeasurement;

class MeasurementResult {
    MeasurementType type;
    String deviceId;
    Object measurement;

    MeasurementResult(String deviceId, LungMeasurement lungMeasurement) {
        this.type = MeasurementType.LUNG_FUNCTION;
        this.deviceId = deviceId;
        this.measurement = lungMeasurement;
    }

    MeasurementResult(String deviceId, BloodPressureAndPulse bloodPressureAndPulse) {
        this.type = MeasurementType.BLOOD_PRESSURE;
        this.deviceId = deviceId;
        this.measurement = bloodPressureAndPulse;
    }

    String toJson() {
        return new Gson().toJson(this);
    }
}
