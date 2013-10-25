package dk.silverbullet.telemed.device.monica.packet;

import java.util.Date;

public abstract class NBlock extends MonicaMessage {

    public NBlock(Date readTime, String input) {
        super(readTime, input);
    }

    private static final NBlock BATTERY_LOW_MESSAGE = new NBlock(null, "") {
        @Override
        public String toString() {
            return "LowBatt";
        }
    };

    static MonicaMessage parse(Date readTime, String input) {
        if (input.matches("N02ANS[A-Fa-f0-9]{8}"))
            return new FetalHeightAndSignalToNoise(readTime, input);
        if (input.startsWith("N02ANB"))
            return BATTERY_LOW_MESSAGE;
        if (input.matches("N02ANV[0-9a-fA-F]{8}"))
            return new BatteryVoltageMessage(readTime, input);
        if (input.startsWith("N02ANI") && input.length() >= 7)
            return new ImpedanceStatus(readTime, input);
        if (input.startsWith("N02ANGOT") && input.length() >= 7)
            return GotDataMessage.INSTANCE;
        if (input.startsWith("N02ANEND"))
            return new DeviceOffMessage(readTime, input);
        if (input.matches("N02ANP[0-9a-fA-F]{8}"))
            return new PatientStatusMessage(readTime, input);
        return new UnknownMessage(readTime, input);
    }

}
