package dk.silverbullet.telemed.utils;

import com.google.gson.*;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

import java.lang.reflect.Type;
import java.util.Date;

public final class DateSerializer implements JsonDeserializer<Date>, JsonSerializer<Date> {
    private static final DateTimeFormatter DATE_TIME_FORMATTER = ISODateTimeFormat.dateTime();

    @Override
    public Date deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext context)
            throws JsonParseException {
        String dateAsString = jsonElement.getAsString();
        return dateAsString.isEmpty() ? null : DATE_TIME_FORMATTER.parseDateTime(dateAsString).toDate();
    }

    @Override
    public JsonElement serialize(Date src, Type typeOfSrc, JsonSerializationContext context) {
        return new JsonPrimitive(src == null ? "" : DATE_TIME_FORMATTER.print(new DateTime(src)));
    }
}