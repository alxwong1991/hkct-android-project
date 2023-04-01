package com.hkct.project.Model;

public class Event extends EventId {
    private String eventImage, eventDescription, eventAddress, eventTitle, eventDate, eventTime, user;

    public String getEventImage() {
        return eventImage;
    }

    public String getEventTitle() {
        return eventTitle;
    }

    public String getEventDescription() {
        return eventDescription;
    }

    public String getEventAddress() {
        return eventAddress;
    }

    public String getEventDate() {
        return eventDate;
    }

    public String getEventTime() {
        return eventTime;
    }

    public String getUser() {
        return user;
    }
}
