package dk.silverbullet.telemed.device.monica.packet;

import java.util.Date;

public class DeviceOffMessage extends MonicaMessage {

    public DeviceOffMessage(Date readTime, String input) {
        super(readTime, input);
    }

    @Override
    public String toString() {
        return getClass().getName();
    }

}
