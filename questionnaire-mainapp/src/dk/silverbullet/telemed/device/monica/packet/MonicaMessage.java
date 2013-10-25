package dk.silverbullet.telemed.device.monica.packet;

import java.util.Date;

public abstract class MonicaMessage {
    protected final Date readTime;
    protected final String input;

    public MonicaMessage(Date readTime, String input) {
        this.readTime = readTime;
        this.input = input;
    }

    public abstract String toString();

    public Date getReadTime() {
        return readTime;
    }
}
