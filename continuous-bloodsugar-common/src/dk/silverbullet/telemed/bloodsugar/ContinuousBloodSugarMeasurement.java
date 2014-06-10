package dk.silverbullet.telemed.bloodsugar;

import com.google.gson.annotations.Expose;

import java.util.Date;

public class ContinuousBloodSugarMeasurement {
    @Expose
    public long recordId;
    @Expose
    public Date timeOfMeasurement;
    @Expose
    public String value; //The reason this is a string is to ensure that values are sent with one decimal place.
}
