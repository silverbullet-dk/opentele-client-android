package dk.silverbullet.telemed.device.monica;

import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import java.util.Date;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import dk.silverbullet.telemed.device.monica.packet.CBlockMessage;
import dk.silverbullet.telemed.device.monica.packet.CBlockMessageTest;
import dk.silverbullet.telemed.questionnaire.node.monica.MonicaDeviceCallback;

@RunWith(MockitoJUnitRunner.class)
public class MessageProcessorTest {
    @Mock
    MonicaDeviceCallback callback;
    MessageProcessor processor;
    long now = System.currentTimeMillis();
    final long startTime = now;
    ArgumentCaptor<float[]> mhrCaptor = ArgumentCaptor.forClass(float[].class);
    ArgumentCaptor<float[]> fhrCaptor = ArgumentCaptor.forClass(float[].class);
    ArgumentCaptor<int[]> qfhrCaptor = ArgumentCaptor.forClass(int[].class);
    ArgumentCaptor<float[]> tocoCaptor = ArgumentCaptor.forClass(float[].class);

    @Before
    public void before() {
        processor = new MessageProcessor(callback);

        when(callback.getSampleTimeMinutes()).thenReturn(1);
    }

    @Test
    public void doesNothingWhileReceivingInitialCBlocks() throws Exception {
        processor.process(initialCBlockAfter(0));
        processor.process(initialCBlockAfter(2000));
        processor.process(initialCBlockAfter(2));
        processor.process(initialCBlockAfter(2000));
        processor.process(initialCBlockAfter(2));

        verifyZeroInteractions(callback);
    }

    @Test
    public void doesNothingBeforeThreeCBlockPairs() throws Exception {
        processor.process(cBlockWithHeartRateAfter(0));
        processor.process(cBlockWithHeartRateAfter(5));
        processor.process(cBlockWithHeartRateAfter(2000));
        processor.process(cBlockWithHeartRateAfter(5));
        processor.process(cBlockWithHeartRateAfter(2000));

        verify(callback, times(5)).updateProgress(anyInt(), anyInt());
        verify(callback, times(5)).getSampleTimeMinutes();
        verifyNoMoreInteractions(callback);
    }

    @Test
    public void acceptsSlowMessageSkewing() throws Exception {
        processor.process(cBlockWithHeartRateAfter(0));
        processor.process(cBlockWithHeartRateAfter(5));

        processor.process(cBlockWithHeartRateAfter(2000));
        processor.process(cBlockWithHeartRateAfter(5));

        processor.process(cBlockWithHeartRateAfter(2000));
        processor.process(cBlockWithHeartRateAfter(5));

        processor.process(cBlockWithHeartRateAfter(1595)); // 400 ms too short, but OK!
        processor.process(cBlockWithHeartRateAfter(5));

        processor.process(cBlockWithHeartRateAfter(1595)); // 800 ms too short, but OK!
        processor.process(cBlockWithHeartRateAfter(5));

        processor.process(cBlockWithHeartRateAfter(1595)); // 1200 ms too short, but OK!
        processor.process(cBlockWithHeartRateAfter(5));

        processor.process(cBlockWithHeartRateAfter(1595)); // 1600 ms too short, but OK!
        processor.process(cBlockWithHeartRateAfter(5));

        processor.process(cBlockWithHeartRateAfter(1595)); // 2000 ms too short, but OK!
        processor.process(cBlockWithHeartRateAfter(5));

        processor.process(cBlockWithHeartRateAfter(1550)); // 2400 ms too short, but OK!
        processor.process(cBlockWithHeartRateAfter(5));

        verify(callback, times(18)).addSamples(mhrCaptor.capture(), fhrCaptor.capture(), qfhrCaptor.capture(),
                tocoCaptor.capture());
        verify(callback, times(18)).updateProgress(anyInt(), anyInt());
        verify(callback, times(18)).getSampleTimeMinutes();
        verify(callback).setStartTimeValue(new Date(startTime));
        verifyNoMoreInteractions(callback);
    }

    @Test
    public void startsSendingMessagesAfterThreeCBlockPairs() throws Exception {
        processor.process(cBlockWithHeartRateAfter(0));
        processor.process(cBlockWithHeartRateAfter(5));
        processor.process(cBlockWithHeartRateAfter(2000));
        processor.process(cBlockWithHeartRateAfter(5));
        processor.process(cBlockWithHeartRateAfter(2000));
        processor.process(cBlockWithHeartRateAfter(5));

        verify(callback, times(6)).addSamples(mhrCaptor.capture(), fhrCaptor.capture(), qfhrCaptor.capture(),
                tocoCaptor.capture());
        verify(callback, times(6)).updateProgress(anyInt(), anyInt());
        verify(callback, times(6)).getSampleTimeMinutes();
        verify(callback).setStartTimeValue(new Date(startTime));
        verifyNoMoreInteractions(callback);
    }

