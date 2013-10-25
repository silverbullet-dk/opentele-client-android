package dk.silverbullet.telemed.video.measurement;

import com.google.gson.Gson;
import dk.silverbullet.telemed.device.vitalographlungmonitor.LungMeasurement;

class MeasurementResult {
    MeasurementType type;
    LungMeasurement measurement;

    MeasurementResult(LungMeasurement lungMeasurement) {
        this.type = MeasurementType.LUNG_FUNCTION;
        this.measurement = lungMeasurement;
    }

    String toJson() {
        return new Gson().toJson(this);
    }
}
