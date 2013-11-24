package dk.silverbullet.telemed.questionnaire.skema;

import dk.silverbullet.telemed.questionnaire.Questionnaire;
import dk.silverbullet.telemed.questionnaire.expression.Variable;
import dk.silverbullet.telemed.questionnaire.node.*;
import dk.silverbullet.telemed.questionnaire.output.OutputSkema;

public class AcknowledgementsSkema implements SkemaDef {

    @Override
    public Skema getSkema(Questionnaire questionnaire) {

        // ////////////////////////////////////////////////////////////////////////////////
        EndNode end = new EndNode(questionnaire, "END");

        // ////////////////////////////////////////////////////////////////////////////////
        AcknowledgementsMenuNode acknowledgementsMenuNode = new AcknowledgementsMenuNode(questionnaire, "KVITTERING-MENU");
        acknowledgementsMenuNode.setNextNode(end);

        // ////////////////////////////////////////////////////////////////////////////////
        Skema skema = new Skema();
        skema.setEndNode(end.getNodeName());
        skema.setName("RUN-KVIT-SKEMA");
        skema.setStartNode(acknowledgementsMenuNode.getNodeName());
        skema.setVersion("1.0");

        skema.addNode(end);
        skema.addNode(acknowledgementsMenuNode);

        return skema;
    }
}
