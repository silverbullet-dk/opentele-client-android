package dk.silverbullet.telemed.device.monica.packet;

import java.util.Date;

public class UnknownMessage extends MonicaMessage {

    public UnknownMessage(Date readTime, String input) {
        super(readTime, input);
    }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < input.length(); i++) {
            char ch = input.charAt(i);
            if (!Character.isDefined(ch) || Character.isISOControl(ch))
                break;
            sb.append(ch);

        }
        String s;
        if (sb.toString().length() == input.length())
            s = "\"" + sb + "\"";
        else
            s = "\"" + sb + "...";
        return "UNKNOWN (" + input.length() + ") " + s;
    }

}
