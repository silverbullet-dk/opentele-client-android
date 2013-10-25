package dk.silverbullet.telemed.device.continua.packet.input;

import java.io.IOException;

import dk.silverbullet.telemed.device.UnexpectedPacketFormatException;
import dk.silverbullet.telemed.device.continua.ContinuaPacketTag;

public abstract class ConfirmedMeasurementDataPacket extends InputPacket {
    protected static final int MDC_NOTI_SCAN_REPORT_FIXED = 0x0D1D;
    protected static final int MDC_NOTI_SCAN_REPORT_VAR = 0x0D1E;
    private int invokeId;
    private int eventTime;
    private int eventType;

    public ConfirmedMeasurementDataPacket(byte[] contents) throws IOException {
        super(ContinuaPacketTag.PRST_APDU, contents);
        checkContents();
    }

    public ConfirmedMeasurementDataPacket(int invokeId, int eventTime) {
        super(ContinuaPacketTag.PRST_APDU, new byte[0]);
        this.invokeId = invokeId;
        this.eventTime = eventTime;
    }

    protected abstract void readObjects(int eventType, OrderedByteReader byteReader) throws IOException;

    private void checkContents() throws IOException {
        OrderedByteReader in = new OrderedByteReader(super.getContents());

        in.readShort(); // OCTET STRING.length - 52 for Nonin oxymeter
        invokeId = in.readShort(); // invoke-id (differentiates this from other outstanding messages)
        in.readShort(); // CHOICE(Remote Operation Invoke | Confirmed Event Report) - 0x0100 for Nonin Oxymeter
        in.readShort(); // CHOICE.length - 46 for Nonin Oxymeter
        checkShort(in, "obj-handle = 0 (MDS object)", 0);
        eventTime = in.readInt();
        eventType = in.readShort(); // event-type
        in.readShort(); // event-info.length - 36 for Nonin Oxymeter
        checkShort(in, "ScanReportInfoFixed.data-req-id", 0xF000);
        in.readShort(); // ScanReportInfoFixed.scan-report-no

        readObjects(eventType, in);

        if (in.available() != 0) {
            throw new UnexpectedPacketFormatException("Superfluous data in packet: " + in.available() + " bytes");
        }

        in.close();
    }

    public int getInvokeId() {
        return invokeId;
    }

    public int getEventTime() {
        return eventTime;
    }

    public int getEventType() {
        return eventType;
    }
}
