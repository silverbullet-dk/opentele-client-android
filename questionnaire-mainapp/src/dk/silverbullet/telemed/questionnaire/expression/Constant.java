package dk.silverbullet.telemed.questionnaire.expression;

import java.util.Map;

public final class Constant<T> implements Expression<T> {

    private static final long serialVersionUID = -8392096681606142917L;
    final Class<T> type;
    final T value;

    public Constant(Class<T> type) {
        this.type = type;
        this.value = null;
    }

    @SuppressWarnings("unchecked")
    public Constant(Class<T> type, String value) {
        this.type = type;
        if (null == value)
            this.value = null; // This should probably not be used
        else if (type.equals(Integer.class)) {
            this.value = (T) Integer.valueOf(Integer.parseInt(value));
        } else if (type.equals(Long.class)) {
            this.value = (T) Long.valueOf(Long.parseLong(value));
        } else if (type.equals(Float.class)) {
            this.value = (T) Float.valueOf(Float.parseFloat(value));
        } else if (type.equals(Double.class)) {
            this.value = (T) Double.valueOf(Double.parseDouble(value));
        } else if (type.equals(String.class)) {
            this.value = (T) value;
        } else
            this.value = null; // Some other value type. Not sure if this is the best way to handle it.
    }

    @SuppressWarnings("unchecked")
    public Constant(T value) {
        this.type = value == null ? null : (Class<T>) value.getClass();
        this.value = value;
    }

    public T getValue() {
        return value;
    }

    @Override
    public T evaluate() {
        return getValue();
    }

    @Override
    public String toString() {
        return value == null ? "null" : value.toString();
    }

    public String getType() {
        String name = type.getName();
        name = name.substring(name.lastIndexOf('.') + 1);
        if (type.isArray())
            return name.substring(0, name.length() - 1) + "[]";
        return name;
    }

    @Override
    public void link(Map<String, Variable<?>> variablePool) {
        // Ignore!
    }
}
