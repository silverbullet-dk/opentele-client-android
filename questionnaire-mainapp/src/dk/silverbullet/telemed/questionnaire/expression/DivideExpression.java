package dk.silverbullet.telemed.questionnaire.expression;

public class DivideExpression<T extends Number> extends NumericalBinaryOperation<T> {

    private static final long serialVersionUID = -5232899477975961861L;

    public DivideExpression(Expression<T> left, Expression<T> right) {
        super(left, right);
    }

    @Override
    public Number evaluate(double left, double right) {
        return left / right;
    }

    @Override
    Number evaluate(long left, long right) {
        return left / right;
    }

    @Override
    Number evaluate(float left, float right) {
        return left / right;
    }

    @Override
    Number evaluate(int left, int right) {
        return left / right;
    }

    @Override
    public String toString() {
        return left.toString() + " / " + right.toString();
    }
}
