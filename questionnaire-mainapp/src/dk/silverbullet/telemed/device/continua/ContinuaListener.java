package dk.silverbullet.telemed.device.continua;

public interface ContinuaListener<T> {
    void connected();

    void disconnected();

    void permanentProblem();

    void temporaryProblem();

    void measurementReceived(String deviceId, T measurement);
}
