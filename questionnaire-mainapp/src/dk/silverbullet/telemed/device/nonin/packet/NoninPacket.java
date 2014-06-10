package dk.silverbullet.telemed.device.nonin.packet;

public abstract class NoninPacket {

    int calculateChecksum(Integer[] data) {
        int checksum = 0;
        for (int i : data) {
            checksum += i;
        }
        return checksum % 256;
    }
}
