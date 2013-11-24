package dk.silverbullet.telemed.schedule;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import dk.silverbullet.telemed.rest.bean.ReminderBean;

public class UpcomingReminders {
    private Date baselineDate;
    private List<ReminderBean> reminders;

    public UpcomingReminders(Date baselineDate, List<ReminderBean> reminders) {
        this.baselineDate = baselineDate;
        this.reminders = reminders;
    }

    public UpcomingReminders(long baselineDateAsLong, List<ReminderBean> reminders) {
        this.baselineDate = new Date(baselineDateAsLong);
        this.reminders = reminders;
    }

    public long getBaselineDateAsLong() {
        return baselineDate.getTime();
    }

    public List<ReminderBean> getReminderBeans() {
        return reminders;
    }

    public boolean hasMoreReminders() {
        return !reminders.isEmpty();
    }

    public Date nextReminder() {
        // We know that we have at least one upcoming reminder, so we'll never actually return Long.MAX_VALUE
        long offsetToNextReminder = Long.MAX_VALUE;

        for (ReminderBean reminderBean : reminders) {
            for (Long alarm : reminderBean.getAlarms()) {
                offsetToNextReminder = Math.min(offsetToNextReminder, alarm);
            }
        }

        return dateFromOffset(offsetToNextReminder);
    }

    public List<String> remindedQuestionnairesAt(Date time) {
        long offset = offsetFromDate(time);
        List<String> result = new ArrayList<String>();

        for (ReminderBean reminderBean : reminders) {
            for (Long alarm : reminderBean.getAlarms()) {
                if (alarm <= offset) {
                    result.add(reminderBean.getQuestionnaireName());
                    break;
                }
            }
        }

        return result;
    }

    /**
     * Removes all reminder beans that would be "fired" at a given time.
     * 
     * Creates a whole new list of ReminderBean instances, since we don't know if the current reminderBeans instance
     * supports removal etc., and anyway it's generally, mutating lists is not so nice.
     */
    public void removeRemindersBeforeOrAt(Date time) {
        long offset = offsetFromDate(time);
        List<ReminderBean> newReminderBeans = new ArrayList<ReminderBean>();

        for (ReminderBean reminderBean : reminders) {
            List<Long> alarms = new ArrayList<Long>();
            for (Long alarm : reminderBean.getAlarms()) {
                if (alarm > offset) {
                    alarms.add(alarm);
                }
            }

            if (!alarms.isEmpty()) {
                ReminderBean newReminderBean = new ReminderBean();
                newReminderBean.setQuestionnaireId(reminderBean.getQuestionnaireId());
                newReminderBean.setQuestionnaireName(reminderBean.getQuestionnaireName());
                newReminderBean.setAlarms(alarms);
                newReminderBeans.add(newReminderBean);
            }
        }

        reminders = newReminderBeans;
    }

    public void removeQuestionnaire(String questionnaireName) {
        List<ReminderBean> newReminderBeans = new ArrayList<ReminderBean>();

        for (ReminderBean reminderBean : reminders) {
            if (!questionnaireName.equals(reminderBean.getQuestionnaireName())) {
                newReminderBeans.add(reminderBean);
            }
        }

        reminders = newReminderBeans;
    }

    private Date dateFromOffset(long offset) {
        return new Date(baselineDate.getTime() + offset * 1000);
    }

    private long offsetFromDate(Date date) {
        return (date.getTime() - baselineDate.getTime()) / 1000;
    }
}
