package dk.silverbullet.telemed.device.nonin;

public interface SaturationPulseListener {
    void connected();

    void permanentProblem();

    void temporaryProblem();

    void measurementReceived(String systemId, SaturationAndPulse measurement);
}
