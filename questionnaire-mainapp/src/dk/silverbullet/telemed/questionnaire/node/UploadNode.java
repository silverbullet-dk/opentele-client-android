package dk.silverbullet.telemed.questionnaire.node;

import android.util.Log;
import dk.silverbullet.telemed.questionnaire.Questionnaire;
import dk.silverbullet.telemed.questionnaire.R;
import dk.silverbullet.telemed.questionnaire.element.ButtonElement;
import dk.silverbullet.telemed.questionnaire.element.TextViewElement;
import dk.silverbullet.telemed.questionnaire.element.TwoButtonElement;
import dk.silverbullet.telemed.questionnaire.expression.Variable;
import dk.silverbullet.telemed.questionnaire.output.OutputSkema;
import dk.silverbullet.telemed.rest.Resources;
import dk.silverbullet.telemed.rest.listener.PostEntityListener;
import dk.silverbullet.telemed.utils.Util;

import java.util.Date;

public class UploadNode extends IONode implements PostEntityListener {
    private static final String TAG = Util.getTag(UploadNode.class);

    private Node nextNode;
    private String titleText;
    private String statusText;

    public UploadNode(Questionnaire questionnaire, String nodeName) {
        super(questionnaire, nodeName);
        titleText = Util.getString(R.string.upload_measurements_uploading, questionnaire);
        statusText = Util.getString(R.string.default_please_wait, questionnaire);
    }

    @Override
    public void enter() {
        Log.d(TAG, "UploadNode...");



        OutputSkema outputSkema = getOutputSkema();
        if (outputSkema != null) {
            Resources.postSkema(outputSkema, questionnaire, this);
        }

        getQuestionnaire().cleanSkemaValuePool();
        setupViewWithStatusText();

        super.enter();
    }

    public OutputSkema getOutputSkema() {
        OutputSkema result = questionnaire.getOutputSkema();
        if (null == result) {
            return null;
        }

        result.setDate(new Date());

        // Fjerner dem som er null. For serverens skyld
        for (Variable<?> vv : getQuestionnaire().getSkemaValuePool().values()) {
            if (null != vv.getExpressionValue().getValue())
                result.addVariable(vv);
        }

        return result;
    }

    @Override
    public void postError() {
        titleText = Util.getString(R.string.upload_measurements_error, questionnaire);
        statusText = Util.getString(R.string.upload_measurements_upload_failed, questionnaire);
        setupViewWithRetryCancelButtons();

        createView();
    }

    @Override
    public void posted() {
        titleText = Util.getString(R.string.upload_measurements_sent, questionnaire);
        statusText = Util.getString(R.string.upload_measurements_measurements_received, questionnaire);
        setupViewWithOkButton();

        createView();
    }

    private void setupViewWithStatusText() {
        clearElements();

        addElement(new TextViewElement(this, titleText));
        addElement(new TextViewElement(this, statusText));
    }

    private void setupViewWithOkButton() {
        setupViewWithStatusText();

        ButtonElement be = new ButtonElement(this, Util.getString(R.string.default_ok, questionnaire));
        be.setNextNode(nextNode);
        addElement(be);
    }

    private void setupViewWithRetryCancelButtons() {
        setupViewWithStatusText();

        TwoButtonElement twoButtonElement = new TwoButtonElement(this, Util.getString(R.string.default_cancel, questionnaire), Util.getString(R.string.default_retry, questionnaire));
        twoButtonElement.setLeftNextNode(nextNode);
        twoButtonElement.setRightNextNode(this);
        addElement(twoButtonElement);
    }

    public void setNextNode(Node nextNode) {
        this.nextNode = nextNode;
    }
}
