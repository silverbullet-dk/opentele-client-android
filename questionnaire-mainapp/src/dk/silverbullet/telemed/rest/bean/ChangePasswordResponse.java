package dk.silverbullet.telemed.rest.bean;

import java.io.Serializable;
import java.util.List;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class ChangePasswordResponse implements Serializable {

    private static final long serialVersionUID = 4113350308594540656L;

    public static final String STATUS_ERROR = "error";

    private String status;
    private List<ChangePasswordError> errors;

    public boolean isError() {
        return null == status || STATUS_ERROR.equals(status) ? true : false;
    }
}
