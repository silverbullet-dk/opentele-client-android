package dk.silverbullet.telemed.device.nonin.packet;

import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class NoninMeasurementPacketTest {
 /*
OOT: An absence of consecutive good pulse signals.
LPRF: Low Perfusion: Amplitude representation of low/no signal quality
MPRF: Marginal Perfusion: Amplitude representation of low/marginal signal quality
SNSA: Sensor Alarm: Device is providing unusable data for analysis (set when the finger is removed)
ARTF: Artifact: Indicated artifact condition on each pulse
SPA: SmartPoint: Algorithm High quality SmartPoint measurement
LOW BAT: Low Battery condition Low Batteries. Replace batteries as soon as possible

  */

    Integer[] validMeasurement = {
            Integer.parseInt("10000000", 2),//Status byte #1: 1 (always set), Reserved(0|1), OOT, LPRF, MPRF, ARTF, PR8, PR7
            0x39, //Pulse rate
            0x62, //SpO2-D
            Integer.parseInt("00100000", 2)//Status #2: 0 (always clear), Reserved(0|1), SPA, Reserved(0|1), SNSA, Reserved(0|1), Reserved(0|1), LOW BAT
    };

    Integer[] validMeasurementNotSmartPoint = {
            Integer.parseInt("10000000", 2),//Status byte #1: 1 (always set), Reserved(0|1), OOT, LPRF, MPRF, ARTF, PR8, PR7
            0x39, //Pulse rate
            0x62, //SpO2-D
            Integer.parseInt("00000000", 2)//Status #2: 0 (always clear), Reserved(0|1), SPA, Reserved(0|1), SNSA, Reserved(0|1), Reserved(0|1), LOW BAT
    };

    Integer[] lowPerfusion = {
            Integer.parseInt("10010000", 2),//Status byte #1: 1 (always set), Reserved(0|1), OOT, LPRF, MPRF, ARTF, PR8, PR7
            0x39, //Pulse rate
            0x62, //SpO2-D
            Integer.parseInt("00000000", 2)//Status #2: 0 (always clear), Reserved(0|1), SPA, Reserved(0|1), SNSA, Reserved(0|1), Reserved(0|1), LOW BAT
    };

    Integer[] marginalPerfusion = {
            Integer.parseInt("10001000", 2),//Status byte #1: 1 (always set), Reserved(0|1), OOT, LPRF, MPRF, ARTF, PR8, PR7
            0x39, //Pulse rate
            0x62, //SpO2-D
            Integer.parseInt("00000000", 2)//Status #2: 0 (always clear), Reserved(0|1), SPA, Reserved(0|1), SNSA, Reserved(0|1), Reserved(0|1), LOW BAT
    };

    Integer[] pulseOverflow =  {
            Integer.parseInt("10001011", 2),//Status byte #1: 1 (always set), Reserved(0|1), OOT, LPRF, MPRF, ARTF, PR8, PR7
            Integer.parseInt("01111111", 2), //Pulse rate
            0x62, //SpO2-D
            Integer.parseInt("00000000", 2)//Status #2: 0 (always clear), Reserved(0|1), SPA, Reserved(0|1), SNSA, Reserved(0|1), Reserved(0|1), LOW BAT
    };

    Integer[] pulseOverflow1 =  {
            Integer.parseInt("10001001", 2),//Status byte #1: 1 (always set), Reserved(0|1), OOT, LPRF, MPRF, ARTF, PR8, PR7
            Integer.parseInt("01101011", 2), //Pulse rate
            0x62, //SpO2-D
            Integer.parseInt("00000000", 2)//Status #2: 0 (always clear), Reserved(0|1), SPA, Reserved(0|1), SNSA, Reserved(0|1), Reserved(0|1), LOW BAT
    };

    Integer[] pulseOverflow2 =  {
            Integer.parseInt("10001010", 2),//Status byte #1: 1 (always set), Reserved(0|1), OOT, LPRF, MPRF, ARTF, PR8, PR7
            Integer.parseInt("01100010", 2), //Pulse rate
            0x62, //SpO2-D
            Integer.parseInt("00000000", 2)//Status #2: 0 (always clear), Reserved(0|1), SPA, Reserved(0|1), SNSA, Reserved(0|1), Reserved(0|1), LOW BAT
    };

    Integer[] fingerRemoved = {
            Integer.parseInt("10000000", 2),//Status byte #1: 1 (always set), Reserved(0|1), OOT, LPRF, MPRF, ARTF, PR8, PR7
            0x39, //Pulse rate
            0x62, //SpO2-D
            Integer.parseInt("00001000", 2)//Status #2: 0 (always clear), Reserved(0|1), SPA, Reserved(0|1), SNSA, Reserved(0|1), Reserved(0|1), LOW BAT
    };

    Integer[] lowBatteryRemoved = {
            Integer.parseInt("10000000", 2),//Status byte #1: 1 (always set), Reserved(0|1), OOT, LPRF, MPRF, ARTF, PR8, PR7
            0x39, //Pulse rate
            0x62, //SpO2-D
            Integer.parseInt("00000001", 2)//Status #2: 0 (always clear), Reserved(0|1), SPA, Reserved(0|1), SNSA, Reserved(0|1), Reserved(0|1), LOW BAT
    };

    Integer[] outOfTrack = {
            Integer.parseInt("10100000", 2),//Status byte #1: 1 (always set), Reserved(0|1), OOT, LPRF, MPRF, ARTF, PR8, PR7
            0x39, //Pulse rate
            0x62, //SpO2-D
            Integer.parseInt("00000001", 2)//Status #2: 0 (always clear), Reserved(0|1), SPA, Reserved(0|1), SNSA, Reserved(0|1), Reserved(0|1), LOW BAT
    };

    Integer[] artifact = {
            Integer.parseInt("10000100", 2),//Status byte #1: 1 (always set), Reserved(0|1), OOT, LPRF, MPRF, ARTF, PR8, PR7
            0x39, //Pulse rate
            0x62, //SpO2-D
            Integer.parseInt("00000001", 2)//Status #2: 0 (always clear), Reserved(0|1), SPA, Reserved(0|1), SNSA, Reserved(0|1), Reserved(0|1), LOW BAT
    };

    Integer[] dataMissingPulseIndicator = {
            Integer.parseInt("10001011", 2),//Status byte #1: 1 (always set), Reserved(0|1), OOT, LPRF, MPRF, ARTF, PR8, PR7
            Integer.parseInt("01111111", 2), //Pulse rate
            0x62, //SpO2-D
            Integer.parseInt("00000000", 2)//Status #2: 0 (always clear), Reserved(0|1), SPA, Reserved(0|1), SNSA, Reserved(0|1), Reserved(0|1), LOW BAT
    };

    Integer[] dataMissingSpO2Indicator = {
            Integer.parseInt("10000000", 2),//Status byte #1: 1 (always set), Reserved(0|1), OOT, LPRF, MPRF, ARTF, PR8, PR7
            0x39, //Pulse rate
            0x7f, //SpO2-D
            Integer.parseInt("00001000", 2)//Status #2: 0 (always clear), Reserved(0|1), SPA, Reserved(0|1), SNSA, Reserved(0|1), Reserved(0|1), LOW BAT
    };




    @Test
    public void canParseSpO2Values() throws IOException {
        NoninMeasurementPacket noninMeasurementPacket = new NoninMeasurementPacket(validMeasurement);
        assertEquals(98, noninMeasurementPacket.sp02);
    }

    @Test
    public void canParsePulseValues() throws IOException {
        NoninMeasurementPacket noninMeasurementPacket = new NoninMeasurementPacket(validMeasurement);
        assertEquals(57, noninMeasurementPacket.pulse);
    }

    @Test
    public void canHandlePulseRateOverflow() throws IOException {
        NoninMeasurementPacket noninMeasurementPacket = new NoninMeasurementPacket(pulseOverflow);
        assertEquals(511, noninMeasurementPacket.pulse);

        NoninMeasurementPacket noninMeasurementPacket1 = new NoninMeasurementPacket(pulseOverflow1);
        assertEquals(235, noninMeasurementPacket1.pulse);

        NoninMeasurementPacket noninMeasurementPacket2 = new NoninMeasurementPacket(pulseOverflow2);
        assertEquals(354, noninMeasurementPacket2.pulse);
    }

    @Test
    public void willSetSmartPointFlag() throws IOException {
        NoninMeasurementPacket noninMeasurementPacket = new NoninMeasurementPacket(validMeasurement);
        assertTrue(noninMeasurementPacket.highQuality);

        noninMeasurementPacket = new NoninMeasurementPacket(validMeasurementNotSmartPoint);
        assertFalse(noninMeasurementPacket.highQuality);
    }

    @Test
    public void willSetLowPerfusionFlag() throws IOException {
        NoninMeasurementPacket noninMeasurementPacket = new NoninMeasurementPacket(lowPerfusion);
        assertTrue(noninMeasurementPacket.lowPerfusion);
    }

    @Test
    public void willSetMarginalPerfusionFlag() throws IOException {
        NoninMeasurementPacket noninMeasurementPacket = new NoninMeasurementPacket(marginalPerfusion);
        assertTrue(noninMeasurementPacket.marginalPerfusion);
    }

    @Test
    public void willSetFingerRemoved() throws IOException {
        NoninMeasurementPacket noninMeasurementPacket = new NoninMeasurementPacket(fingerRemoved);
        assertTrue(noninMeasurementPacket.fingerRemoved);
    }

    @Test
    public void willSetLowBattery() throws IOException {
        NoninMeasurementPacket noninMeasurementPacket = new NoninMeasurementPacket(lowBatteryRemoved);
        assertTrue(noninMeasurementPacket.lowBattery);
    }

    @Test
    public void willSetOutOfTrack() throws IOException {
        NoninMeasurementPacket noninMeasurementPacket = new NoninMeasurementPacket(outOfTrack);
        assertTrue(noninMeasurementPacket.outOfTrack);
    }

    @Test
    public void willSetArtifact() throws IOException {
        NoninMeasurementPacket noninMeasurementPacket = new NoninMeasurementPacket(artifact);
        assertTrue(noninMeasurementPacket.artifact);
    }

    @Test
    public void willDetectMissingData() throws IOException {
        NoninMeasurementPacket noninMeasurementPacket = new NoninMeasurementPacket(dataMissingPulseIndicator);
        assertTrue(noninMeasurementPacket.measurementMissing);

        noninMeasurementPacket = new NoninMeasurementPacket(dataMissingSpO2Indicator);
        assertTrue(noninMeasurementPacket.measurementMissing);
    }

}
