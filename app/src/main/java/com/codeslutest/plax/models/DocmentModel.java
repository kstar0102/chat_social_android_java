package com.codeslutest.plax.models;

public class DocmentModel {
    private String title;
    private int image;

    public DocmentModel(String title, int image){
        this.title = title;
        this.image = image;
    }

    public int getImage() {
        return image;
    }

    public String getTitle() {
        return title;
    }
}
