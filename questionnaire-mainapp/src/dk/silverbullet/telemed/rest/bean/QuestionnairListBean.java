package dk.silverbullet.telemed.rest.bean;


public class QuestionnairListBean {
    private Long id;
    private String name;
    private String version;
    private String cron;

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
