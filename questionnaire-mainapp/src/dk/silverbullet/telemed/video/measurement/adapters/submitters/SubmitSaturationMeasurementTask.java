package dk.silverbullet.telemed.video.measurement.adapters.submitters;

import dk.silverbullet.telemed.device.nonin.SaturationAndPulse;
import dk.silverbullet.telemed.video.measurement.MeasurementInformer;
import dk.silverbullet.telemed.video.measurement.MeasurementResult;
import dk.silverbullet.telemed.video.measurement.adapters.DeviceIdAndMeasurement;

public class SubmitSaturationMeasurementTask extends SubmitMeasurementTask<SaturationAndPulse> {
    public SubmitSaturationMeasurementTask(MeasurementInformer informer) {
        super(informer);
    }

    @Override
    protected MeasurementResult createMeasurementResult(DeviceIdAndMeasurement<SaturationAndPulse> measurement) {
        return new MeasurementResult(measurement.getDeviceId(), measurement.getMeasurement());
    }
}
