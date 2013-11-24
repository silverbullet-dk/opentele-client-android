package dk.silverbullet.telemed.rest.bean.acknowledgement;

import com.google.gson.annotations.Expose;

public class Acknowledgement {

    @Expose private Long id;
    @Expose private String message;

    public String getMessage() {
        return message;
    }

    public Long getId() {
        return id;
    }
}