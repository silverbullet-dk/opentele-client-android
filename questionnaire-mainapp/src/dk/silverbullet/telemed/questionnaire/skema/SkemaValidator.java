package dk.silverbullet.telemed.questionnaire.skema;

import android.util.Log;
import dk.silverbullet.telemed.questionnaire.node.Node;
import dk.silverbullet.telemed.utils.Util;

public class SkemaValidator {

    private static final String TAG = Util.getTag(SkemaValidator.class);

    private Skema skema;

    public void validate(Skema skema) {
        this.skema = skema;

        // Startnode
        if (!contains(skema.getStartNodeNode())) {
            throw new RuntimeException("Startnode not found");
        }

        // Endnode
        if (!contains(skema.getEndNodeNode())) {
            throw new RuntimeException("Endnode not found");
        }

        for (Node node : skema.getNodes()) {
            Log.d(TAG, node.getNodeName());
        }
    }

    private boolean contains(Node node) {
        return node != null && skema.getNodes().contains(node);
    }
}
