package com.hkct.project.Model;

import java.util.Date;

public class Notification extends NotificationId {
    private String title, type, user, content, reference;

    private Date time;

    public String getTitle() {
        return title;
    }

    public String getType() {
        return type;
    }

    public String getUser() {
        return user;
    }

    public Date getTime() {
        return time;
    }

    public String getContent() {
        return content;
    }

    public String getReference() {
        return reference;
    }
}
