package dk.silverbullet.telemed.video.measurement;

import android.content.Context;
import dk.silverbullet.telemed.rest.client.ServerInformation;

public interface MeasurementInformer extends ServerInformation {
    void setMeasurementTypeText(String measurementTypeText);
    void setStatusText(String statusText);
    void reveal();
    void hide();

    String getClientVersion();

    Context getContext();
}
