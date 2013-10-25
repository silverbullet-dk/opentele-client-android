package dk.silverbullet.telemed.device.continua.packet.input;

import java.io.IOException;

import dk.silverbullet.telemed.device.UnexpectedPacketFormatException;
import dk.silverbullet.telemed.device.continua.ContinuaPacketTag;
import dk.silverbullet.telemed.device.continua.packet.SystemId;

public class AssociationRequestPacket extends InputPacket {
    private static final int ASSOC_VERSION = 0x80000000;
    private SystemId systemId;
    private int deviceConfigurationId;
    private int dataRequestModeFlags;

    public AssociationRequestPacket(byte[] contents) throws IOException {
        super(ContinuaPacketTag.AARQ_APDU, contents);
        checkContents();
    }

    // For test purposes
    public AssociationRequestPacket(SystemId systemId) {
        super(ContinuaPacketTag.AARQ_APDU, new byte[0]);
        this.systemId = systemId;
    }

    public SystemId getSystemId() {
        return systemId;
    }

    public int getDeviceConfigurationId() {
        return deviceConfigurationId;
    }

    public int getDataRequestModeFlags() {
        return dataRequestModeFlags;
    }

    @Override
    public String toString() {
        return "AssociationRequestPacket [systemId=" + getSystemId() + "]";
    }

    private void checkContents() throws IOException {
        OrderedByteReader in = new OrderedByteReader(super.getContents());

        checkInt(in, "association version", ASSOC_VERSION);
        checkShort(in, "proto.list.count", 1);

        in.readShort(); // List length (42) - ignore for now!

        checkShort(in, "protocol", 20601);

        in.readShort(); // Skip data-proto-info length (38)

        checkInt(in, "protocolVersion", 0x80000000);
        checkShort(in, "encodingRules", 0x8000);
        checkInt(in, "nomenclature version", 0x80000000);
        checkInt(in, "functional units", 0x00000000);
        checkInt(in, "system type", 0x00800000);
        checkShort(in, "system-id length", 8);
        systemId = new SystemId(in.readLong());
        deviceConfigurationId = in.readShort(); // 0x0191 for Nonin, 0x4000 for A&D blood pressure device
        dataRequestModeFlags = in.readShort(); // 0x0001 for Nonin
        checkByte(in, "data-req-init-agent-count", 0x01);
        checkByte(in, "data-req-init-manager-count", 0x00);
        checkShort(in, "optionList.count", 0);
        checkShort(in, "optionList.length", 0);

        if (in.available() != 0) {
            throw new UnexpectedPacketFormatException("Superfluous data in packet: " + in.available() + " bytes");
        }

        in.close();
    }
}
