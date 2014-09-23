package dk.silverbullet.telemed.questionnaire.node.monica.realtime;

import android.app.Activity;
import dk.silverbullet.telemed.questionnaire.node.monica.realtime.communicators.MilouCommunicator;
import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.w3c.dom.Document;

import java.util.Calendar;
import java.util.UUID;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class RealtimeCTGMeasurementsExportWorkerTest {


    private RealtimeCTGMeasurementsExportWorker exportWorker;
    private BlockingQueue<RealTimeCTGMessage> queue;

    @Mock
    private Activity activity;
    @Mock
    private RealTimeCTGNode node;
    @Mock
    private MilouCommunicator communicator;

    @Before
    public void setupExportWorker() {
        PatientInfo patientInfo = new PatientInfo();

        patientInfo.firstName = "NancyAnn";
        patientInfo.lastName = "Berggren";
        patientInfo.id = 1l;

        queue = new ArrayBlockingQueue<RealTimeCTGMessage>(50000);
        exportWorker = new RealtimeCTGMeasurementsExportWorker(communicator, queue, patientInfo, UUID.randomUUID(), node);
    }

    @Test
    public void willEmptyQueue() {

        //Give the worker some messages to work on
        queue.add(new SignalMessage(Calendar.getInstance().getTime()));
        queue.add(new SignalMessage(Calendar.getInstance().getTime()));
        queue.add(new SignalMessage(Calendar.getInstance().getTime()));
        queue.add(new SignalMessage(Calendar.getInstance().getTime()));

        Assert.assertEquals(4, queue.size());

        //Start the worker
        exportWorker.start();

        //Wait for queue to become empty. This test will never fail just run continue forever.
        while(queue.size() < 0) {}

    }

    @Test
    public void willSendDocumentsToCommunicator() throws InterruptedException {

        //Add enough messages to require 4 batches to be processed
        for(int i = 0;  i < RealtimeCTGMeasurementsExportWorker.MAXIMUM_BATCH_SIZE * 4; i++) {
            queue.add(new SignalMessage(Calendar.getInstance().getTime()));
        }

        //Tell the communicator mock to report that messages were successfully sent
        when(communicator.sendMessagesDocument(any(Document.class))).thenReturn(true);

        //Start the worker
        exportWorker.start();

        //Wait for queue to empty
        while(queue.size() < 0) {}
        //Then wait a bit more to allow the worker to process the messages
        Thread.sleep(1000);

        //Verify that the messages were sent in four batches
        verify(communicator, times(4)).sendMessagesDocument(any(Document.class));
    }

    @Test
    public void willStopWhenReachingAStopMessage() throws InterruptedException {
        //Give the worker some messages to work on
        queue.add(new SignalMessage(Calendar.getInstance().getTime()));
        queue.add(new SignalMessage(Calendar.getInstance().getTime()));
        queue.add(new SignalMessage(Calendar.getInstance().getTime()));

        //Signal the worker to stop by placing stop message on queue.
        queue.add(new StopMessage());

        //Start the worker
        exportWorker.start();

        //Wait for stop message have been reached
        while(queue.size() < 1) {}

        //Then wait a bit more to allow the worker to process the messages
        Thread.sleep(1000);

        //Verify that the messages have been processed and sent
        verify(communicator, times(1)).sendMessagesDocument(any(Document.class));
        verify(communicator, times(1)).sendStopMessage(any(Document.class));  //Verify that worker has processed the stop message correctly

        //We add some more messages to verify that the worker as stopped
        queue.add(new SignalMessage(Calendar.getInstance().getTime()));
        queue.add(new SignalMessage(Calendar.getInstance().getTime()));
        queue.add(new SignalMessage(Calendar.getInstance().getTime()));
        queue.add(new SignalMessage(Calendar.getInstance().getTime()));

        //Give the worker thread ample time to process messages (which it should not)
        Thread.sleep(2000);

        //Last messages in queue should not have been sent
        verifyNoMoreInteractions(communicator);

        //Verify that the queue remains untouched
        Assert.assertEquals(4, queue.size());
    }

    @Test
    public void willTellRealTimeCTGNodeOfErrors() throws InterruptedException {

        //Give the worker some messages to work on
        queue.add(new SignalMessage(Calendar.getInstance().getTime()));
        queue.add(new SignalMessage(Calendar.getInstance().getTime()));
        queue.add(new SignalMessage(Calendar.getInstance().getTime()));

        //Setup communicator mock to report that sending the messages failed
        when(communicator.sendMessagesDocument(any(Document.class))).thenReturn(false);

        //Start the worker
        exportWorker.start();

        //Wait for queue to empty
        while(queue.size() > 0){}

        //Then wait a bit more to allow the worker to process the messages
        Thread.sleep(1000);

        //We expect the worker to stop working after encountering the first error
        verify(communicator, times(1)).sendMessagesDocument(any(Document.class));

        //Add some more messages to the queue to verify that the remain unprocessed
        queue.add(new SignalMessage(Calendar.getInstance().getTime()));
        queue.add(new SignalMessage(Calendar.getInstance().getTime()));
        queue.add(new SignalMessage(Calendar.getInstance().getTime()));

        //Give the worker thread ample time to process messages (which it should not)
        Thread.sleep(2000);

        //We expect the "abort" method to have been called once
        verify(node, times(1)).connectionProblems();

        //Verify that the queue remains untouched
        Assert.assertEquals(3, queue.size());

    }
}
