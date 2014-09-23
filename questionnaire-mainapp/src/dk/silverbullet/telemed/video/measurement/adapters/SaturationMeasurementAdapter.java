package dk.silverbullet.telemed.video.measurement.adapters;

import dk.silverbullet.telemed.OpenTeleApplication;
import dk.silverbullet.telemed.device.DeviceInitialisationException;
import dk.silverbullet.telemed.device.nonin.NoninController;
import dk.silverbullet.telemed.device.nonin.SaturationAndPulse;
import dk.silverbullet.telemed.device.nonin.SaturationController;
import dk.silverbullet.telemed.device.nonin.SaturationPulseListener;
import dk.silverbullet.telemed.questionnaire.R;
import dk.silverbullet.telemed.utils.Util;
import dk.silverbullet.telemed.video.measurement.TakeMeasurementFragment;
import dk.silverbullet.telemed.video.measurement.adapters.submitters.SubmitSaturationMeasurementTask;

public class SaturationMeasurementAdapter implements VideoMeasurementAdapter, SaturationPulseListener {
    private final TakeMeasurementFragment fragment;
    private SaturationController controller;

    public SaturationMeasurementAdapter(TakeMeasurementFragment fragment) {
        this.fragment = fragment;
    }

    @Override
    public void start() {
        try {
            fragment.setMeasurementTypeText(Util.getString(R.string.video_saturation_saturation, fragment.getContext()));
            fragment.setStatusText(Util.getString(R.string.video_saturation_equip_device, fragment.getContext()));
            controller = NoninController.create(this);
        } catch (DeviceInitialisationException e) {
            OpenTeleApplication.instance().logException(e);
            fragment.setStatusText(Util.getString(R.string.video_saturation_connection_problem, fragment.getContext()));
        }
    }

    @Override
    public void close() {
        if (controller != null) {
            controller.close();
        }
    }

    @Override
    public void connected() {
        fragment.setStatusText(Util.getString(R.string.video_saturation_connected, fragment.getContext()));
    }

    @Override
    public void temporaryProblem() {
        fragment.setStatusText(Util.getString(R.string.video_saturation_temporary_problem, fragment.getContext()));
    }

    @Override
    public void measurementReceived(String deviceId, SaturationAndPulse measurement) {
        fragment.setStatusText(Util.getString(R.string.video_saturation_measurement_received, fragment.getContext()));
        controller.close();

        DeviceIdAndMeasurement<SaturationAndPulse> deviceIdAndMeasurement = new DeviceIdAndMeasurement<SaturationAndPulse>(deviceId, measurement);
        new SubmitSaturationMeasurementTask(fragment).execute(deviceIdAndMeasurement);
    }

    @Override
    public void firstTimeOut() {
        fragment.setStatusText(Util.getString(R.string.video_saturation_first_timeout, fragment.getContext()));
    }

    @Override
    public void finalTimeOut(String serial, SaturationAndPulse measurement) {
        if(measurement != null) {
            measurementReceived(serial, measurement);
        } else {
            fragment.setStatusText(Util.getString(R.string.video_saturation_connection_problem, fragment.getContext()));
        }

    }
}
