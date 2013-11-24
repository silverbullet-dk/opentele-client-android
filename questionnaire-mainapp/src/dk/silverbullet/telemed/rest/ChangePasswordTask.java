package dk.silverbullet.telemed.rest;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import android.os.AsyncTask;
import dk.silverbullet.telemed.rest.bean.ChangePasswordError;
import dk.silverbullet.telemed.utils.Json;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import android.util.Log;

import dk.silverbullet.telemed.questionnaire.Questionnaire;
import dk.silverbullet.telemed.rest.bean.ChangePasswordBean;
import dk.silverbullet.telemed.rest.bean.ChangePasswordResponse;
import dk.silverbullet.telemed.rest.listener.ChangePasswordListener;
import dk.silverbullet.telemed.utils.Util;

public class ChangePasswordTask extends AsyncTask<String, Void, ChangePasswordTask.Result> {
    private static final String TAG = Util.getTag(ChangePasswordTask.class);
    private static final String CHANGE_PASSWORD_URL_PREFIX = "rest/password/update";

    static enum Result { SUCCESS, ERROR, COMMUNICATION_ERROR }

    private final ChangePasswordListener changePasswordListener;
    private final Questionnaire questionnaire;
    private final String password;
    private final String passwordRepeat;

    private List<String> errorTexts;

    public ChangePasswordTask(Questionnaire questionnaire, ChangePasswordListener changePasswordListener,
                              String password, String passwordRepeat) {
        this.questionnaire = questionnaire;
        this.changePasswordListener = changePasswordListener;
        this.password = password;
        this.passwordRepeat = passwordRepeat;
    }

    @Override
    protected Result doInBackground(String... params) {
        Log.d(TAG, "changePasswordListener....");
        ChangePasswordBean changePasswordBean = new ChangePasswordBean();
        changePasswordBean.setCurrentPassword(Util.getStringVariableValue(questionnaire, Util.VARIABLE_PASSWORD));
        changePasswordBean.setPassword(password);
        changePasswordBean.setPasswordRepeat(passwordRepeat);

        DefaultHttpClient httpClient = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost(Util.getServerUrl(questionnaire) + CHANGE_PASSWORD_URL_PREFIX);
        Util.setHeaders(httpPost, questionnaire);

        try {
            httpPost.setEntity(new StringEntity(Json.print(changePasswordBean), "UTF-8"));

            HttpResponse response = httpClient.execute(httpPost);
            if (response.getStatusLine().getStatusCode() != 200) {
                return Result.COMMUNICATION_ERROR;
            }

            ChangePasswordResponse responseBean = Json.parse(new InputStreamReader(response.getEntity().getContent(), "UTF-8"), ChangePasswordResponse.class);
            Log.d(TAG, "Response..:" + responseBean);

            if (responseBean.isError()) {
                errorTexts = new ArrayList<String>();
                for (ChangePasswordError error : responseBean.getErrors()) {
                    errorTexts.add(error.getError());
                }
                return Result.ERROR;
            }

            return Result.SUCCESS;
        } catch (IOException e) {
            Log.e(TAG, "Got exception", e);
            return Result.COMMUNICATION_ERROR;
        }
    }

    @Override
    protected void onPostExecute(Result result) {
        switch (result) {
            case SUCCESS:
                Util.setStringVariableValue(questionnaire, Util.VARIABLE_PASSWORD, password);
                changePasswordListener.changePasswordSucceeded();
                break;
            case ERROR:
                changePasswordListener.changePasswordFailed(errorTexts);
                break;
            case COMMUNICATION_ERROR:
                changePasswordListener.communicationError();
                break;
            default:
                throw new IllegalArgumentException("Unknown result: " + result);
        }
    }
}
