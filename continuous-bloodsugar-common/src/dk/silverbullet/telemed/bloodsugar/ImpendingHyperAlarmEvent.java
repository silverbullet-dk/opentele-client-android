package dk.silverbullet.telemed.bloodsugar;

import com.google.gson.annotations.Expose;

/**
 * A high blood sugar condition is about to happen
 */
public class ImpendingHyperAlarmEvent extends Event {

    @Expose
    public final String eventType = "ImpendingHyperAlarmEvent";

    @Expose
    public String glucoseValueInmmolPerl;

    @Expose
    public String impendingNess;

}
