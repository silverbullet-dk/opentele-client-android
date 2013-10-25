package dk.silverbullet.telemed.questionnaire.node;

import dk.silverbullet.telemed.device.accuchek.AccuChekContoller;
import dk.silverbullet.telemed.device.usb.android.AndroidUSBMAssStorageController;
import dk.silverbullet.telemed.questionnaire.Questionnaire;

public class BloodSugarDeviceNode extends AbstractBloodSugarDeviceNode {
    private AccuChekContoller accuChekController;

    public BloodSugarDeviceNode(Questionnaire questionnaire, String nodeName) {
        super(questionnaire, nodeName);
    }

    @Override
    public void enter() {
        super.enter();

        if (accuChekController == null) {
            accuChekController = new AccuChekContoller(this, new AndroidUSBMAssStorageController(
                    questionnaire.getActivity()));
        }
    }

    @Override
    public void deviceLeave() {
        if (accuChekController != null) {
            accuChekController.close();
            accuChekController = null;
        }
    }
}
