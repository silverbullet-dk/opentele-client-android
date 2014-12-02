package dk.silverbullet.telemed.bloodsugar;

import com.google.gson.annotations.Expose;

/**
 * A low blood sugar condition is about to happen
 */
public class ImpendingHypoAlarmEvent extends Event {

    @Expose
    public String eventType = "ImpendingHypoAlarmEvent";

    @Expose
    public String glucoseValueInmmolPerl;

    @Expose
    public String impendingNess;

}
