package dk.silverbullet.telemed.bloodsugar;

import com.google.gson.annotations.Expose;

/**
 * User entered Insulin entry
 */
public class InsulinEvent extends Event {

    @Expose
    public final String eventType = "InsulinEvent";

    public enum InsulinType {
        RAPID_ACTING,
        LONG_ACTING,
        PRE_MIX,
        INTERMEDIATE,
        SHORT_ACTING,
        UNKNOWN
    }

    @Expose
    public InsulinType insulinType;

    @Expose
    public String units;
}
