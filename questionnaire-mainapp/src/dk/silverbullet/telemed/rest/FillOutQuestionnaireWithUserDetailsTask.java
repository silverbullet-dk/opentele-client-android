package dk.silverbullet.telemed.rest;

import android.os.AsyncTask;
import android.util.Log;
import dk.silverbullet.telemed.questionnaire.MainQuestionnaire;
import dk.silverbullet.telemed.questionnaire.Questionnaire;
import dk.silverbullet.telemed.questionnaire.expression.Variable;
import dk.silverbullet.telemed.rest.bean.LoginBean;
import dk.silverbullet.telemed.rest.listener.LoginListener;
import dk.silverbullet.telemed.rest.httpclient.HttpClientFactory;
import dk.silverbullet.telemed.utils.Json;
import dk.silverbullet.telemed.utils.Util;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.net.URISyntaxException;

public class FillOutQuestionnaireWithUserDetailsTask extends AsyncTask<String, Void, FillOutQuestionnaireWithUserDetailsTask.LoginResult> {
    private Questionnaire questionnaire;
    private static final String LOGIN_URL_PREFIX = "rest/patient/login";
    private static final String TAG = Util.getTag(FillOutQuestionnaireWithUserDetailsTask.class);
    private final LoginListener loginListener;
    Variable<Boolean> isLoggedIn;
    Variable<Boolean> isLoggedInAsAdmin;

    static enum LoginResult {ACCOUNT_LOCKED, SUCCESS, WRONG_PASSWORD, FAILED, SUCCESS_CHANGE_PASSWORD}

    public FillOutQuestionnaireWithUserDetailsTask(Questionnaire questionnaire, LoginListener loginListener) {
        this.questionnaire = questionnaire;
        this.loginListener = loginListener;
        isLoggedIn = (Variable<Boolean>) questionnaire.getValuePool().get(Util.VARIABLE_IS_LOGGED_IN);
        isLoggedInAsAdmin = (Variable<Boolean>) questionnaire.getValuePool().get(Util.VARIABLE_IS_LOGGED_IN_AS_ADMIN);

    }

    @Override
    protected LoginResult doInBackground(String... params) {
        Log.d(TAG, "Logging in....");
        addUsernameAndPasswordVariables(params);

        if (isAdmin()) {
            isLoggedIn.setValue(true);
            isLoggedInAsAdmin.setValue(true);
            return LoginResult.SUCCESS;
        } else {
            HttpClient httpClient = HttpClientFactory.createHttpClient(questionnaire.getActivity());
            HttpGet httpGet;

            try {
                httpGet = new HttpGet(Util.getServerUrl(questionnaire, LOGIN_URL_PREFIX));

                Util.setHeaders(httpGet, questionnaire);

                HttpResponse response = httpClient.execute(httpGet);
                HttpEntity httpEntity = response.getEntity();
                String res = EntityUtils.toString(httpEntity);

                if (accountIsLocked(response)) {
                    return LoginResult.ACCOUNT_LOCKED;
                }

                if (loginSucceeded(response)) {
                    LoginBean loginBean = Json.parse(res, LoginBean.class);

                    questionnaire.addVariable(new Variable<String>(Util.VARIABLE_REAL_NAME, loginBean.getFullName()));
                    questionnaire.addVariable(new Variable<Long>(Util.VARIABLE_USER_ID, loginBean.getId()));

                    ((MainQuestionnaire) questionnaire).notifyActivityOfUserLogin();

                    if(mustChangePassword(loginBean)) {
                        return LoginResult.SUCCESS_CHANGE_PASSWORD;
                    }

                    return LoginResult.SUCCESS;
                } else if(badCredentials(response)) {
                    return LoginResult.WRONG_PASSWORD;
                } else {
                    Log.w(TAG, "Got status code:" + response.getStatusLine().getStatusCode() + ", reason:" + response.getStatusLine().getReasonPhrase());
                    return LoginResult.FAILED;
                }

            } catch (IOException e) {
                Log.w(TAG, e);
                return LoginResult.FAILED;
            } catch (URISyntaxException e) {
                Log.w(TAG, e);
                return LoginResult.FAILED;
            }
        }
    }

    private void addUsernameAndPasswordVariables(String[] params) {
        questionnaire.addVariable(new Variable<String>(Util.VARIABLE_USERNAME, params[0]));
        questionnaire.addVariable(new Variable<String>(Util.VARIABLE_PASSWORD, params[1]));
    }

    @Override
    protected void onPostExecute(LoginResult result) {
        switch (result) {
            case ACCOUNT_LOCKED:
                isLoggedIn.setValue(false);
                loginListener.accountLocked();
                break;
            case SUCCESS:
                isLoggedIn.setValue(true);
                loginListener.loggedIn();
                break;
            case WRONG_PASSWORD:
                isLoggedIn.setValue(false);
                loginListener.loginFailed();
                break;
            case FAILED:
                isLoggedIn.setValue(false);
                loginListener.sendError();
                break;
            case SUCCESS_CHANGE_PASSWORD:
                isLoggedIn.setValue(true);
                loginListener.changePassword();
                break;
            default:
                throw new RuntimeException("Unknown loggedIn result" + result);
        }
    }

    private boolean isAdmin() {
        return Util.getStringVariableValue(questionnaire, Util.VARIABLE_USERNAME).equals(Util.ADMINUSER_NAME) && Util.getStringVariableValue(questionnaire, Util.VARIABLE_PASSWORD).equals(Util.ADMINUSER_PASS);
    }

    private boolean mustChangePassword(LoginBean loginBean) {
        return loginBean.getUser().isChangePassword();
    }

    private boolean badCredentials(HttpResponse response) {
        return response.getStatusLine().getStatusCode() == 401;
    }

    private boolean loginSucceeded(HttpResponse response) {
        return response.getStatusLine().getStatusCode() == 200;
    }

    private boolean accountIsLocked(HttpResponse response) {
        return response.getHeaders("AccountIsLocked").length > 0;
    }
}
