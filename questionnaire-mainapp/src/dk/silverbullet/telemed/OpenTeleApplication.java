package dk.silverbullet.telemed;

import android.app.Application;
import android.util.Log;
import dk.silverbullet.telemed.logreports.LogReporter;
import dk.silverbullet.telemed.questionnaire.R;
import dk.silverbullet.telemed.utils.ReflectionHelper;
import dk.silverbullet.telemed.utils.Util;

import java.net.URL;

public class OpenTeleApplication extends Application {
    private static final String TAG = Util.getTag(OpenTeleApplication.class);
    private static OpenTeleApplication instance;
    private LogReporter reporter;

    public static OpenTeleApplication instance() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        if(isCentralLoggingEnabled()) {
            initializeCentralLogging();
        } else {
            Log.d(TAG, "Central log collection not supported");
        }
    }

    private void initializeCentralLogging() {
        try {
            String reportServerUrl = getResources().getString(R.string.report_server_url);

            if(reportServerUrl.equals("${report.server.url}")) {
                return;
            }

            reporter = (LogReporter) ReflectionHelper.getInstance(this, "dk.silverbullet.opentele.logreports.AcraBasedReporter");
            reporter.initialize(new URL(reportServerUrl), this, getResources().getString(R.string.server_url));

        } catch (Exception e) {
            Log.w(TAG, "Could not initialize cental logging", e);
        }
    }

    private boolean isCentralLoggingEnabled() {
        return ReflectionHelper.classCanBeLoaded(this, "dk.silverbullet.opentele.logreports.AcraBasedReporter");
    }


    public void logException(Exception exception) {
        if(reporter != null) {
            reporter.logException(exception);
        }
    }

    public void logMessage(String message) {
        if(reporter != null) {
            reporter.logMessage(message);
        }
    }
}
