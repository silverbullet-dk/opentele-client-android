package dk.silverbullet.telemed.device.monica;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import android.util.Log;
import dk.silverbullet.telemed.device.monica.packet.PatientStatusMessage;
import dk.silverbullet.telemed.utils.Util;

public class MessageFactory {

    public static final String TAG = Util.getTag(MessageFactory.class);

    public static byte[] getInfoMessage() {
        return "?I".getBytes();
    }

    public static byte[] requestBatteryLevel() {
        return "N02PCBAT".getBytes();
    }

    public static byte[] setPatientStatus(PatientStatusMessage.Status status, boolean uterusActivity) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        try {
            bytes.write("N02PCPST".getBytes());
        } catch (IOException ioe) {
            Log.e(TAG, "Should never happen!!", ioe);
            return null;
        }
        bytes.write(status.value);
        bytes.write(uterusActivity ? 1 : 0);
        return bytes.toByteArray();
    }

    public static byte[] requestImpedanceStatus1() {
        return "N02PCIMP".getBytes();
    }

    public static byte[] requestImpedanceStatus2() {
        return "N02PCIM2".getBytes();
    }

    public static byte[] selectContinuousMode() {
        return "N02PCCONT".getBytes();
    }

    public static byte[] deleteExistingData() {
        return "N02PCDEL".getBytes();
    }

    public static byte[] bypassImpedanceTest() {
        return "N02PCBYP".getBytes();
    }

    public static byte[] deleteDataAndSwitchOff() {
        return "N02PCOFD".getBytes();
    }

    public static byte[] switchOff() {
        return "N02PCOFF".getBytes();
    }

    public static byte[] downloadData() {
        return "G".getBytes();
    }

    public static byte[] halt() {
        return "H".getBytes();
    }

}
