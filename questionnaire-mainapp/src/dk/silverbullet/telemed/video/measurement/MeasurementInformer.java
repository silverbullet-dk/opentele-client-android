package dk.silverbullet.telemed.video.measurement;

public interface MeasurementInformer {
    void setMeasurementTypeText(String measurementTypeText);
    void setStatusText(String statusText);
    void reveal();
    void hide();

    String getClientVersion();
    String getUsername();
    String getPassword();
    String getServerUrl();
}
