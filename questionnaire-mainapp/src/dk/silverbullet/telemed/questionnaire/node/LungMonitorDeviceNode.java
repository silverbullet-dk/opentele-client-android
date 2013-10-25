package dk.silverbullet.telemed.questionnaire.node;

import java.util.Map;

import lombok.Data;
import lombok.EqualsAndHashCode;

import com.google.gson.annotations.Expose;

import dk.silverbullet.telemed.device.DeviceInitialisationException;
import dk.silverbullet.telemed.device.vitalographlungmonitor.LungMeasurement;
import dk.silverbullet.telemed.device.vitalographlungmonitor.LungMonitorController;
import dk.silverbullet.telemed.device.vitalographlungmonitor.LungMonitorListener;
import dk.silverbullet.telemed.device.vitalographlungmonitor.VitalographLungMonitorController;
import dk.silverbullet.telemed.questionnaire.Questionnaire;
import dk.silverbullet.telemed.questionnaire.element.TextViewElement;
import dk.silverbullet.telemed.questionnaire.element.TwoButtonElement;
import dk.silverbullet.telemed.questionnaire.expression.Variable;
import dk.silverbullet.telemed.questionnaire.expression.VariableLinkFailedException;
import dk.silverbullet.telemed.utils.Util;

@Data
@EqualsAndHashCode(callSuper = false)
public class LungMonitorDeviceNode extends DeviceNode implements LungMonitorListener {
    @Expose
    private Variable<Float> fev1;
    @Expose
    private Variable<Float> fev6;
    @Expose
    private Variable<Float> fev1Fev6Ratio;
    @Expose
    private Variable<Float> fef2575;
    @Expose
    private Variable<Boolean> goodTest;
    @Expose
    private Variable<Integer> softwareVersion;
    @Expose
    String text;
    private TextViewElement statusText;
    private TextViewElement fev1DisplayText;
    private TextViewElement fev6DisplayText;
    private TwoButtonElement be;
    private LungMonitorController controller;

    public LungMonitorDeviceNode(Questionnaire questionnaire, String nodeName) {
        super(questionnaire, nodeName);
    }

    @Override
    public void enter() {
        clearElements();

        addElement(new TextViewElement(this, text));

        statusText = new TextViewElement(this);
        setStatusText("Tænd for apparatet og udfør lungefunktionstest.");
        addElement(statusText);

        fev1DisplayText = new TextViewElement(this);
        addElement(fev1DisplayText);

        fev6DisplayText = new TextViewElement(this);
        addElement(fev6DisplayText);

        be = new TwoButtonElement(this);
        be.setLeftNextNode(getNextFailNode());
        be.setLeftText("Undlad");
        be.hideRightButton();
        addElement(be);

        super.enter();

        try {
            controller = createController();
        } catch (DeviceInitialisationException e) {
            setStatusText("Kunne ikke forbinde til lungefunktionsmåler.");
        }
    }

    protected LungMonitorController createController() throws DeviceInitialisationException {
        return VitalographLungMonitorController.create(this);
    }

    @Override
    public void linkVariables(Map<String, Variable<?>> variablePool) throws VariableLinkFailedException {
        super.linkVariables(variablePool);
        fev1 = Util.linkVariable(variablePool, fev1);
        fev6 = Util.linkVariable(variablePool, fev6, true);
        fev1Fev6Ratio = Util.linkVariable(variablePool, fev1Fev6Ratio, true);
        fef2575 = Util.linkVariable(variablePool, fef2575, true);
        goodTest = Util.linkVariable(variablePool, goodTest);
        softwareVersion = Util.linkVariable(variablePool, softwareVersion, true);
    }

    @Override
    public void deviceLeave() {
        if (controller != null) {
            controller.close();
            controller = null;
        }
    }

    @Override
    public void connected() {
        setStatusText("Udfør lungefunktionstest.");
    }

    @Override
    public void permanentProblem() {
        setStatusText("Der kan ikke skabes forbindelse.");
    }

    @Override
    public void temporaryProblem() {
        setStatusText("Kunne ikke hente data. Prøv evt. igen.");
    }

    @Override
    public void measurementReceived(final String systemId, final LungMeasurement measurement) {
        questionnaire.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (measurement.isGoodTest()) {
                    statusText.setText("Måling modtaget.");
                    setDeviceIdString(systemId);
                    setVariableValue(fev1, measurement.getFev1());
                    setVariableValue(fev6, measurement.getFev6());
                    setVariableValue(fev1Fev6Ratio, measurement.getFev1Fev6Ratio());
                    setVariableValue(fef2575, measurement.getFef2575());
                    setVariableValue(goodTest, measurement.isGoodTest());
                    setVariableValue(softwareVersion, measurement.getSoftwareVersion());

                    controller.close();
                    controller = null;

                    be.setRightNextNode(getNextNode());
                    be.setRightText("Fortsæt");
                } else {
                    statusText.setText("Dårlig måling modtaget. Prøv igen.");
                }

                fev1DisplayText.setText(String.format("FEV1: %.2f", measurement.getFev1()));
            }
        });
    }

    private <T> void setVariableValue(Variable<T> variable, T value) {
        if (variable != null) {
            variable.setValue(value);
        }
    }

    private void setStatusText(final String text) {
        questionnaire.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                statusText.setText(text);
            }
        });
    }
}
