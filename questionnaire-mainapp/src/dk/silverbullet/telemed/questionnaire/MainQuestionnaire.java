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
        IOMenuNode2 ioMenuNode2 = new IOMenuNode2(this, "MENU2");
        ioMenuNode2.setMenu(menu);

        IOMenuNode mainMenu = new IOMenuNode(this, "MENU");
        mainMenu.setNextNode(ioMenuNode2);
        mainMenu.setMenu(menu);

        ioMenuNode2.setNextNode(mainMenu);

        ChangePasswordNode changePasswordNode = new ChangePasswordNode(this, "CHANGE_PASSWORD");
        changePasswordNode.setHideBackButton(true);
        changePasswordNode.setHideMenuButton(true);
        changePasswordNode.setNext(mainMenu);

        LoginNode loginNode = new LoginNode(this, "LOGIN");
        loginNode.setChangePasswordNode(changePasswordNode);

        DecisionNode decisionNode = new DecisionNode(this, "decisionNode", isLoggedIn);
        decisionNode.setNextNode(mainMenu);
        decisionNode.setNextFalseNode(loginNode);

        loginNode.setNext(decisionNode);

        startNode = new StartNode(this, "START");
        startNode.setNext(decisionNode);

        super.setStartNode(startNode);
    }

    public void notifyActivityOfUserLogin() {
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
