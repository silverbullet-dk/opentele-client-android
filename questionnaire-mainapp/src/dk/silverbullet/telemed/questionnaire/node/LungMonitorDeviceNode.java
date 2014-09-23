package dk.silverbullet.telemed.questionnaire.node;

import com.google.gson.annotations.Expose;
import dk.silverbullet.telemed.OpenTeleApplication;
import dk.silverbullet.telemed.device.DeviceInitialisationException;
import dk.silverbullet.telemed.device.vitalographlungmonitor.LungMeasurement;
import dk.silverbullet.telemed.device.vitalographlungmonitor.LungMonitorController;
import dk.silverbullet.telemed.device.vitalographlungmonitor.LungMonitorListener;
import dk.silverbullet.telemed.device.vitalographlungmonitor.VitalographLungMonitorController;
import dk.silverbullet.telemed.questionnaire.Questionnaire;
import dk.silverbullet.telemed.questionnaire.R;
import dk.silverbullet.telemed.questionnaire.element.TextViewElement;
import dk.silverbullet.telemed.questionnaire.element.TwoButtonElement;
import dk.silverbullet.telemed.questionnaire.expression.Variable;
import dk.silverbullet.telemed.questionnaire.expression.VariableLinkFailedException;
import dk.silverbullet.telemed.utils.Util;

import java.util.Map;

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
        setStatusText(Util.getString(R.string.lung_monitor_connect, questionnaire));
        addElement(statusText);

        fev1DisplayText = new TextViewElement(this);
        addElement(fev1DisplayText);

        fev6DisplayText = new TextViewElement(this);
        addElement(fev6DisplayText);

        be = new TwoButtonElement(this);
        be.setLeftNextNode(getNextFailNode());
        be.setLeftText(Util.getString(R.string.default_omit, questionnaire));
        be.hideRightButton();
        addElement(be);

        super.enter();

        try {
            controller = createController();
        } catch (DeviceInitialisationException e) {
            OpenTeleApplication.instance().logException(e);
            setStatusText(Util.getString(R.string.lung_monitor_could_not_connect, questionnaire));
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
        setStatusText(Util.getString(R.string.lung_monitor_perform_test, questionnaire));
    }

    @Override
    public void permanentProblem() {
        setStatusText(Util.getString(R.string.lung_monitor_permanent_problem, questionnaire));
    }

    @Override
    public void temporaryProblem() {
        setStatusText(Util.getString(R.string.lung_monitor_temporary_problem, questionnaire));
    }

    @Override
    public void measurementReceived(final String systemId, final LungMeasurement measurement) {
        questionnaire.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (measurement.isGoodTest()) {
                    statusText.setText(Util.getString(R.string.lung_monitor_measurement_received, questionnaire));
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
                    be.setRightText(Util.getString(R.string.default_proceed, questionnaire));
                } else {
                    statusText.setText(Util.getString(R.string.lung_monitor_bad_measurement, questionnaire));
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


    public void setFev1(Variable<Float> fev1) {
        this.fev1 = fev1;
    }

    public void setFev6(Variable<Float> fev6) {
        this.fev6 = fev6;
    }

    public void setFev1Fev6Ratio(Variable<Float> fev1Fev6Ratio) {
        this.fev1Fev6Ratio = fev1Fev6Ratio;
    }

    public void setFef2575(Variable<Float> fef2575) {
        this.fef2575 = fef2575;
    }

    public void setGoodTest(Variable<Boolean> goodTest) {
        this.goodTest = goodTest;
    }

    public void setSoftwareVersion(Variable<Integer> softwareVersion) {
        this.softwareVersion = softwareVersion;
    }


}
