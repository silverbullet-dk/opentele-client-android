package dk.silverbullet.telemed.rest.bean;

import java.util.List;

import lombok.Data;
import dk.silverbullet.telemed.schedule.bean.QuestionnaireSchedule;

@Data
public class ListBean {
    private List<QuestionnaireSchedule> questionnaires;
}
