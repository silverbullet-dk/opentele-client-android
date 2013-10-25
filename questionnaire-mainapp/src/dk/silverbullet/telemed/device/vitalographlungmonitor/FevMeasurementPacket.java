package dk.silverbullet.telemed.device.vitalographlungmonitor;

import java.io.IOException;
import java.io.StringReader;

import dk.silverbullet.telemed.device.UnexpectedPacketFormatException;
import dk.silverbullet.telemed.device.vitalographlungmonitor.packet.VitalographPacket;

public class FevMeasurementPacket extends VitalographPacket {
    private final String deviceId;
    private final float fev1;
    private final float fev6;
    private final float fev1Fev6Ratio;
    private final float fef2575;
    private final boolean goodTest;
    private final int softwareVersion;

    @SuppressWarnings("unused")
    public FevMeasurementPacket(String data) throws IOException {
        StringReader reader = new StringReader(data);

        String f = readString(reader, 1, "F");
        String td = readString(reader, 2, "TD");
        deviceId = readString(reader, 10, ".*");
        fev1 = readInt(reader, 3) / 100f;
        fev6 = readInt(reader, 3) / 100f;
        fev1Fev6Ratio = readInt(reader, 3) / 100f;
        fef2575 = readInt(reader, 3) / 100f;
        readInt(reader, 3); // FEV1 Personal Best
        readInt(reader, 3); // FEV1%
        readInt(reader, 3); // Green Zone
        readInt(reader, 3); // Yellow Zone
        readInt(reader, 3); // Orange Zone
        readInt(reader, 2); // Year
        readInt(reader, 2); // Month
        readInt(reader, 2); // Day
        readInt(reader, 2); // Hour
        readInt(reader, 2); // Minute
        readInt(reader, 2); // Second
        goodTest = !readBoolean(reader);
        softwareVersion = readInt(reader, 3);

        if (reader.read() != -1) {
            throw new UnexpectedPacketFormatException("Expected no more bytes from input");
        }
    }

    public String getDeviceId() {
        return deviceId;
    }

    public float getFev1() {
        return fev1;
    }

    public float getFev6() {
        return fev6;
    }

    public float getFev1Fev6Ratio() {
        return fev1Fev6Ratio;
    }

    public float getFef2575() {
        return fef2575;
    }

    public boolean isGoodTest() {
        return goodTest;
    }

    public int getSoftwareVersion() {
        return softwareVersion;
    }

    @Override
    public String toString() {
        return "FevMeasurement [deviceId=" + deviceId + ", fev1=" + fev1 + ", fev6=" + fev6 + ", goodTest=" + goodTest
                + ", softwareVersion=" + softwareVersion + "]";
    }

    private String readString(StringReader reader, int numberOfCharacters, String expectedPattern) throws IOException {
        char[] buffer = new char[numberOfCharacters];
        int count = reader.read(buffer, 0, numberOfCharacters);
        if (count != numberOfCharacters) {
            throw new UnexpectedPacketFormatException("Attempted to read " + numberOfCharacters
                    + " characters, only read " + count);
        }
        String string = new String(buffer);
        if (!string.matches(expectedPattern)) {
            throw new UnexpectedPacketFormatException("String '" + string + "' did not match pattern '"
                    + expectedPattern + "'");
        }
        return string;
    }

    private int readInt(StringReader reader, int numberOfCharacters) throws IOException {
        String string = readString(reader, numberOfCharacters, "\\d+");
        return Integer.parseInt(string);
    }

    private boolean readBoolean(StringReader reader) throws IOException {
        String string = readString(reader, 1, "0|1");
        return string.equals("1");
    }
}
