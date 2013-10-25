package dk.silverbullet.telemed.questionnaire.node;

import java.util.Map;

import com.google.gson.annotations.Expose;

import dk.silverbullet.telemed.device.accuchek.AccuChekListener;
import dk.silverbullet.telemed.device.accuchek.BloodSugarMeasurements;
import dk.silverbullet.telemed.questionnaire.Questionnaire;
import dk.silverbullet.telemed.questionnaire.element.TextViewElement;
import dk.silverbullet.telemed.questionnaire.element.TwoButtonElement;
import dk.silverbullet.telemed.questionnaire.expression.Variable;
import dk.silverbullet.telemed.questionnaire.expression.VariableLinkFailedException;
import dk.silverbullet.telemed.utils.Util;

public abstract class AbstractBloodSugarDeviceNode extends DeviceNode implements AccuChekListener {
    private TextViewElement infoElement;
    private TwoButtonElement be;
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
                "Tilslut din blodsukkermåler. Vær sikker på at blodsukkermåleren er slukket inden den tilsluttes.");
        addElement(infoElement);

        be = new TwoButtonElement(this);
        be.setLeftNextNode(getNextFailNode());
        be.setLeftText("Undlad");
        be.setRightNextNode(this);
        be.setRightText("Prøv igen");
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
        updateInfoElement("Henter blodsukkermålinger.");
    }

    @Override
    public void connected() {
        updateInfoElement("Blodsukkermåler fundet.");
    }

    @Override
    public void diaryNotFound() {
        updateInfoElement("Kunne ikke finde blodsukkermålinger.");
    }

    @Override
    public void tooManyDiariesFound() {
        updateInfoElement("For mange blodsukkermålinger.");
    }

    @Override
    public void measurementsParsed(BloodSugarMeasurements measurements) {
        updateInfoElement("Blodsukkermålinger hentet.");
        getDeviceId().setValue(measurements.serialNumber);
        bloodSugarMeasurements.setValue(measurements);

        questionnaire.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                be.setRightText("Næste");
                be.showRightButton();
                be.setRightNextNode(getNextNode());
            }
        });
    }

    @Override
    public void parsingFailed() {
        updateInfoElement("Kunne ikke læse blodsukkermålinger. Kontakt din kontaktperson.");
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
