package dk.silverbullet.telemed.cgm;

import android.content.Context;

public interface ContinuousBloodSugarDeviceDriver {
    public void setContext(Context context);
    public void setListener(ContinuousBloodSugarDeviceListener listener);
    public void setLastRecordNumber(Long lastRecordNumber);
    public void collectMeasurements() throws CGMDriverException;

}