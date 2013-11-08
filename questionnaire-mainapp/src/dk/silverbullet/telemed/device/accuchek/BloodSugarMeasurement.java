package dk.silverbullet.telemed.device.accuchek;

import com.google.gson.annotations.Expose;

import java.util.Date;

/**
 * Simple value object representing a blood sugar measurement.
 * 
 * No use of encapsulating the fields, IMHO. Change it if your inner autistic tells you so.
 */

public class BloodSugarMeasurement {
    @Expose
    public Date timeOfMeasurement;
    @Expose
    public Double result;
    @Expose
    public Boolean hasTemperatureWarning;
    @Expose
    public Boolean isOutOfBounds;
    @Expose
    public Boolean otherInformation;
    @Expose
    public Boolean isBeforeMeal;
    @Expose
    public Boolean isAfterMeal;
    @Expose
    public Boolean isControlMeasurement;
}
