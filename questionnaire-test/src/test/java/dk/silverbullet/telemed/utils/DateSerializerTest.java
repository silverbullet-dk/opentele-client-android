package dk.silverbullet.telemed.utils;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static org.junit.Assert.assertEquals;

public class DateSerializerTest {
    private DateSerializer serializer;

    @Before
    public void before() {
        serializer = new DateSerializer();
    }

    @Test
    public void serializesDates() throws ParseException {
        Date dateToSerialize = new SimpleDateFormat("yyyy-MM-dd hh:mm").parse("2012-01-15 17:04");
        JsonElement serialized = serializer.serialize(dateToSerialize, Date.class, null);

        assertEquals("2012-01-15T17:04:00.000+01:00", serialized.getAsString());
    }

    @Test
    public void deserializesDates() throws ParseException {
        JsonElement elementToDeserialize = new JsonPrimitive("2012-01-15T17:04:00.000+01:00");
        Date deserialized = serializer.deserialize(elementToDeserialize, Date.class, null);

        Date expectedDate = new SimpleDateFormat("yyyy-MM-dd hh:mm").parse("2012-01-15 17:04");
        assertEquals(expectedDate, deserialized);
    }
}
