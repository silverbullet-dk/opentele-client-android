package dk.silverbullet.telemed.questionnaire.expression;

import java.io.Serializable;
import java.util.Map;

public interface Expression<T> extends Serializable {

    T evaluate();

    void link(Map<String, Variable<?>> variablePool) throws VariableLinkFailedException;

    String toString();
}
