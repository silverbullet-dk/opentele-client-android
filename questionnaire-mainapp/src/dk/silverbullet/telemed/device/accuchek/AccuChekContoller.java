package dk.silverbullet.telemed.device.accuchek;

import android.os.Handler;
import android.os.HandlerThread;
import dk.silverbullet.telemed.device.usb.USBController;
import dk.silverbullet.telemed.questionnaire.R;

public class AccuChekContoller {
    private final AccuCheckUSBHandler usbHandler;
    private final Handler handler;

    public AccuChekContoller(BloodSugarDeviceListener listener, USBController usbController) {
        HandlerThread handlerThread = new HandlerThread("AccutChek handler thread");
        handlerThread.start();

        usbHandler = new AccuCheckUSBHandler(listener, usbController);

        handler = new Handler(handlerThread.getLooper(), usbHandler);
        usbHandler.setHander(handler);
        handler.sendEmptyMessage(R.id.message_accu_chek_connect_to_device);
    }

    public void close() {
        handler.sendMessageAtFrontOfQueue(handler.obtainMessage(R.id.message_handler_stop));
    }
}
