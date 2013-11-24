package dk.silverbullet.telemed.rest.bean;

import com.google.gson.annotations.Expose;

public class QuestionnairListBean {
    @Expose private Long id;
    @Expose private String name;
    @Expose private String version;

    public String getName() {
        return name;
    }

    public Long getId() {
        return id;
    }

    public String getVersion() {
        return version;
    }
}
