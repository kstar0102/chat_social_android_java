package ar.codeslu.plax.lists;

import java.util.ArrayList;

public class StoryList {

String link;
long time;

    public StoryList() {
    }

    public StoryList(String link, long time) {
        this.link = link;
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
}