    @Test
    public void holdsBackMessagesWhenLongPausesInBetween() throws Exception {
        // CBlocks to get the processor started
        processor.process(cBlockWithHeartRateAfter(0));
        processor.process(cBlockWithHeartRateAfter(5));
        processor.process(cBlockWithHeartRateAfter(2000));
        processor.process(cBlockWithHeartRateAfter(5));
        processor.process(cBlockWithHeartRateAfter(2000));
        processor.process(cBlockWithHeartRateAfter(5));

        // Not delivered yet
        processor.process(cBlockWithHeartRateAfter(2600));
        processor.process(cBlockWithHeartRateAfter(0));
        processor.process(cBlockWithHeartRateAfter(0));
        processor.process(cBlockWithHeartRateAfter(0));

        verify(callback, times(6)).addSamples(mhrCaptor.capture(), fhrCaptor.capture(), qfhrCaptor.capture(),
                tocoCaptor.capture());
    }

    @Test(expected = MonicaSamplesMissingException.class)
    public void stopsReadingWhenNewMessagesArrivingDoesNotRealignWithTime() throws Exception {
        // CBlocks to get the processor started
        processor.process(cBlockWithHeartRateAfter(2000));
        processor.process(cBlockWithHeartRateAfter(5));
        processor.process(cBlockWithHeartRateAfter(2000));
        processor.process(cBlockWithHeartRateAfter(5));
        processor.process(cBlockWithHeartRateAfter(2000));
        processor.process(cBlockWithHeartRateAfter(5));

        // Not delivered until messages start arriving quickly again
        processor.process(cBlockWithHeartRateAfter(4000));
        processor.process(cBlockWithHeartRateAfter(3));

        // Messages to get message delivery started again, but they are too late
        processor.process(cBlockWithHeartRateAfter(2000));
        processor.process(cBlockWithHeartRateAfter(2));
        processor.process(cBlockWithHeartRateAfter(2000));
        processor.process(cBlockWithHeartRateAfter(2));
    }

    @Test
    public void restartsMessageSendingWhenMessagesStartAgainAndRealignWithTimeAfterLongPauses() throws Exception {
        // CBlocks to get the processor started
        processor.process(cBlockWithHeartRateAfter(0));
        processor.process(cBlockWithHeartRateAfter(5));
        processor.process(cBlockWithHeartRateAfter(2000));
        processor.process(cBlockWithHeartRateAfter(5));
        processor.process(cBlockWithHeartRateAfter(2000));
        processor.process(cBlockWithHeartRateAfter(5));

        // Not delivered until messages start arriving quickly again
        processor.process(cBlockWithHeartRateAfter(4000));

        // Messages to get message delivery started again - delivered in shorter
        // intervals, because we need to catch up
        processor.process(cBlockWithHeartRateAfter(2));
        processor.process(cBlockWithHeartRateAfter(2));
        processor.process(cBlockWithHeartRateAfter(2));
        processor.process(cBlockWithHeartRateAfter(2000));
        processor.process(cBlockWithHeartRateAfter(2));
        processor.process(cBlockWithHeartRateAfter(2000));
        processor.process(cBlockWithHeartRateAfter(2));
        processor.process(cBlockWithHeartRateAfter(2000));
        processor.process(cBlockWithHeartRateAfter(2));

        verify(callback, times(16)).addSamples(mhrCaptor.capture(), fhrCaptor.capture(), qfhrCaptor.capture(),
                tocoCaptor.capture());
    }

    @Test(expected = MonicaSamplesMissingException.class)
    public void stopsReadingMessagesIfAnUnevenNumberOfMessagesAppear() throws Exception {
        // CBlocks to get the processor started
        processor.process(cBlockWithHeartRateAfter(0));
        processor.process(cBlockWithHeartRateAfter(5));
        processor.process(cBlockWithHeartRateAfter(2000));
        processor.process(cBlockWithHeartRateAfter(5));
        processor.process(cBlockWithHeartRateAfter(2000));
        processor.process(cBlockWithHeartRateAfter(5));

        // Not delivered until messages start arriving quickly again
        processor.process(cBlockWithHeartRateAfter(2600));
        // Message dropped out here. When we catch up, we have an uneven number
        // of messages.

        // Messages to get message delivery started again - delivered in shorter
        // intervals, because we need to catch up
        processor.process(cBlockWithHeartRateAfter(2600));
        processor.process(cBlockWithHeartRateAfter(2));
        processor.process(cBlockWithHeartRateAfter(1501));
        processor.process(cBlockWithHeartRateAfter(2));
        processor.process(cBlockWithHeartRateAfter(2001));
        processor.process(cBlockWithHeartRateAfter(2));
    }

    private CBlockMessage initialCBlockAfter(long time) {
        now += time;
        return new CBlockMessage(new Date(now), CBlockMessageTest.initialCBlock);
    }

    private CBlockMessage cBlockWithHeartRateAfter(long time) {
        now += time;
        return new CBlockMessage(new Date(now), CBlockMessageTest.cBlock);
    }
}
