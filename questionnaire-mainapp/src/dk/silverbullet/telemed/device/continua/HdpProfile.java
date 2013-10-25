package dk.silverbullet.telemed.device.continua;

public enum HdpProfile {
    // Use the appropriate IEEE 11073 data types based on the devices used.
    // Below are some examples. Refer to relevant Bluetooth HDP specifications
    // for detail.

    PULSE_OXIMETER(0x1004), BLOOD_PRESSURE_METER(0x1007), BODY_THERMOMETER(0x1008), BODY_WEIGHT_SCALE(0x100F);

    private final int profileId;

    private HdpProfile(int profileId) {
        this.profileId = profileId;
    }

    public int getProfileId() {
        return profileId;
    }
}
