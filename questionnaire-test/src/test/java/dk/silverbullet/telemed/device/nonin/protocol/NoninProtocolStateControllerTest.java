package dk.silverbullet.telemed.device.nonin.protocol;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import dk.silverbullet.telemed.device.continua.packet.SystemId;
import dk.silverbullet.telemed.device.continua.packet.input.AssociationRequestPacket;
import dk.silverbullet.telemed.device.continua.packet.output.AssociationResponsePacket;
import dk.silverbullet.telemed.device.continua.packet.output.ConfirmedMeasurementResponsePacket;
import dk.silverbullet.telemed.device.continua.packet.output.OutputPacket;
import dk.silverbullet.telemed.device.continua.protocol.ProtocolStateListener;
import dk.silverbullet.telemed.device.nonin.SaturationAndPulse;
import dk.silverbullet.telemed.device.nonin.packet.input.NoninConfirmedMeasurementDataPacket;

@RunWith(MockitoJUnitRunner.class)
public class NoninProtocolStateControllerTest {
    @Mock
    ProtocolStateListener<SaturationAndPulse> listener;
    NoninProtocolStateController controller;

    @Before
    public void before() {
        controller = new NoninProtocolStateController(listener);
    }

    @Test
    public void sendsAssociationResponseWhenAssociationRequestIsReceived() throws IOException {
        controller.receive(new AssociationRequestPacket(new SystemId("1234567890")));

        verify(listener).sendPacket(any(AssociationResponsePacket.class));
        verifyNoMoreInteractions(listener);
    }

    @Test
    public void sendsMeasurementReceivedWhenConfirmedMeasurementIsReceived() throws IOException {
        controller.receive(new AssociationRequestPacket(new SystemId("1234567890")));
        controller.receive(new NoninConfirmedMeasurementDataPacket(100, 45, 1023, 0));

        ArgumentCaptor<OutputPacket> captor = ArgumentCaptor.forClass(OutputPacket.class);
        verify(listener, times(2)).sendPacket(captor.capture());

        ConfirmedMeasurementResponsePacket responsePacket = (ConfirmedMeasurementResponsePacket) captor.getAllValues().get(1);
        assertEquals(1023, responsePacket.getInvokeId());
    }

    @Test
    public void notifiesThatMeasurementIsReceived() {
        controller.receive(new AssociationRequestPacket(new SystemId("1234567890")));
        controller.receive(new NoninConfirmedMeasurementDataPacket(100, 45, 1023, 0));

        verify(listener).measurementReceived(new SystemId("1234567890"), new SaturationAndPulse(100, 45));
    }

     @Test
     public void notifiesWhenTooManyUnexpectedPacketsReceived() {
         controller.receive(new NoninConfirmedMeasurementDataPacket(100, 45, 1023, 0));
         controller.receive(new NoninConfirmedMeasurementDataPacket(100, 45, 1023, 0));
         controller.receive(new NoninConfirmedMeasurementDataPacket(100, 45, 1023, 0));
         controller.receive(new NoninConfirmedMeasurementDataPacket(100, 45, 1023, 0));
         controller.receive(new NoninConfirmedMeasurementDataPacket(100, 45, 1023, 0));
        
         verify(listener).tooManyRetries();
         verifyNoMoreInteractions(listener);
     }
}
