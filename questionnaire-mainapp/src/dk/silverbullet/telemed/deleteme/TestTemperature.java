package dk.silverbullet.telemed.deleteme;

import android.util.Log;
import dk.silverbullet.telemed.questionnaire.Questionnaire;
import dk.silverbullet.telemed.questionnaire.QuestionnaireFragment;
import dk.silverbullet.telemed.questionnaire.expression.Variable;
import dk.silverbullet.telemed.questionnaire.node.EndNode;
import dk.silverbullet.telemed.questionnaire.node.TemperatureDeviceNode;
import dk.silverbullet.telemed.questionnaire.node.UnknownNodeException;
import dk.silverbullet.telemed.questionnaire.output.OutputSkema;
import dk.silverbullet.telemed.questionnaire.skema.Skema;
import dk.silverbullet.telemed.utils.Json;
import dk.silverbullet.telemed.utils.Util;

public class TestTemperature implements TestSkema {

    private static final String TAG = Util.getTag(TestTemperature.class);

    public Skema getInternSkema(Questionnaire questionnaire) throws UnknownNodeException {

        OutputSkema outputSkema = new OutputSkema();
        Variable<Float> temperature = new Variable<Float>("temperature", Float.class);
        Variable<String> startTime = new Variable<String>("1.startTime", String.class);
        Variable<String> endTime = new Variable<String>("2.endTime", String.class);

        outputSkema.addVariable(temperature);
        outputSkema.addVariable(startTime);
        outputSkema.addVariable(endTime);

        EndNode end = new EndNode(questionnaire, "End");

        TemperatureDeviceNode tdn = new TemperatureDeviceNode(questionnaire, "TDN");
        tdn.setTemperature(temperature);
        tdn.setNext(end.getNodeName());
        tdn.setNextFail(end.getNodeName());
        tdn.setStartTime(startTime);
        tdn.setEndTime(endTime);

        Skema skema = new Skema();
        skema.setEndNode(end.getNodeName());
        skema.setName("Mini");
        skema.setStartNode(tdn.getNodeName());
        skema.setVersion("0.1");

        for (Variable<?> output : outputSkema.getOutput()) {
            questionnaire.addSkemaVariable(output);
            skema.addVariable(output);
        }

        skema.addNode(end);
        // skema.addNode(ioNode);
        skema.addNode(tdn);

        skema.link();

        return skema;
    }

    @Override
    public Skema getSkema() {
        Questionnaire q = new Questionnaire(new QuestionnaireFragment());
        try {
            String json = Json.print(getInternSkema(q));
            return Json.parse(json, Skema.class);
        } catch (UnknownNodeException e) {
            Log.e(TAG, "Got exception", e);
        }
        return null;
    }
}
