package com.andrew.link.models;

public class HistoryModel {
    private String name, des, badge, state;
    private int image;

    public HistoryModel(String name, String des, String badge,String state, int image){
        this.name = name;
        this.des = des;
        this.image = image;
        this.badge = badge;
        this.state = state;
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

    public String getBadge() {
        return badge;
    }

    public String getState() {
        return state;
    }
}
