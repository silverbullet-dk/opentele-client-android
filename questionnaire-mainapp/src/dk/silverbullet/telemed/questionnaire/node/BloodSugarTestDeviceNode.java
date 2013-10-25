package dk.silverbullet.telemed.questionnaire.node;

import dk.silverbullet.telemed.device.test.accuchek.BloodSugarTestDeviceController;
import dk.silverbullet.telemed.questionnaire.Questionnaire;

public class BloodSugarTestDeviceNode extends AbstractBloodSugarDeviceNode {
    private BloodSugarTestDeviceController controller;

    public BloodSugarTestDeviceNode(Questionnaire questionnaire, String nodeName) {
        super(questionnaire, nodeName);
    }

    @Override
    public void enter() {
        super.enter();

        controller = new BloodSugarTestDeviceController(this);
    }

    @Override
    public void deviceLeave() {
        controller.close();
    }
}
