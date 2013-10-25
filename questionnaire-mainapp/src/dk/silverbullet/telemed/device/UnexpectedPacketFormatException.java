package dk.silverbullet.telemed.device;

import java.io.IOException;

public class UnexpectedPacketFormatException extends IOException {

    private static final long serialVersionUID = 2409627902741856815L;

    public UnexpectedPacketFormatException(String message) {
        super(message);
    }
}
