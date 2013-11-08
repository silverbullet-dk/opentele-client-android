package dk.silverbullet.telemed.video.measurement.adapters;

import dk.silverbullet.telemed.device.DeviceInitialisationException;
import dk.silverbullet.telemed.device.continua.ContinuaDeviceController;
import dk.silverbullet.telemed.device.continua.ContinuaListener;
import dk.silverbullet.telemed.device.continua.android.AndroidHdpController;
import dk.silverbullet.telemed.device.nonin.NoninController;
import dk.silverbullet.telemed.device.nonin.SaturationAndPulse;
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
            fragment.setMeasurementTypeText("Iltmætning");
            fragment.setStatusText("Sæt måleren på din finger.");
            controller = NoninController.create(this, new AndroidHdpController(fragment.getActivity()));
        } catch (DeviceInitialisationException e) {
            fragment.setStatusText("Kunne ikke forbinde til måler.");
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
        fragment.setStatusText("Forbindelse afbrudt.");
    }

    @Override
    public void permanentProblem() {
        fragment.setStatusText("Der kan ikke skabes forbindelse.");
    }

    @Override
    public void temporaryProblem() {
        fragment.setStatusText("Kunne ikke hente data. Sluk og tænd evt. oxymeteret.");
    }

    @Override
    public void measurementReceived(String deviceId, SaturationAndPulse measurement) {
        fragment.setStatusText("Måling modtaget.");
        controller.close();

        DeviceIdAndMeasurement<SaturationAndPulse> deviceIdAndMeasurement = new DeviceIdAndMeasurement<SaturationAndPulse>(deviceId, measurement);
        new SubmitSaturationMeasurementTask(fragment).execute(deviceIdAndMeasurement);
    }
}
