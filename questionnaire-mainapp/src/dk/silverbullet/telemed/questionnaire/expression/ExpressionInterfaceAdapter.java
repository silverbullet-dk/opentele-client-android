package dk.silverbullet.telemed.questionnaire.expression;

import java.lang.reflect.Array;
import java.lang.reflect.Type;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

public class ExpressionInterfaceAdapter<T extends Expression<?>> implements JsonSerializer<T>, JsonDeserializer<T> {

    public JsonElement serialize(T object, Type interfaceType, JsonSerializationContext context) {
        final JsonObject wrapper = new JsonObject();

        if (object instanceof BinaryOperation) {
            JsonObject operands = new JsonObject();
            operands.add("left", context.serialize(((BinaryOperation<?, ?>) object).getLeft(), Expression.class));
            operands.add("right", context.serialize(((BinaryOperation<?, ?>) object).getRight(), Expression.class));
            if (object instanceof NumericalBinaryOperation) { // taking two operands
                if (object instanceof AddExpression) {
                    wrapper.add("add", operands);
                } else if (object instanceof SubtractExpression) {
                    wrapper.add("sub", operands);
                } else if (object instanceof MultiplyExpression) {
                    wrapper.add("mul", operands);
                } else if (object instanceof DivideExpression) {
                    wrapper.add("div", operands);
                } else
                    throw new RuntimeException("Unknown object type: " + object.getClass().getName());
            } else if (object instanceof CompareExpression) { // taking two operands
                if (object instanceof LessThan) {
                    wrapper.add("lt", operands);
                } else if (object instanceof GreaterThan) {
                    wrapper.add("gt", operands);
                } else if (object instanceof LessThanOrEqual) {
                    wrapper.add("lte", operands);
                } else if (object instanceof GreaterThanOrEqual) {
                    wrapper.add("gte", operands);
                } else if (object instanceof Equals) {
                    wrapper.add("eq", operands);
                } else
                    throw new RuntimeException("Unknown object type: " + object.getClass().getName());
            }
        } else if (object instanceof Variable) {
            wrapper.addProperty("type", "name");
            wrapper.addProperty("value", ((Variable<?>) object).getName());
        } else if (object instanceof Constant) {
            Constant<?> value = (Constant<?>) object;
            wrapper.addProperty("type", value.getType());
            wrapper.add("value", context.serialize(((Constant<?>) object).getValue()));
        } else
            throw new RuntimeException("Unknown object type: " + object.getClass().getName());

        return wrapper;
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public T deserialize(JsonElement elem, Type interfaceType, JsonDeserializationContext context)
            throws JsonParseException {
        final JsonObject wrapper = (JsonObject) elem;
        if (wrapper.has("add")) {
            JsonElement operands = wrapper.get("add");
            return context.deserialize(operands, AddExpression.class);
        } else if (wrapper.has("sub")) {
            JsonElement operands = wrapper.get("sub");
            return context.deserialize(operands, SubtractExpression.class);
        } else if (wrapper.has("mul")) {
            JsonElement operands = wrapper.get("mul");
            return context.deserialize(operands, MultiplyExpression.class);
        } else if (wrapper.has("div")) {
            JsonElement operands = wrapper.get("div");
            return context.deserialize(operands, DivideExpression.class);
        } else if (wrapper.has("lt")) {
            JsonElement operands = wrapper.get("lt");
            return context.deserialize(operands, LessThan.class);
        } else if (wrapper.has("lte")) {
            JsonElement operands = wrapper.get("lte");
            return context.deserialize(operands, LessThanOrEqual.class);
        } else if (wrapper.has("gt")) {
            JsonElement operands = wrapper.get("gt");
            return context.deserialize(operands, GreaterThan.class);
        } else if (wrapper.has("gte")) {
            JsonElement operands = wrapper.get("gte");
            return context.deserialize(operands, GreaterThanOrEqual.class);
        } else if (wrapper.has("eq")) {
            JsonElement operands = wrapper.get("eq");
            return context.deserialize(operands, Equals.class);
        } else if (wrapper.has("type")) {
            String typeName = wrapper.get("type").getAsString();
            if (typeName.equals("name")) {
                return (T) new Variable<Object>(wrapper.get("value").getAsString(), Object.class);
            } else {
                Class<?>[] types = new Class[] { Integer.class, Float.class, Double.class, String.class, Short.class,
                        Byte.class, Long.class, Boolean.class };
                boolean array = typeName.endsWith("[]");
                if (array)
                    typeName = typeName.substring(0, typeName.length() - 2);

                for (Class<?> cls : types) {
                    String name = cls.getName();
                    if (name.substring(name.lastIndexOf('.') + 1).startsWith(typeName)) {
                        if (array) {
                            return (T) new Constant(context.deserialize(wrapper.get("value"), Array.newInstance(cls, 0)
                                    .getClass()));
                        } else {
                            return (T) new Constant(context.deserialize(wrapper.get("value"), cls));
                        }
                    }
                }
            }
        }
        throw new JsonParseException("Unrecognized element: " + elem);
    }
}