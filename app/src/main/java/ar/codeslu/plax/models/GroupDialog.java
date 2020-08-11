package ar.codeslu.plax.models;

import com.stfalcon.chatkit.commons.models.IDialogG;
import com.stfalcon.chatkit.me.Message;
import com.stfalcon.chatkit.me.GroupIn;

import java.util.ArrayList;

/**
 * Created by CodeSlu on 24/01/19.
 */
public class GroupDialog implements IDialogG<Message> {

    private String id;
    private String dialogPhoto;
    private String dialogName;
    private ArrayList<GroupIn> users;
    private Message lastMessage;
    private int unreadCount;
    private String lastSenderAva;

    public GroupDialog(String id, String name, String photo,
                       ArrayList<GroupIn> users, Message lastMessageP, int unreadCount,String lastSenderAva) {

        this.id = id;
        this.dialogName = name;
        this.dialogPhoto = photo;
        this.users = users;
        this.unreadCount = unreadCount;
        this.lastMessage = lastMessageP;
        this.lastSenderAva = lastSenderAva;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getLastSenderAva() {
        return lastSenderAva;
    }

    @Override
    public String getDialogPhoto() {
        return dialogPhoto;
    }


    @Override
    public String getDialogName() {
        return dialogName;
    }

    @Override
    public ArrayList<GroupIn> getUsers() {
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

}