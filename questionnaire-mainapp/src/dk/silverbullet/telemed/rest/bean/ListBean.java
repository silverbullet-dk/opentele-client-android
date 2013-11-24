package dk.silverbullet.telemed.rest.bean;

import com.google.gson.annotations.Expose;
import dk.silverbullet.telemed.schedule.bean.QuestionnaireSchedule;

import java.util.List;

public class ListBean {
    @Expose private List<QuestionnaireSchedule> questionnaires;

    public List<QuestionnaireSchedule> getQuestionnaires() {
        return questionnaires;
    }
}


