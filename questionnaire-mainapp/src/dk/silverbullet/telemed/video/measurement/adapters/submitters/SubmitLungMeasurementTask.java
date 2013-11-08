package dk.silverbullet.telemed.video.measurement.adapters.submitters;

import dk.silverbullet.telemed.device.vitalographlungmonitor.LungMeasurement;
import dk.silverbullet.telemed.video.measurement.MeasurementInformer;
import dk.silverbullet.telemed.video.measurement.MeasurementResult;
import dk.silverbullet.telemed.video.measurement.adapters.DeviceIdAndMeasurement;

public class SubmitLungMeasurementTask extends SubmitMeasurementTask<LungMeasurement> {
    public SubmitLungMeasurementTask(MeasurementInformer informer) {
        super(informer);
    }

    @Override
    protected String createJson(DeviceIdAndMeasurement<LungMeasurement> measurement) {
        return new MeasurementResult(measurement.getDeviceId(), measurement.getMeasurement()).toJson();
    }
}
