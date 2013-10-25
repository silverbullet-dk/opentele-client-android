package dk.silverbullet.telemed.questionnaire.expression;

import java.util.Map;

import com.google.gson.annotations.Expose;

public abstract class BinaryOperation<In, Out> implements Expression<Out> {
    /**
     * 
     */
    private static final long serialVersionUID = 9137953755797839390L;
    @Expose
    protected Expression<In> left;
    @Expose
    protected Expression<In> right;

    public BinaryOperation(Expression<In> left2, Expression<In> right2) {
        this.left = left2;
        this.right = right2;
    }

    public Expression<In> getLeft() {
        return left;
    }

    public Expression<In> getRight() {
        return right;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void link(Map<String, Variable<?>> variablePool) throws VariableLinkFailedException {
        if (left instanceof Variable) {
            String name = ((Variable<In>) left).getName();
            if (variablePool.containsKey(name))
                left = (Expression<In>) variablePool.get(name);
            else
                throw new UnknownVariableException(name);
        } else
            left.link(variablePool);

        if (right instanceof Variable) {
            String name = ((Variable<In>) right).getName();
            if (variablePool.containsKey(name))
                right = (Expression<In>) variablePool.get(name);
            else
                throw new UnknownVariableException(name);
        } else
            right.link(variablePool);
    }
}
