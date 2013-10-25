package dk.silverbullet.telemed.deleteme;

import android.util.Log;
import dk.silverbullet.telemed.questionnaire.Questionnaire;
import dk.silverbullet.telemed.questionnaire.QuestionnaireFragment;
import dk.silverbullet.telemed.questionnaire.expression.Constant;
import dk.silverbullet.telemed.questionnaire.expression.MultiplyExpression;
import dk.silverbullet.telemed.questionnaire.expression.Variable;
import dk.silverbullet.telemed.questionnaire.node.AssignmentNode;
import dk.silverbullet.telemed.questionnaire.node.EndNode;
import dk.silverbullet.telemed.questionnaire.node.UnknownNodeException;
import dk.silverbullet.telemed.questionnaire.node.UrineDeviceNode;
import dk.silverbullet.telemed.questionnaire.output.OutputSkema;
import dk.silverbullet.telemed.questionnaire.skema.Skema;
import dk.silverbullet.telemed.utils.Util;

public class TestUrine implements TestSkema {

    private static final String TAG = Util.getTag(TestUrine.class);

    public Skema getInternSkema(Questionnaire questionnaire) throws UnknownNodeException {

        OutputSkema outputSkema = new OutputSkema();
        Variable<Integer> urine = new Variable<Integer>("urine", 0);
        Variable<String> startTime = new Variable<String>("1.startTime", "");
        Variable<String> endTime = new Variable<String>("2.endTime", "");
        Variable<Integer> urine2 = new Variable<Integer>("urine2", 0);

        outputSkema.addVariable(urine);
        outputSkema.addVariable(urine2);
        outputSkema.addVariable(startTime);
        outputSkema.addVariable(endTime);

        EndNode end = new EndNode(questionnaire, "End");

        AssignmentNode<Integer> assignment = new AssignmentNode<Integer>(questionnaire, "asgn", urine2,
                new MultiplyExpression<Integer>(urine, new Constant<Integer>(1)));
        assignment.setNext(end.getNodeName());

        UrineDeviceNode udn = new UrineDeviceNode(questionnaire, "TDN");
        udn.setUrine(urine);

        udn.setNext(assignment.getNodeName());
        udn.setNextFail(end.getNodeName());
        udn.setStartTime(startTime);
        udn.setEndTime(endTime);

        Skema skema = new Skema();
        skema.setCron(null);
        skema.setEndNode(end.getNodeName());
        skema.setName("Mini");
        skema.setStartNode(udn.getNodeName());
        skema.setVersion("0.1");

        for (Variable<?> output : outputSkema.getOutput()) {
            questionnaire.addSkemaVariable(output);
            skema.addVariable(output);
        }

        skema.addNode(end);
        skema.addNode(assignment);
        skema.addNode(udn);

        skema.link();

        return skema;
    }

    @Override
    public Skema getSkema() {
        Skema result = null;
        Questionnaire q = new Questionnaire(new QuestionnaireFragment());
        try {
            String json = Util.getGson().toJson(getInternSkema(q));
            result = Util.getGson().fromJson(json, Skema.class);
        } catch (UnknownNodeException e) {
            Log.e(TAG, "Got exception", e);
        }
        return result;
    }
}
