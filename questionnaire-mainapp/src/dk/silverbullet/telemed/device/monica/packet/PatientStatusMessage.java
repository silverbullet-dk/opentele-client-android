package dk.silverbullet.telemed.device.monica.packet;

import java.util.Date;

import android.util.Log;

public class PatientStatusMessage extends NBlock {

    public enum Status {
        UNKNOWN(0), ANTENATAL(1), INDUCTION(2), L_AND_D(3);
        public int value;

        Status(int value) {
            this.value = value;
        }
    }

    private static final String TAG = null;;

    public PatientStatusMessage(Date readTime, String input) {
        super(readTime, input);
    }

    @Override
    public String toString() {
        return "PatientStatusMessage(" + getStatus() + " // " + input + ")";
    }

    public Status getStatus() {
        int value = Integer.parseInt(input.substring("N02ANP".length()), 16);
        for (Status status : Status.values()) {
            if (status.value == value)
                return status;
        }
        Log.w(TAG, "UNKNOWN status assumed. Actual Monica status value not recognized: " + value + " input:" + input);
        return Status.UNKNOWN;
    }

}
