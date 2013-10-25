package dk.silverbullet.telemed.rest.bean;

import java.io.Serializable;
import java.util.List;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class ReminderBean implements Serializable {

    private static final long serialVersionUID = 6582059327386086772L;

    private long questionnaireId;
    private String questionnaireName;
    private List<Integer> alarms;
}
