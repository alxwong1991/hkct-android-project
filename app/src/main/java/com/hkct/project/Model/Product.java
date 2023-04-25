package com.hkct.project.Model;

import java.util.Date;

public class Product extends ProductId {
    private String user, image, name, detail, price;

    public Date time;

    public String getUser() {
        return user;
    }

    public String getImage() {
        return image;
    }

    public String getName() {
        return name;
    }

    public String getPrice() {
        return price;
    }

    public String getDetail() {
        return detail;
    }

    public Date getTime() {
        return time;
    }
}
