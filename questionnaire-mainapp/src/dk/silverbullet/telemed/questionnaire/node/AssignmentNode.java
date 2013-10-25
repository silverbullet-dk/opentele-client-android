package dk.silverbullet.telemed.questionnaire.node;

import java.util.Map;

import lombok.Data;
import lombok.EqualsAndHashCode;

import com.google.gson.annotations.Expose;

import dk.silverbullet.telemed.questionnaire.Questionnaire;
import dk.silverbullet.telemed.questionnaire.expression.Expression;
import dk.silverbullet.telemed.questionnaire.expression.UnknownVariableException;
import dk.silverbullet.telemed.questionnaire.expression.Variable;
import dk.silverbullet.telemed.questionnaire.expression.VariableLinkFailedException;

@Data
@EqualsAndHashCode(callSuper = false)
public class AssignmentNode<T> extends Node {
    @Expose
    private String next;
    private Node nextNode;

    @Expose
    private Variable<T> variable;
    @Expose
    private Expression<T> expression;

    public AssignmentNode(Questionnaire questionnaire, String nodeName, Node next, Variable<T> Variable,
            Expression<T> expression) {

        this(questionnaire, nodeName, Variable, expression);
        setNextNode(next);
    }

    public AssignmentNode(Questionnaire questionnaire, String nodeName, Variable<T> Variable, Expression<T> expression) {
        super(questionnaire, nodeName);
        this.variable = Variable;
        this.expression = expression;
    }

    @Override
    public void enter() {
        variable.setValue(expression.evaluate());
        questionnaire.setCurrentNode(getNextNode());
    }

    @Override
    public void leave() {
    }

    @Override
    public void linkNodes(Map<String, Node> map) {
        nextNode = map.get(next);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void linkVariables(Map<String, Variable<?>> variablePool) throws VariableLinkFailedException {
        if (variablePool.containsKey(variable.getName()))
            variable = (Variable<T>) variablePool.get(variable.getName());
        else
            throw new UnknownVariableException(variable.getName());

        if (expression instanceof Variable) {
            String name = ((Variable<Boolean>) expression).getName();
            if (variablePool.containsKey(name))
                expression = (Expression<T>) variablePool.get(name);
            else
                throw new UnknownVariableException(name);
        } else
            expression.link(variablePool);
    }

    @Override
    public String toString() {
        return "AsignmentNode(\"" + getNodeName() + "\") -> " + getNextNode().getNodeName() + " " + getVariable() + "="
                + getExpression();
    }
}
