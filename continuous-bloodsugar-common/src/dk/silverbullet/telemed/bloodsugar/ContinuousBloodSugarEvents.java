package dk.silverbullet.telemed.bloodsugar;

import com.google.gson.annotations.Expose;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ContinuousBloodSugarEvents {
    @Expose
    public String deviceId;

    @Expose
    public Date transferTime;

    @Expose
    public List<Event> events = new ArrayList<Event>();
}
