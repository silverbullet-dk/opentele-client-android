package dk.silverbullet.telemed.rest.tasks;

import android.os.AsyncTask;
import android.util.Log;
import dk.silverbullet.telemed.questionnaire.MainQuestionnaire;
import dk.silverbullet.telemed.questionnaire.Questionnaire;
import dk.silverbullet.telemed.questionnaire.expression.Variable;
import dk.silverbullet.telemed.rest.bean.LoginBean;
import dk.silverbullet.telemed.rest.client.RestClient;
import dk.silverbullet.telemed.rest.client.RestException;
import dk.silverbullet.telemed.rest.client.WrongHttpStatusCodeException;
import dk.silverbullet.telemed.rest.listener.LoginListener;
import dk.silverbullet.telemed.utils.Util;
import org.apache.http.HttpResponse;

public class FillOutQuestionnaireWithUserDetailsTask extends AsyncTask<String, Void, FillOutQuestionnaireWithUserDetailsTask.LoginResult> {
    private Questionnaire questionnaire;
    private static final String LOGIN_PATH = "rest/patient/login";
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
            try {
                LoginBean loginBean = RestClient.getJson(questionnaire, LOGIN_PATH, LoginBean.class);

                questionnaire.addVariable(new Variable<String>(Util.VARIABLE_REAL_NAME, loginBean.getFullName()));
                questionnaire.addVariable(new Variable<Long>(Util.VARIABLE_USER_ID, loginBean.getId()));

                ((MainQuestionnaire) questionnaire).notifyActivityOfUserLogin();

                if(mustChangePassword(loginBean)) {
                    return LoginResult.SUCCESS_CHANGE_PASSWORD;
                }

                return LoginResult.SUCCESS;
            } catch (WrongHttpStatusCodeException e) {
                if (accountIsLocked(e.getResponse())) {
                    return LoginResult.ACCOUNT_LOCKED;
                }
                if (badCredentials(e.getResponse())) {
                    return LoginResult.WRONG_PASSWORD;
                }
                Log.w(TAG, "Unknown status code:" + e.getStatusCode() + ", reason:" + e.getReason());
                return LoginResult.FAILED;
            } catch (RestException e) {
                Log.e(TAG, "Could not log in", e);
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

    private boolean accountIsLocked(HttpResponse response) {
        return response.getHeaders("AccountIsLocked").length > 0;
    }
}
