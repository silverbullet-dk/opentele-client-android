package dk.silverbullet.telemed.device.bluetooth;

import java.util.regex.Pattern;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothHealth;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.BluetoothProfile.ServiceListener;
import android.content.Context;
import android.util.Log;
import dk.silverbullet.telemed.device.AmbiguousDeviceException;
import dk.silverbullet.telemed.device.BluetoothDisabledException;
import dk.silverbullet.telemed.device.BluetoothNotAvailableException;
import dk.silverbullet.telemed.device.DeviceInitialisationException;
import dk.silverbullet.telemed.device.DeviceNotFoundException;
import dk.silverbullet.telemed.utils.Util;

public class BluetoothConnector {
    private static final String TAG = Util.getTag(BluetoothConnector.class);
    private BluetoothAdapter bluetoothAdapter;

    public void initiate() throws DeviceInitialisationException {
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        if (bluetoothAdapter == null) {
            throw new BluetoothNotAvailableException();
        }
        if (!bluetoothAdapter.isEnabled()) {
            throw new BluetoothDisabledException();
        }
    }

    public BluetoothDevice getDevice(Pattern deviceNamePattern, String deviceMacAddressPrefix)
            throws DeviceInitialisationException {
        BluetoothDevice result = null;
        for (BluetoothDevice potentialDevice : bluetoothAdapter.getBondedDevices()) {
            String address = potentialDevice.getAddress();
            String name = potentialDevice.getName();
            Log.d(TAG, name + ": " + address);

            boolean hasMatchingPrefix = address.startsWith(deviceMacAddressPrefix);
            boolean hasMatchingName = deviceNamePattern.matcher(name).matches();
            if (hasMatchingPrefix && hasMatchingName) {
                if (result != null) {
                    throw new AmbiguousDeviceException();
                }
                result = potentialDevice;
            }
        }

        if (result == null) {
            throw new DeviceNotFoundException();
        }

        return result;
    }

    public void openHdp(Context context, ServiceListener serviceListener) throws DeviceInitialisationException {
        boolean couldGetProfileProxy = bluetoothAdapter.getProfileProxy(context, serviceListener,
                BluetoothProfile.HEALTH);
        if (!couldGetProfileProxy) {
            throw new DeviceInitialisationException("Could not get profile proxy");
        }
    }

    public void closeHdp(BluetoothHealth bluetoothHealth) {
        bluetoothAdapter.closeProfileProxy(BluetoothProfile.HEALTH, bluetoothHealth);
    }
}
