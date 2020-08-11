package ar.codeslu.plax.settings;

/**
 * Created by CodeSlu on 22/03/19.
 */

public class SettingList {
    private String itemName;
    private int itemPhoto;

    public SettingList() {
    }

    public SettingList(String itemName, int itemPhoto) {
        this.itemName = itemName;
        this.itemPhoto = itemPhoto;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public int getItemPhoto() {
        return itemPhoto;
    }

    public void setItemPhoto(int itemPhoto) {
        this.itemPhoto = itemPhoto;
    }
}
