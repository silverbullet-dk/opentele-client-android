package dk.silverbullet.telemed.device.nonin;

import java.util.regex.Pattern;

import dk.silverbullet.telemed.device.DeviceInitialisationException;
import dk.silverbullet.telemed.device.continua.ContinuaDeviceController;
import dk.silverbullet.telemed.device.continua.ContinuaListener;
import dk.silverbullet.telemed.device.continua.DeviceController;
import dk.silverbullet.telemed.device.continua.HdpController;
import dk.silverbullet.telemed.device.continua.HdpProfile;
import dk.silverbullet.telemed.device.continua.PacketCollector;
import dk.silverbullet.telemed.device.continua.packet.SystemId;
import dk.silverbullet.telemed.device.nonin.protocol.NoninProtocolStateController;

/**
 * Main entry point to communicating with the Nonin blood saturation device.
 */
public class NoninController extends DeviceController<SaturationAndPulse> {
    private static final Pattern DEVICE_NAME_PATTERN = Pattern.compile("Nonin_Medical_Inc._\\d{6}");
    private static final String MAC_ADDRESS_FOR_NONIN_MEDICAL_INC = "00:1C:05:";
    private long timestampForConnectionEstablishment;

    /**
     * Creates a new NoninController.
     * 
     * @param listener
     *            Object given callbacks for e.g. notifying the user about the progress through the GUI.
     * @param hdpController
     *            The specific Bluetooth controller to use for the underlying Bluetooth communication. See {@see
     *            AndroidBluetoothController}.
     * @throws DeviceInitialisationException
     *             if Bluetooth is not available, not enabled, or if a general Bluetooth error occurs.
     */
    public static ContinuaDeviceController create(ContinuaListener<SaturationAndPulse> listener,
            HdpController hdpController) throws DeviceInitialisationException {
        return new NoninController(listener, hdpController);
    }

    /**
     * Private constructor. It's private since no client needs to know any other methods than what is defined in
     * {@link ContinuaDeviceController}.
     */
    private NoninController(ContinuaListener<SaturationAndPulse> listener, HdpController hdpController)
            throws DeviceInitialisationException {
        super(listener, hdpController, HdpProfile.PULSE_OXIMETER);

        hdpController.setPacketCollector(new PacketCollector(new NoninProtocolStateController(this)));
        hdpController.setPollForConnection(true);
        hdpController.initiate(DEVICE_NAME_PATTERN, MAC_ADDRESS_FOR_NONIN_MEDICAL_INC);
    }

    @Override
    public void connectionEstablished() {
        timestampForConnectionEstablishment = System.currentTimeMillis();
        super.connectionEstablished();
    }

    @Override
    public void measurementReceived(SystemId systemId, SaturationAndPulse measurement) {
        // The Nonin device may have stored a previous measurement, in case it was unable to deliver the last
        // measurement through Bluetooth. In that case, we quickly get a measurement after connection is
        // established. We need to ignore these measurements.

        long millisSinceConnectionEstablished = System.currentTimeMillis() - timestampForConnectionEstablishment;
        if (millisSinceConnectionEstablished > 1000) {
            super.measurementReceived(systemId, measurement);
        }
    }
}
