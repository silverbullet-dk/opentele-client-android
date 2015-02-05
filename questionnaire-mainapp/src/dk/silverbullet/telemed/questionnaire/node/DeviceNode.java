package dk.silverbullet.telemed.questionnaire.node;

import com.google.gson.annotations.Expose;
import dk.silverbullet.telemed.questionnaire.Questionnaire;
import dk.silverbullet.telemed.questionnaire.expression.Variable;
import dk.silverbullet.telemed.questionnaire.expression.VariableLinkFailedException;

import java.util.Date;
import java.util.Map;

import static dk.silverbullet.telemed.utils.Json.ISO8601_DATE_TIME_FORMAT;
import static dk.silverbullet.telemed.utils.Util.linkVariable;

public abstract class DeviceNode extends IONode {

    @Expose
    private Variable<String> deviceId;

    @Expose
    private String next;

    private Node nextNode;

    @Expose
    private String nextFail;

    private Node nextFailNode;

    @Expose
    private Variable<String> startTime;

    @Expose
    private Variable<String> endTime;

    @Expose
    private String helpText;

    @Expose
    private String helpImage;

    public DeviceNode(Questionnaire questionnaire, String nodeName) {
        super(questionnaire, nodeName);
    }

    @Override
    public void enter() {
        super.enter();
        setStartTimeValue(new Date());
    }

    @Override
    public void linkNodes(Map<String, Node> map) throws UnknownNodeException {
        super.linkNodes(map);

        if (!map.containsKey(next))
            throw new UnknownNodeException(next);
        if (!map.containsKey(nextFail))
            throw new UnknownNodeException(nextFail);

        nextNode = map.get(next);
        nextFailNode = map.get(nextFail);
    }

    public void setDeviceIdString(String id) {
        if (deviceId != null && id != null)
            deviceId.setValue(id);
    }

    @Override
    public void linkVariables(Map<String, Variable<?>> variablePool) throws VariableLinkFailedException {
        super.linkVariables(variablePool);
        deviceId = linkVariable(variablePool, deviceId, true);
        endTime = linkVariable(variablePool, endTime, true);
        startTime = linkVariable(variablePool, startTime, true);
    }

    @Override
    public final void leave() {
        super.leave();
        deviceLeave();
        if (endTime != null && endTime.evaluate() == null) // if can be set but not set...
            setEndTimeValue(new Date());
    }

    abstract public void deviceLeave();

    public void setStartTimeValue(Date dateTime) {
        if (dateTime != null && startTime != null)
            startTime.setValue(ISO8601_DATE_TIME_FORMAT.format(dateTime));
    }

    public void setEndTimeValue(Date dateTime) {
        if (null != dateTime && null != endTime)
            endTime.setValue(ISO8601_DATE_TIME_FORMAT.format(dateTime));
    }

    public Node getNextFailNode() {
        return nextFailNode;
    }

    public Node getNextNode() {
        return nextNode;
    }

    public Variable<String> getDeviceId() {
        return deviceId;
    }

    public void setNextNode(Node nextNode) {
        this.nextNode = nextNode;
    }

    public void setDeviceId(Variable<String> deviceId) {
        this.deviceId = deviceId;
    }

    public void setNext(String next) {
        this.next = next;
    }

    public void setNextFail(String nextFail) {
        this.nextFail = nextFail;
    }

    public void setStartTime(Variable<String> startTime) {
        this.startTime = startTime;
    }

    public void setEndTime(Variable<String> endTime) {
        this.endTime = endTime;
    }

    public String getHelpText() {
        return helpText;
    }

    public String getHelpImage() {
        return helpImage;
    }

    public void setText(String text) {
        this.helpText = text;
    }

    public void setHelpImage(String helpImage) {
        this.helpImage = helpImage;
    }

    public boolean hasHelp() {
        return ((helpText != null && !helpText.isEmpty()) || (helpImage != null && helpImage.isEmpty()));
    }

}
