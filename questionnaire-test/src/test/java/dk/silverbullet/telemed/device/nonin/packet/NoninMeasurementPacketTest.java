package dk.silverbullet.telemed.device.nonin.packet;

import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class NoninMeasurementPacketTest {
    Integer[] validNonMemoryMeasurementPacket = {
            0x02, // STX
            0x00, // OP_CODE Most significant byte
            0x0d, // OP_CODE Least significant byte
            0x00, // Data length Most siginifant byte
            0x0e, // Data length Least siginifant byte
            0x20, // Date information
            0x07, // Date information
            0x10, // Date information
            0x01, // Date information
            0x00, // Date information
            0x00, // Date information
            0x54, // Date information
            0x00, // Date information
            0x02, // Status Most significant byte
            0x00, // Status Least significant byte
            0x00, // Pulse rate most significant byte
            0x3c, // Pulse rate Least significant byte
            0x00, // Reserved for future use
            0x63, // SpO2
            0x2d, // Checksum
            0x03  // ETX
    };

    Integer[] validMemoryMeasurementPacket = {
            0x02, // STX
            0x00, // OP_CODE Most significant byte
            0x0d, // OP_CODE Least significant byte
            0x00, // Data length Most siginifant byte
            0x0e, // Data length Least siginifant byte
            0x20, // Date information
            0x07, // Date information
            0x10, // Date information
            0x01, // Date information
            0x18, // Date information
            0x48, // Date information
            0x21, // Date information
            0x00, // Date information
            0x02, // Status Most significant byte
            0x10, // Status Least significant byte
            0x00, // Pulse rate most significant byte
            0x34, // Pulse rate Least significant byte
            0x00, // Reserved for future use
            0x63, // SpO2
            0x62, // Checksum
            0x03 // ETX
    };

    @Test
    public void canParseSpO2Values() throws IOException {
        NoninMeasurementPacket noninMeasurementPacket = new NoninMeasurementPacket(validNonMemoryMeasurementPacket);
        assertEquals(99, noninMeasurementPacket.sp02);
    }

    @Test
    public void canParsePulseValues() throws IOException {
        NoninMeasurementPacket noninMeasurementPacket = new NoninMeasurementPacket(validNonMemoryMeasurementPacket);
        assertEquals(60, noninMeasurementPacket.pulse);
    }

    @Test
    public void canParsePulseValuesThatOverflowIntoMostSignificantBit() throws IOException {
        Integer[] pulseOverflowingMeasurement = {
                0x02, // STX
                0x00, // OP_CODE Most significant byte
                0x0d, // OP_CODE Least significant byte
                0x00, // Data length Most significant byte
                0x0e, // Data length Least significant byte
                0x20, // Date information
                0x07, // Date information
                0x10, // Date information
                0x01, // Date information
                0x18, // Date information
                0x48, // Date information
                0x21, // Date information
                0x00, // Date information
                0x02, // Status Most significant byte
                0x10, // Status Least significant byte
                0x01, // Pulse rate most significant byte
                0xf2, // Pulse rate Least significant byte
                0x00, // Reserved for future use
                0x63, // SpO2
                0x21, // Checksum
                0x03 // ETX
        };

        NoninMeasurementPacket noninMeasurementPacket = new NoninMeasurementPacket(pulseOverflowingMeasurement);
        assertEquals(498, noninMeasurementPacket.pulse);
    }

    @Test
    public void canDiscernMeasurementFromMemoryFromRecentMeasurements() throws IOException {
        NoninMeasurementPacket noninMeasurementPacket = new NoninMeasurementPacket(validNonMemoryMeasurementPacket);
        assertEquals(false, noninMeasurementPacket.isFromMemory);

        NoninMeasurementPacket noninMeasurementPacketFromMemory = new NoninMeasurementPacket(validMemoryMeasurementPacket);
        assertEquals(true, noninMeasurementPacketFromMemory.isFromMemory);
    }

    @Test(expected = IOException.class)
    public void willThrowIOExceptionOnBadChecksum() throws IOException {
        Integer[] badChecksumMeasurementPacket = {
                0x02, // STX
                0x00, // OP_CODE Most significant byte
                0x0d, // OP_CODE Least significant byte
                0x00, // Data length Most significant byte
                0x0e, // Data length Least significant byte
                0x20, // Date information
                0x07, // Date information
                0x10, // Date information
                0x01, // Date information
                0x18, // Date information
                0x48, // Date information
                0x21, // Date information
                0x00, // Date information
                0x02, // Status Most significant byte
                0x10, // Status Least significant byte
                0x00, // Pulse rate most significant byte
                0x34, // Pulse rate Least significant byte
                0x00, // Reserved for future use
                0x63, // SpO2
                0x12, // Checksum, should have been 0x62
                0x03 // ETX
        };

        new NoninMeasurementPacket(badChecksumMeasurementPacket);

    }

    @Test(expected = IOException.class)
    public void willThrowIOExceptionOnUnexpectedDataLengthIndicationOnMostSignificantByte() throws IOException {
        Integer[] badDataLengthMSBValue = {
                0x02, // STX
                0x00, // OP_CODE Most significant byte
                0x0d, // OP_CODE Least significant byte
                0x01, // Data length Most significant byte, should have been 00
                0x0e, // Data length Least significant byte
                0x20, // Date information
                0x07, // Date information
                0x10, // Date information
                0x01, // Date information
                0x18, // Date information
                0x48, // Date information
                0x21, // Date information
                0x00, // Date information
                0x02, // Status Most significant byte
                0x10, // Status Least significant byte
                0x00, // Pulse rate most significant byte
                0x34, // Pulse rate Least significant byte
                0x00, // Reserved for future use
                0x63, // SpO2
                0x62, // Checksum
                0x03 // ETX
        };

        new NoninMeasurementPacket(badDataLengthMSBValue);
    }

    @Test(expected = IOException.class)
    public void willThrowIOExceptionOnUnexpectedDataLengthIndicationOnLeastSignificantByte() throws IOException {
        Integer[] badDataLengthLSBValue = {
                0x02, // STX
                0x00, // OP_CODE Most significant byte
                0x0d, // OP_CODE Least significant byte
                0x00, // Data length Most significant byte
                0x01, // Data length Least significant byte, should have been 0e
                0x20, // Date information
                0x07, // Date information
                0x10, // Date information
                0x01, // Date information
                0x18, // Date information
                0x48, // Date information
                0x21, // Date information
                0x00, // Date information
                0x02, // Status Most significant byte
                0x10, // Status Least significant byte
                0x00, // Pulse rate most significant byte
                0x34, // Pulse rate Least significant byte
                0x00, // Reserved for future use
                0x63, // SpO2
                0x62, // Checksum
                0x03 // ETX
        };

        new NoninMeasurementPacket(badDataLengthLSBValue);
    }

    @Test
    public void willDetectMissingData() throws IOException {
        Integer[] missingDataPacket = {
                0x02, // STX
                0x00, // OP_CODE Most significant byte
                0x0d, // OP_CODE Least significant byte
                0x00, // Data length Most significant byte
                0x0e, // Data length Least significant byte
                0x20, // Date information
                0x07, // Date information
                0x10, // Date information
                0x01, // Date information
                0x18, // Date information
                0x48, // Date information
                0x21, // Date information
                0x00, // Date information
                0x02, // Status Most significant byte
                0x10, // Status Least significant byte
                0x01, // Pulse rate most significant byte
                0x7F, // Pulse rate Least significant byte
                0x00, // Reserved for future use
                0x7F, // SpO2
                0xCA, // Checksum
                0x03 // ETX
        };

        NoninMeasurementPacket noninMeasurementPacket = new NoninMeasurementPacket(missingDataPacket);
        assertTrue(noninMeasurementPacket.isDataMissing);
    }

}
