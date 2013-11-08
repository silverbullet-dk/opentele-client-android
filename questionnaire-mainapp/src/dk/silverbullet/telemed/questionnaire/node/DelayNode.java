package dk.silverbullet.telemed.questionnaire.node;

import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import com.google.gson.annotations.Expose;
import dk.silverbullet.telemed.questionnaire.Questionnaire;
import dk.silverbullet.telemed.questionnaire.element.TextViewElement;

import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class DelayNode extends IONode {

    private int currentTick;
    private int numberOfticks;
    private String timeText;
    private Timer timer;

    private TextViewElement counterText;
    private TextViewElement displayText;

    @Expose
    private String next;
    @Expose
    private Node nextNode;

    @Expose
    private String displayTextString;
    @Expose
    private int countTime;
    @Expose
    private boolean countUp;

    public DelayNode(Questionnaire questionnaire, String nodeName) {
        super(questionnaire, nodeName);
    }

    @Override
    public void enter() {
        numberOfticks = countTime;
        if (countUp) {
            currentTick = 0;
            timeText = "Tid gået: ";
        } else {
            currentTick = numberOfticks;
            timeText = "Tid tilbage: ";
        }

        clearElements();
        displayText = new TextViewElement(this, "");
        addElement(displayText);

        // Create countdown/up display
        counterText = new TextViewElement(this);
        counterText.setText(displayTextString + "\n\n" + timeText + " " + currentTick + " ud af " + numberOfticks
                + " sekunder.");
        addElement(counterText);

        super.enter();

        timer = new Timer();

        final Handler handler = new Handler(new Callback() {
            @Override
            public boolean handleMessage(Message arg0) {
                counterText.setText(displayTextString + "\n\n" + timeText + " " + currentTick + " ud af "
                        + numberOfticks + " sekunder.");

                if (countUp) {
                    if (currentTick >= numberOfticks) {
                        done();
                    }
                    currentTick++;
                } else {
                    if (currentTick <= 0) {
                        done();
                    }
                    currentTick--;
                }

                return false;
            }
        });

        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                handler.sendEmptyMessage(0);
            }
        }, 0, 1000);

    }

    private void done() {
        // Stop the TimerTask and go to the next node
        timer.cancel();
        getQuestionnaire().setCurrentNode(nextNode);
    }

    @Override
    public void leave() {
        timer.cancel();
    }

    @Override
    public void linkNodes(Map<String, Node> map) {
        nextNode = map.get(next);
    }

    public void setNext(String next) {
        this.next = next;
    }
}
