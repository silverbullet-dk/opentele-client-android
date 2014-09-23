package dk.silverbullet.telemed.questionnaire.node;

import dk.silverbullet.telemed.questionnaire.Questionnaire;
import dk.silverbullet.telemed.questionnaire.skema.Skema;
import dk.silverbullet.telemed.questionnaire.skema.SkemaDef;
import dk.silverbullet.telemed.utils.Util;

public class ChangeNoninDataModeSkema implements SkemaDef {

    private static final String TAG = Util.getTag(ChangeNoninDataModeSkema.class);

    public Skema getSkema(Questionnaire questionnaire) {

        EndNode end = new EndNode(questionnaire, "End");

        SetNoninDataModeNode setAlarmTestNode = new SetNoninDataModeNode(questionnaire, "SetNoninDataModeNode");

        Skema skema = new Skema();
        skema.setEndNode(end.getNodeName());
        skema.setName("SET_NONIN_DATAMODE");
        skema.setStartNode(setAlarmTestNode.getNodeName());
        skema.setVersion("1.0");

        skema.addNode(end);
        skema.addNode(setAlarmTestNode);

        return skema;
    }
}
