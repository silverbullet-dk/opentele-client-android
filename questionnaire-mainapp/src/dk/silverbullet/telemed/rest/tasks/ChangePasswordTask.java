package dk.silverbullet.telemed.rest.tasks;

import java.util.ArrayList;
import java.util.List;

import android.os.AsyncTask;
import dk.silverbullet.telemed.rest.bean.ChangePasswordError;
import dk.silverbullet.telemed.rest.client.RestClient;
import dk.silverbullet.telemed.rest.client.RestException;

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

        try {
            ChangePasswordResponse responseBean = RestClient.postJson(questionnaire, CHANGE_PASSWORD_URL_PREFIX, changePasswordBean, ChangePasswordResponse.class);
            Log.d(TAG, "Response..:" + responseBean);

            if (responseBean.isError()) {
                errorTexts = new ArrayList<String>();
                for (ChangePasswordError error : responseBean.getErrors()) {
                    errorTexts.add(error.getError());
                }
                return Result.ERROR;
            }

            return Result.SUCCESS;
        } catch (RestException e) {
            Log.e(TAG, "Could not change password", e);
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
