package dk.silverbullet.telemed.device.continua.packet.output;

public class ConfirmedMeasurementResponsePacket implements OutputPacket {
    private final int invokeId;
    private final int eventType;

    public ConfirmedMeasurementResponsePacket(int invokeId, int eventType) {
        this.invokeId = invokeId;
        this.eventType = eventType;
    }

    @Override
    public byte[] getContents() {
        OrderedByteWriter writer = new OrderedByteWriter();

        writer.writeShort(0xE700); // APDU CHOICE Type (PrstApdu)
        writer.writeShort(0x0012); // CHOICE.length = 18
        writer.writeShort(0x0010); // OCTET STRING.length = 16
        writer.writeShort(invokeId); // invoke-id (mirrored from invocation)
        writer.writeShort(0x0201); // CHOICE(Remote Operation Response | Confirmed Event Report)
        writer.writeShort(0x000A); // CHOICE.length = 10
        writer.writeShort(0x0000); // obj-handle = 0 (MDS object)
        writer.writeInt(0x00000000); // currentTime = 0
        writer.writeShort(eventType); // event-type
        writer.writeShort(0x0000); // event-reply-info.length = 0

        return writer.getBytes();
    }

    public int getInvokeId() {
        return invokeId;
    }

    @Override
    public String toString() {
        return "ConfirmedMeasurementResponsePacket [invokeId=" + invokeId + "]";
    }
}
