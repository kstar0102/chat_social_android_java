package com.stfalcon.chatkit.me;

import com.stfalcon.chatkit.commons.models.IUser;

import java.util.ArrayList;

/**
 * Created by mostafa on 28/10/18.
 */

public class GroupIn implements IUser {
    String  name, avatar,id,lastmessage,lastsender,lastsenderava,whoT,whoR;
    private ArrayList<String> users,admins;
    boolean typing,audio;
    long messDate,created;
    int noOfUnread;

    public GroupIn() {
    }

    public GroupIn(String name, String lastsender, String lastsenderava) {
        this.name = name;
        this.lastsender = lastsender;
        this.lastsenderava = lastsenderava;
    }

    public GroupIn(String name, String avatar, String id, String lastmessage, String lastsender, String lastsenderava, long messDate, int noOfUnread) {
        this.name = name;
        this.avatar = avatar;
        this.id = id;
        this.lastmessage = lastmessage;
        this.lastsender = lastsender;
        this.lastsenderava = lastsenderava;
        this.messDate = messDate;
        this.noOfUnread = noOfUnread;
    }

    public GroupIn(String name, String avatar, String id, String lastmessage, String lastsender, String lastsenderava, String whoT, String whoR, ArrayList<String> users, ArrayList<String> admins, boolean typing, boolean audio, long messDate, long created, int noOfUnread) {
        this.name = name;
        this.avatar = avatar;
        this.id = id;
        this.lastmessage = lastmessage;
        this.lastsender = lastsender;
        this.lastsenderava = lastsenderava;
        this.whoT = whoT;
        this.whoR = whoR;
        this.users = users;
        this.admins = admins;
        this.typing = typing;
        this.audio = audio;
        this.messDate = messDate;
        this.created = created;
        this.noOfUnread = noOfUnread;
    }

    public String getWhoT() {
        return whoT;
    }

    public void setWhoT(String whoT) {
        this.whoT = whoT;
    }

    public String getWhoR() {
        return whoR;
    }

    public void setWhoR(String whoR) {
        this.whoR = whoR;
    }

    public long getCreated() {
        return created;
    }

    public void setCreated(long created) {
        this.created = created;
    }

    public ArrayList<String> getAdmins() {
        return admins;
    }

    public void setAdmins(ArrayList<String> admins) {
        this.admins = admins;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLastmessage() {
        return lastmessage;
    }

    public void setLastmessage(String lastmessage) {
        this.lastmessage = lastmessage;
    }

    public String getLastsender() {
        return lastsender;
    }

    public void setLastsender(String lastsender) {
        this.lastsender = lastsender;
    }

    public String getLastsenderava() {
        return lastsenderava;
    }

    public void setLastsenderava(String lastsenderava) {
        this.lastsenderava = lastsenderava;
    }

    public ArrayList<String> getUsers() {
        return users;
    }

    public void setUsers(ArrayList<String> users) {
        this.users = users;
    }

    public boolean isTyping() {
        return typing;
    }

    public void setTyping(boolean typing) {
        this.typing = typing;
    }

    public boolean isAudio() {
        return audio;
    }

    public void setAudio(boolean audio) {
        this.audio = audio;
    }

    public long getMessDate() {
        return messDate;
    }

    public void setMessDate(long messDate) {
        this.messDate = messDate;
    }

    public int getNoOfUnread() {
        return noOfUnread;
    }

    public void setNoOfUnread(int noOfUnread) {
        this.noOfUnread = noOfUnread;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getAvatar() {
        return avatar;
    }
}