package dk.silverbullet.telemed.questionnaire.skema;

import android.util.Log;
import dk.silverbullet.telemed.questionnaire.Questionnaire;
import dk.silverbullet.telemed.questionnaire.node.EndNode;
import dk.silverbullet.telemed.questionnaire.node.RealTimeCTGNode;
import dk.silverbullet.telemed.questionnaire.node.UnknownNodeException;
import dk.silverbullet.telemed.utils.Util;

public class RealTimeCTGSkema implements SkemaDef {
    private static final String TAG = Util.getTag(RealTimeCTGSkema.class);

    @Override
    public Skema getSkema(Questionnaire questionnaire) {

        EndNode end = new EndNode(questionnaire, "End");
        RealTimeCTGNode realTimeCTGNode = new RealTimeCTGNode(questionnaire, "realtimeCTGNode");

        Skema skema = new Skema();
        skema.setEndNode(end.getNodeName());
        skema.setName("REALTIME_CTG");
        skema.setStartNode(realTimeCTGNode.getNodeName());
        skema.setVersion("1.0");

        skema.addNode(end);
        skema.addNode(realTimeCTGNode);

        try {
            skema.link();
        } catch (UnknownNodeException e) {
            Log.e(TAG, "Got Exception", e);
        }

        return skema;
    }

}
