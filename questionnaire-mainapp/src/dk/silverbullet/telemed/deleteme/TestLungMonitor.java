package dk.silverbullet.telemed.deleteme;

import android.util.Log;
import dk.silverbullet.telemed.questionnaire.Questionnaire;
import dk.silverbullet.telemed.questionnaire.QuestionnaireFragment;
import dk.silverbullet.telemed.questionnaire.expression.Variable;
import dk.silverbullet.telemed.questionnaire.node.EndNode;
import dk.silverbullet.telemed.questionnaire.node.LungMonitorDeviceNode;
import dk.silverbullet.telemed.questionnaire.node.UnknownNodeException;
import dk.silverbullet.telemed.questionnaire.output.OutputSkema;
import dk.silverbullet.telemed.questionnaire.skema.Skema;
import dk.silverbullet.telemed.utils.Json;
import dk.silverbullet.telemed.utils.Util;

public class TestLungMonitor implements TestSkema {
    private static final String TAG = Util.getTag(TestLungMonitor.class);

    public Skema getInternSkema(Questionnaire questionnaire) throws UnknownNodeException {
        OutputSkema outputSkema = new OutputSkema();
        Variable<Float> fev1 = new Variable<Float>("fev1", Float.class);
        Variable<Float> fev6 = new Variable<Float>("fev6", Float.class);
        Variable<Float> fev1Fev6Ratio = new Variable<Float>("fev1Fev6Ratio", Float.class);
        Variable<Float> fef2575 = new Variable<Float>("fef2575", Float.class);
        Variable<Boolean> goodTest = new Variable<Boolean>("goodTest", Boolean.class);
        Variable<Integer> softwareVersion = new Variable<Integer>("softwareVersion", Integer.class);

        outputSkema.addVariable(fev1);
        outputSkema.addVariable(fev6);
        outputSkema.addVariable(fev1Fev6Ratio);
        outputSkema.addVariable(fef2575);
        outputSkema.addVariable(goodTest);
        outputSkema.addVariable(softwareVersion);

        EndNode end = new EndNode(questionnaire, "End");

        LungMonitorDeviceNode lungMonitorDeviceNode = new LungMonitorDeviceNode(questionnaire, "LungMonitorDeviceNode");
        lungMonitorDeviceNode.setFev1(fev1);
        lungMonitorDeviceNode.setFev6(fev6);
        lungMonitorDeviceNode.setFev1Fev6Ratio(fev1Fev6Ratio);
        lungMonitorDeviceNode.setFef2575(fef2575);
        lungMonitorDeviceNode.setGoodTest(goodTest);
        lungMonitorDeviceNode.setSoftwareVersion(softwareVersion);
        lungMonitorDeviceNode.setNext(end.getNodeName());
        lungMonitorDeviceNode.setNextFail(end.getNodeName());

        Skema skema = new Skema();
        skema.setEndNode(end.getNodeName());
        skema.setName("Lungefunktion");
        skema.setStartNode(lungMonitorDeviceNode.getNodeName());
        skema.setVersion("0.1");

        for (Variable<?> output : outputSkema.getOutput()) {
            questionnaire.addSkemaVariable(output);
            skema.addVariable(output);
        }

        skema.addNode(end);
        skema.addNode(lungMonitorDeviceNode);

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
