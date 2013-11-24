package dk.silverbullet.telemed.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dk.silverbullet.telemed.questionnaire.element.Element;
import dk.silverbullet.telemed.questionnaire.element.ElementAdapter;
import dk.silverbullet.telemed.questionnaire.expression.*;
import dk.silverbullet.telemed.questionnaire.node.Node;
import dk.silverbullet.telemed.questionnaire.node.NodeAdapter;

import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class Json {
    public static final SimpleDateFormat ISO8601_DATE_TIME_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
    public static final SimpleDateFormat ISO8601_DATE_TIME_FORMAT_SHORT = new SimpleDateFormat("yyyyMMdd'T'HHmmss'Z'");

    private static final Gson GSON;

    static {
        // 'Zulu time' in accordance with ISO-8601: http://en.wikipedia.org/wiki/Iso8601#UTC
        ISO8601_DATE_TIME_FORMAT.setTimeZone(TimeZone.getTimeZone("UTC"));
        ISO8601_DATE_TIME_FORMAT_SHORT.setTimeZone(TimeZone.getTimeZone("UTC"));

        // Set up a Gson builder which handles our node types and dates
        GsonBuilder builder = new GsonBuilder();
        builder.registerTypeAdapter(Element.class, new ElementAdapter());
        builder.registerTypeAdapter(Node.class, new NodeAdapter());
        Object expressionInterfaceAdapter = new ExpressionInterfaceAdapter<Expression<?>>();
        builder.registerTypeAdapter(Expression.class, expressionInterfaceAdapter);
        builder.registerTypeAdapter(Variable.class, new VariableAdapter());
        builder.registerTypeAdapter(Date.class, new DateSerializer());
        GSON = builder.excludeFieldsWithoutExposeAnnotation().create();
    }


    public static <T> T parse(String json, Class<T> type) {
        return GSON.fromJson(json, type);
    }

    public static <T> T parse(InputStreamReader reader, Class<T> type) {
        return GSON.fromJson(reader, type);
    }

    public static String print(Object object) {
        return GSON.toJson(object);
    }

    public static <T> String print(Object object, Class<T> type) {
        return GSON.toJson(object, type);
    }
}
