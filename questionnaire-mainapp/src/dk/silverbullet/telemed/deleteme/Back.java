package dk.silverbullet.telemed.deleteme;

import android.util.Log;
import dk.silverbullet.telemed.questionnaire.Questionnaire;
import dk.silverbullet.telemed.questionnaire.QuestionnaireFragment;
import dk.silverbullet.telemed.questionnaire.node.EndNode;
import dk.silverbullet.telemed.questionnaire.node.UnknownNodeException;
import dk.silverbullet.telemed.questionnaire.skema.Skema;
import dk.silverbullet.telemed.utils.Util;

public class Back implements TestSkema {

    private static final String TAG = Util.getTag(Back.class);

    public Skema getInternSkema(Questionnaire questionnaire) throws UnknownNodeException {

        EndNode end = new EndNode(questionnaire, "End");

        Skema skema = new Skema();
        skema.setCron(null);
        skema.setEndNode(end.getNodeName());
        skema.setName("Mini");
        skema.setStartNode(end.getNodeName());
        skema.setVersion("0.1");

        skema.addNode(end);

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
