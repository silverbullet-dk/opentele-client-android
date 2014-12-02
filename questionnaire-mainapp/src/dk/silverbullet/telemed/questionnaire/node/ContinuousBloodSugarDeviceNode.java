package dk.silverbullet.telemed.questionnaire.node;

import com.google.gson.annotations.Expose;
import dk.silverbullet.telemed.bloodsugar.ContinuousBloodSugarEvents;
import dk.silverbullet.telemed.cgm.CGMDriverException;
import dk.silverbullet.telemed.cgm.ContinuousBloodSugarDeviceDriver;
import dk.silverbullet.telemed.cgm.ContinuousBloodSugarDeviceListener;
import dk.silverbullet.telemed.questionnaire.Questionnaire;
import dk.silverbullet.telemed.questionnaire.R;
import dk.silverbullet.telemed.questionnaire.element.TextViewElement;
import dk.silverbullet.telemed.questionnaire.element.TwoButtonElement;
import dk.silverbullet.telemed.questionnaire.expression.Variable;
import dk.silverbullet.telemed.questionnaire.expression.VariableLinkFailedException;
import dk.silverbullet.telemed.rest.Resources;
import dk.silverbullet.telemed.rest.listener.RetrieveEntityListener;
import dk.silverbullet.telemed.utils.ReflectionHelper;
import dk.silverbullet.telemed.utils.ReflectionHelperException;
import dk.silverbullet.telemed.utils.Util;

import java.util.Map;

public class ContinuousBloodSugarDeviceNode  extends DeviceNode implements ContinuousBloodSugarDeviceListener, RetrieveEntityListener<Long[]> {

    private TextViewElement infoElement;
    private TwoButtonElement be;
    @Expose
    private Variable<ContinuousBloodSugarEvents> events;
    @Expose
    String text;

    private static final String TAG = Util.getTag(ContinuousBloodSugarDeviceNode.class);
    private Long lastRecordNumber;

    public ContinuousBloodSugarDeviceNode(Questionnaire questionnaire, String nodeName) {
        super(questionnaire, nodeName);
    }

    @Override
    public void enter() {
        clearElements();

        Resources.getLastContinuousBloodSugarLogNumber(questionnaire, this);


        addElement(new TextViewElement(this, text));

        infoElement = new TextViewElement(this, Util.getString(R.string.default_please_wait, questionnaire));
        addElement(infoElement);

        be = new TwoButtonElement(this);
        be.setLeftNextNode(getNextFailNode());
        be.setLeftText(Util.getString(R.string.default_omit, questionnaire));
        be.setRightNextNode(this);
        be.setRightText(Util.getString(R.string.default_retry, questionnaire));
        be.hideRightButton();
        addElement(be);

        super.enter();
    }

    @Override
    public void linkVariables(Map<String, Variable<?>> variablePool) throws VariableLinkFailedException {
        super.linkVariables(variablePool);
        events = Util.linkVariable(variablePool, events);
    }

    @Override
    public void deviceLeave() {}

    @Override
    public void connected() {
        updateInfoElement(Util.getString(R.string.cgm_connected, questionnaire));
    }

    @Override
    public void measurementsParsed(ContinuousBloodSugarEvents bloodSugarMeasurements) {
        this.events.setValue(bloodSugarMeasurements);
        updateInfoElement(Util.getString(R.string.cgm_measurements_fetched, questionnaire));

        questionnaire.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                be.setRightText(Util.getString(R.string.default_next, questionnaire));
                be.showRightButton();
                be.setRightNextNode(getNextNode());
            }
        });
    }

    @Override
    public void userDeniedAccessToUSBDevice() {
        updateInfoElement(Util.getString(R.string.cgm_access_denied, questionnaire));

        enableRetry();
    }

    private void enableRetry() {
        questionnaire.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                be.setRightText(Util.getString(R.string.default_retry, questionnaire));
                be.showRightButton();
                be.setRightNextNode(ContinuousBloodSugarDeviceNode.this);
            }
        });
    }

    @Override
    public void couldNotConnectToDevice() {
        updateInfoElement(Util.getString(R.string.cgm_could_not_connect_to_device, questionnaire));
    }

    private void loadContinuousBloodSugarDriver() {
        if(ReflectionHelper.classCanBeLoaded(questionnaire.getContext(), "dk.silverbullet.opentele.abbott.AbbotDriver")) {
            try {
                ContinuousBloodSugarDeviceDriver driver = (ContinuousBloodSugarDeviceDriver)ReflectionHelper.getInstance(questionnaire.getContext(), "dk.silverbullet.opentele.abbott.AbbotDriver");
                driver.setContext(questionnaire.getContext());
                driver.setListener(this);
                driver.setLastRecordNumber(lastRecordNumber);

                driver.collectMeasurements();
            } catch (CGMDriverException e) {
                updateInfoElement(Util.getString(R.string.abbott_freestyle_library_not_available, questionnaire));
            } catch (ReflectionHelperException e) {
                updateInfoElement(Util.getString(R.string.abbott_freestyle_library_not_available, questionnaire));
            }
        } else {
            updateInfoElement(Util.getString(R.string.abbott_freestyle_library_not_available, questionnaire));
        }
    }

    private void updateInfoElement(final String content) {
        questionnaire.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                infoElement.setText(content);
            }
        });
    }

    @Override
    public void retrieveError() {
        updateInfoElement(Util.getString(R.string.client_server_version_connection_problem_body, questionnaire));
        enableRetry();
    }

    @Override
    public void retrieved(Long[] result) {
        if(result != null) {
            this.lastRecordNumber = result[0];
        }

        updateInfoElement(Util.getString(R.string.cgm_connect_device, questionnaire));
        loadContinuousBloodSugarDriver();
    }
}
