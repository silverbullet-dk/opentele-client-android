package dk.silverbullet.telemed.bloodsugar;

import com.google.gson.annotations.Expose;

public class ContinuousBloodSugarMeasurement extends Event {

    @Expose
    public final String eventType = "ContinuousBloodSugarMeasurement";

    @Expose
    public String glucoseValueInmmolPerl; //The reason this is a string is to ensure that values are sent with one decimal place.

}
