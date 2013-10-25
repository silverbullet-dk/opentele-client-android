package dk.silverbullet.telemed.utils;

import static dk.silverbullet.telemed.utils.Util.getUnsignedIntBits;
import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class TestUtil {
    @Test
    public void test0getUnsignedIntBits() {

        byte[] in = { 123 };
        int value = getUnsignedIntBits(in, 0, 8);
        assertEquals("Get first byte", 123, value);
    }

    @Test
    public void test2getUnsignedIntBits() {

        byte[] in = { 0, 68 };
        int value = getUnsignedIntBits(in, 8, 8);
        assertEquals("Get last byte", 68, value);
    }

    @Test
    public void test3getUnsignedIntBits() {

        byte[] in = { 0x7F, (byte) 0x80 };
        int value = getUnsignedIntBits(in, 1, 8);
        assertEquals("Get one byte", 255, value);
    }

    @Test
    public void test4getUnsignedIntBits() {

        byte[] in = { 0x01, (byte) 0xFE };
        int value = getUnsignedIntBits(in, 7, 8);
        assertEquals("Get one byte", 255, value);
    }
}
