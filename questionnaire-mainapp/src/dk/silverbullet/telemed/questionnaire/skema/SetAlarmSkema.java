package dk.silverbullet.telemed.questionnaire.skema;

import android.util.Log;
import dk.silverbullet.telemed.questionnaire.Questionnaire;
import dk.silverbullet.telemed.questionnaire.expression.Variable;
import dk.silverbullet.telemed.questionnaire.node.EndNode;
import dk.silverbullet.telemed.questionnaire.node.SetAlarmTestNode;
import dk.silverbullet.telemed.questionnaire.output.OutputSkema;
import dk.silverbullet.telemed.utils.Util;

public class SetAlarmSkema implements SkemaDef {

    private static final String TAG = Util.getTag(SetAlarmSkema.class);

    @SuppressWarnings("unchecked")
    @Override
    public Skema getSkema(Questionnaire questionnaire) {

        Log.d(TAG, "SetAlarmSkema.getSkema.....");

        // Variable
        Variable<?> alarm = questionnaire.getValuePool().get(Util.VARIABLE_SERVER_IP);

        if (null == alarm)
            alarm = new Variable<String>(Util.VARIABLE_ALARM_TEST, String.class);

        OutputSkema outputSkema = new OutputSkema();
        outputSkema.addVariable(alarm);

        for (Variable<?> output : outputSkema.getOutput()) {
            questionnaire.addVariable(output);
            // skema.addVariable(output);
        }

        // ////////////////////////////////////////////////////////////////////////////////

        EndNode end = new EndNode(questionnaire, "End");

        SetAlarmTestNode setAlarmTestNode = new SetAlarmTestNode(questionnaire, "SetAlarmTestNode");
        setAlarmTestNode.setNextNode(end);
        setAlarmTestNode.setServerIP((Variable<String>) alarm);

        Skema skema = new Skema();
        skema.setEndNode(end.getNodeName());
        skema.setName("ALARM_TEST");
        skema.setStartNode(setAlarmTestNode.getNodeName());
        skema.setVersion("1.0");

        skema.addNode(end);
        skema.addNode(setAlarmTestNode);

        return skema;
    }
}
