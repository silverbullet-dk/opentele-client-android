package dk.silverbullet.telemed.video.measurement.adapters;

import android.content.Context;
import dk.silverbullet.telemed.device.DeviceController;
import dk.silverbullet.telemed.device.DeviceInitialisationException;
import dk.silverbullet.telemed.device.vitalographlungmonitor.LungMeasurement;
import dk.silverbullet.telemed.device.vitalographlungmonitor.LungMonitorListener;
import dk.silverbullet.telemed.device.vitalographlungmonitor.VitalographLungMonitorController;
import dk.silverbullet.telemed.questionnaire.R;
import dk.silverbullet.telemed.utils.Util;
import dk.silverbullet.telemed.video.measurement.MeasurementInformer;
import dk.silverbullet.telemed.video.measurement.adapters.submitters.SubmitLungMeasurementTask;

public class LungMeasurementAdapter implements VideoMeasurementAdapter, LungMonitorListener {
    private final MeasurementInformer informer;
    private Context context;
    private DeviceController controller;

    public LungMeasurementAdapter(MeasurementInformer informer, Context context) {
        this.informer = informer;
        this.context = context;
    }

    @Override
    public void start() {
        try {
            informer.setMeasurementTypeText(Util.getString(R.string.video_lung_function_lung_function, context));
            informer.setStatusText(Util.getString(R.string.video_lung_function_turn_on_device, context));
            controller = VitalographLungMonitorController.create(this);
        } catch (DeviceInitialisationException e) {
            informer.setStatusText(Util.getString(R.string.video_lung_function_could_not_connect, context));
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
        informer.setStatusText(Util.getString(R.string.video_lung_function_perform_test, context));
    }

    @Override
    public void permanentProblem() {
        informer.setStatusText(Util.getString(R.string.video_lung_function_permanent_problem, context));
    }

    @Override
    public void temporaryProblem() {
        informer.setStatusText(Util.getString(R.string.video_lung_function_temporary_problem, context));
    }

    @Override
    public void measurementReceived(String deviceId, LungMeasurement measurement) {
        if (measurement.isGoodTest()) {
            informer.setStatusText(Util.getString(R.string.video_lung_function_measurement_recived, context));
            controller.close();

            DeviceIdAndMeasurement<LungMeasurement> deviceIdAndMeasurement = new DeviceIdAndMeasurement<LungMeasurement>(deviceId, measurement);
            new SubmitLungMeasurementTask(informer).execute(deviceIdAndMeasurement);
        } else {
            informer.setStatusText(Util.getString(R.string.video_lung_function_bad_measurement, context));
        }
    }
}
