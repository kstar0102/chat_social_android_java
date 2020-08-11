package ar.codeslu.plax.lists;

public class UserDetailsStory {

    String name,ava,id,phone;

    public UserDetailsStory() {
    }

    public UserDetailsStory(String name, String ava, String id, String phone) {
        this.name = name;
        this.ava = ava;
        this.id = id;
        this.phone = phone;
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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
