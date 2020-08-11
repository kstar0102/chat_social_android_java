package ar.codeslu.plax.lists;

import java.util.ArrayList;

public class StoryList {

String link,id,name,ava;
long time;


    public StoryList() {
    }

    public StoryList(String link, String id, String name, String ava, long time) {
        this.link = link;
        this.id = id;
        this.name = name;
        this.ava = ava;
        this.time = time;
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

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
