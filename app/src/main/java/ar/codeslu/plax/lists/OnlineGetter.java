package ar.codeslu.plax.lists;

/**
 * Created by mostafa on 04/03/19.
 */

public class OnlineGetter {
    boolean onstatue,deleted;
    String statue;

    public OnlineGetter() {
    }

    public OnlineGetter(boolean onstatue, boolean deleted, String statue) {
        onstatue = onstatue;
        this.deleted = deleted;
        this.statue = statue;
    }

    public boolean isOnstatue() {
        return onstatue;
    }

    public void setOnstatue(boolean onstatue) {
        onstatue = onstatue;
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
