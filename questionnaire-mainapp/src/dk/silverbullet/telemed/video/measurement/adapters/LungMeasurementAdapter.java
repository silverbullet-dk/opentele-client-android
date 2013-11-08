package dk.silverbullet.telemed.video.measurement.adapters;

import dk.silverbullet.telemed.device.DeviceController;
import dk.silverbullet.telemed.device.DeviceInitialisationException;
import dk.silverbullet.telemed.device.vitalographlungmonitor.LungMeasurement;
import dk.silverbullet.telemed.device.vitalographlungmonitor.LungMonitorListener;
import dk.silverbullet.telemed.device.vitalographlungmonitor.VitalographLungMonitorController;
import dk.silverbullet.telemed.video.measurement.MeasurementInformer;
import dk.silverbullet.telemed.video.measurement.adapters.submitters.SubmitLungMeasurementTask;

public class LungMeasurementAdapter implements VideoMeasurementAdapter, LungMonitorListener {
    private final MeasurementInformer informer;
    private DeviceController controller;

    public LungMeasurementAdapter(MeasurementInformer informer) {
        this.informer = informer;
    }

    @Override
    public void start() {
        try {
            informer.setMeasurementTypeText("Lungefunktion");
            informer.setStatusText("Tænd for apparatet og udfør lungefunktionstest.");
            controller = VitalographLungMonitorController.create(this);
        } catch (DeviceInitialisationException e) {
            informer.setStatusText("Kunne ikke forbinde til lungefunktionsmåler.");
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
        informer.setStatusText("Udfør lungefunktionstest.");
    }

    @Override
    public void permanentProblem() {
        informer.setStatusText("Der kan ikke skabes forbindelse.");
    }

    @Override
    public void temporaryProblem() {
        informer.setStatusText("Kunne ikke hente data. Prøv evt. igen.");
    }

    @Override
    public void measurementReceived(String deviceId, LungMeasurement measurement) {
        if (measurement.isGoodTest()) {
            informer.setStatusText("Måling modtaget.");
            controller.close();

            DeviceIdAndMeasurement<LungMeasurement> deviceIdAndMeasurement = new DeviceIdAndMeasurement<LungMeasurement>(deviceId, measurement);
            new SubmitLungMeasurementTask(informer).execute(deviceIdAndMeasurement);
        } else {
            informer.setStatusText("Dårlig måling modtaget. Prøv igen.");
        }
    }
}
