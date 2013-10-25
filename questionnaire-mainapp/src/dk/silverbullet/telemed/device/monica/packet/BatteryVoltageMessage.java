package dk.silverbullet.telemed.device.monica.packet;

import java.util.Date;

public class BatteryVoltageMessage extends NBlock {

    public BatteryVoltageMessage(Date readTime, String input) {
        super(readTime, input);
    }

    @Override
    public String toString() {
        return "BatteryVoltageMessage(" + getVoltage() + " // " + input + ")";
    }

    public float getVoltage() {
        float voltage = Integer.parseInt(input.substring("N02ANV".length()), 16) / 100.0f;
        return voltage;
    }

}
