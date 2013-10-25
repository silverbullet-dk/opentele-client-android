package dk.silverbullet.telemed.questionnaire.expression;

import java.util.Map;

import com.google.gson.annotations.Expose;

public class Variable<T> implements Expression<T> {

    private static final long serialVersionUID = 6755363189569863254L;

    @Expose
    private Constant<T> value;

    @Expose
    private final String name;

    private Class<T> type;

    @SuppressWarnings("unchecked")
    public Variable(String name, T initialValue) {
        this.name = name;
        this.value = new Constant<T>(initialValue);
        this.type = (Class<T>) initialValue.getClass();
    }

    public Class<T> getType() {
        return type;
    }

    public Variable(String name, Class<T> type) {
        this.name = name;
        this.value = new Constant<T>(type);
        this.type = type;
    }

    public void setValue(T value) {
        this.value = new Constant<T>(value);
    }

    public void setValue(Constant<T> value) {
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public Constant<T> getExpressionValue() {
        return value;
    }

    @Override
    public void link(Map<String, Variable<?>> variablePool) throws VariableLinkFailedException {
        throw new VariableLinkFailedException(getName());
    }

    @Override
    public T evaluate() {
        if (value == null)
            return null;
        return value.evaluate();
    }

    @Override
    public String toString() {
        return "${" + name + "=" + evaluate() + "}";
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }
}
