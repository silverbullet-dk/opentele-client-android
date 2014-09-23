package dk.silverbullet.telemed.rest;

import dk.silverbullet.telemed.questionnaire.Questionnaire;
import dk.silverbullet.telemed.questionnaire.output.OutputSkema;
import dk.silverbullet.telemed.questionnaire.skema.Skema;
import dk.silverbullet.telemed.rest.bean.ListBean;
import dk.silverbullet.telemed.rest.bean.ReminderBean;
import dk.silverbullet.telemed.rest.bean.acknowledgement.AcknowledgementListBean;
import dk.silverbullet.telemed.rest.bean.message.MessageRecipient;
import dk.silverbullet.telemed.rest.bean.message.MessageWrite;
import dk.silverbullet.telemed.rest.bean.message.Messages;
import dk.silverbullet.telemed.rest.listener.PostEntityListener;
import dk.silverbullet.telemed.rest.listener.RetrieveEntityListener;
import dk.silverbullet.telemed.rest.tasks.PostEntityTask;
import dk.silverbullet.telemed.rest.tasks.RetrieveEntityTask;

/**
 * Encapsulates access to the various server resources accessed by the app.
 *
 * All access is done through AsyncTasks which make callbacks on the supplied listener upon completion.
 */
public class Resources {
    //
    // Questionnaire (Skema)
    //
    public static void getSkemas(Questionnaire questionnaire, RetrieveEntityListener<ListBean> listener) {
        new RetrieveEntityTask<ListBean>("rest/questionnaire/listing", questionnaire, listener, ListBean.class).execute();
    }

    public static void getSkema(String id, Questionnaire questionnaire, RetrieveEntityListener<Skema> listener) {
        new RetrieveEntityTask<Skema>("rest/questionnaire/download/" + id, questionnaire, listener, Skema.class).execute();
    }

    public static void postSkema(OutputSkema skema, Questionnaire questionnaire, PostEntityListener listener) {
        new PostEntityTask<OutputSkema>(skema, "rest/questionnaire/listing", questionnaire, listener).execute();
    }

    //
    // Acknowledgement list
    //
    public static void getAcknowledgementList(Questionnaire questionnaire, RetrieveEntityListener<AcknowledgementListBean> listener) {
        new RetrieveEntityTask<AcknowledgementListBean>("rest/questionnaire/acknowledgements", questionnaire, listener, AcknowledgementListBean.class).execute();
    }

    //
    // Message
    //
    public static void getMessages(Questionnaire questionnaire, RetrieveEntityListener<Messages> listener) {
        new RetrieveEntityTask<Messages>("rest/message/list/", questionnaire, listener, Messages.class).execute();
    }

    public static void postMessage(MessageWrite message, Questionnaire questionnaire, PostEntityListener listener) {
        new PostEntityTask<MessageWrite>(message, "rest/message/list/", questionnaire, listener).execute();
    }

    //
    // Message recipient
    //
    public static void getMessageRecipients(Questionnaire questionnaire, RetrieveEntityListener<MessageRecipient[]> listener) {
        new RetrieveEntityTask<MessageRecipient[]>("rest/message/recipients/", questionnaire, listener, MessageRecipient[].class).execute();
    }

    //
    // Reminder
    //
    public static void getUpcomingReminders(Questionnaire questionnaire, RetrieveEntityListener<ReminderBean[]> listener) {
        new RetrieveEntityTask<ReminderBean[]>("rest/reminder/next", questionnaire, listener, ReminderBean[].class).execute();
    }

    //
    // Continuous BloodSugarNode
    //
    public static void getLastContinuousBloodSugarLogNumber(Questionnaire questionnaire, RetrieveEntityListener<Long[]> listener) {
        new RetrieveEntityTask<Long[]>("rest/measurements/lastContinuousBloodSugarRecordNumber", questionnaire, listener, Long[].class).execute();
    }
}
