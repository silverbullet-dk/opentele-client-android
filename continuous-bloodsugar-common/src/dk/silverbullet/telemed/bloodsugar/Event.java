package dk.silverbullet.telemed.bloodsugar;

import com.google.gson.annotations.Expose;

import java.util.Date;

public abstract class Event {
    public static final String UNSELECTED_DEFAULT_STRING = "UNSELECTED_DEFAULT";
    public int eventID;

    @Expose
    public long recordId;
    @Expose
    public Date eventTime;

}
