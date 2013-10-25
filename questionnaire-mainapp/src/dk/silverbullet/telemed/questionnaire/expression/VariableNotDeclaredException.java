package dk.silverbullet.telemed.questionnaire.expression;

public class VariableNotDeclaredException extends VariableLinkFailedException {

    private static final long serialVersionUID = -2727741850698161825L;

    public VariableNotDeclaredException() {
        super("(unknown)");
    }
}
