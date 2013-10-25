package dk.silverbullet.telemed.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.Test;

public class ProgressiveProgressTest {

    @Test
    public void print() {
        ProgressiveProgress p = new ProgressiveProgress(15, 10, 5, 15, 30);
        for (int i = 0; i < p.getStepCount(); i++) {
            System.out.println(i + ": " + p.step2value(i));
            if (i % 10 == 0)
                System.out.println("-----------");
        }
    }

    @Test
    public void testStep2Value() {
        ProgressiveProgress p = new ProgressiveProgress(15, 10, 5, 15, 30);

        assertEquals(15, p.step2value(0));
        assertEquals(20, p.step2value(1));
        assertEquals(25, p.step2value(2));

        assertEquals(60, p.step2value(9));
        assertEquals(75, p.step2value(10));
        assertEquals(90, p.step2value(11));

        assertEquals(210, p.step2value(19));
        assertEquals(240, p.step2value(20));
        assertEquals(270, p.step2value(21));
    }

    @Test
    public void testValue2Step() {
        ProgressiveProgress p = new ProgressiveProgress(15, 10, 5, 15, 30);

        assertEquals(0, p.value2step(15));
        assertEquals(1, p.value2step(20));
        assertEquals(2, p.value2step(25));

        assertEquals(9, p.value2step(60));
        assertEquals(10, p.value2step(75));
        assertEquals(11, p.value2step(90));

        assertEquals(19, p.value2step(210));
        assertEquals(20, p.value2step(240));
        assertEquals(21, p.value2step(270));

        // Check correct rounding:

        assertEquals(0, p.value2step(17));
        assertEquals(1, p.value2step(18));
        assertEquals(1, p.value2step(19));
        assertEquals(1, p.value2step(20));
        assertEquals(1, p.value2step(21));
        assertEquals(1, p.value2step(22));
        assertEquals(2, p.value2step(23));

    }

    @Test
    public void testArgumentExceptions() {
        ProgressiveProgress p = new ProgressiveProgress(15, 10, 5, 15, 30);

        try {
            p.step2value(-1);
            fail();
        } catch (IllegalArgumentException iae) {
            // OK!
        }

        try {
            p.step2value(30);
            fail();
        } catch (IllegalArgumentException iae) {
            // OK!
        }

        try {
            p.value2step(8);
            fail();
        } catch (IllegalArgumentException iae) {
            // OK!
        }

        try {
            p.value2step(526);
            fail();
        } catch (IllegalArgumentException iae) {
            // OK!
        }
    }
}
