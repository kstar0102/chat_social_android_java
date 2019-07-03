package ar.codeslu.plax.lists;

import java.util.ArrayList;

import xute.storyview.StoryModel;

public class StoryListRetr {

    ArrayList<StoryModel> listS;
    String name,ava;

    public StoryListRetr() {
    }

    public StoryListRetr(ArrayList<StoryModel> listS, String name, String ava) {
        this.listS = listS;
        this.name = name;
        this.ava = ava;
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
}
