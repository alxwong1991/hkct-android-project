package com.hkct.project.Model;

import java.util.Date;

public class Event extends EventId {
    private String image, user, description, address;
    private Date time;

    public String getUser() {
        return user;
    }

    public String getImage() {
        return image;
    }

    public String getDescription() {
        return description;
    }

    public String getAddress() {
        return address;
    }

    public Date getTime() {
        return time;
    }
}
