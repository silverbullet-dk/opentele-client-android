package dk.silverbullet.telemed.schedule;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import dk.silverbullet.telemed.ReminderActivity;
import dk.silverbullet.telemed.questionnaire.R;
import dk.silverbullet.telemed.utils.Util;

public class OnAlarmReceiver extends BroadcastReceiver {
    private static final String TAG = Util.getTag(OnAlarmReceiver.class);

    @Override
    public final void onReceive(Context context, Intent intent) {
        Log.d(TAG, "MyBroadcastReceiver....");

        displayNotification(context, Util.getString(R.string.alarm_user_prompt, context));

        ReminderService.updateReminders(context);
    }

    private void displayNotification(Context context, String message) {
        PendingIntent contentIntent = PendingIntent.getActivity(context, 0, new Intent(context, ReminderActivity.class), PendingIntent.FLAG_UPDATE_CURRENT);
        Notification notification = new Notification.Builder(context)
            .setWhen(System.currentTimeMillis())
            .setContentIntent(contentIntent)
            .setContentTitle(Util.getString(R.string.alarm_alarm, context))
            .setDefaults(Notification.DEFAULT_ALL)
            .setContentText(message)
            .setAutoCancel(true)
            .setSmallIcon(R.drawable.ic_launcher)
            .getNotification();

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(1, notification);
    }
}
