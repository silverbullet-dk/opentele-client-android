package dk.silverbullet.telemed.deleteme;

import android.util.Log;
import dk.silverbullet.telemed.questionnaire.Questionnaire;
import dk.silverbullet.telemed.questionnaire.QuestionnaireFragment;
import dk.silverbullet.telemed.questionnaire.expression.Variable;
import dk.silverbullet.telemed.questionnaire.node.EndNode;
import dk.silverbullet.telemed.questionnaire.node.SaturationDeviceNode;
import dk.silverbullet.telemed.questionnaire.node.UnknownNodeException;
import dk.silverbullet.telemed.questionnaire.output.OutputSkema;
import dk.silverbullet.telemed.questionnaire.skema.Skema;
import dk.silverbullet.telemed.utils.Json;
import dk.silverbullet.telemed.utils.Util;

public class TestSaturation implements TestSkema {
    private static final String TAG = Util.getTag(TestSaturation.class);

    public Skema getInternSkema(Questionnaire questionnaire) throws UnknownNodeException {
        OutputSkema outputSkema = new OutputSkema();
        Variable<Integer> pulse = new Variable<Integer>("pulse", 0);
        Variable<Integer> saturation = new Variable<Integer>("saturation", 0);
        Variable<String> deviceId = new Variable<String>("deviceId", "");

        outputSkema.addVariable(pulse);
        outputSkema.addVariable(saturation);
        outputSkema.addVariable(deviceId);

        EndNode end = new EndNode(questionnaire, "End");

        SaturationDeviceNode saturationDeviceNode = new SaturationDeviceNode(questionnaire, "TDN");
        saturationDeviceNode.setPulse(pulse);
        saturationDeviceNode.setSaturation(saturation);
        saturationDeviceNode.setDeviceId(deviceId);

        saturationDeviceNode.setNext(end.getNodeName());
        saturationDeviceNode.setNextFail(end.getNodeName());

        Skema skema = new Skema();
        skema.setEndNode(end.getNodeName());
        skema.setName("Mini");
        skema.setStartNode(saturationDeviceNode.getNodeName());
        skema.setVersion("0.1");

        for (Variable<?> output : outputSkema.getOutput()) {
            questionnaire.addSkemaVariable(output);
            skema.addVariable(output);
        }

        skema.addNode(end);
        skema.addNode(saturationDeviceNode);
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
