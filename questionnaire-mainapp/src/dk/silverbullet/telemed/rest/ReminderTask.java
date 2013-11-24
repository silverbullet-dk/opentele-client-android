package dk.silverbullet.telemed.rest;

import java.io.IOException;
import java.util.Arrays;

import dk.silverbullet.telemed.utils.Json;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;

import android.content.Context;
import android.util.Log;

import com.google.gson.JsonSyntaxException;

import dk.silverbullet.telemed.questionnaire.Questionnaire;
import dk.silverbullet.telemed.rest.bean.ReminderBean;
import dk.silverbullet.telemed.schedule.ReminderService;
import dk.silverbullet.telemed.utils.Util;

public class ReminderTask extends RetrieveTask {
    private static final String TAG = Util.getTag(ReminderTask.class);
    private static final String REMINDER_URL_PREFIX = "rest/reminder/next";

    public ReminderTask(Questionnaire questionnaire) {
        this.questionnaire = questionnaire;
    }

    @Override
    protected String doInBackground(String... params) {
        try {
            DefaultHttpClient httpClient = new DefaultHttpClient();
            HttpGet httpGet = new HttpGet(Util.getServerUrl(questionnaire) + REMINDER_URL_PREFIX);
            Util.setHeaders(httpGet, questionnaire);

            String reminderResponse = httpClient.execute(httpGet, new BasicResponseHandler());
            Log.d(TAG, "Upcoming reminders: " + reminderResponse);

            return reminderResponse;
        } catch (IOException e) {
            Log.e(TAG, "Could not get upcoming reminders", e);
            return "";
        }
    }

    @Override
    protected void onPostExecute(String result) {
        ReminderBean[] reminderBeans;
        try {
            reminderBeans = Json.parse(result, ReminderBean[].class);
        } catch (JsonSyntaxException e) {
            Log.e(TAG, "Could not deserialize response from server", e);
            reminderBeans = new ReminderBean[0];
        }
        Log.d(TAG, "Response..:" + Arrays.asList(reminderBeans));

        Context context = questionnaire.getActivity().getApplicationContext();
        ReminderService.setRemindersTo(context, reminderBeans);
    }
}
