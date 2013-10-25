package dk.silverbullet.telemed.questionnaire.node;

import java.lang.reflect.Type;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

public class NodeAdapter implements JsonSerializer<Node>, JsonDeserializer<Node> {

    @Override
    public JsonElement serialize(Node src, Type typeOfSrc, JsonSerializationContext context) {

        JsonObject retValue = new JsonObject();
        String className = src.getClass().getCanonicalName();
        String aClassName = className.substring(className.lastIndexOf(".") + 1);
        retValue.add(aClassName, context.serialize(src));
        return retValue;
    }

    @SuppressWarnings("rawtypes")
    @Override
    public Node deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
            throws JsonParseException {

        Class[] classes = new Class[] { //
        AssignmentNode.class, //
                BloodPressureDeviceNode.class, //
                BloodPressureTestDeviceNode.class, //
                BloodSugarDeviceNode.class, //
                BloodSugarManualDeviceNode.class, //
                BloodSugarTestDeviceNode.class, //
                CRPNode.class, //
                DebugListPoolNode.class, //
                DeviceNode.class, //
                DecisionNode.class, //
                DelayNode.class, //
                EndNode.class, //
                IONode.class, //
                IOSkemaMenuNode.class, //
                LungMonitorDeviceNode.class, //
                LungMonitorTestDeviceNode.class, //
                MonicaDeviceNode.class, //
                MonicaTestDeviceNode.class, //
                Node.class, //
                RunQuestionnaireNode.class, //
                SaturationDeviceNode.class, //
                SaturationTestDeviceNode.class, //
                SaturationWithoutPulseDeviceNode.class, //
                SaturationWithoutPulseTestDeviceNode.class, //
                SaveFileNode.class, //
                TemperatureDeviceNode.class, //
                UploadNode.class, //
                UrineDeviceNode.class, //
                GlucoseUrineDeviceNode.class, //
                WeightDeviceNode.class, //
                WeightTestDeviceNode.class, //
                HaemoglobinDeviceNode.class };

        JsonObject jsonObject = json.getAsJsonObject();
        String className = "";
        for (Class c : classes) {
            className = c.toString();
            String aClassName = className.substring(className.lastIndexOf(".") + 1);

            if (jsonObject.has(aClassName))
                return context.deserialize(jsonObject.get(aClassName), c);
        }

        throw new JsonParseException("Can't parse " + jsonObject.toString() + " in " + json);
    }
}
