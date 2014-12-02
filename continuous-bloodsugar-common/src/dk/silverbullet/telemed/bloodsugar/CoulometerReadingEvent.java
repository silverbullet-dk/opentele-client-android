package dk.silverbullet.telemed.bloodsugar;

import com.google.gson.annotations.Expose;

/**
 * Discrete blood glucose measurement
 */
public class CoulometerReadingEvent extends Event {

    @Expose
    public final String eventType = "CoulometerReadingEvent";

    @Expose
    public String glucoseValueInmmolPerl;

}
