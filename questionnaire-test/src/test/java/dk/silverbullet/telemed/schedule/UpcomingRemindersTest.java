package dk.silverbullet.telemed.schedule;

import static org.junit.Assert.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;

import org.junit.Test;

import dk.silverbullet.telemed.rest.bean.ReminderBean;

public class UpcomingRemindersTest {
	Date now = time("2013-06-05 16:12:02");
	
	@Test
	public void hasNoUpcomingRemindersWhenReminderBeanListIsEmpty() {
		UpcomingReminders reminders = new UpcomingReminders(now, new ArrayList<ReminderBean>());
		assertFalse(reminders.hasMoreReminders());
	}
	
	@Test
	public void knowsUpcomingAlarmForOneReminderBean() {
		ReminderBean reminderBean = reminderBean(23, "First questionnaire", secondsTo("2013-06-05 17:00:00"));
	
		UpcomingReminders reminders = new UpcomingReminders(now, Arrays.asList(reminderBean));
		assertTrue(reminders.hasMoreReminders());
		assertEquals(time("2013-06-05 17:00:00"), reminders.nextReminder());
	}

    @Test
    public void handlesUpcomingAlarmsInFarFuture() {
        long farInTheFuture = 59000000; // Overflows an int when multiplied with 1000 to go from seconds to milliseconds
        ReminderBean reminderBean = reminderBean(23, "First questionnaire", farInTheFuture);

        UpcomingReminders reminders = new UpcomingReminders(now, Arrays.asList(reminderBean));
        assertTrue(reminders.hasMoreReminders());
        assertTrue(reminders.nextReminder().after(new Date()));
    }
	
	@Test
	public void knowsFirstUpcomingAlarmForSeveralReminderBeans() {
		ReminderBean reminderBean1 = reminderBean(23, "First questionnaire", secondsTo("2013-06-05 17:00:00"), secondsTo("2013-06-05 17:15:00"));
		ReminderBean reminderBean2 = reminderBean(23, "Second questionnaire", secondsTo("2013-06-05 16:15:00"), secondsTo("2013-06-05 16:30:00"));
		ReminderBean reminderBean3 = reminderBean(23, "Third questionnaire", secondsTo("2013-06-05 17:30:00"));
		
		UpcomingReminders reminders = new UpcomingReminders(now, Arrays.asList(reminderBean1, reminderBean2, reminderBean3));
		assertTrue(reminders.hasMoreReminders());
		assertEquals(time("2013-06-05 16:15:00"), reminders.nextReminder());
	}
	
	@Test
	public void knowsWhichQuestionnairesHaveHadAlarmsAtSpecificTime() {
		ReminderBean reminderBean1 = reminderBean(23, "First questionnaire", secondsTo("2013-06-05 17:00:00"), secondsTo("2013-06-05 17:15:00"));
		ReminderBean reminderBean2 = reminderBean(23, "Second questionnaire", secondsTo("2013-06-05 16:15:00"), secondsTo("2013-06-05 16:30:00"));
		ReminderBean reminderBean3 = reminderBean(23, "Third questionnaire", secondsTo("2013-06-05 17:30:00"));
		
		UpcomingReminders reminders = new UpcomingReminders(now, Arrays.asList(reminderBean1, reminderBean2, reminderBean3));
		
		assertEquals(Collections.emptyList(), reminders.remindedQuestionnairesAt(time("2013-06-05 16:14:00")));
		
		assertEquals(Arrays.asList("Second questionnaire"), reminders.remindedQuestionnairesAt(time("2013-06-05 16:15:00")));
		assertEquals(Arrays.asList("Second questionnaire"), reminders.remindedQuestionnairesAt(time("2013-06-05 16:59:00")));

		assertEquals(Arrays.asList("First questionnaire", "Second questionnaire"), reminders.remindedQuestionnairesAt(time("2013-06-05 17:00:00")));
		assertEquals(Arrays.asList("First questionnaire", "Second questionnaire"), reminders.remindedQuestionnairesAt(time("2013-06-05 17:29:00")));

		assertEquals(Arrays.asList("First questionnaire", "Second questionnaire", "Third questionnaire"), reminders.remindedQuestionnairesAt(time("2013-06-05 17:30:00")));
		assertEquals(Arrays.asList("First questionnaire", "Second questionnaire", "Third questionnaire"), reminders.remindedQuestionnairesAt(time("2013-06-05 19:00:00")));
	}
	
