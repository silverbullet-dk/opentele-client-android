package dk.silverbullet.telemed.schedule.bean;

import com.google.gson.annotations.Expose;

import java.io.Serializable;

public class QuestionnaireSchedule implements Serializable {
    private static final long serialVersionUID = 1164198289104699427L;

    @Expose private String name;
    @Expose private String version;
    @Expose private Long id;

    public String getSkemaName() {
        return name + " (" + "ver. " + version + ")";
    }
}
