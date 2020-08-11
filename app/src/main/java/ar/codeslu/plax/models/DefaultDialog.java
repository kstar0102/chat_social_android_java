package ar.codeslu.plax.models;

import com.stfalcon.chatkit.commons.models.IDialog;
import com.stfalcon.chatkit.me.Message;
import com.stfalcon.chatkit.me.UserIn;

import java.util.ArrayList;

/**
 * Created by CodeSlu on 24/01/19.
 */
public class DefaultDialog implements IDialog<Message> {

    private String id;
    private String dialogPhoto;
    private String dialogName;
    private String dialogPhone;
    private boolean dialogscreen;
    private ArrayList<UserIn> users;
    private Message lastMessage;
    private int unreadCount;

    public DefaultDialog(String id, String name, String photo,
                         ArrayList<UserIn> users,Message lastMessageP, int unreadCount,String dialogPhone,boolean screen) {

        this.id = id;
        this.dialogName = name;
        this.dialogPhoto = photo;
        this.users = users;
        this.unreadCount = unreadCount;
        this.lastMessage = lastMessageP;
        this.dialogPhone = dialogPhone;
        this.dialogscreen = screen;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getDialogPhoto() {
        return dialogPhoto;
    }

    @Override
    public String getDialogPhone() {
        return dialogPhone;
    }

    @Override
    public String getDialogName() {
        return dialogName;
    }

    @Override
    public ArrayList<UserIn> getUsers() {
        return users;
    }

    @Override
    public Message getLastMessage() {
        return lastMessage;
    }

    @Override
    public void setLastMessage(Message lastMessage) {
        this.lastMessage = lastMessage;
    }

    @Override
    public int getUnreadCount() {
        return unreadCount;
    }

    public void setUnreadCount(int unreadCount) {
        this.unreadCount = unreadCount;
    }

    public boolean isDialogscreen() {
        return dialogscreen;
    }

    public void setDialogscreen(boolean dialogscreen) {
        this.dialogscreen = dialogscreen;
    }
}