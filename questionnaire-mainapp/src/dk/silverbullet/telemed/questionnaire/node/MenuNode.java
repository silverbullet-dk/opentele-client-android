package dk.silverbullet.telemed.questionnaire.node;

import dk.silverbullet.telemed.questionnaire.Questionnaire;
import dk.silverbullet.telemed.questionnaire.expression.Variable;
import dk.silverbullet.telemed.questionnaire.expression.VariableLinkFailedException;
import dk.silverbullet.telemed.questionnaire.skema.Skema;
import dk.silverbullet.telemed.questionnaire.skema.SkemaDef;

public class MenuNode extends IONode {
    public MenuNode(Questionnaire questionnaire, String nodeName) {
        super(questionnaire, nodeName);
    }

    protected void setupAndRunSkema(SkemaDef skemaDef) {
        Skema skema = null;
        try {
            skema = skemaDef.getSkema(questionnaire);
            setupSkema(skema);
            runSkema(skema);
        } catch (UnknownNodeException e) {
            throw new IllegalArgumentException("Could not start schema: " + skema.getName(), e);
        } catch (VariableLinkFailedException e) {
            throw new IllegalArgumentException("Could not start schema: " + skema.getName(), e);
        }
    }

    private void runSkema(Skema skema) {
        questionnaire.setCurrentNode(skema.getStartNodeNode());
    }

    private void setupSkema(Skema skema) throws UnknownNodeException, VariableLinkFailedException {
        skema.setQuestionnaire(questionnaire);
        skema.link();
        for (Variable<?> output : skema.getOutput())
            questionnaire.addSkemaVariable(output);

        for (Node node : skema.getNodes()) {
            node.linkVariables(questionnaire.getValuePool());
        }
        skema.getEndNodeNode().setNextNode(this);
        questionnaire.setStartNode(skema.getStartNodeNode());
    }
}
