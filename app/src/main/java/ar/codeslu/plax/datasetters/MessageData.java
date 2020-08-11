package ar.codeslu.plax.datasetters;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import ar.codeslu.plax.global.Global;
import ar.codeslu.plax.global.encryption;

import com.stfalcon.chatkit.me.Message;
import com.stfalcon.chatkit.me.MessageIn;
import com.stfalcon.chatkit.me.UserIn;



import static ar.codeslu.plax.datasetters.DialogData.timeStamp;
import static ar.codeslu.plax.datasetters.DialogData.userList;

/**
 * Created by CodeSlu on 30/01/19.
 */


public class MessageData {

    static ArrayList<MessageIn> messagelist;
    static ArrayList<Message> chats;
    DatabaseReference mData;



    private MessageData() {
        throw new AssertionError();
    }


    public static Message getImageMessage(String link, String id, int i, Date creat, String type, boolean deleted,String ava,boolean chat) {
        Message message = new Message(id, getUser(messagelist.get(i).getFrom()), link, creat, messagelist.get(i).getStatue(), type, messagelist.get(i).getMessId(), messagelist.get(i).isDeleted(),messagelist.get(i).getReact(),ava,chat,messagelist.get(i).isForw(),messagelist.get(i).isCall(), encryption.decryptOrNull(messagelist.get(i).getReply()));
        message.setImage(new Message.Image(link));
        return message;
    }

    public static Message getVoiceMessage(String link, String duration, String id, int i, Date creat, String type, boolean deleted,String ava,boolean chat) {
        Message message = new Message(id, getUser(messagelist.get(i).getFrom()), link, creat, messagelist.get(i).getStatue(), type, messagelist.get(i).getMessId(), messagelist.get(i).isDeleted(),messagelist.get(i).getReact(),ava,chat,messagelist.get(i).isForw(),messagelist.get(i).isCall(),encryption.decryptOrNull(messagelist.get(i).getReply()));
        message.setVoice(new Message.Voice(link, duration));
        return message;
    }

    public static Message getFileMessage(String link, String filename, String id, int i, Date creat, String type, boolean deleted,String ava,boolean chat) {
        Message message = new Message(id, getUser(messagelist.get(i).getFrom()), link, creat, messagelist.get(i).getStatue(), type, messagelist.get(i).getMessId(), messagelist.get(i).isDeleted(),messagelist.get(i).getReact(),ava,chat,messagelist.get(i).isForw(),messagelist.get(i).isCall(),encryption.decryptOrNull(messagelist.get(i).getReply()));
        message.setFile(new Message.File(link, filename));
        return message;
    }

    public static Message getVideoMessage(String link, String duration, String id, int i, Date creat, String type, String thumb, boolean deleted,String ava,boolean chat) {
        Message message = new Message(id, getUser(messagelist.get(i).getFrom()), link, creat, messagelist.get(i).getStatue(), type, messagelist.get(i).getMessId(), thumb, messagelist.get(i).isDeleted(),messagelist.get(i).getReact(),ava,chat,messagelist.get(i).isForw(),messagelist.get(i).isCall(),encryption.decryptOrNull(messagelist.get(i).getReply()));
        message.setVideo(new Message.Video(link, duration, thumb));
        return message;
    }

    public static Message getTextMessage(String text, String id, int i, Date creat, String type, boolean deleted,String ava,boolean chat) {
        return new Message(id, getUser(messagelist.get(i).getFrom()), text, creat, messagelist.get(i).getStatue(), type, messagelist.get(i).getMessId(), messagelist.get(i).isDeleted(),messagelist.get(i).getReact(),ava,chat,messagelist.get(i).isForw(),messagelist.get(i).isCall(),encryption.decryptOrNull(messagelist.get(i).getReply()));
    }

    public static Message getLocationMessage(String location, String id, int i, Date creat, String type, boolean deleted,String ava,boolean chat) {
        Message message = new Message(id, getUser(messagelist.get(i).getFrom()), location, creat, messagelist.get(i).getStatue(), type, messagelist.get(i).getMessId(), messagelist.get(i).isDeleted(),messagelist.get(i).getReact(),ava,chat,messagelist.get(i).isForw(),messagelist.get(i).isCall(),encryption.decryptOrNull(messagelist.get(i).getReply()));
        message.setMap(new Message.Map(location));
        return message;
    }

