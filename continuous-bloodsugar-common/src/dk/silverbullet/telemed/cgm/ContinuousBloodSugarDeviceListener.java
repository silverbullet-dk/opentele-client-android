package dk.silverbullet.telemed.cgm;

import dk.silverbullet.telemed.bloodsugar.ContinuousBloodSugarEvents;

public interface ContinuousBloodSugarDeviceListener {
    void connected();

    void measurementsParsed(ContinuousBloodSugarEvents bloodSugarMeasurements);

    void userDeniedAccessToUSBDevice();

    void couldNotConnectToDevice();
}
