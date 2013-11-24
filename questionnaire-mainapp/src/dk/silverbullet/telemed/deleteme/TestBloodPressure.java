package dk.silverbullet.telemed.deleteme;

import android.util.Log;
import dk.silverbullet.telemed.questionnaire.Questionnaire;
import dk.silverbullet.telemed.questionnaire.QuestionnaireFragment;
import dk.silverbullet.telemed.questionnaire.expression.Variable;
import dk.silverbullet.telemed.questionnaire.node.BloodPressureDeviceNode;
import dk.silverbullet.telemed.questionnaire.node.EndNode;
import dk.silverbullet.telemed.questionnaire.node.UnknownNodeException;
import dk.silverbullet.telemed.questionnaire.output.OutputSkema;
import dk.silverbullet.telemed.questionnaire.skema.Skema;
import dk.silverbullet.telemed.utils.Json;
import dk.silverbullet.telemed.utils.Util;

public class TestBloodPressure implements TestSkema {

    private static final String TAG = Util.getTag(TestBloodPressure.class);

    public Skema getInternSkema(Questionnaire questionnaire) throws UnknownNodeException {
        OutputSkema outputSkema = new OutputSkema();
        Variable<Integer> systolic = new Variable<Integer>("systolic", Integer.class);
        Variable<Integer> diastolic = new Variable<Integer>("diastolic", Integer.class);
        Variable<Integer> meanArterialPressure = new Variable<Integer>("meanArterialPressure", Integer.class);
        Variable<Integer> pulse = new Variable<Integer>("pulse", Integer.class);

        outputSkema.addVariable(systolic);
        outputSkema.addVariable(diastolic);
        outputSkema.addVariable(meanArterialPressure);
        outputSkema.addVariable(pulse);

        EndNode end = new EndNode(questionnaire, "End");

        BloodPressureDeviceNode bpdn = new BloodPressureDeviceNode(questionnaire, "BPDN");
        bpdn.setDiastolic(diastolic);
        bpdn.setSystolic(systolic);
        bpdn.setMeanArterialPressure(meanArterialPressure);
        bpdn.setPulse(pulse);
        bpdn.setNext(end.getNodeName());
        bpdn.setNextFail(end.getNodeName());

        Skema skema = new Skema();
        skema.setEndNode(end.getNodeName());
        skema.setName("Lokal :: Blodtryk");
        skema.setStartNode(bpdn.getNodeName());
        skema.setVersion("0.1");

        for (Variable<?> output : outputSkema.getOutput()) {
            questionnaire.addSkemaVariable(output);
            skema.addVariable(output);
        }

        skema.addNode(end);
        skema.addNode(bpdn);

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
