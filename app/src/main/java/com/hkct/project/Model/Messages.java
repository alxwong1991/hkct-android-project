package com.hkct.project.Model;

import java.util.Date;

public class Messages {
    private String message, user, messageId;
    private Date timestamp;

    public String getMessageId() {
        return messageId;
    }

    public String getUser() {
        return user;
    }

    public String getMessage() {
        return message;
    }

    public Date getTimestamp() {
        return timestamp;
    }
}
