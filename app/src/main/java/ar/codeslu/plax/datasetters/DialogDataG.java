package ar.codeslu.plax.datasetters;


import android.content.Context;
import android.util.Log;

import com.stfalcon.chatkit.me.Message;
import com.stfalcon.chatkit.me.GroupIn;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import ar.codeslu.plax.R;
import ar.codeslu.plax.global.Global;
import ar.codeslu.plax.global.encryption;
import ar.codeslu.plax.models.GroupDialog;


/**
 * Created by CodeSlu on 29/01/19.
 */


public final class DialogDataG {

    static long timeStamp;

    static ArrayList<GroupIn> userList;

    public DialogDataG() {
        throw new AssertionError();
    }

    public static ArrayList<GroupDialog> getDialogs(Context conn,ArrayList<GroupIn> arrayList) {
        userList = new ArrayList<>();
        userList = arrayList;
        arrange();
        ArrayList<GroupDialog> chats = new ArrayList<>();
        if (chats.size() != 0)
            chats.clear();
        for (int i = 0; i < userList.size(); i++) {
            Calendar calendar = Calendar.getInstance();
            timeStamp = userList.get(i).getMessDate();
            calendar.setTimeInMillis(timeStamp);
            chats.add(getDialog(i, calendar.getTime(),conn));
        }

        return chats;
    }

    //put data in the list
    private static GroupDialog getDialog(int i, Date time,Context context) {

        String ava ="";
        try {
          if  (encryption.decryptOrNull(userList.get(i).getAvatar()) == null)
              ava = userList.get(i).getAvatar();
          else
              ava = encryption.decryptOrNull(userList.get(i).getAvatar());

        }catch (NullPointerException e)
        {
            ava = userList.get(i).getAvatar();
        }
        if (userList.get(i).getNoOfUnread() > 99) {

            return new GroupDialog(
                    userList.get(i).getId(), userList.get(i).getName(), ava, userList, getMessage(i, time,context), 99,userList.get(i).getLastsenderava());

        } else {

            return new GroupDialog(
                    userList.get(i).getId(), userList.get(i).getName(), ava, userList, getMessage(i, time,context), userList.get(i).getNoOfUnread(),userList.get(i).getLastsenderava());

        }
    }

    //get last sender
    private static GroupIn getUser(int i) {
        return new GroupIn(
                userList.get(i).getName(), userList.get(i).getLastsender(), userList.get(i).getLastsenderava());

    }

    //get last message
    private static Message getMessage(int i, final Date date,Context connL) {
        String messageL = encryption.decryptOrNull(userList.get(i).getLastmessage());
        try {
            if (encryption.decryptOrNull(userList.get(i).getLastmessage()).equals("777default099////ar.codeslu.plax//"))
                messageL = connL.getResources().getString(R.string.you_added) +" "+ userList.get(i).getName();
        }catch (NullPointerException e)
        {
            messageL = encryption.decryptOrNull(userList.get(i).getLastmessage());
        }
        return new Message(
                userList.get(i).getLastsender(), getUser(i), messageL, date,Global.avaLocal,false,false,false,"");

    }

    private static void arrange() {
        GroupIn temp;
        for (int i = 0; i < userList.size(); i++) {
            if (i != userList.size() - 1) {
                if (userList.get(i).getMessDate() < userList.get(i + 1).getMessDate()) {
                    temp = userList.get(i);
                    userList.set(i, userList.get(i + 1));
                    userList.set(i + 1, temp);
                    arrange();
                    break;
                }
            }

        }
    }


}