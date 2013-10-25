package dk.silverbullet.telemed.questionnaire.expression;

import java.util.Map;

import com.google.gson.annotations.Expose;

public class Not implements Expression<Boolean> {

    private static final long serialVersionUID = 2092159816852915369L;

    @Expose
    private Expression<Boolean> expression;

    public Not(Expression<Boolean> expression) {
        this.expression = expression;
    }

    @Override
    public Boolean evaluate() {
        return !expression.evaluate();
    }

    @Override
    public String toString() {
        return "NOT (" + expression.toString() + ")";
    }

    @SuppressWarnings("unchecked")
    @Override
    public void link(Map<String, Variable<?>> variablePool) throws VariableLinkFailedException {
        if (expression instanceof Variable) {
            String name = ((Variable<Boolean>) expression).getName();
            if (variablePool.containsKey(name))
                expression = (Expression<Boolean>) variablePool.get(name);
            else
                throw new UnknownVariableException(name);
        } else
            expression.link(variablePool);
    }
}
