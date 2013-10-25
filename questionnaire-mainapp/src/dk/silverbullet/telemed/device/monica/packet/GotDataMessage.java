package dk.silverbullet.telemed.device.monica.packet;

import java.util.Date;

public class GotDataMessage extends MonicaMessage {

    public static final GotDataMessage INSTANCE = new GotDataMessage(null, "N02ANGOT");

    private GotDataMessage(Date readTime, String input) {
        super(readTime, input);
    }

    @Override
    public String toString() {
        return "GotDataMessage";
    }

}
