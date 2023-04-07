package com.hkct.project.Model;

public class Event extends EventId {
    private String image, description, location, title, date, time, user;

    public String getUser() {
        return user;
    }

    public String getImage() {
        return image;
    }

    public String getDescription() {
        return description;
    }

    public String getLocation() {
        return location;
    }

    public String getTime() {
        return time;
    }

    public String getTitle() {
        return title;
    }

    public String getDate() {
        return date;
    }
}
