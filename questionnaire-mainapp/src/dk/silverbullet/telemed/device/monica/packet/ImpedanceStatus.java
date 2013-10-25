package dk.silverbullet.telemed.device.monica.packet;

import java.util.Date;

public class ImpedanceStatus extends NBlock {

    private final char status;

    public ImpedanceStatus(Date readTime, String input) {
        super(readTime, input);
        status = input.charAt(6);
    }

    public boolean isStatusKnown() {
        return status != 0;
    }

    public boolean isGreenOK() {
        if (isAllOk())
            return true;
        return isStatusKnown() && ((status & 0x01) == 0);
    }

    public boolean isWhiteOK() {
        if (isAllOk())
            return true;
        return isStatusKnown() && ((status & 0x02) == 0);
    }

    public boolean isOrangeOK() {
        if (isAllOk())
            return true;
        return isStatusKnown() && ((status & 0x04) == 0);
    }

    public boolean isYellowOK() {
        if (isAllOk())
            return true;
        return isStatusKnown() && ((status & 0x08) == 0);
    }

    public boolean isBlackOK() {
        if (isAllOk())
            return true;
        return isStatusKnown() && ((status & 0x10) == 0);
    }

    @Override
    public String toString() {
        return "ImpedanceStatus [isStatusKnown()=" + isStatusKnown() + ", isGreenOK()=" + isGreenOK()
                + ", isWhiteOK()=" + isWhiteOK() + ", isOrangeOK()=" + isOrangeOK() + ", isYellowOK()=" + isYellowOK()
                + ", isBlackOK()=" + isBlackOK() + "]";
    }

    public boolean isAllOk() {
        return status == 0xF1;
    }

}
