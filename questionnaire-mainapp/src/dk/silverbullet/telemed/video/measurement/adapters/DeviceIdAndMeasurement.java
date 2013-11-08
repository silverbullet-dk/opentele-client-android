package dk.silverbullet.telemed.video.measurement.adapters;

public class DeviceIdAndMeasurement<T> {
    private final String deviceId;
    private final T measurement;

    public DeviceIdAndMeasurement(String deviceId, T measurement) {
        this.deviceId = deviceId;
        this.measurement = measurement;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public T getMeasurement() {
        return measurement;
    }
}
