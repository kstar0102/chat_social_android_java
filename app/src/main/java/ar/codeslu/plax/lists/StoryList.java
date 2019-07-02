package ar.codeslu.plax.lists;

import java.util.ArrayList;

public class StoryList {

    String name,ava;
    long date;
    int count;
    ArrayList<String> links;

    public StoryList() {
    }

    public StoryList(String name, String ava, long date, int count, ArrayList<String> links) {
        this.name = name;
        this.ava = ava;
        this.date = date;
        this.count = count;
        this.links = links;
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

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public ArrayList<String> getLinks() {
        return links;
    }

    public void setLinks(ArrayList<String> links) {
        this.links = links;
    }
}
