package dk.silverbullet.telemed.deleteme;

import dk.silverbullet.telemed.questionnaire.expression.VariableLinkFailedException;
import dk.silverbullet.telemed.questionnaire.node.UnknownNodeException;
import dk.silverbullet.telemed.questionnaire.skema.Skema;

public interface TestSkema {

    Skema getSkema() throws UnknownNodeException, VariableLinkFailedException;
}
