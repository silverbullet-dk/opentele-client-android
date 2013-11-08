package dk.silverbullet.telemed.rest.bean;

import java.io.Serializable;
import java.util.List;

public class ReminderBean implements Serializable {

    private static final long serialVersionUID = 6582059327386086772L;
    private long questionnaireId;
    private String questionnaireName;
    private List<Integer> alarms;

    public void setQuestionnaireName(String questionnaireName) {
        this.questionnaireName = questionnaireName;
    }

    public void setAlarms(List<Integer> alarms) {
        this.alarms = alarms;
    }

    public List<Integer> getAlarms() {
        return alarms;
    }

    public String getQuestionnaireName() {
        return questionnaireName;
    }

    public long getQuestionnaireId() {
        return questionnaireId;
    }

    public void setQuestionnaireId(long questionnaireId) {
        this.questionnaireId = questionnaireId;
    }
}
