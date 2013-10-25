package dk.silverbullet.telemed.device.accuchek;

import java.io.IOException;

public class CsvFormatException extends IOException {

    private static final long serialVersionUID = -2721261992559047097L;

    public CsvFormatException(String message) {
        super(message);
    }

    public CsvFormatException(String message, Throwable cause) {
        super(message, cause);
    }
}
