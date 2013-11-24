package dk.silverbullet.telemed.video.measurement.adapters;

import dk.silverbullet.telemed.device.DeviceInitialisationException;
import dk.silverbullet.telemed.device.continua.ContinuaDeviceController;
import dk.silverbullet.telemed.device.continua.ContinuaListener;
import dk.silverbullet.telemed.device.continua.android.AndroidHdpController;
import dk.silverbullet.telemed.device.nonin.NoninController;
import dk.silverbullet.telemed.device.nonin.SaturationAndPulse;
import dk.silverbullet.telemed.questionnaire.R;
import dk.silverbullet.telemed.utils.Util;
import dk.silverbullet.telemed.video.measurement.TakeMeasurementFragment;
import dk.silverbullet.telemed.video.measurement.adapters.submitters.SubmitSaturationMeasurementTask;

public class SaturationMeasurementAdapter implements VideoMeasurementAdapter, ContinuaListener<SaturationAndPulse> {
    private final TakeMeasurementFragment fragment;
    private ContinuaDeviceController controller;

    public SaturationMeasurementAdapter(TakeMeasurementFragment fragment) {
        this.fragment = fragment;
    }

    @Override
    public void start() {
        try {
            fragment.setMeasurementTypeText(Util.getString(R.string.video_saturation_saturation, fragment.getActivity()));
            fragment.setStatusText(Util.getString(R.string.video_saturation_equip_device, fragment.getActivity()));
            controller = NoninController.create(this, new AndroidHdpController(fragment.getActivity()));
        } catch (DeviceInitialisationException e) {
            fragment.setStatusText(Util.getString(R.string.video_saturation_connection_problem, fragment.getActivity()));
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
        fragment.setStatusText(Util.getString(R.string.video_saturation_connected, fragment.getActivity()));
    }

    @Override
    public void disconnected() {
        fragment.setStatusText(Util.getString(R.string.video_saturation_disconnected, fragment.getActivity()));
    }

    @Override
    public void permanentProblem() {
        fragment.setStatusText(Util.getString(R.string.video_saturation_permanent_problem, fragment.getActivity()));
    }

    @Override
    public void temporaryProblem() {
        fragment.setStatusText(Util.getString(R.string.video_saturation_temporary_problem, fragment.getActivity()));
    }

    @Override
    public void measurementReceived(String deviceId, SaturationAndPulse measurement) {
        fragment.setStatusText(Util.getString(R.string.video_saturation_measurement_received, fragment.getActivity()));
        controller.close();

        DeviceIdAndMeasurement<SaturationAndPulse> deviceIdAndMeasurement = new DeviceIdAndMeasurement<SaturationAndPulse>(deviceId, measurement);
        new SubmitSaturationMeasurementTask(fragment).execute(deviceIdAndMeasurement);
    }
}
