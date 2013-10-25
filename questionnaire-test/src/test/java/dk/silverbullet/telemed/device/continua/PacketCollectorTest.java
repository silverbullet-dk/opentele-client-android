package dk.silverbullet.telemed.device.continua;

import static org.mockito.Mockito.*;

import java.io.IOException;
import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import dk.silverbullet.telemed.device.continua.ContinuaPacketTag;
import dk.silverbullet.telemed.device.continua.PacketCollector;
import dk.silverbullet.telemed.device.continua.PacketParser;
import dk.silverbullet.telemed.device.continua.packet.PrettyByteParser;

@RunWith(MockitoJUnitRunner.class)
public class PacketCollectorTest {
    private final static String PACKET_OF_UNKNOWN_TYPE =
    		"AA BB "; // APDU CHOICE
    
    private final static String PACKET_OF_UNREASONABLE_LENGTH =
    		"E2 00 " + // APDU CHOICE
            "FF FF "; // COICE.length (very long...)

    private final static String noninAssociationRequest =
    		"E2 00 " + // APDU CHOICE
            "00 32 " + // COICE.length = 50
            "80 00 00 00 " + // assoc-version
            "00 01 00 2A " + // data-proto-list.count=1 | length=42
            "50 79 " + // data-proto-id=20601
            "00 26 " + // data-proto-info length = 38
            "80 00 00 00 " + // protocolVersion
            "80 00 " + // encoding rules = MDER or PER
            "80 00 00 00 " + // nomenclatureVersion
            "00 00 00 00 " + // functionalUnits – no test association
                             // capabilities
            "00 80 00 00 " + // systemType = sys-type-agent
            "00 08 " + // system-id length = 8 and value (manufacturer- and
                       // device- specific)
            "00 1C 05 01 00 00 95 33 " + // (Nonin \"BDA\")
            "01 91 " + // dev-config-id – extended configuration
            "00 01 " + // data-req-mode-flags
            "01 00 " + // data-req-init-agent-count, data-req-init-manager-count
            "00 00 00 00"; // optionList.count = 0 | optionList.length = 0
    @Mock
    PacketParser packetParser;
    PacketCollector collector;

    @Before
    public void before() {
        collector = new PacketCollector(packetParser);
    }

    @Test
    public void resetsWhenPacketHasUnknownType() {
        feedBytesToCollector(PrettyByteParser.parse(PACKET_OF_UNKNOWN_TYPE));

        verify(packetParser).reset();
        verifyNoMoreInteractions(packetParser);
    }

    @Test
    public void resetsSeveralTimesWhenPacketsHaveUnknownType() {
        feedBytesToCollector(PrettyByteParser.parse(PACKET_OF_UNKNOWN_TYPE));
        feedBytesToCollector(PrettyByteParser.parse(PACKET_OF_UNKNOWN_TYPE));
        
        verify(packetParser, times(2)).reset();
        verifyNoMoreInteractions(packetParser);
    }
    
    @Test
    public void resetsWhenPacketIsTooLong() {
        feedBytesToCollector(PrettyByteParser.parse(PACKET_OF_UNREASONABLE_LENGTH));

        verify(packetParser).reset();
        verifyNoMoreInteractions(packetParser);
    }
    
    @Test
    public void resetsSeveralTimesWhenPacketsAreTooLong() {
        feedBytesToCollector(PrettyByteParser.parse(PACKET_OF_UNREASONABLE_LENGTH));
        feedBytesToCollector(PrettyByteParser.parse(PACKET_OF_UNREASONABLE_LENGTH));

        verify(packetParser, times(2)).reset();
        verifyNoMoreInteractions(packetParser);
    }

    @Test
    public void collectsNoninAssociationRequest() throws Exception {
        byte[] unparsedBytes = PrettyByteParser.parse(noninAssociationRequest);
		feedBytesToCollector(unparsedBytes);

        verify(packetParser).handle(ContinuaPacketTag.AARQ_APDU, Arrays.copyOfRange(unparsedBytes, 4, unparsedBytes.length));
        verifyNoMoreInteractions(packetParser);
    }

    @Test
    public void handlesTwoConsecutivePackets() throws Exception {
        byte[] unparsedBytes = PrettyByteParser.parse(noninAssociationRequest);
        feedBytesToCollector(unparsedBytes);
        feedBytesToCollector(unparsedBytes);

        verify(packetParser, times(2)).handle(ContinuaPacketTag.AARQ_APDU, Arrays.copyOfRange(unparsedBytes, 4, unparsedBytes.length));
		verifyNoMoreInteractions(packetParser);
    }
    
    @Test
    public void picksUpSecondPacketIfFirstPacketFails() throws Exception {
    	IOException exceptionForFirstPacket = new IOException();
        byte[] unparsedBytes = PrettyByteParser.parse(noninAssociationRequest);

        doThrow(exceptionForFirstPacket).
    	doNothing().
    	when(packetParser).handle(ContinuaPacketTag.AARQ_APDU, Arrays.copyOfRange(unparsedBytes, 4, unparsedBytes.length));
    	
        feedBytesToCollector(unparsedBytes);
        feedBytesToCollector(unparsedBytes);

        verify(packetParser, times(2)).handle(ContinuaPacketTag.AARQ_APDU, Arrays.copyOfRange(unparsedBytes, 4, unparsedBytes.length));
        verify(packetParser).errorReceived(exceptionForFirstPacket);
		verifyNoMoreInteractions(packetParser);
    }

    private void feedBytesToCollector(byte[] unparsedBytes) {
        for (byte b : unparsedBytes) {
            collector.receive(b);
        }
    }
}
