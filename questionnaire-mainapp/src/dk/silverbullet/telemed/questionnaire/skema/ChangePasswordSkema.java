package dk.silverbullet.telemed.questionnaire.skema;

import android.util.Log;
import dk.silverbullet.telemed.questionnaire.Questionnaire;
import dk.silverbullet.telemed.questionnaire.expression.Variable;
import dk.silverbullet.telemed.questionnaire.node.ChangePasswordNode;
import dk.silverbullet.telemed.questionnaire.node.ChangePasswordNode2;
import dk.silverbullet.telemed.questionnaire.node.EndNode;
import dk.silverbullet.telemed.questionnaire.node.UnknownNodeException;
import dk.silverbullet.telemed.questionnaire.output.OutputSkema;
import dk.silverbullet.telemed.utils.Util;

public class ChangePasswordSkema implements SkemaDef {
    private static final String TAG = Util.getTag(ChangePasswordSkema.class);

    @Override
    public Skema getSkema(Questionnaire questionnaire) {

        Variable<String> currentPassword = new Variable<String>("currentPassword", String.class);
        Variable<String> newPassword = new Variable<String>("newPassword", String.class);
        Variable<String> passwordRepeat = new Variable<String>("passwordRepeat", String.class);
        Variable<String> currentPasswordErrorText = new Variable<String>("currentPasswordErrorText", String.class);
        Variable<String> passwordErrorText = new Variable<String>("passwordErrorText", String.class);
        Variable<String> passwordRepeatErrorText = new Variable<String>("passwordRepeatErrorText", String.class);

        OutputSkema outputSkema = new OutputSkema();
        outputSkema.addVariable(currentPassword);
        outputSkema.addVariable(newPassword);
        outputSkema.addVariable(passwordRepeat);
        outputSkema.addVariable(currentPasswordErrorText);
        outputSkema.addVariable(passwordErrorText);
        outputSkema.addVariable(passwordRepeatErrorText);

        for (Variable<?> output : outputSkema.getOutput()) {
            questionnaire.addVariable(output);
            // skema.addVariable(output);
        }

        EndNode end = new EndNode(questionnaire, "End");

        ChangePasswordNode2 changePasswordNode2 = new ChangePasswordNode2(questionnaire, "CHANGE_PASSWORD_2");
        changePasswordNode2.setPasswordErrorText(passwordErrorText);
        changePasswordNode2.setCurrentPasswordErrorText(currentPasswordErrorText);
        changePasswordNode2.setPasswordRepeatErrorText(passwordRepeatErrorText);
        changePasswordNode2.setCurrentPassword(currentPassword);
        changePasswordNode2.setPassword(newPassword);
        changePasswordNode2.setPasswordRepeat(passwordRepeat);

        ChangePasswordNode changePasswordNode = new ChangePasswordNode(questionnaire, "CHANGE_PASSWORD");
        changePasswordNode.setPasswordErrorText(passwordErrorText);
        changePasswordNode.setCurrentPasswordErrorText(currentPasswordErrorText);
        changePasswordNode.setPasswordRepeatErrorText(passwordRepeatErrorText);
        changePasswordNode.setCurrentPassword(currentPassword);
        changePasswordNode.setPassword(newPassword);
        changePasswordNode.setPasswordRepeat(passwordRepeat);
        changePasswordNode.setNext(changePasswordNode2);

        changePasswordNode2.setNext(end);
        changePasswordNode2.setNextFail(changePasswordNode);

        Skema skema = new Skema();
        skema.setCron(null);
        skema.setEndNode(end.getNodeName());
        skema.setName("CHANGE_PASSWORD_SKEMA");
        skema.setStartNode(changePasswordNode.getNodeName());
        skema.setVersion("1.0");

        skema.addNode(end);
        skema.addNode(changePasswordNode2);
        skema.addNode(changePasswordNode);

        try {
            skema.link();
        } catch (UnknownNodeException e) {
            Log.e(TAG, "Got Exception", e);
        }

        return skema;
    }
}
