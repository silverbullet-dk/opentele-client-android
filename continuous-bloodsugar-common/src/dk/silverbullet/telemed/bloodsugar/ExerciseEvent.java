package dk.silverbullet.telemed.bloodsugar;

import com.google.gson.annotations.Expose;

/**
 * User entered Exercise
 */
public class ExerciseEvent extends Event {

    @Expose
    public final String eventType = "ExerciseEvent";

    public enum ExerciseType {
        AEROBICS,
        WALKING,
        JOGGING,
        RUNNING,
        SWIMMING,
        BIKING,
        WEIGHTS,
        OTHER,
        UNKNOWN
    }

    public enum ExerciseIntensity {
        NONE,
        LOW,
        MEDIUM,
        HIGH,
        UNKNOWN
    }

    @Expose
    public String durationInMinutes;

    @Expose
    public ExerciseType exerciseType;

    @Expose
    public  ExerciseIntensity exerciseIntensity;
}
