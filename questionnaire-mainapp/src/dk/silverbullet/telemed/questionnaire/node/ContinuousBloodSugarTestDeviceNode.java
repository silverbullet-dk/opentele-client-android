package dk.silverbullet.telemed.questionnaire.node;

import com.google.gson.annotations.Expose;
import dk.silverbullet.telemed.bloodsugar.ContinuousBloodSugarMeasurement;
import dk.silverbullet.telemed.bloodsugar.ContinuousBloodSugarMeasurements;
import dk.silverbullet.telemed.questionnaire.Questionnaire;
import dk.silverbullet.telemed.questionnaire.R;
import dk.silverbullet.telemed.questionnaire.element.TextViewElement;
import dk.silverbullet.telemed.questionnaire.element.TwoButtonElement;
import dk.silverbullet.telemed.questionnaire.expression.Variable;
import dk.silverbullet.telemed.questionnaire.expression.VariableLinkFailedException;
import dk.silverbullet.telemed.utils.Util;

import java.util.Date;
import java.util.Map;

public class ContinuousBloodSugarTestDeviceNode extends DeviceNode {
    private TextViewElement infoElement;
    @Expose
    private Variable<ContinuousBloodSugarMeasurements> bloodSugarMeasurements;
    @Expose
    String text;

    public ContinuousBloodSugarTestDeviceNode(Questionnaire questionnaire, String nodeName) {
        super(questionnaire, nodeName);
    }

    @Override
    public void enter() {
        clearElements();

        addElement(new TextViewElement(this, text));

        infoElement = new TextViewElement(this);
        updateInfoElement("Simuleret CGM");
        addElement(infoElement);

        TwoButtonElement be = new TwoButtonElement(this);
        be.setLeftNextNode(getNextFailNode());
        be.setLeftText(Util.getString(R.string.default_omit, questionnaire));
        be.setRightNextNode(getNextNode());
        be.setRightText(Util.getString(R.string.default_next, questionnaire));
        addElement(be);

        super.enter();
    }

    @Override
    public void linkVariables(Map<String, Variable<?>> variablePool) throws VariableLinkFailedException {
        super.linkVariables(variablePool);
        bloodSugarMeasurements = Util.linkVariable(variablePool, bloodSugarMeasurements);
    }

    public void deviceLeave() {
        ContinuousBloodSugarMeasurements measurements = new ContinuousBloodSugarMeasurements();
        measurements.serialNumber = "123456";
        measurements.transferTime = new Date();
        for (int i=0; i<1000; i++) {
            ContinuousBloodSugarMeasurement measurement = new ContinuousBloodSugarMeasurement();
            measurement.recordId = i;
            measurement.timeOfMeasurement = new Date(System.currentTimeMillis() - i*(1000 * 60 * 5));
            measurement.value = (5 + 50*Math.sin(i / 100d)) + "";
            measurements.measurements.add(measurement);
        }

        bloodSugarMeasurements.setValue(measurements);
    }

    private void updateInfoElement(final String text) {
        questionnaire.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                infoElement.setText(text);
            }
        });
    }
}
