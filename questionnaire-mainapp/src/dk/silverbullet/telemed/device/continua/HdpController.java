package dk.silverbullet.telemed.device.continua;

import java.io.IOException;
import java.util.regex.Pattern;

import dk.silverbullet.telemed.device.DeviceInitialisationException;

public interface HdpController {
    void setHdpProfile(HdpProfile bloodPressureMeter);

    void setPacketCollector(PacketCollector collector);

    void setBluetoothListener(HdpListener listener);

    void setPollForConnection(boolean pollForConnection);

    void initiate(Pattern deviceNamePattern, String deviceMacAddressPrefix) throws DeviceInitialisationException;

    void send(byte[] contents) throws IOException;

    void terminate();
}
