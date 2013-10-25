package dk.silverbullet.telemed.questionnaire.expression;

public abstract class NumericalBinaryOperation<T extends Number> extends BinaryOperation<T, T> {

    private static final long serialVersionUID = 2895227025149848346L;

    public NumericalBinaryOperation(Expression<T> left, Expression<T> right) {
        super(left, right);
    }

    @SuppressWarnings("unchecked")
    @Override
    public T evaluate() {
        Number leftValue = left.evaluate();
        Number rightValue = right.evaluate();

        if (leftValue.getClass() == Double.class || rightValue.getClass() == Double.class) {
            return (T) evaluate(leftValue.doubleValue(), rightValue.doubleValue());
        } else if (leftValue.getClass() == Float.class || rightValue.getClass() == Float.class) {
            return (T) evaluate(leftValue.floatValue(), rightValue.floatValue());
        } else if (leftValue.getClass() == Long.class || rightValue.getClass() == Long.class) {
            return (T) evaluate(leftValue.longValue(), rightValue.longValue());
        } else { // Int, short, whatever just becomes int...
            return (T) evaluate(leftValue.intValue(), rightValue.intValue());
        }
    }

    abstract Number evaluate(double left, double right);

    abstract Number evaluate(long left, long right);

    abstract Number evaluate(float left, float right);

    abstract Number evaluate(int left, int right);

}
