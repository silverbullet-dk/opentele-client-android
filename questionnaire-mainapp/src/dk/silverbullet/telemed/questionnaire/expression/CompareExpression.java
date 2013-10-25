package dk.silverbullet.telemed.questionnaire.expression;

import lombok.ToString;

@ToString
public abstract class CompareExpression<T extends Comparable<T>> extends BinaryOperation<T, Boolean> {
    private static final long serialVersionUID = -2106422002220741013L;

    public CompareExpression(Expression<T> left, Expression<T> right) {
        super(left, right);
    }

    public final Boolean evaluate() {
        T leftObj = left.evaluate();
        T rightObj = right.evaluate();
        if (leftObj instanceof Number && rightObj instanceof Number) {
            Number leftValue = (Number) leftObj;
            Number rightValue = (Number) rightObj;

            if (leftValue.getClass() == Double.class || rightValue.getClass() == Double.class) {
                return compare(leftValue.doubleValue(), rightValue.doubleValue());
            } else if (leftValue.getClass() == Float.class || rightValue.getClass() == Float.class) {
                return compare(leftValue.floatValue(), rightValue.floatValue());
            } else if (leftValue.getClass() == Long.class || rightValue.getClass() == Long.class) {
                return compare(leftValue.longValue(), rightValue.longValue());
            } else { // Int, short, whatever just becomes int...
                return compare(leftValue.intValue(), rightValue.intValue());
            }
        } else
            return compare(leftObj, rightObj);
    }

    public abstract Boolean compare(double left, double right);

    public abstract Boolean compare(long left, long right);

    public abstract Boolean compare(float left, float right);

    public abstract Boolean compare(int left, int right);

    public abstract Boolean compare(T left, T right);
}
