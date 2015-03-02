package dk.silverbullet.telemed.questionnaire.node.monica;

import java.util.Calendar;
import java.util.Date;

public interface MonicaDeviceCallback {

    void addSamples(float[] mhr, float[] fhr, int[] qfhr, float[] toco, Date readTime);

    void updateProgress(int i, int samples);

    void setProbeState(boolean orange, boolean white, boolean green, boolean black, boolean yellow);

    void setState(DeviceState state);

    void abort(String string);

    void done(String message);

    void done();

    void setStartVoltage(float voltage);

    void setEndVoltage(float voltage);

    void setStartTimeValue(Date dateTime);

    void setEndTimeValue(Date dateTime);

    void addSignal(Date dateTime);

    void setDeviceIdString(String deviceId);

    int getSampleTimeMinutes();

    Date getStartTimeValue();

    void addFetalHeight(int fetalHeight);

    void addSignalToNoise(int signalToNoise);

}
