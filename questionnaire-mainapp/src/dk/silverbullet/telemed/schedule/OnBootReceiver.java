package dk.silverbullet.telemed.schedule;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import dk.silverbullet.telemed.utils.Util;

public class OnBootReceiver extends BroadcastReceiver {
    private static final String TAG = Util.getTag(OnBootReceiver.class);

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(TAG, "...onReceive...");
        ReminderService.setupReminders(context);
    }
}
