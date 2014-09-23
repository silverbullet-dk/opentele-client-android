package dk.silverbullet.telemed.device.nonin;

public interface SaturationPulseListener {
    void connected();
    void temporaryProblem();
    void measurementReceived(String systemId, SaturationAndPulse measurement);
    void firstTimeOut();
    void finalTimeOut(String systemId, SaturationAndPulse measurement);
}
