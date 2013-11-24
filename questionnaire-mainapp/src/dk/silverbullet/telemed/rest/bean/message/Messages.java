package dk.silverbullet.telemed.rest.bean.message;

import com.google.gson.annotations.Expose;

public class Messages {
    @Expose public int unread;
    @Expose public MessageItem[] messages;
}
