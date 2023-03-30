package com.hkct.project.Model;

import com.google.type.DateTime;

import java.util.Date;

public class Event extends EventId {
    private String eventImage, eventUser, eventDescription, eventAddress, eventTitle;
    private Date eventDate;
    private DateTime eventTime;

    public String getEventUser() {
        return eventUser;
    }

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

    public Date getEventDate() {
        return eventDate;
    }

    public DateTime getEventTime() {
        return eventTime;
    }
}
