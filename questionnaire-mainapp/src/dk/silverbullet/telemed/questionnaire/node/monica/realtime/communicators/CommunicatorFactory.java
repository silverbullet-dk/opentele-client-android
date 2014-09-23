package dk.silverbullet.telemed.questionnaire.node.monica.realtime.communicators;

import android.util.Log;
import dk.silverbullet.telemed.OpenTeleApplication;
import dk.silverbullet.telemed.questionnaire.Questionnaire;
import dk.silverbullet.telemed.questionnaire.R;
import dk.silverbullet.telemed.utils.Util;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class CommunicatorFactory {

    private static Questionnaire questionnaire;
    private static final String TAG = Util.getTag(CommunicatorFactory.class);

    public static Communicator getCommunicator(Questionnaire questionnaire) {
        CommunicatorFactory.questionnaire = questionnaire;
        if(canConnectToMilou()) {
            return new MilouCommunicator(questionnaire);
        } else {
            return  new OpenTeleCommunicator(questionnaire);
        }
    }


    private static boolean canConnectToMilou() {
        try {
            URL milouServerUrl = new URL(Util.getString(R.string.milou_realtime_server_url, questionnaire.getContext()));
            HttpURLConnection milouServerConnection = (HttpURLConnection) milouServerUrl.openConnection();
            milouServerConnection.setConnectTimeout(5000);
            milouServerConnection.connect();
            milouServerConnection.disconnect();

            return true;
        } catch (MalformedURLException e) {
            Log.w(TAG, "Malformed milou server url:" + Util.getString(R.string.milou_realtime_server_url, questionnaire.getContext()), e);
            OpenTeleApplication.instance().logException(e);

            return false;
        } catch (IOException e) {
            Log.w(TAG, "Could not connect to milou server at url:" + Util.getString(R.string.milou_realtime_server_url, questionnaire.getContext()), e);
            OpenTeleApplication.instance().logException(e);

            return false;
        }
    }
}
