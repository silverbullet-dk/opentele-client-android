package dk.silverbullet.telemed.questionnaire.expression;

public class AddExpression<T extends Number> extends NumericalBinaryOperation<T> implements Expression<T> {

    private static final long serialVersionUID = -3546851607829546539L;

    public AddExpression(Expression<T> left, Expression<T> right) {
        super(left, right);
    }

    @SuppressWarnings("unchecked")
    public AddExpression(Variable<Integer> left, int right) {
        super((Expression<T>) left, (Expression<T>) new Constant<Integer>(right));
    }

    @SuppressWarnings("unchecked")
    public AddExpression(int left, Variable<Integer> right) {
        super((Expression<T>) new Constant<Integer>(left), (Expression<T>) right);
    }

    @Override
    public T evaluate() {
        return super.evaluate();
    };

    @Override
    Number evaluate(double left, double right) {
        return left + right;
    }

    @Override
    Number evaluate(long left, long right) {
        return left + right;
    }

    @Override
    Number evaluate(float left, float right) {
        return left + right;
    }

    @Override
    Number evaluate(int left, int right) {
        return left + right;
    }

    @Override
    public String toString() {
        return "(" + left.toString() + " + " + right.toString() + ")";
    }
}
