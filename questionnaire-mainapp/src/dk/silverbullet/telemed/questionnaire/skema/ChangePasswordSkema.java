package dk.silverbullet.telemed.questionnaire.skema;

import android.util.Log;
import dk.silverbullet.telemed.questionnaire.Questionnaire;
import dk.silverbullet.telemed.questionnaire.node.ChangePasswordNode;
import dk.silverbullet.telemed.questionnaire.node.EndNode;
import dk.silverbullet.telemed.questionnaire.node.UnknownNodeException;
import dk.silverbullet.telemed.utils.Util;

public class ChangePasswordSkema implements SkemaDef {
    private static final String TAG = Util.getTag(ChangePasswordSkema.class);

    @Override
    public Skema getSkema(Questionnaire questionnaire) {
        EndNode end = new EndNode(questionnaire, "End");

        ChangePasswordNode changePasswordNode = new ChangePasswordNode(questionnaire, "CHANGE_PASSWORD");
        changePasswordNode.setHideBackButton(true);
        changePasswordNode.setNext(end);

        Skema skema = new Skema();
        skema.setEndNode(end.getNodeName());
        skema.setName("CHANGE_PASSWORD_SKEMA");
        skema.setStartNode(changePasswordNode.getNodeName());
        skema.setVersion("1.0");

        skema.addNode(end);
        skema.addNode(changePasswordNode);

        try {
            skema.link();
        } catch (UnknownNodeException e) {
            Log.e(TAG, "Got Exception", e);
        }

        return skema;
    }
}
