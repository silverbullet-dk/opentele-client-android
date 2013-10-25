package dk.silverbullet.telemed.rest.bean.message;

import java.io.Serializable;

import lombok.Data;
import lombok.ToString;

import com.google.gson.annotations.SerializedName;

@Data
@ToString
public class MessageWrite implements Serializable {

    private static final long serialVersionUID = 5892450544356032404L;

    @SerializedName("id")
    private Long userId;

    @SerializedName("department")
    private Long departmentId;

    private String title;
    private String text;
}
