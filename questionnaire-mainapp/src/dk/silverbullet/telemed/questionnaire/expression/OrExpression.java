package dk.silverbullet.telemed.questionnaire.expression;

public class OrExpression extends BinaryOperation<Boolean, Boolean> {

    private static final long serialVersionUID = -3815113989720028001L;

    public OrExpression(Expression<Boolean> left, Expression<Boolean> right) {
        super(left, right);
    }

    @Override
    public Boolean evaluate() {
        return left.evaluate() || right.evaluate();
    }

    @Override
    public String toString() {
        return "(" + left.toString() + " OR " + right.toString() + ")";
    }
}
