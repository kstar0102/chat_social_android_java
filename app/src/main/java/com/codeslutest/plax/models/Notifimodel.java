package com.codeslutest.plax.models;

public class Notifimodel {
    private String name, des, day;
    private int image;

    public Notifimodel(String name, String des, String day, int image){
        this.name = name;
        this.des = des;
        this.image = image;
        this.day = day;
    }

    public int getImage() {
        return image;
    }

    public String getDes() {
        return des;
    }

    public String getName() {
        return name;
    }

    public String getDay() {
        return day;
    }
}
