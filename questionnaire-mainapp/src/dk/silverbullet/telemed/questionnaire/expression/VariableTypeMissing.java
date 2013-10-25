package dk.silverbullet.telemed.questionnaire.expression;

public class VariableTypeMissing extends VariableLinkFailedException {

    private static final long serialVersionUID = 6311436070679997789L;

    public VariableTypeMissing(String variableName) {
        super(variableName);
    }
}
