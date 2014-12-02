package dk.silverbullet.telemed.bloodsugar;

import com.google.gson.annotations.Expose;

/**
 * High blood sugar condition occurred
 */
public class HyperAlarmEvent extends Event {

    @Expose
    public final String eventType = "HyperAlarmEvent";

    @Expose
    public String glucoseValueInmmolPerl;

}
