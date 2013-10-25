package dk.silverbullet.telemed.deleteme;

import android.util.Log;
import dk.silverbullet.telemed.device.accuchek.BloodSugarMeasurements;
import dk.silverbullet.telemed.questionnaire.Questionnaire;
import dk.silverbullet.telemed.questionnaire.QuestionnaireFragment;
import dk.silverbullet.telemed.questionnaire.expression.Variable;
import dk.silverbullet.telemed.questionnaire.node.BloodSugarDeviceNode;
import dk.silverbullet.telemed.questionnaire.node.EndNode;
import dk.silverbullet.telemed.questionnaire.node.UnknownNodeException;
import dk.silverbullet.telemed.questionnaire.output.OutputSkema;
import dk.silverbullet.telemed.questionnaire.skema.Skema;
import dk.silverbullet.telemed.utils.Util;

public class TestBloodSugar implements TestSkema {
    private static final String TAG = Util.getTag(TestBloodSugar.class);

    public Skema getInternSkema(Questionnaire questionnaire) throws UnknownNodeException {
        OutputSkema outputSkema = new OutputSkema();
        Variable<BloodSugarMeasurements> bloodsugarMeasurements = new Variable<BloodSugarMeasurements>(
                "bloodSugarMeasurements", new BloodSugarMeasurements());

        outputSkema.addVariable(bloodsugarMeasurements);

        EndNode end = new EndNode(questionnaire, "End");

        BloodSugarDeviceNode bloodSugarDeviceNode = new BloodSugarDeviceNode(questionnaire, "TDN");

        bloodSugarDeviceNode.setNext(end.getNodeName());
        bloodSugarDeviceNode.setNextFail(end.getNodeName());
        bloodSugarDeviceNode.setBloodSugarMeasurements(bloodsugarMeasurements);

        Skema skema = new Skema();
        skema.setEndNode(end.getNodeName());
        skema.setName("Blodsukker");
        skema.setStartNode(bloodSugarDeviceNode.getNodeName());
        skema.setVersion("0.1");

        for (Variable<?> output : outputSkema.getOutput()) {
            questionnaire.addSkemaVariable(output);
            skema.addVariable(output);
        }

        skema.addNode(end);
        skema.addNode(bloodSugarDeviceNode);
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
