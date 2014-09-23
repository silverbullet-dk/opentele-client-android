package dk.silverbullet.telemed.questionnaire.node.monica.realtime;

public enum MilouSoapActions {
    NEW_MESSAGE("http://tempuri.org/IOpenTeleRT/NewMessage"),
    STOP_REGISTRATION("http://tempuri.org/IOpenTeleRT/StopRegistration");

    private final String actionString;

    MilouSoapActions(String soapActionString) {
        this.actionString = soapActionString;
    }

    public String getActionString() {
        return actionString;
    }
}
