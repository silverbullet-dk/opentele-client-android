package dk.silverbullet.telemed.questionnaire.expression;

public class AndExpression extends BinaryOperation<Boolean, Boolean> {

    private static final long serialVersionUID = 112524504134463625L;

    public AndExpression(Expression<Boolean> left, Expression<Boolean> right) {
        super(left, right);
    }

    @Override
    public Boolean evaluate() {
        return left.evaluate() && right.evaluate();
    }

    @Override
    public String toString() {
        return "(" + left.toString() + " AND " + right.toString() + ")";
    }
}
