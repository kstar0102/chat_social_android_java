package ar.codeslu.plax.lists;

/**
 * Created by mostafa on 28/10/18.
 */

public class UserData  {
    String name,nameL, statue, avatar, phone, lastOn,id;
    boolean onstatue,screen;
    long time,timeC;

    public UserData() {
    }

    public UserData(String id,String ava, String name,String statue, String phone,boolean online,boolean screen) {
        this.nameL = name;
        this.statue = statue;
        this.phone = phone;
        this.id = id;
        this.avatar = ava;
        onstatue = online;
        screen = screen;

    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNameL() {
        return nameL;
    }

    public void setNameL(String nameL) {
        this.nameL = nameL;
    }

    public String getStatue() {
        return statue;
    }

    public void setStatue(String statue) {
        this.statue = statue;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getLastOn() {
        return lastOn;
    }

    public void setLastOn(String lastOn) {
        this.lastOn = lastOn;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public boolean isOnstatue() {
        return onstatue;
    }

    public void setOnstatue(boolean onstatue) {
        onstatue = onstatue;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }
    public long getTimeC() {
        return timeC;
    }

    public void setTimeC(long timeC) {
        this.timeC = timeC;
    }

    public boolean isScreen() {
        return screen;
    }

    public void setScreen(boolean screen) {
        this.screen = screen;
    }
}