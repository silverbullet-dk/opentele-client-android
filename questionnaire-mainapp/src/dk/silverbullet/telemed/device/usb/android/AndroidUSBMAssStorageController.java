package dk.silverbullet.telemed.device.usb.android;

import java.io.File;
import java.io.FileFilter;
import java.io.FilenameFilter;

import android.content.Context;
import android.hardware.usb.UsbManager;
import dk.silverbullet.telemed.device.usb.USBController;

public class AndroidUSBMAssStorageController implements USBController {
    private final UsbManager manager;
    private File selectedUsbDevice;

    public AndroidUSBMAssStorageController(Context context) {
        manager = (UsbManager) context.getSystemService(Context.USB_SERVICE);
    }

    @Override
    public boolean isConnected(String expectedFolder) {
        if (manager.getDeviceList().size() > 1) {
            return false;
        }

        File[] usbDirs = findMountedUsbDevices();
        if (usbDirs == null) {
            return false;
        }

        for (File usbDirectory : usbDirs) {
            if (containsExpectedDir(usbDirectory, expectedFolder)) {
                this.selectedUsbDevice = usbDirectory;
                return true;
            }
        }
        return false;
    }

    @Override
    public File[] getFiles(String path, final String type) {
        File searchPath = new File(selectedUsbDevice.getPath() + "/" + path + "/");

        FilenameFilter fileTypeFilter = new FilenameFilter() {
            @Override
            public boolean accept(File dir, String filename) {
                return filename.endsWith(type);
            }
        };

        return searchPath.listFiles(fileTypeFilter);
    }

    private boolean containsExpectedDir(File usbDirectory, String expectedFolder) {
        return new File(usbDirectory.getPath() + "/" + expectedFolder).exists();
    }

    private File[] findMountedUsbDevices() {
        File mnt = new File("/mnt");
        FileFilter usbFilter = new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                return pathname.getPath().toLowerCase().contains("usb");
            }
        };
        return mnt.listFiles(usbFilter);
    }
}
