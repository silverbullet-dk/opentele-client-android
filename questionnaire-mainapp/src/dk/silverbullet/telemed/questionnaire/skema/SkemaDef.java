package dk.silverbullet.telemed.questionnaire.skema;

import dk.silverbullet.telemed.questionnaire.Questionnaire;
import dk.silverbullet.telemed.questionnaire.expression.VariableLinkFailedException;
import dk.silverbullet.telemed.questionnaire.node.UnknownNodeException;

public interface SkemaDef {

    Skema getSkema(Questionnaire questionnaire) throws UnknownNodeException, VariableLinkFailedException;
}
