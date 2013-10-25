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

import dk.silverbullet.telemed.device.accuchek.BloodSugarMeasurement;
import dk.silverbullet.telemed.device.accuchek.BloodSugarMeasurements;

public class VariableAdapter implements JsonSerializer<Variable<?>>, JsonDeserializer<Variable<?>> {

    @SuppressWarnings("unused")
    private static final String TAG = "VariableAdapter";

    public JsonElement serialize(Variable<?> src, Type interfaceType, JsonSerializationContext context) {

        final JsonObject retValue = new JsonObject();

        retValue.add("name", context.serialize(src.getName()));
        retValue.addProperty("type", src.getExpressionValue().getType());
        if (src.getExpressionValue() != null)
            retValue.add("value", context.serialize(src.getExpressionValue().getValue()));

        return retValue;
    }

    /**
     * @see com.google.gson.JsonDeserializer#deserialize(com.google.gson.JsonElement, java.lang.reflect.Type,
     *      com.google.gson.JsonDeserializationContext)
     */
    public Variable<?> deserialize(JsonElement elem, Type interfaceType, JsonDeserializationContext context)
            throws JsonParseException {

        final JsonObject wrapper = (JsonObject) elem;

        String variableName = wrapper.get("name").getAsString();
        String typeName = wrapper.get("type").getAsString();

        boolean array;
        if (typeName.endsWith("[]")) {
            typeName = typeName.substring(0, typeName.length() - 2);
            array = true;
        } else {
            array = false;
        }

        try {
            Class<?> type = null;

            if (typeName.equals("BloodSugarMeasurements")) {
                type = BloodSugarMeasurements.class;
            } else if (typeName.equals("BloodSugarMeasurement")) {
                type = BloodSugarMeasurement.class;
            } else {
                type = Class.forName("java.lang." + typeName);
            }
            if (array) {
                type = Array.newInstance(type, 0).getClass();
            }
            if (wrapper.has("value")) {
                Object obj = context.deserialize(wrapper.get("value"), type);
                return newVariable(variableName, obj, type);
            } else {
                return newVariable(variableName, type);
            }
        } catch (ClassNotFoundException e) {
            throw new JsonParseException("Cannot load classname " + typeName);
        }
    }

    @SuppressWarnings("unchecked")
    private <T> Variable<T> newVariable(String variableName, Object initialValue, T type) {
        return new Variable<T>(variableName, (T) initialValue);
    }

    private <T> Variable<T> newVariable(String variableName, Class<T> type) {
        Variable<T> var = new Variable<T>(variableName, type);
        return var;
    }
}