package dk.silverbullet.telemed.device.monica.packet;

import java.util.Date;

public class FBlockMessage extends MonicaMessage {

    public FBlockMessage(Date readTime, String input) {
        super(readTime, input);
    }

    @Override
    public String toString() {
        return "F (" + input.length() + ")";
    }

}
