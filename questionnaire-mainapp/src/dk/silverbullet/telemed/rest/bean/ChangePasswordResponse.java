package dk.silverbullet.telemed.rest.bean;

import java.io.Serializable;
import java.util.List;

public class ChangePasswordResponse implements Serializable {
    private static final long serialVersionUID = 4113350308594540656L;
    private static final String STATUS_ERROR = "error";

    private String status;
    private List<ChangePasswordError> errors;

    public boolean isError() {
        return status == null || STATUS_ERROR.equals(status);
    }

    public List<ChangePasswordError> getErrors() {
        return errors;
    }
}
