package dk.silverbullet.telemed.device.andbloodpressure;

import java.util.regex.Pattern;

import dk.silverbullet.telemed.device.DeviceInitialisationException;
import dk.silverbullet.telemed.device.andbloodpressure.protocol.AndBloodPressureProtocolStateController;
import dk.silverbullet.telemed.device.continua.ContinuaDeviceController;
import dk.silverbullet.telemed.device.continua.ContinuaListener;
import dk.silverbullet.telemed.device.continua.DeviceController;
import dk.silverbullet.telemed.device.continua.HdpController;
import dk.silverbullet.telemed.device.continua.HdpProfile;
import dk.silverbullet.telemed.device.continua.PacketCollector;

/**
 * Main entry point to communicating with the A&D Medical blood pressure device.
 */
public class AndBloodPressureController extends DeviceController<BloodPressureAndPulse> {
    private static final Pattern DEVICE_NAME_PATTERN = Pattern.compile("A(&|N)D BP UA-767PBT-C");
    private static final String MAC_ADDRESS_FOR_AD_MEDICAL = "00:09:1F:";

    /**
     * Creates the device controller.
     * 
     * @param listener
     *            Object given callbacks for e.g. notifying the user about the progress through the GUI.
     * @param hdpController
     *            The specific Bluetooth controller to use for the underlying Bluetooth communication. See {@see
     *            AndroidBluetoothController}.
     * @throws DeviceInitialisationException
     *             if Bluetooth is not available, not enabled, or if a general Bluetooth error occurs.
     */
    public static ContinuaDeviceController create(ContinuaListener<BloodPressureAndPulse> listener,
            HdpController hdpController) throws DeviceInitialisationException {
        return new AndBloodPressureController(listener, hdpController);
    }

    /**
     * Private constructor. It's private since no client needs to know any other methods than what is defined in
     * {@link ContinuaDeviceController}.
     */
    private AndBloodPressureController(ContinuaListener<BloodPressureAndPulse> listener, HdpController hdpController)
            throws DeviceInitialisationException {
        super(listener, hdpController, HdpProfile.BLOOD_PRESSURE_METER);

        hdpController.setPacketCollector(new PacketCollector(new AndBloodPressureProtocolStateController(this)));
        // hdpController.setPollForConnection(true);
        hdpController.initiate(DEVICE_NAME_PATTERN, MAC_ADDRESS_FOR_AD_MEDICAL);
    }
}
