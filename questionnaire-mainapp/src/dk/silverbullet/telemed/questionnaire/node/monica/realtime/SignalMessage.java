package dk.silverbullet.telemed.questionnaire.node.monica.realtime;

import java.util.Date;

public class SignalMessage implements RealTimeCTGMessage {
    Date dateTime;

    public SignalMessage(Date dateTime) {
        super();
        this.dateTime = dateTime;
    }

    @Override
    public String toString() {
        return "<time>" + dateTime + "</time>";
    }
}
