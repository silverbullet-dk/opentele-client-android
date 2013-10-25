package dk.silverbullet.telemed.questionnaire;

public interface QuestionnaireFragmentContainer {

    void questionnaireCreated(Questionnaire questionnaire);

    void userLoggedIn();

    void userLoggedOut();
}
