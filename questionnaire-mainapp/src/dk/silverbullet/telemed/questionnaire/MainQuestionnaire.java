package dk.silverbullet.telemed.questionnaire;

import dk.silverbullet.telemed.questionnaire.expression.Variable;
import dk.silverbullet.telemed.questionnaire.node.ChangePasswordNode;
import dk.silverbullet.telemed.questionnaire.node.ChangePasswordNode2;
import dk.silverbullet.telemed.questionnaire.node.DecisionNode;
import dk.silverbullet.telemed.questionnaire.node.IOMenuNode;
import dk.silverbullet.telemed.questionnaire.node.IOMenuNode2;
import dk.silverbullet.telemed.questionnaire.node.IONode;
import dk.silverbullet.telemed.questionnaire.node.LoginNode;
import dk.silverbullet.telemed.questionnaire.node.LoginNode2;
import dk.silverbullet.telemed.questionnaire.node.StartNode;
import dk.silverbullet.telemed.rest.bean.LoginBean;
import dk.silverbullet.telemed.utils.Util;

public class MainQuestionnaire extends Questionnaire {

    private static MainQuestionnaire instance;
    private StartNode startNode;

    public MainQuestionnaire(QuestionnaireFragment fragment) {
        super(fragment);
        createNodes();
        instance = this;
    }

    public void createNodes() {
        Variable<Boolean> isLoggedIn = new Variable<Boolean>(Util.VARIABLE_IS_LOGGED_IN, Boolean.class);
        isLoggedIn.setValue(false);
        addVariable(isLoggedIn);

        // Variables...
        Variable<String> menu = new Variable<String>("menu", "sdfoiausæogiu");
        Variable<String> userName = new Variable<String>(Util.VARIABLE_USERNAME, String.class);
        Variable<String> password = new Variable<String>(Util.VARIABLE_PASSWORD, String.class);
        Variable<Boolean> changePassword = new Variable<Boolean>(Util.VARIABLE_CHANGE_PASSWORD, Boolean.class);
        Variable<String> errorText = new Variable<String>("errorText", String.class);
        Variable<String> messageText = new Variable<String>(Util.VARIABLE_MESSAGE_TEXT, String.class);

        Variable<String> currentPassword = new Variable<String>(Util.VARIABLE_CURRENT_PASSWORD, String.class);
        Variable<String> newPassword = new Variable<String>("newPassword", String.class);
        Variable<String> passwordRepeat = new Variable<String>("passwordRepeat", String.class);
        Variable<String> currentPasswordErrorText = new Variable<String>("currentPasswordErrorText", String.class);
        Variable<String> passwordErrorText = new Variable<String>("passwordErrorText", String.class);
        Variable<String> passwordRepeatErrorText = new Variable<String>("passwordRepeatErrorText", String.class);

        addVariable(menu);
        addVariable(userName);
        addVariable(password);
        addVariable(changePassword);
        addVariable(errorText);
        addVariable(messageText);

        // Nodes...
        IOMenuNode2 ioMenuNode2 = new IOMenuNode2(this, "MENU2");
        ioMenuNode2.setMenu(menu);

        IOMenuNode mainMenu = new IOMenuNode(this, "MENU");
        mainMenu.setNextNode(ioMenuNode2);
        mainMenu.setMenu(menu);

        ioMenuNode2.setNextNode(mainMenu);

        ChangePasswordNode2 changePasswordNode2 = new ChangePasswordNode2(this, "CHANGE_PASSWORD_2");
        changePasswordNode2.setPasswordErrorText(passwordErrorText);
        changePasswordNode2.setCurrentPasswordErrorText(currentPasswordErrorText);
        changePasswordNode2.setPasswordRepeatErrorText(passwordRepeatErrorText);
        changePasswordNode2.setCurrentPassword(currentPassword);
        changePasswordNode2.setPassword(newPassword);
        changePasswordNode2.setPasswordRepeat(passwordRepeat);

        ChangePasswordNode changePasswordNode = new ChangePasswordNode(this, "CHANGE_PASSWORD");
        changePasswordNode.setPasswordErrorText(passwordErrorText);
        changePasswordNode.setCurrentPasswordErrorText(currentPasswordErrorText);
        changePasswordNode.setPasswordRepeatErrorText(passwordRepeatErrorText);
        changePasswordNode.setCurrentPassword(currentPassword);
        changePasswordNode.setPassword(newPassword);
        changePasswordNode.setPasswordRepeat(passwordRepeat);
        changePasswordNode.setNext(changePasswordNode2);

        LoginNode2 loginNode2 = new LoginNode2(this, "LOGIN2");
        loginNode2.setErrorText(errorText);
        loginNode2.setChangePassword(changePasswordNode);
        loginNode2.setPassword(password);
        loginNode2.setCurrentPassword(currentPassword);

        LoginNode loginNode = new LoginNode(this, "LOGIN");
        loginNode.setNext(loginNode2);
        loginNode.setUserName(userName);
        loginNode.setPassword(password);
        loginNode.setErrorText(errorText);
        loginNode.setHideTopPanel(true);

        loginNode2.setNextFail(loginNode);
        changePasswordNode2.setNext(loginNode);
        changePasswordNode2.setNextFail(changePasswordNode);

        DecisionNode decisionNode = new DecisionNode(this, "decisionNode", isLoggedIn);
        decisionNode.setNextNode(mainMenu);
        decisionNode.setNextFalseNode(loginNode);

        loginNode2.setNext(decisionNode);

        startNode = new StartNode(this, "START");
        startNode.setNext(decisionNode);

        super.setStartNode(startNode);
    }

    public void adviceActivityOfUserLogin(LoginBean loginBean) {
        QuestionnaireFragmentContainer parentActivity = (QuestionnaireFragmentContainer) getActivity();
        parentActivity.userLoggedIn();
    }

    public void adviceActivityOfUserLogout() {
        QuestionnaireFragmentContainer parentActivity = (QuestionnaireFragmentContainer) getActivity();
        parentActivity.userLoggedOut();
    }

    public static MainQuestionnaire getInstance() {
        return instance;
    }

    public IONode getMainMenu() {
        return startNode;
    }
}
