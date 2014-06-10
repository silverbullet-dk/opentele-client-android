package dk.silverbullet.telemed.device.accuchek;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import dk.silverbullet.telemed.device.usb.USBController;
import dk.silverbullet.telemed.questionnaire.R;

import java.io.File;
import java.io.IOException;

class AccuCheckUSBHandler implements Handler.Callback {

    private static final String TAG = AccuCheckUSBHandler.class.getName();
    private final BloodSugarDeviceListener listener;
    private final USBController usbController;
    private Handler handler;

    public AccuCheckUSBHandler(BloodSugarDeviceListener listener, USBController usbController) {
        this.listener = listener;
        this.usbController = usbController;
    }

    @Override
    public boolean handleMessage(Message message) {
        switch (message.what) {
        case R.id.message_accu_chek_connect_to_device:
            Log.d(TAG, "Looking for device");
            if (usbController.isConnected("ACCU-CHEK Mobile/Reports")) {
                listener.connected();
                handler.sendEmptyMessage(R.id.message_accu_chek_find_diary);
            } else {
                handler.sendEmptyMessageDelayed(R.id.message_accu_chek_connect_to_device, 1000);
            }

            return true;

        case R.id.message_accu_chek_find_diary:
            Log.d(TAG, "Fetching diary");
            listener.fetchingDiary();
            File[] diaries = usbController.getFiles("ACCU-CHEK Mobile/Reports", ".csv");

            if (diaries == null || diaries.length == 0) {
                listener.diaryNotFound();
                handler.sendEmptyMessage(R.id.message_handler_stop);
            }

            handler.sendMessage(handler.obtainMessage(R.id.message_accu_chek_parse_diary, diaries));

            return true;

        case R.id.message_accu_chek_parse_diary:
            File[] foundDiaries = (File[]) message.obj;
            if (foundDiaries.length > 1) {
                listener.tooManyDiariesFound();
                return true;
            }

            Log.d(TAG, "Parsing diaries" + foundDiaries[0].getPath());
            BloodSugarMeasurements measurements = null;

            try {
                measurements = CsvFileReader.readFile(foundDiaries[0]);
            } catch (IOException e) {
                Log.e(TAG, "Could not parse csv file", e);
                listener.parsingFailed();
                return true;
            }

            listener.measurementsParsed(measurements);

            return true;

        case R.id.message_handler_stop:
            handler.removeCallbacksAndMessages(null); // Null signals remove all messages and runnables
            Looper.myLooper().quit();
            return true;

        default:
            return false;
        }
    }

    public void setHander(Handler handler) {
        this.handler = handler;
    }
}