package dk.silverbullet.telemed.rest.bean.message;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class MessageWrite implements Serializable {

    private static final long serialVersionUID = 5892450544356032404L;

    @SerializedName("id")
    private Long userId;

    @SerializedName("department")
    private Long departmentId;

    private String title;
    private String text;

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public void setDepartmentId(Long departmentId) {
        this.departmentId = departmentId;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setText(String text) {
        this.text = text;
    }
}
