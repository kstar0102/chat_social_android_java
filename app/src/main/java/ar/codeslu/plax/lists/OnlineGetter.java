package ar.codeslu.plax.lists;

/**
 * Created by CodeSlu on 04/03/19.
 */

public class OnlineGetter {
    boolean online,deleted;
    String statue;

    public OnlineGetter() {
    }

    public OnlineGetter(boolean onstatue, boolean deleted, String statue) {
        this.online = onstatue;
        this.deleted = deleted;
        this.statue = statue;
    }

    public boolean isOnline() {
        return online;
    }

    public void setOnline(boolean online) {
        this.online = online;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public String getStatue() {
        return statue;
    }

    public void setStatue(String statue) {
        this.statue = statue;
    }
}
