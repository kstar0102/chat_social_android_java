package ar.codeslu.plax.lists;

import java.util.ArrayList;

import xute.storyview.StoryModel;

public class StoryListRetr {

    ArrayList<StoryModel> listS;
    String name,ava,UID;
    long lastTime;

    public StoryListRetr() {
    }

    public StoryListRetr(ArrayList<StoryModel> listS, String name, String ava, String UID, long lastTime) {
        this.listS = listS;
        this.name = name;
        this.ava = ava;
        this.UID = UID;
        this.lastTime = lastTime;
    }

    public ArrayList<StoryModel> getListS() {
        return listS;
    }

    public void setListS(ArrayList<StoryModel> listS) {
        this.listS = listS;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAva() {
        return ava;
    }

    public void setAva(String ava) {
        this.ava = ava;
    }

    public String getUID() {
        return UID;
    }

    public void setUID(String lastID) {
        this.UID = lastID;
    }

    public long getLastTime() {
        return lastTime;
    }

    public void setLastTime(long lastTime) {
        this.lastTime = lastTime;
    }
}