	@Test
	public void canRemoveRemindersBeforeOrAtSpecificTime() {
		ReminderBean reminderBean1 = reminderBean(23, "First questionnaire", secondsTo("2013-06-05 17:00:00"), secondsTo("2013-06-05 17:15:00"));
		ReminderBean reminderBean2 = reminderBean(23, "Second questionnaire", secondsTo("2013-06-05 16:15:00"), secondsTo("2013-06-05 16:30:00"));
		ReminderBean reminderBean3 = reminderBean(23, "Third questionnaire", secondsTo("2013-06-05 17:30:00"));
		
		UpcomingReminders reminders = new UpcomingReminders(now, Arrays.asList(reminderBean1, reminderBean2, reminderBean3));
		
		assertEquals(Arrays.asList("Second questionnaire"), reminders.remindedQuestionnairesAt(time("2013-06-05 16:15:00")));
		reminders.removeRemindersBeforeOrAt(time("2013-06-05 16:15:00"));
		assertEquals(Collections.emptyList(), reminders.remindedQuestionnairesAt(time("2013-06-05 16:15:00")));
		assertTrue(reminders.hasMoreReminders());
		
		assertEquals(Arrays.asList("Second questionnaire"), reminders.remindedQuestionnairesAt(time("2013-06-05 16:30:00")));
		reminders.removeRemindersBeforeOrAt(time("2013-06-05 16:30:00"));
		assertEquals(Collections.emptyList(), reminders.remindedQuestionnairesAt(time("2013-06-05 16:30:00")));
		assertTrue(reminders.hasMoreReminders());

		assertEquals(Arrays.asList("First questionnaire"), reminders.remindedQuestionnairesAt(time("2013-06-05 17:00:00")));
		reminders.removeRemindersBeforeOrAt(time("2013-06-05 17:00:00"));
		assertEquals(Collections.emptyList(), reminders.remindedQuestionnairesAt(time("2013-06-05 17:00:00")));
		assertTrue(reminders.hasMoreReminders());
		
		assertEquals(Arrays.asList("First questionnaire", "Third questionnaire"), reminders.remindedQuestionnairesAt(time("2013-06-05 17:30:00")));
		reminders.removeRemindersBeforeOrAt(time("2013-06-05 17:30:00"));
		assertEquals(Collections.emptyList(), reminders.remindedQuestionnairesAt(time("2013-06-05 17:30:00")));
		assertFalse(reminders.hasMoreReminders());
	}
	
	@Test
	public void canRemoveRemindersForSpecificQuestionnaire() {
		ReminderBean reminderBean1 = reminderBean(23, "First questionnaire", secondsTo("2013-06-05 17:00:00"), secondsTo("2013-06-05 17:15:00"));
		ReminderBean reminderBean2 = reminderBean(23, "Second questionnaire", secondsTo("2013-06-05 16:15:00"), secondsTo("2013-06-05 16:30:00"));
		
		UpcomingReminders reminders = new UpcomingReminders(now, Arrays.asList(reminderBean1, reminderBean2));
		reminders.removeQuestionnaire("First questionnaire");
		assertEquals(Arrays.asList("Second questionnaire"), reminders.remindedQuestionnairesAt(time("2013-06-05 17:30:00")));
	}
	
	private ReminderBean reminderBean(long questionnaireId, String questionnaireName, Long... alarms) {
		ReminderBean result = new ReminderBean();
		result.setQuestionnaireId(questionnaireId);
		result.setQuestionnaireName(questionnaireName);
		result.setAlarms(Arrays.asList(alarms));
		return result;
	}

	private long secondsTo(String timeAsString) {
		return (time(timeAsString).getTime() - now.getTime()) / 1000;
	}
	
	private Date time(String asString) {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {
			return format.parse(asString);
		} catch (ParseException e) {
			throw new RuntimeException("Could not parse '" + asString + "'", e);
		}
	}
}
