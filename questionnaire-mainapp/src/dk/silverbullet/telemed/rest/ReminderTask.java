package dk.silverbullet.telemed.rest;

import java.io.IOException;
import java.util.Arrays;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import dk.silverbullet.telemed.questionnaire.Questionnaire;
import dk.silverbullet.telemed.rest.bean.ReminderBean;
import dk.silverbullet.telemed.schedule.ReminderService;
import dk.silverbullet.telemed.utils.Util;

public class ReminderTask extends RetrieveTask {

    private static final String TAG = Util.getTag(ReminderTask.class);
    public static final String REMINDER_URL_PREFIX = "rest/reminder/next";

    public ReminderTask(Questionnaire questionnaire) {
        this.questionnaire = questionnaire;
    }

    @Override
    protected String doInBackground(String... params) {
        Log.d(TAG, "ReminderTask....");

        DefaultHttpClient httpclient = new DefaultHttpClient();
        HttpGet httpget = new HttpGet(Util.getServerUrl(questionnaire) + REMINDER_URL_PREFIX);
        Log.d(TAG, "Serverurl..:" + httpget.getURI());

        httpget.setHeader("Content-type", "application/json");
        httpget.setHeader("Accept", "application/json");
        httpget.setHeader("X-Requested-With", "json");

        try {
            setHeaders(httpget);

            String reminderResponse = httpclient.execute(httpget, new BasicResponseHandler());
            Log.d(TAG, "Response..:" + reminderResponse);

            return reminderResponse;
        } catch (IOException e) {
            Log.e(TAG, "Got exception", e);
            return "";
        }
    }

    @Override
    protected void onPostExecute(String result) {
        Log.d(TAG, "TODO....:" + result);

        ReminderBean[] reminderBeans;
        try {
            reminderBeans = new Gson().fromJson(result, ReminderBean[].class);
        } catch (JsonSyntaxException e) {
            Log.e(TAG, "Could not deserialize response from server", e);
            reminderBeans = new ReminderBean[0];
        }
        Log.d(TAG, "Response..:" + Arrays.asList(reminderBeans));

        Context context = questionnaire.getActivity().getApplicationContext();
        ReminderService.setRemindersTo(context, reminderBeans);
    }
}
