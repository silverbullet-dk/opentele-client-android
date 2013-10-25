package dk.silverbullet.telemed.device.andweightscale;

import java.util.regex.Pattern;

import dk.silverbullet.telemed.device.DeviceInitialisationException;
import dk.silverbullet.telemed.device.andweightscale.protocol.AndWeightScaleProtocolStateController;
import dk.silverbullet.telemed.device.continua.ContinuaDeviceController;
import dk.silverbullet.telemed.device.continua.ContinuaListener;
import dk.silverbullet.telemed.device.continua.DeviceController;
import dk.silverbullet.telemed.device.continua.HdpController;
import dk.silverbullet.telemed.device.continua.HdpProfile;
import dk.silverbullet.telemed.device.continua.PacketCollector;

/**
 * Main entry point to communicating with the A&D Medical weight scale.
 */
public class AndWeightScaleController extends DeviceController<Weight> {
    private static final Pattern DEVICE_NAME_PATTERN = Pattern.compile("A(&|N)D WS UC-321PBT-C");
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
    public static ContinuaDeviceController create(ContinuaListener<Weight> listener, HdpController hdpController)
            throws DeviceInitialisationException {
        return new AndWeightScaleController(listener, hdpController);
    }

    /**
     * Private constructor. It's private since no client needs to know any other methods than what is defined in
     * {@link ContinuaDeviceController}.
     */
    private AndWeightScaleController(ContinuaListener<Weight> listener, HdpController hdpController)
            throws DeviceInitialisationException {
        super(listener, hdpController, HdpProfile.BODY_WEIGHT_SCALE);

        hdpController.setPacketCollector(new PacketCollector(new AndWeightScaleProtocolStateController(this)));
        hdpController.initiate(DEVICE_NAME_PATTERN, MAC_ADDRESS_FOR_AD_MEDICAL);
    }
}
