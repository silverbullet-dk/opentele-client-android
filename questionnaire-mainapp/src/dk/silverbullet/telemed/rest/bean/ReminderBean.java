package dk.silverbullet.telemed.rest.bean;

import com.google.gson.annotations.Expose;

import java.io.Serializable;
import java.util.List;

public class ReminderBean implements Serializable {

    private static final long serialVersionUID = 6582059327386086772L;
    @Expose private long questionnaireId;
    @Expose private String questionnaireName;
    @Expose private List<Long> alarms;

    public void setQuestionnaireName(String questionnaireName) {
        this.questionnaireName = questionnaireName;
    }

    public void setAlarms(List<Long> alarms) {
        this.alarms = alarms;
    }

    public List<Long> getAlarms() {
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
