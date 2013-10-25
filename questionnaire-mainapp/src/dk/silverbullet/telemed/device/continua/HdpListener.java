package dk.silverbullet.telemed.device.continua;

public interface HdpListener {
    void applicationConfigurationRegistered();

    void applicationConfigurationRegistrationFailed();

    void applicationConfigurationUnregistered();

    void applicationConfigurationUnregistrationFailed();

    void serviceConnectionFailed();

    void connectionEstablished();

    void disconnected();
}
