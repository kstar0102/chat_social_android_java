package ar.codeslu.plax.lists;

import java.util.ArrayList;

public class StoryList {

String link,id;
long time;


    public StoryList() {
    }

    public StoryList(String link, String id, long time) {
        this.link = link;
        this.id = id;
        this.time = time;
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
