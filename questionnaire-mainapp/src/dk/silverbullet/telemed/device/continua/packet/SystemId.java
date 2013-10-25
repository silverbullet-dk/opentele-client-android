package dk.silverbullet.telemed.device.continua.packet;

public class SystemId {
    private final long value;

    public SystemId(String asString) {
        value = parseHexAsLong(asString);
    }

    public SystemId(long asLong) {
        value = asLong;
    }

    public long asLong() {
        return value;
    }

    public String asString() {
        return String.format("%016X", value);
    }

    @Override
    public String toString() {
        return "SystemId [" + asString() + "]";
    }

    @Override
    public int hashCode() {
        return ((Long) value).hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj.getClass() != this.getClass()) {
            return false;
        }
        SystemId other = (SystemId) obj;
        return this.value == other.value;
    }

    private long parseHexAsLong(String asString) {
        long result = 0;
        for (char c : asString.toCharArray()) {
            result <<= 4;
            result += Long.parseLong("" + c, 16);
        }
        return result;
    }
}
