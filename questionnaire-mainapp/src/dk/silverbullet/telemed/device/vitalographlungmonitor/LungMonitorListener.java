package dk.silverbullet.telemed.device.vitalographlungmonitor;

public interface LungMonitorListener {
    void connected();

    void permanentProblem();

    void temporaryProblem();

    void measurementReceived(String systemId, LungMeasurement measurement);
}
