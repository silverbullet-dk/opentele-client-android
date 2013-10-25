package dk.silverbullet.telemed.device.continua.packet.output;

import dk.silverbullet.telemed.device.continua.packet.SystemId;

public class AssociationResponsePacket implements OutputPacket {
    private final SystemId systemId;

    public AssociationResponsePacket(SystemId systemId) {
        this.systemId = systemId;
    }

    @Override
    public byte[] getContents() {
        OrderedByteWriter writer = new OrderedByteWriter();

        writer.writeShort(0xE300); // APDU CHOICE Type (AareApdu)
        writer.writeShort(0x002C); // CHOICE.length = 44
        writer.writeShort(0x0000); // result = accepted
        writer.writeShort(0x5079); // data-proto-id = 20601
        writer.writeShort(0x0026); // data-proto-info length = 38
        writer.writeInt(0x80000000); // protocolVersion
        writer.writeShort(0x8000); // encoding rules = MDER
        writer.writeInt(0x80000000); // nomenclatureVersion
        writer.writeInt(0x00000000); // functionalUnits – normal Association
        writer.writeInt(0x80000000); // systemType = sys-type-manager
        writer.writeShort(0x0008); // system-id length = 8 and value (manufacturer- and device- specific)
        writer.writeLong(systemId.asLong()); // System id
        writer.writeShort(0x0000); // Manager’s response to config-id is always 0
        writer.writeShort(0x0000); // Manager’s response to data-req-mode-flags is always 0
        writer.writeShort(0x0000); // data-req-init-agent-count and data-req-init-manager-count are always 0
        writer.writeShort(0x0000); // optionList.count = 0
        writer.writeShort(0x0000); // optionList.length = 0

        return writer.getBytes();
    }

    @Override
    public String toString() {
        return "AssociationResponsePacket [systemId=" + systemId + "]";
    }
}
