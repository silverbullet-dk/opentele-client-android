package dk.silverbullet.telemed.bloodsugar;

import com.google.gson.annotations.Expose;

/**
 * User entered State of health
 */
public class StateOfHealthEvent extends Event {

    @Expose
    public final String eventType = "StateOfHealthEvent";

    public enum HealthState {
        NORMAL,
        COLD,
        SORE_THROAT,
        INFECTION,
        TIRED,
        STRESS,
        FEVER,
        FLU,
        ALLERGY,
        PERIOD,
        DIZZY,
        FEEL_LOW,
        FEEL_HIGH,
        UNKNOWN
    }

    @Expose
    public HealthState stateOfHealth;

}
