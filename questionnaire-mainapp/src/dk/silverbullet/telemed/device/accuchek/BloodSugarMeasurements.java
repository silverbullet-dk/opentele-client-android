package dk.silverbullet.telemed.device.accuchek;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.google.gson.annotations.Expose;

/**
 * Simple value object representing a transferred CSV file with blood sugar measurements.
 * 
 * No use of encapsulating the fields, IMHO. Change it if your inner autistic tells you so.
 */
public class BloodSugarMeasurements {
    @Expose
    public String serialNumber;
    @Expose
    public Date transferTime;
    @Expose
    public List<BloodSugarMeasurement> measurements = new ArrayList<BloodSugarMeasurement>();
}
