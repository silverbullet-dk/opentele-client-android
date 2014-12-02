package dk.silverbullet.telemed.questionnaire.node.monica.realtime;

import android.util.Log;
import dk.silverbullet.telemed.questionnaire.node.monica.realtime.communicators.Communicator;
import dk.silverbullet.telemed.utils.Util;
import org.w3c.dom.Document;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;

public class RealtimeCTGMeasurementsExportWorker implements Runnable {
    private static final String TAG = Util.getTag(RealtimeCTGMeasurementsExportWorker.class);
    static final int MAXIMUM_BATCH_SIZE = 10;

    private final MilouRealtimeDocumentBuilder documentBuilder;
    private final Communicator communicator;
    private BlockingQueue<RealTimeCTGMessage> measurementsQueue;
    private RealTimeCTGNode realTimeCTGNode;

    private boolean abort = false;
    private Thread thread;
    private boolean running;

    public RealtimeCTGMeasurementsExportWorker(Communicator communicator, BlockingQueue<RealTimeCTGMessage> measurementsQueue, PatientInfo patientInfo, UUID registrationIdentifier, RealTimeCTGNode realTimeCTGNode) {
        this.measurementsQueue = measurementsQueue;
        this.realTimeCTGNode = realTimeCTGNode;
        this.documentBuilder = new MilouRealtimeDocumentBuilder(realTimeCTGNode.getDeviceName(), registrationIdentifier, patientInfo);
        this.communicator = communicator;
    }

    public void start() {
        thread = new Thread(this);
        thread.start();
    }

    @Override
    public void run() {
        this.running = true;
        while(!abort) {
            processMessages();
        }

        this.running = false;
    }

    private void processMessages() {
        try {
            Log.d(TAG, "QUEUE length:" + measurementsQueue.size());

            List<RealTimeCTGMessage> messageBatch = takeBatch(); //Blocks until there are measurements

            //Start newDocument
            documentBuilder.startNewMessagesDocument();
            StopMessage parkedStopMessage = null;
            for(RealTimeCTGMessage message: messageBatch) {
                if(message instanceof StopMessage) {
                    parkedStopMessage = (StopMessage) message;
                } else {
                    documentBuilder.addMessageToDocument(message);
                }
            }


            Document messagesDocument = documentBuilder.finishDocument();
            boolean didSendToServer = communicator.sendMessagesDocument(messagesDocument);

            if(parkedStopMessage != null) {
                Document stopDocument = documentBuilder.buildStopDocument();
                communicator.sendStopMessage(stopDocument);
                stopWorking();
            }

            if(!didSendToServer) {
                stopWorking();
                realTimeCTGNode.connectionProblems();
            }

        } catch (InterruptedException e) {
            //Ignored
        }
    }

    private void stopWorking() {
        abort = true;
    }

    private List<RealTimeCTGMessage> takeBatch() throws InterruptedException {
        List<RealTimeCTGMessage> messageBatch = new ArrayList<RealTimeCTGMessage>(MAXIMUM_BATCH_SIZE);

        messageBatch.add(measurementsQueue.take()); //We know we have at least one message, since take is blocking
        measurementsQueue.drainTo(messageBatch, MAXIMUM_BATCH_SIZE - 1); //We have already added one element above.
        return messageBatch;
    }


    public boolean isRunning() {
        return running;
    }
}
