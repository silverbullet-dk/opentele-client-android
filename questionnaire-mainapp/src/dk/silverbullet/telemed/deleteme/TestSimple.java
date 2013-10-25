package dk.silverbullet.telemed.deleteme;

import android.util.Log;
import dk.silverbullet.telemed.questionnaire.Questionnaire;
import dk.silverbullet.telemed.questionnaire.QuestionnaireFragment;
import dk.silverbullet.telemed.questionnaire.expression.Variable;
import dk.silverbullet.telemed.questionnaire.node.EndNode;
import dk.silverbullet.telemed.questionnaire.node.UnknownNodeException;
import dk.silverbullet.telemed.questionnaire.node.WeightTestDeviceNode;
import dk.silverbullet.telemed.questionnaire.output.OutputSkema;
import dk.silverbullet.telemed.questionnaire.skema.Skema;
import dk.silverbullet.telemed.utils.Util;

public class TestSimple implements TestSkema {

    private static final String TAG = Util.getTag(TestSimple.class);

    public Skema getInternSkema(Questionnaire questionnaire) throws UnknownNodeException {

        OutputSkema outputSkema = new OutputSkema();
        Variable<Float> weight = new Variable<Float>("weight", Float.class);
        Variable<String> startTime = new Variable<String>("1.startTime", String.class);
        Variable<String> endTime = new Variable<String>("2.endTime", String.class);

        outputSkema.addVariable(weight);
        outputSkema.addVariable(startTime);
        outputSkema.addVariable(endTime);

        EndNode end = new EndNode(questionnaire, "End");

        // IONode ioNode = new IONode(questionnaire, "START");
        // ButtonElement be = new ButtonElement(questionnaire);
        // be.setNext(end.getNodeName());
        // be.setText("OK");
        // ioNode.addElement(be);

        WeightTestDeviceNode wdn = new WeightTestDeviceNode(questionnaire, "WDN");
        wdn.setNext(end.getNodeName());
        wdn.setNextFail(end.getNodeName());
        wdn.setWeight(weight);
        wdn.setStartTime(startTime);
        wdn.setEndTime(endTime);

        Skema skema = new Skema();
        skema.setCron(null);
        skema.setEndNode(end.getNodeName());
        skema.setName("Mini");
        skema.setStartNode(wdn.getNodeName());
        skema.setVersion("0.1");

        for (Variable<?> output : outputSkema.getOutput()) {
            questionnaire.addSkemaVariable(output);
            skema.addVariable(output);
        }

        skema.addNode(end);
        // skema.addNode(ioNode);
        skema.addNode(wdn);

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
