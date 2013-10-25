package dk.silverbullet.telemed.device.usb;

import java.io.File;

public interface USBController {
    boolean isConnected(String string);

    File[] getFiles(String path, String suffix);
}
