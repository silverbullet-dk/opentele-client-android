package dk.silverbullet.telemed.cgm;

import dk.silverbullet.telemed.bloodsugar.ContinuousBloodSugarMeasurements;

public interface ContinuousBloodSugarDeviceListener {
    void connected();

    void measurementsParsed(ContinuousBloodSugarMeasurements bloodSugarMeasurements);

    void userDeniedAccessToUSBDevice();

    void couldNotConnectToDevice();
}
