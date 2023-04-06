package com.hkct.project.Model;

import java.util.Date;

public class Post extends PostId {
    public String image, user, caption;
    public Date time;

    public String getImage() {
        return image;
    }

    public String getUser() {
        return user;
    }

    public String getCaption() {
        return caption;
    }

    public Date getTime() {
        return time;
    }
}
