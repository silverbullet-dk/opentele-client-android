package dk.silverbullet.telemed.video.measurement.adapters.submitters;

import dk.silverbullet.telemed.device.andbloodpressure.BloodPressureAndPulse;
import dk.silverbullet.telemed.video.measurement.MeasurementInformer;
import dk.silverbullet.telemed.video.measurement.MeasurementResult;
import dk.silverbullet.telemed.video.measurement.adapters.DeviceIdAndMeasurement;

public class SubmitBloodPressureMeasurementTask extends SubmitMeasurementTask<BloodPressureAndPulse> {
    public SubmitBloodPressureMeasurementTask(MeasurementInformer informer) {
        super(informer);
    }

    @Override
    protected MeasurementResult createMeasurementResult(DeviceIdAndMeasurement<BloodPressureAndPulse> measurement) {
        return new MeasurementResult(measurement.getDeviceId(), measurement.getMeasurement());
    }
}