    public static ArrayList<Message> getMessages() {
        messagelist = new ArrayList<>();
        messagelist.clear();
        messagelist = Global.messG;
        ArrayList<Message> chats = new ArrayList<>();
        if (chats.size() != 0)
            chats.clear();
        for (int i = 0; i < messagelist.size(); i++) {
            Calendar calendar = Calendar.getInstance();
            timeStamp = messagelist.get(i).getTime();
            calendar.setTimeInMillis(timeStamp);
            if (messagelist.get(i).getType().equals("text")) {
                chats.add(getTextMessage(encryption.decryptOrNull(messagelist.get(i).getMessage()), messagelist.get(i).getFrom(), i, calendar.getTime(), messagelist.get(i).getType(), messagelist.get(i).isDeleted(),encryption.decryptOrNull(messagelist.get(i).getAvatar()),messagelist.get(i).isChat()));
            } else if (messagelist.get(i).getType().equals("image")) {
                chats.add(getImageMessage(encryption.decryptOrNull(messagelist.get(i).getLinkI()), messagelist.get(i).getFrom(), i, calendar.getTime(), messagelist.get(i).getType(), messagelist.get(i).isDeleted(),encryption.decryptOrNull(messagelist.get(i).getAvatar()),messagelist.get(i).isChat()));
            } else if (messagelist.get(i).getType().equals("voice")) {
                chats.add(getVoiceMessage(encryption.decryptOrNull(messagelist.get(i).getLinkV()), messagelist.get(i).getDuration(), messagelist.get(i).getFrom(), i, calendar.getTime(), messagelist.get(i).getType(), messagelist.get(i).isDeleted(),encryption.decryptOrNull(messagelist.get(i).getAvatar()),messagelist.get(i).isChat()));
            } else if (messagelist.get(i).getType().equals("file")) {
                chats.add(getFileMessage(encryption.decryptOrNull(messagelist.get(i).getLinkF()), messagelist.get(i).getFilename(), messagelist.get(i).getFrom(), i, calendar.getTime(), messagelist.get(i).getType(), messagelist.get(i).isDeleted(),encryption.decryptOrNull(messagelist.get(i).getAvatar()),messagelist.get(i).isChat()));
            } else if (messagelist.get(i).getType().equals("video")) {
                chats.add(getVideoMessage(encryption.decryptOrNull(messagelist.get(i).getLinkVideo()), messagelist.get(i).getDuration(), messagelist.get(i).getFrom(), i, calendar.getTime(), messagelist.get(i).getType(), messagelist.get(i).getThumb(), messagelist.get(i).isDeleted(),encryption.decryptOrNull(messagelist.get(i).getAvatar()),messagelist.get(i).isChat()));
            } else if (messagelist.get(i).getType().equals("map")) {
                chats.add(getLocationMessage(encryption.decryptOrNull(messagelist.get(i).getLocation()), messagelist.get(i).getFrom(), i, calendar.getTime(), messagelist.get(i).getType(), messagelist.get(i).isDeleted(),encryption.decryptOrNull(messagelist.get(i).getAvatar()),messagelist.get(i).isChat()));
            }
        }
        return chats;
    }

