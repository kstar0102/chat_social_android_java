package com.codeslutest.plax.models;

public class GalleryModel {
    private int image1, image2, image3;

    public GalleryModel(int image1, int image2, int image3) {
        this.image1 = image1;
        this.image2 = image2;
        this.image3 = image3;
    }

    public int getImage1() {
        return image1;
    }

    public int getImage2() {
        return image2;
    }

    public int getImage3() {
        return image3;
    }
}
