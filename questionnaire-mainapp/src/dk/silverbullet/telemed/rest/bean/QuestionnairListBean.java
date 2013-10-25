package dk.silverbullet.telemed.rest.bean;

import lombok.Data;

@Data
public class QuestionnairListBean {

    private Long id;
    private String name;
    private String version;
    private String cron;
}
