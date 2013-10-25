package dk.silverbullet.telemed.questionnaire.expression;

public class UnknownVariableException extends VariableLinkFailedException {

    private static final long serialVersionUID = -1436954467709900636L;

    public UnknownVariableException(String variableName) {
        super(variableName);
    }

    @Override
    public String toString() {
        return "UnknownVariable [variableName=\"" + variableName + "\"]";
    }
}