    public static ArrayList<Message> getMessagesG() {
        messagelist = new ArrayList<>();
        messagelist.clear();
        messagelist = Global.messGGG;

        ArrayList<Message> chats = new ArrayList<>();
        if (chats.size() != 0)
            chats.clear();
        for (int i = 0; i < messagelist.size(); i++) {
            Calendar calendar = Calendar.getInstance();
            timeStamp = messagelist.get(i).getTime();
            calendar.setTimeInMillis(timeStamp);
            if (messagelist.get(i).getType().equals("text")) {
                chats.add(getTextMessage(encryption.decryptOrNull(messagelist.get(i).getMessage()), messagelist.get(i).getFrom(), i, calendar.getTime(), messagelist.get(i).getType(), messagelist.get(i).isDeleted(),encryption.decryptOrNull(messagelist.get(i).getAvatar()),messagelist.get(i).isChat()));
            } else if (messagelist.get(i).getType().equals("image")) {
                chats.add(getImageMessage(encryption.decryptOrNull(messagelist.get(i).getLinkI()), messagelist.get(i).getFrom(), i, calendar.getTime(), messagelist.get(i).getType(), messagelist.get(i).isDeleted(),encryption.decryptOrNull(messagelist.get(i).getAvatar()),messagelist.get(i).isChat()));
            } else if (messagelist.get(i).getType().equals("voice")) {
                chats.add(getVoiceMessage(encryption.decryptOrNull(messagelist.get(i).getLinkV()), messagelist.get(i).getDuration(), messagelist.get(i).getFrom(), i, calendar.getTime(), messagelist.get(i).getType(), messagelist.get(i).isDeleted(),encryption.decryptOrNull(messagelist.get(i).getAvatar()),messagelist.get(i).isChat()));
            } else if (messagelist.get(i).getType().equals("file")) {
                chats.add(getFileMessage(encryption.decryptOrNull(messagelist.get(i).getLinkF()), messagelist.get(i).getFilename(), messagelist.get(i).getFrom(), i, calendar.getTime(), messagelist.get(i).getType(), messagelist.get(i).isDeleted(),encryption.decryptOrNull(messagelist.get(i).getAvatar()),messagelist.get(i).isChat()));
            } else if (messagelist.get(i).getType().equals("video")) {
                chats.add(getVideoMessage(encryption.decryptOrNull(messagelist.get(i).getLinkVideo()), messagelist.get(i).getDuration(), messagelist.get(i).getFrom(), i, calendar.getTime(), messagelist.get(i).getType(), messagelist.get(i).getThumb(), messagelist.get(i).isDeleted(),encryption.decryptOrNull(messagelist.get(i).getAvatar()),messagelist.get(i).isChat()));
            } else if (messagelist.get(i).getType().equals("map")) {
                chats.add(getLocationMessage(encryption.decryptOrNull(messagelist.get(i).getLocation()), messagelist.get(i).getFrom(), i, calendar.getTime(), messagelist.get(i).getType(), messagelist.get(i).isDeleted(),encryption.decryptOrNull(messagelist.get(i).getAvatar()),messagelist.get(i).isChat()));
            }
        }
        return chats;
    }

    public static Message getMessagesSingle(MessageIn mess) {

        Calendar calendar = Calendar.getInstance();
        timeStamp = mess.getTime();
        calendar.setTimeInMillis(timeStamp);
        if (mess.getType().equals("text")) {
            return getTextMessage(encryption.decryptOrNull(mess.getMessage()), mess.getFrom(), 0, calendar.getTime(), mess.getType(), mess.isDeleted(),encryption.decryptOrNull(mess.getAvatar()),mess.isChat());
        } else if (mess.getType().equals("image")) {
            return getImageMessage(encryption.decryptOrNull(mess.getLinkI()), mess.getFrom(), 0, calendar.getTime(), mess.getType(), mess.isDeleted(),encryption.decryptOrNull(mess.getAvatar()),mess.isChat());
        } else if ( mess.getType().equals("voice")) {
            return getVoiceMessage(encryption.decryptOrNull(mess.getLinkV()), mess.getDuration(), mess.getFrom(), 0, calendar.getTime(), mess.getType(), mess.isDeleted(),encryption.decryptOrNull(mess.getAvatar()),mess.isChat());
        } else if (mess.getType().equals("file")) {
            return getFileMessage(encryption.decryptOrNull(mess.getLinkF()), mess.getFilename(), mess.getFrom(), 0, calendar.getTime(), mess.getType(), mess.isDeleted(),encryption.decryptOrNull(mess.getAvatar()),mess.isChat());
        } else if (mess.getType().equals("video")) {
            return getVideoMessage(encryption.decryptOrNull(mess.getLinkVideo()), mess.getDuration(), mess.getFrom(), 0, calendar.getTime(), mess.getType(), mess.getThumb(), mess.isDeleted(),encryption.decryptOrNull(mess.getAvatar()),mess.isChat());
        } else if (mess.getType().equals("map")) {
            return getLocationMessage(encryption.decryptOrNull(mess.getLocation()), mess.getFrom(), 0, calendar.getTime(), mess.getType(), mess.isDeleted(),encryption.decryptOrNull(mess.getAvatar()),mess.isChat());
        }

        return null;
    }

    private static UserIn getUser(String id) {

        if (id.equals(FirebaseAuth.getInstance().getUid())) {
            return new UserIn(
                    id, Global.nameLocal, Global.avaLocal,Global.myscreen);
        } else {
            return new UserIn(
                    id, Global.currname, Global.currAva,Global.currscreen);

        }


    }
}
