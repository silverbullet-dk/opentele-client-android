package dk.silverbullet.telemed.questionnaire.node;

import dk.silverbullet.telemed.device.test.lungmonitor.LungMonitorTestDeviceController;
import dk.silverbullet.telemed.device.vitalographlungmonitor.LungMonitorController;
import dk.silverbullet.telemed.questionnaire.Questionnaire;

public class LungMonitorTestDeviceNode extends LungMonitorDeviceNode {

    public LungMonitorTestDeviceNode(Questionnaire questionnaire, String nodeName) {
        super(questionnaire, nodeName);
    }

    @Override
    protected LungMonitorController createController() {
        return new LungMonitorTestDeviceController(this);
    }
}
