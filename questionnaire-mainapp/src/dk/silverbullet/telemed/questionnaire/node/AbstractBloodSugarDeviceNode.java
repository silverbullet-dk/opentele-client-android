package dk.silverbullet.telemed.questionnaire.node;

import com.google.gson.annotations.Expose;
import dk.silverbullet.telemed.device.accuchek.BloodSugarDeviceListener;
import dk.silverbullet.telemed.device.accuchek.BloodSugarMeasurements;
import dk.silverbullet.telemed.questionnaire.Questionnaire;
import dk.silverbullet.telemed.questionnaire.R;
import dk.silverbullet.telemed.questionnaire.element.HelpTextElement;
import dk.silverbullet.telemed.questionnaire.element.TextViewElement;
import dk.silverbullet.telemed.questionnaire.element.TwoButtonElement;
import dk.silverbullet.telemed.questionnaire.expression.Variable;
import dk.silverbullet.telemed.questionnaire.expression.VariableLinkFailedException;
import dk.silverbullet.telemed.utils.Util;

import java.util.Map;

public abstract class AbstractBloodSugarDeviceNode extends DeviceNode implements BloodSugarDeviceListener {
    private TextViewElement infoElement;
    private TwoButtonElement be;
    private HelpTextElement helpTextElement;
    @Expose
    private Variable<BloodSugarMeasurements> bloodSugarMeasurements;
    @Expose
    String text;

    public AbstractBloodSugarDeviceNode(Questionnaire questionnaire, String nodeName) {
        super(questionnaire, nodeName);
    }

    @Override
    public void enter() {
        clearElements();
        addElement(new TextViewElement(this, text));

        infoElement = new TextViewElement(this,
                Util.getString(R.string.bloodsugar_connect_device, questionnaire));
        addElement(infoElement);

        if (hasHelp()) {
            helpTextElement = new HelpTextElement(this, getHelpText(), getHelpImage());
            addElement(helpTextElement);
        }

        be = new TwoButtonElement(this);
        be.setLeftNextNode(getNextFailNode());
        be.setLeftText(Util.getString(R.string.default_omit, questionnaire));
        be.setRightNextNode(this);
        be.setRightText(Util.getString(R.string.default_retry, questionnaire));
        be.hideRightButton();
        addElement(be);

        super.enter();
    }

    public void setBloodSugarMeasurements(Variable<BloodSugarMeasurements> bloodSugarMeasurements) {
        this.bloodSugarMeasurements = bloodSugarMeasurements;
    }

    @Override
    public abstract void deviceLeave();

    @Override
    public void linkVariables(Map<String, Variable<?>> variablePool) throws VariableLinkFailedException {
        super.linkVariables(variablePool);
        bloodSugarMeasurements = Util.linkVariable(variablePool, bloodSugarMeasurements);
    }

    @Override
    public void fetchingDiary() {
        updateInfoElement(Util.getString(R.string.bloodsugar_fetching_measurements, questionnaire));
    }

    @Override
    public void connected() {
        updateInfoElement(Util.getString(R.string.bloodsugar_connected, questionnaire));
    }

    @Override
    public void diaryNotFound() {
        updateInfoElement(Util.getString(R.string.bloodsugar_no_diary, questionnaire));
    }

    @Override
    public void tooManyDiariesFound() {
        updateInfoElement(Util.getString(R.string.bloodsugar_too_many_diaries, questionnaire));
    }

    @Override
    public void measurementsParsed(BloodSugarMeasurements measurements) {
        updateInfoElement(Util.getString(R.string.bloodsugar_measurements_fetched, questionnaire));
        getDeviceId().setValue(measurements.serialNumber);
        bloodSugarMeasurements.setValue(measurements);

        questionnaire.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                be.setRightText(Util.getString(R.string.default_next, questionnaire));
                be.showRightButton();
                be.setRightNextNode(getNextNode());

                if (helpTextElement != null)
                    helpTextElement.hideButton();
            }
        });
    }

    @Override
    public void parsingFailed() {
        updateInfoElement(Util.getString(R.string.bloodsugar_parse_failed, questionnaire));
    }

    private void updateInfoElement(final String content) {
        questionnaire.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                infoElement.setText(content);
            }
        });
    }
}
