package dk.silverbullet.telemed.device.continua;

import android.util.Log;
import dk.silverbullet.telemed.utils.Util;

public enum ContinuaPacketTag {
    AARQ_APDU(0xE200), AARE_APDU(0xE300), RLRQ_APDU(0xE400), ABRT_APDU(0xE600), PRST_APDU(0xE700);

    private static final String TAG = Util.getTag(ContinuaPacketTag.class);
    private final int tag;

    private ContinuaPacketTag(int tag) {
        this.tag = tag;
    }

    public int getTag() {
        return tag;
    }

    public static ContinuaPacketTag packetTagForValue(int tag) {
        for (ContinuaPacketTag continuaPacketTag : values()) {
            if (continuaPacketTag.getTag() == tag) {
                return continuaPacketTag;
            }
        }
        throw new IllegalArgumentException("Unknown tag: 0x" + Integer.toString(tag, 16));
    }

    public static boolean isKnownTagValue(int tag) {
        for (ContinuaPacketTag continuaPacketTag : values()) {
            if (continuaPacketTag.getTag() == tag) {
                return true;
            }
        }
        Log.w(TAG, "Unknown tag: 0x" + Integer.toString(tag, 16));
        return false;
    }
}
