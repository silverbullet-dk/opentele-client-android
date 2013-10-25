package dk.silverbullet.telemed.questionnaire.node;

import static dk.silverbullet.telemed.utils.Util.ISO8601_DATE_TIME_FORMAT;
import static dk.silverbullet.telemed.utils.Util.linkVariable;

import java.util.Date;
import java.util.Map;

import lombok.Data;
import lombok.EqualsAndHashCode;

import com.google.gson.annotations.Expose;

import dk.silverbullet.telemed.questionnaire.Questionnaire;
import dk.silverbullet.telemed.questionnaire.expression.Variable;
import dk.silverbullet.telemed.questionnaire.expression.VariableLinkFailedException;

@Data
@EqualsAndHashCode(callSuper = false)
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
}
