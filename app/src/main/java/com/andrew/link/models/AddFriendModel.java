package com.andrew.link.models;

public class AddFriendModel {
    private String name, phone, id, ava;

    public AddFriendModel(String id, String name, String phone,  String ava){
        this.name = name;
        this.phone = phone;
        this.id = id;
        this.ava = ava;
    }
    public String getName() {
        return name;
    }

    public String getAva() {
        return ava;
    }

    public String getId() {
        return id;
    }

    public String getPhone() {
        return phone;
    }
}
