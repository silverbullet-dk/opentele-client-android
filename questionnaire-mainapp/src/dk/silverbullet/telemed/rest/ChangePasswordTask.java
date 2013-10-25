package dk.silverbullet.telemed.rest;

import java.io.IOException;

import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;

import android.util.Log;

import com.google.gson.Gson;

import dk.silverbullet.telemed.questionnaire.Questionnaire;
import dk.silverbullet.telemed.rest.bean.ChangePasswordBean;
import dk.silverbullet.telemed.rest.bean.ChangePasswordResponse;
import dk.silverbullet.telemed.rest.listener.ChangePasswordListener;
import dk.silverbullet.telemed.utils.Util;

public class ChangePasswordTask extends RetrieveTask {

    private static final String TAG = Util.getTag(ChangePasswordTask.class);
    public static final String CHANGE_PASSWORD_URL_PREFIX = "rest/password/update";
    private final ChangePasswordListener changePasswordListener;

    public ChangePasswordTask(Questionnaire questionnaire, ChangePasswordListener changePasswordListener) {
        this.questionnaire = questionnaire;
        this.changePasswordListener = changePasswordListener;
    }

    @Override
    protected String doInBackground(String... params) {
        Log.d(TAG, "changePasswordListener....");
        ChangePasswordBean changePasswordBean = new ChangePasswordBean();
        changePasswordBean.setCurrentPassword(changePasswordListener.getCurrentPassword());
        changePasswordBean.setPassword(changePasswordListener.getPassword());
        changePasswordBean.setPasswordRepeat(changePasswordListener.getPasswordRepeat());

        DefaultHttpClient httpclient = new DefaultHttpClient();
        HttpPost httppost = new HttpPost(Util.getServerUrl(questionnaire) + CHANGE_PASSWORD_URL_PREFIX);
        Log.d(TAG, "Serverurl..:" + httppost.getURI());

        httppost.setHeader("Content-type", "application/json");
        httppost.setHeader("Accept", "application/json");
        httppost.setHeader("X-Requested-With", "json");

        try {
            setHeaders(httppost);
            httppost.setEntity(new StringEntity(new Gson().toJson(changePasswordBean), "UTF-8"));

            String schemaResponse = httpclient.execute(httppost, new BasicResponseHandler());
            Log.d(TAG, "Response..:" + schemaResponse);
            ChangePasswordResponse responseBean = new Gson().fromJson(schemaResponse, ChangePasswordResponse.class);

            if (responseBean.getStatus().equals(ChangePasswordResponse.STATUS_ERROR)) {
                // questionnaire.addVariable(new Variable<String>(Util.VARIABLE_REAL_NAME, loginBean.getFullName()));
            }

            Log.d(TAG, "Response..:" + responseBean);

            return schemaResponse;
        } catch (IOException e) {
            Log.e(TAG, "Got exception", e);
            changePasswordListener.sendError();

            return "";
        }
    }

    @Override
    protected void onPostExecute(String result) {
        changePasswordListener.response(result);
    }
}
