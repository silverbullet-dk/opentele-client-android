package dk.silverbullet.telemed.questionnaire.expression;

public class LessThanOrEqual<T extends Comparable<T>> extends CompareExpression<T> {

    private static final long serialVersionUID = 4696692087903935L;

    public LessThanOrEqual(Expression<T> left, Expression<T> right) {
        super(left, right);
    }

    @Override
    public String toString() {
        return "(" + getLeft().toString() + " <= " + getRight().toString() + ")";
    }

    @Override
    public Boolean compare(double left, double right) {
        return left <= right;
    }

    @Override
    public Boolean compare(long left, long right) {
        return left <= right;
    }

    @Override
    public Boolean compare(float left, float right) {
        return left <= right;
    }

    @Override
    public Boolean compare(int left, int right) {
        return left <= right;
    }

    @Override
    public Boolean compare(T left, T right) {
        return left.compareTo(right) <= 0;
    }
}
