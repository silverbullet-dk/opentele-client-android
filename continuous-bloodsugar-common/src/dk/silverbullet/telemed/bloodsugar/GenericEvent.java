package dk.silverbullet.telemed.bloodsugar;

import com.google.gson.annotations.Expose;

/**
 * User entered State of health
 */
public class GenericEvent extends Event {

    @Expose
    public final String eventType = "GenericEvent";

    @Expose
    public String indicatedEvent;
}
