package dk.silverbullet.telemed.questionnaire.element;

import java.lang.reflect.Type;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

public class ElementAdapter implements JsonSerializer<Element>, JsonDeserializer<Element> {

    @Override
    public JsonElement serialize(Element src, Type typeOfSrc, JsonSerializationContext context) {

        JsonObject jsonObject = new JsonObject();
        String className = src.getClass().getCanonicalName();
        String aClassName = className.substring(className.lastIndexOf(".") + 1);
        jsonObject.add(aClassName, context.serialize(src));
        return jsonObject;
    }

    @SuppressWarnings("rawtypes")
    @Override
    public Element deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
            throws JsonParseException {

        Class[] classes = new Class[] { ButtonElement.class, EditTextElement.class, Element.class, MyTextView.class,
                TestElement.class, TextViewElement.class, TwoButtonElement.class, RadioButtonElement.class, HelpTextElement.class };

        JsonObject jsonObject = json.getAsJsonObject();
        for (Class c : classes) {
            String className = c.toString();
            String aClassName = className.substring(className.lastIndexOf(".") + 1);

            if (jsonObject.has(aClassName))
                return context.deserialize(jsonObject.get(aClassName), c);
        }

        throw new JsonParseException("Can't parse " + json);
    }
}
