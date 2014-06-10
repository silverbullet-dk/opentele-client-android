package dk.silverbullet.telemed.questionnaire;

import dk.silverbullet.telemed.questionnaire.expression.Variable;
import dk.silverbullet.telemed.questionnaire.node.*;
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

        Variable<Boolean> isLoggedInAsAdmin = new Variable<Boolean>(Util.VARIABLE_IS_LOGGED_IN_AS_ADMIN, Boolean.class);
        isLoggedInAsAdmin.setValue(false);
        addVariable(isLoggedInAsAdmin);

        // Variables...
        Variable<String> menu = new Variable<String>("menu", "sdfoiausæogiu");
        Variable<String> userName = new Variable<String>(Util.VARIABLE_USERNAME, String.class);
        Variable<String> password = new Variable<String>(Util.VARIABLE_PASSWORD, String.class);
        Variable<String> errorText = new Variable<String>("errorText", String.class);
        Variable<String> messageText = new Variable<String>(Util.VARIABLE_MESSAGE_TEXT, String.class);

        addVariable(menu);
        addVariable(userName);
        addVariable(password);
        addVariable(errorText);
        addVariable(messageText);

        // Nodes...

        PatientMenuNode patientMenuNode = new PatientMenuNode(this, "MENU");

        ChangePasswordNode changePasswordNode = new ChangePasswordNode(this, "CHANGE_PASSWORD");
        changePasswordNode.setHideBackButton(true);
        changePasswordNode.setHideMenuButton(true);
        changePasswordNode.setNext(patientMenuNode);

        LoginNode loginNode = new LoginNode(this, "LOGIN");
        loginNode.setChangePasswordNode(changePasswordNode);

        //If logged in as admin user show admin menu instead of patient menu
        DecisionNode adminLoginDecisionNode = setupAdminMenu(isLoggedInAsAdmin, patientMenuNode);

        DecisionNode decisionNode = new DecisionNode(this, "decisionNode", isLoggedIn);
        decisionNode.setNextNode(adminLoginDecisionNode);
        decisionNode.setNextFalseNode(loginNode);

        loginNode.setNext(decisionNode);

        startNode = new StartNode(this, "START");
        startNode.setNext(decisionNode);

        super.setStartNode(startNode);
    }

    private DecisionNode setupAdminMenu(Variable<Boolean> isLoggedInAsAdmin, PatientMenuNode patientMenuNode) {

        AdminMenuNode adminMenu = new AdminMenuNode(this, "ADMIN_MENU");

        DecisionNode adminLoginDecisionNode = new DecisionNode(this, "adminLoginDecisionNode", isLoggedInAsAdmin);
        adminLoginDecisionNode.setNextFalseNode(patientMenuNode);
        adminLoginDecisionNode.setNextNode(adminMenu);

        return adminLoginDecisionNode;
    }

    public void notifyActivityOfUserLogin() {
        QuestionnaireFragmentContainer parentActivity = (QuestionnaireFragmentContainer) getContext();
        parentActivity.userLoggedIn();
    }

    public void adviceActivityOfUserLogout() {
        QuestionnaireFragmentContainer parentActivity = (QuestionnaireFragmentContainer) getContext();
        parentActivity.userLoggedOut();
    }

    public static MainQuestionnaire getInstance() {
        return instance;
    }

    public IONode getMainMenu() {
        return startNode;
    }
}
