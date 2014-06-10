package dk.silverbullet.telemed.bloodsugar;

import com.google.gson.annotations.Expose;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ContinuousBloodSugarMeasurements {
    @Expose
    public String serialNumber;
    @Expose
    public Date transferTime;
    @Expose
    public List<ContinuousBloodSugarMeasurement> measurements = new ArrayList<ContinuousBloodSugarMeasurement>();
}
