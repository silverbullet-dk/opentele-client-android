package dk.silverbullet.telemed.video.measurement.adapters;

import dk.silverbullet.telemed.device.DeviceInitialisationException;
import dk.silverbullet.telemed.device.andbloodpressure.AndBloodPressureController;
import dk.silverbullet.telemed.device.andbloodpressure.BloodPressureAndPulse;
import dk.silverbullet.telemed.device.continua.ContinuaDeviceController;
import dk.silverbullet.telemed.device.continua.ContinuaListener;
import dk.silverbullet.telemed.device.continua.android.AndroidHdpController;
import dk.silverbullet.telemed.questionnaire.R;
import dk.silverbullet.telemed.utils.Util;
import dk.silverbullet.telemed.video.measurement.TakeMeasurementFragment;
import dk.silverbullet.telemed.video.measurement.adapters.submitters.SubmitBloodPressureMeasurementTask;

public class BloodPressureAdapter implements VideoMeasurementAdapter, ContinuaListener<BloodPressureAndPulse> {
    private final TakeMeasurementFragment fragment;
    private ContinuaDeviceController controller;

    public BloodPressureAdapter(TakeMeasurementFragment fragment) {
        this.fragment = fragment;
    }

    @Override
    public void start() {
        try {
            fragment.setMeasurementTypeText(Util.getString(R.string.video_bloodpressure_bloodpressure_and_pulse, fragment.getActivity()));
            fragment.setStatusText(Util.getString(R.string.video_bloodpressure_press_start, fragment.getActivity()));
            controller = AndBloodPressureController.create(this, new AndroidHdpController(fragment.getActivity()));
        } catch (DeviceInitialisationException e) {
            fragment.setStatusText(Util.getString(R.string.video_bloodpressure_could_not_connect, fragment.getActivity()));
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
        fragment.setStatusText(Util.getString(R.string.video_bloodpressure_waiting_for_measurement, fragment.getActivity()));
    }

    @Override
    public void disconnected() {
        fragment.setStatusText(Util.getString(R.string.video_bloodpressure_disconnected, fragment.getActivity()));
    }

    @Override
    public void permanentProblem() {
        fragment.setStatusText(Util.getString(R.string.video_bloodpressure_permanent_problem, fragment.getActivity()));
    }

    @Override
    public void temporaryProblem() {
        fragment.setStatusText(Util.getString(R.string.video_bloodpressure_temporary_problem, fragment.getActivity()));
    }

    @Override
    public void measurementReceived(String deviceId, BloodPressureAndPulse measurement) {
        fragment.setStatusText(Util.getString(R.string.video_bloodpressure_measurement_received, fragment.getActivity()));
        controller.close();

        DeviceIdAndMeasurement<BloodPressureAndPulse> deviceIdAndMeasurement = new DeviceIdAndMeasurement<BloodPressureAndPulse>(deviceId, measurement);
        new SubmitBloodPressureMeasurementTask(fragment).execute(deviceIdAndMeasurement);
    }
}
