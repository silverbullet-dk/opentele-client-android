package dk.silverbullet.telemed.video.measurement.adapters;

import dk.silverbullet.telemed.device.DeviceInitialisationException;
import dk.silverbullet.telemed.device.andbloodpressure.AndBloodPressureController;
import dk.silverbullet.telemed.device.andbloodpressure.BloodPressureAndPulse;
import dk.silverbullet.telemed.device.continua.ContinuaDeviceController;
import dk.silverbullet.telemed.device.continua.ContinuaListener;
import dk.silverbullet.telemed.device.continua.android.AndroidHdpController;
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
            fragment.setMeasurementTypeText("Blodtryk og puls");
            fragment.setStatusText("Tryk på START-knappen på blodtryksmåleren.");
            controller = AndBloodPressureController.create(this, new AndroidHdpController(fragment.getActivity()));
        } catch (DeviceInitialisationException e) {
            fragment.setStatusText("Kunne ikke forbinde til blodtryksmåler.");
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
        fragment.setStatusText("Venter på måling. Hold dig i ro.");
    }

    @Override
    public void disconnected() {
        fragment.setStatusText("Afkoblet.");
    }

    @Override
    public void permanentProblem() {
        fragment.setStatusText("Der kan ikke skabes forbindelse.");
    }

    @Override
    public void temporaryProblem() {
        fragment.setStatusText("Kunne ikke hente data. Prøv evt. en ny blodtryksmåling.");
    }

    @Override
    public void measurementReceived(String deviceId, BloodPressureAndPulse measurement) {
        fragment.setStatusText("Måling modtaget.");
        controller.close();

        DeviceIdAndMeasurement<BloodPressureAndPulse> deviceIdAndMeasurement = new DeviceIdAndMeasurement<BloodPressureAndPulse>(deviceId, measurement);
        new SubmitBloodPressureMeasurementTask(fragment).execute(deviceIdAndMeasurement);
    }
}
