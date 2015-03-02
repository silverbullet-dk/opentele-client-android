package dk.silverbullet.telemed.device.monica;

import android.util.Log;
import dk.silverbullet.telemed.device.monica.packet.CBlockMessage;
import dk.silverbullet.telemed.device.monica.packet.FetalHeightAndSignalToNoise;
import dk.silverbullet.telemed.device.monica.packet.MmMessage;
import dk.silverbullet.telemed.device.monica.packet.MonicaMessage;
import dk.silverbullet.telemed.questionnaire.node.monica.MonicaDeviceCallback;
import dk.silverbullet.telemed.utils.Util;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

public class MessageProcessor {

    private static final int MINIMUM_ONTIME_COUNT = 2;
    private static final String TAG = Util.getTag(MessageProcessor.class);
    private final MonicaDeviceCallback monicaCallback;
    private int ontimeMessageCount = 0;
    private Date lastOntimeMessage;
    private final List<MonicaMessage> msgBuffer = new LinkedList<MonicaMessage>();
    private int count;
    private boolean mhrReceived = false;
    private Date startTime;
    private long lastMessageTime;
    private long lastTimeDiff = 0;
    private boolean shortPauseExpected = false;
    private boolean longPauseObserved;

    public MessageProcessor(MonicaDeviceCallback monicaCallback) {
        this.monicaCallback = monicaCallback;
    }

    public void process(CBlockMessage msg) throws MonicaSamplesMissingException {
        if (!mhrReceived) {
            mhrReceived = isMhrPresent(msg);
            if (!mhrReceived)
                return; // Ignore until MHR values received!
        }

        count++;
        msgBuffer.add(msg);

        long timeSpan = msg.getReadTime().getTime() - lastMessageTime;

        if (timeSpan > 1500 && timeSpan < 2500 && !shortPauseExpected) {
            Log.d(TAG, "Long message timing: " + timeSpan);
            shortPauseExpected = true;
            longPauseObserved = true;
        } else if (timeSpan < 100 && shortPauseExpected && longPauseObserved) {
            Log.d(TAG, "Short message timing: " + timeSpan);
            longPauseObserved = false;
            shortPauseExpected = false;
            ontimeMessageCount++;

            Log.d(TAG, "On time messages: " + ontimeMessageCount);

            if (ontimeMessageCount >= MINIMUM_ONTIME_COUNT) {
                processBuffer();
                lastOntimeMessage = msg.getReadTime();
            }
        } else {
            Log.d(TAG, "Message timing: " + timeSpan + ", expected " + (shortPauseExpected ? "10" : "2000") + " ms");
            longPauseObserved = false;
            ontimeMessageCount = 0;
            shortPauseExpected = timeSpan > 1000;
        }

        monicaCallback.updateProgress(count, monicaCallback.getSampleTimeMinutes() * 60);
        lastMessageTime = msg.getReadTime().getTime();
    }

    private boolean isMhrPresent(CBlockMessage msg) {
        for (float mhr : msg.getMHR()) {
            if (mhr != 0F)
                return true;
        }
        return false;
    }

    private void processBuffer() throws MonicaSamplesMissingException {
        Date currentTime = null;
        CBlockMessage firstCBlock = null;
        int cbCount = 0;
        for (MonicaMessage m : msgBuffer) {
            if (m instanceof CBlockMessage) {
                cbCount++;
                if (firstCBlock == null) {
                    firstCBlock = (CBlockMessage) m;
                }
                currentTime = m.getReadTime();
            }
        }

        if (startTime == null) { // No messages added yet!
            startTime = firstCBlock.getReadTime();
            lastOntimeMessage = startTime;
            monicaCallback.setStartTimeValue(startTime);
        } else if (count % 2 != 0) {
            throw new MonicaSamplesMissingException("Message lost! count=" + count);
        } else {
            long timeDiff = startTime.getTime() - currentTime.getTime() + 1000 * (count - 2);
            long delta = timeDiff - lastTimeDiff;
            Log.d(TAG, "timeDiff: " + timeDiff + " delta: " + delta + " count: " + count + " (buffered:" + cbCount
                    + ")");
            if (delta < -2000 || delta > 2000) {
                throw new MonicaSamplesMissingException("timeDiff: " + timeDiff + " delta: " + delta + " count: "
                        + count + " (buffered: " + cbCount + "/" + msgBuffer.size() + ")");
            }
            lastTimeDiff = timeDiff;
        }

        flushMessageBuffer();
    }

    public void flushMessageBuffer() {

        Log.d(TAG, "Flushing " + msgBuffer.size() + " messages");

        long estimatedTime = lastOntimeMessage == null ? 0 : lastOntimeMessage.getTime();
        for (MonicaMessage m : msgBuffer) {
            if (m instanceof CBlockMessage) {
                estimatedTime += 1000;
                CBlockMessage cb = (CBlockMessage) m;
                monicaCallback.addSamples(cb.getMHR(), cb.getFHR1(), cb.getQFHR1(), cb.getTOCO(), cb.getReadTime());
            } else if (m instanceof MmMessage) {
                monicaCallback.addSignal(new Date(estimatedTime + 500));
            } else if (m instanceof FetalHeightAndSignalToNoise) {
                FetalHeightAndSignalToNoise msg = (FetalHeightAndSignalToNoise) m;
                monicaCallback.addFetalHeight(msg.getFetalHeight());
                monicaCallback.addSignalToNoise(msg.getFetalHeight());
            } else {
                Log.w(TAG, "Unknown message type: " + m);
            }
        }

        msgBuffer.clear();
    }

    public void process(MmMessage msg) {
        if (mhrReceived) {
            msgBuffer.add(msg);
        }
    }

    public void process(FetalHeightAndSignalToNoise msg) {
        if (mhrReceived) {
            msgBuffer.add(msg);
        }
    }
}
