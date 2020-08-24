package com.andrew.link.models;

public class MessageModel {
    String Listname, Listdes;
    int ListImage;

    public MessageModel(String Listname,String Listdes,int ListImage) {
        this.ListImage=ListImage;
        this.Listname=Listname;
        this.Listdes=Listdes;
    }

    public String getListname()
    {
        return Listname;
    }

    public String getListdes()
    {
        return Listdes;
    }

    public int getListImage()
    {
        return ListImage;
    }
}
