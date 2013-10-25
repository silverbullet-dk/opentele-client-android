package dk.silverbullet.telemed.device.monica.packet;

import java.util.Date;

public class MmMessage extends MonicaMessage {

    public MmMessage(Date readTime, String input) {
        super(readTime, input);
    }

    @Override
    public String toString() {
        return "MM (" + input.length() + ")";
    }

}
