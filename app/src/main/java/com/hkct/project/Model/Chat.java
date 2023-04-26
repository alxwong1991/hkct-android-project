package com.hkct.project.Model;

import java.util.Date;

public class Chat {
    private String message, user;
    private Date timestamp;

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
