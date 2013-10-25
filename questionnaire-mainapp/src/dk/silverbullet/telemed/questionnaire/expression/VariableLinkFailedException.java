package dk.silverbullet.telemed.questionnaire.expression;

public class VariableLinkFailedException extends Exception {

    private static final long serialVersionUID = 326825683967200322L;

    protected final String variableName;

    public VariableLinkFailedException(String variableName) {
        this.variableName = variableName;
    }

    @Override
    public String toString() {
        return "VariableLinkFailedException(\"" + variableName + "\")";
    }
}
