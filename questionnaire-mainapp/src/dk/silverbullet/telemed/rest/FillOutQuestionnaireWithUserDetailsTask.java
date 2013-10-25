package dk.silverbullet.telemed.rest;

import java.io.IOException;
import java.net.URISyntaxException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import android.util.Log;

import com.google.gson.Gson;

import dk.silverbullet.telemed.questionnaire.MainQuestionnaire;
import dk.silverbullet.telemed.questionnaire.Questionnaire;
import dk.silverbullet.telemed.questionnaire.expression.Variable;
import dk.silverbullet.telemed.rest.bean.LoginBean;
import dk.silverbullet.telemed.rest.listener.LoginListener;
import dk.silverbullet.telemed.utils.Util;

public class FillOutQuestionnaireWithUserDetailsTask extends RetrieveTask {

    private static final String LOGIN_URL_PREFIX = "rest/patient/login";
    private static final String TAG = Util.getTag(FillOutQuestionnaireWithUserDetailsTask.class);
    private static final String ACCOUNT_LOCKED = "accountIsLocked";
    private final LoginListener loginListener;

    public FillOutQuestionnaireWithUserDetailsTask(Questionnaire questionnaire, LoginListener loginListener) {
        this.questionnaire = questionnaire;
        this.loginListener = loginListener;
    }

    @Override
    protected String doInBackground(String... params) {
        Log.d(TAG, "login....");
        @SuppressWarnings("unchecked")
        Variable<Boolean> isLoggedIn = (Variable<Boolean>) questionnaire.getValuePool().get(Util.VARIABLE_IS_LOGGED_IN);

        if (isAdmin()) {
            isLoggedIn.setValue(true);
        } else {
            HttpClient httpclient = new DefaultHttpClient();
            HttpGet httpGet;
            try {
                httpGet = new HttpGet(Util.getServerUrl(questionnaire, LOGIN_URL_PREFIX));

                setHeaders(httpGet);

                HttpResponse response = httpclient.execute(httpGet);
                HttpEntity httpEntity = response.getEntity();
                String res = EntityUtils.toString(httpEntity);

                if (response.getHeaders("AccountIsLocked").length > 0) {
                    return ACCOUNT_LOCKED;
                }

                if (200 == response.getStatusLine().getStatusCode()) {
                    Log.d(TAG, "response..:" + res);
                    LoginBean loginBean = new Gson().fromJson(res, LoginBean.class);
                    questionnaire.addVariable(new Variable<String>(Util.VARIABLE_REAL_NAME, loginBean.getFullName()));
                    questionnaire.addVariable(new Variable<Long>(Util.VARIABLE_USER_ID, loginBean.getId()));
                    questionnaire.addVariable(new Variable<Boolean>(Util.VARIABLE_CHANGE_PASSWORD, loginBean.getUser()
                            .isChangePassword()));
                    ((MainQuestionnaire) questionnaire).adviceActivityOfUserLogin(loginBean);
                }
                isLoggedIn.setValue(200 == response.getStatusLine().getStatusCode());
            } catch (IOException e) {
                Log.w(TAG, e.getMessage());
                loginListener.sendError();
                e.printStackTrace();

                return "";
            } catch (URISyntaxException e) {
                Log.w(TAG, e.getMessage());
                loginListener.sendError();
                e.printStackTrace();

                return "";
            }
        }

        return isLoggedIn.getExpressionValue().toString();
    }

    @Override
    protected void onPostExecute(String result) {
        if (result.equals(ACCOUNT_LOCKED)) {
            loginListener.accountLocked();
        } else {
            loginListener.login(result);
        }
    }

    private boolean isAdmin() {
        return Util.getStringVariableValue(questionnaire, Util.VARIABLE_USERNAME).equals(Util.ADMINUSER_NAME)
                && Util.getStringVariableValue(questionnaire, Util.VARIABLE_PASSWORD).equals(Util.ADMINUSER_PASS);
    }
}
