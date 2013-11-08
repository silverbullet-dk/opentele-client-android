package dk.silverbullet.telemed.schedule.bean;

import java.io.Serializable;

public class QuestionnaireSchedule implements Serializable {
    private static final long serialVersionUID = 1164198289104699427L;

    private String name;
    private String version;
    private Long id;

    public String getSkemaName() {
        return name + " (ver. " + version + ")";
    }
}
