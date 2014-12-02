package dk.silverbullet.telemed.bloodsugar;

import com.google.gson.annotations.Expose;

/**
 * Low blood sugar condition occurred
 */
public class HypoAlarmEvent extends Event {

    @Expose
    public final String eventType = "HypoAlarmEvent";

    @Expose
    public String glucoseValueInmmolPerl;
}
