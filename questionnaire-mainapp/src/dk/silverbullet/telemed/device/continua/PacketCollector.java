package dk.silverbullet.telemed.device.continua;

import java.io.IOException;

public class PacketCollector {
    private static final int NUMBER_OF_HEADER_BYTES = 4;

    private final PacketParser parser;
    private int counter;
    private int tagValue;
    private ContinuaPacketTag tag;
    private int length;
    private byte[] contents;

    public PacketCollector(PacketParser parser) {
        this.parser = parser;
    }

    public void receive(byte b) {
        switch (counter) {
        case 0:
            tagValue = (b & 0xFF) << 8;
            break;
        case 1:
            tagValue |= (b & 0xFF);
            if (!ContinuaPacketTag.isKnownTagValue(tagValue)) {
                reset();
                return;
            }
            tag = ContinuaPacketTag.packetTagForValue(tagValue);
            break;
        case 2:
            length = (b & 0xFF) << 8;
            break;
        case 3:
            length |= (b & 0xFF);
            if (!reasonableLength(length)) {
                reset();
                return;
            }
            contents = new byte[length];
            break;
        default:
            contents[counter - NUMBER_OF_HEADER_BYTES] = b;
        }
        counter++;

        if (hasReceivedWholeBody()) {
            try {
                parser.handle(tag, contents);
            } catch (IOException ex) {
                error(ex);
            } finally {
                resetBuffer();
            }
        }
    }

    public void error(IOException e) {
        parser.errorReceived(e);
    }

    public void reset() {
        resetBuffer();
        parser.reset();
    }

    private boolean hasReceivedWholeBody() {
        int receivedNumberOfBodyBytes = counter - NUMBER_OF_HEADER_BYTES;
        return receivedNumberOfBodyBytes == length;
    }

    private void resetBuffer() {
        counter = 0;
    }

    private boolean reasonableLength(int length) {
        return length >= 2 && length < 1000;
    }
}
