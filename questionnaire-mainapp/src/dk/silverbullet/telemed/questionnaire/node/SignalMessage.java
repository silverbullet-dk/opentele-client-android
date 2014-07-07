package dk.silverbullet.telemed.questionnaire.node;

import java.util.Date;

public class SignalMessage extends RealTimeCTGMessage {
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
