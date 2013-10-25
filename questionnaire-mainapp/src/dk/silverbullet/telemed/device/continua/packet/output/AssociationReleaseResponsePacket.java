package dk.silverbullet.telemed.device.continua.packet.output;

public class AssociationReleaseResponsePacket implements OutputPacket {
    @Override
    public byte[] getContents() {
        OrderedByteWriter writer = new OrderedByteWriter();

        writer.writeShort(0xE500); // APDU CHOICE Type (RlrqApdu)
        writer.writeShort(0x0002); // CHOICE.length = 2
        writer.writeShort(0x0000); // reason = normal

        return writer.getBytes();
    }

    @Override
    public String toString() {
        return "[AssociationReleaseResponsePacket]";
    }
}
