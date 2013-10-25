package dk.silverbullet.telemed.device.monica.packet;

import java.util.Date;

public class IBlockMessage extends MonicaMessage {

    // IAN24V1A30A.02.00000000XXXX

    public IBlockMessage(Date readTime, String input) {
        super(readTime, input);
    }

    public String getDeviceType() {
        if (input.length() != 27)
            return ""; // Unknown device!
        return input.substring(1, 11); // "AN24V1A30A"
    }

    public String getDeviceVersion() {
        if (!input.startsWith("IAN24V1A30A") || input.length() != 27)
            return ""; // Unknown device!
        return input.substring(15);
    }

    @Override
    public String toString() {
        return input;
    }

}
