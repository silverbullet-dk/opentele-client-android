package dk.silverbullet.telemed.schedule;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.google.gson.JsonParseException;

import dk.silverbullet.telemed.rest.bean.ReminderBean;
import dk.silverbullet.telemed.utils.Json;
import dk.silverbullet.telemed.utils.Util;

public class ReminderService {
    private static final String TAG = Util.getTag(ReminderService.class);
    private static final String SHARED_PREFRENCES_QUESTIONNAIRE_SCHEDULES = "PREF_QUESTIONNAIRE_SCHEDULES";
    private static final String SHARED_PREFRENCES_QUESTIONNAIRE_SCHEDULES_BASELINE = "PREF_QUESTIONNAIRE_SCHEDULES_BASELINE";
    private static final Set<String> QUESTIONNAIRES_TO_HIGHLIGHT = new HashSet<String>();

    /**
     * Sets reminders to the specified list of reminders, stores them, and sets up the required alarms.
     */
    public static void setRemindersTo(Context context, ReminderBean... reminderBeans) {
        Date now = new Date();

        UpcomingReminders upcomingReminders = new UpcomingReminders(now, Arrays.asList(reminderBeans));
        saveUpcomingReminders(context, upcomingReminders);

        setupReminders(context, now);
    }

    /**
     * For initializing the application. Loads stored reminders.
     */
    public static void setupReminders(Context context) {
        setupReminders(context, new Date());
    }

    /**
     * Used when an alarm has been issued and the reminders need to be updated accordingly.
     */
    public static void updateReminders(Context context) {
        Date now = new Date();

        UpcomingReminders upcomingReminders = getUpcomingReminders(context);
        QUESTIONNAIRES_TO_HIGHLIGHT.addAll(upcomingReminders.remindedQuestionnairesAt(now));

        setupReminders(context, now);
    }

    /**
     * Used when the user has started filling in a questionnaire, since it makes no sense to still issue reminders for
     * that questionnaire.
     */
    public static void clearRemindersForQuestionnaire(Context context, String questionnaireName) {
        UpcomingReminders upcomingReminders = getUpcomingReminders(context);

        QUESTIONNAIRES_TO_HIGHLIGHT.remove(questionnaireName);
        upcomingReminders.removeQuestionnaire(questionnaireName);

        saveUpcomingReminders(context, upcomingReminders);
    }

    public static boolean shouldHighlightQuestionnaire(String questionnaireName) {
        return QUESTIONNAIRES_TO_HIGHLIGHT.contains(questionnaireName);
    }

    private static void setupReminders(Context context, Date now) {
        cancelUpcomingAlarm(context);

        UpcomingReminders upcomingReminders = getUpcomingReminders(context);
        upcomingReminders.removeRemindersBeforeOrAt(now);
        saveUpcomingReminders(context, upcomingReminders);

        if (upcomingReminders.hasMoreReminders()) {
            setAlarm(context, upcomingReminders.nextReminder());
        }
    }

    private static void setAlarm(Context context, Date alarmTime) {
        Log.d(TAG, "Setting alarm: " + alarmTime);

        long timeInMillis = alarmTime.getTime();
        AlarmManager alarmManager = getAlarmManager(context);
        alarmManager.set(AlarmManager.RTC_WAKEUP, timeInMillis, getPendingIntent(context));
    }

    private static void cancelUpcomingAlarm(Context context) {
        AlarmManager alarmManager = getAlarmManager(context);
        alarmManager.cancel(getPendingIntent(context));
    }

    private static PendingIntent getPendingIntent(Context context) {
        Intent intent = new Intent(context, OnAlarmReceiver.class);
        return PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
    }

    private static void saveUpcomingReminders(Context context, UpcomingReminders upcomingReminders) {
        String reminderBeansAsJson = Json.print(upcomingReminders.getReminderBeans());
        long baselineDate = upcomingReminders.getBaselineDateAsLong();

        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
        editor.putString(SHARED_PREFRENCES_QUESTIONNAIRE_SCHEDULES, reminderBeansAsJson);
        editor.putLong(SHARED_PREFRENCES_QUESTIONNAIRE_SCHEDULES_BASELINE, baselineDate);
        editor.commit();
    }

    private static UpcomingReminders getUpcomingReminders(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        String reminderBeansAsJson = preferences.getString(SHARED_PREFRENCES_QUESTIONNAIRE_SCHEDULES, null);
        long baselineDate = preferences.getLong(SHARED_PREFRENCES_QUESTIONNAIRE_SCHEDULES_BASELINE, 0);

        if (reminderBeansAsJson != null && baselineDate != 0) {
            ReminderBean[] reminderBeans;
            try {
                reminderBeans = Json.parse(reminderBeansAsJson, ReminderBean[].class);
            } catch (JsonParseException e) {
                Log.e(TAG, "Could not deserialize reminder beans");
                return noReminders();
            }
            return new UpcomingReminders(baselineDate, Arrays.asList(reminderBeans));
        }
        return noReminders();
    }

    private static UpcomingReminders noReminders() {
        return new UpcomingReminders(new Date(), new ArrayList<ReminderBean>());
    }

    private static AlarmManager getAlarmManager(Context context) {
        return (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
    }
}
