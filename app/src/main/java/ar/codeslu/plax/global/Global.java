package ar.codeslu.plax.global;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.PowerManager;

import java.util.ArrayList;
import java.util.List;

import com.stfalcon.chatkit.dialogs.DialogsListAdapter;
import com.stfalcon.chatkit.me.GroupIn;
import com.stfalcon.chatkit.me.Message;
import com.stfalcon.chatkit.me.MessageFav;
import com.stfalcon.chatkit.me.MessageIn;
import com.stfalcon.chatkit.me.UserIn;

import ar.codeslu.plax.lists.StoryListRetr;
import ar.codeslu.plax.lists.UserData;
import ar.codeslu.plax.lists.UserDetailsStory;
import ar.codeslu.plax.lists.calls;
import ar.codeslu.plax.models.DefaultDialog;
import ar.codeslu.plax.notify.FCM;
import ar.codeslu.plax.notify.FCMclient;
import nl.changer.audiowife.AudioWife;
import xute.storyview.StoryModel;

/**
 * Created by Java_Dude on 10/17/2018.
 */

public class Global {
    public static final String fcmurl = "https://fcm.googleapis.com/";

    public static FCM getFCMservies() {
        return FCMclient.getClient(fcmurl).create(FCM.class);
    }


    //admob ads
    public final static boolean ADMOB_ENABLE = true;


    //Database Constants
    public final static String USERS = "Users";
    public final static String CHATS = "Chats";
    public final static String TIME = "Time";
    public final static String MUTE = "Mute";
    public final static String BLOCK = "Block";
    public final static String FAV = "Favourite";
    public final static String GROUPS = "Groups";
    public final static String Phones = "Phones";
    public final static String Online = "online";
    public final static String avatar = "avatar";
    public final static String time = "time";
    public final static String Messages = "messages";
    public final static String tokens = "Tokens";
    public final static String CALLS = "Calls";


    //Storage Constants
    public final static String AvatarS = "Avatar";
    public final static String StoryS = "Stories";
    public final static String myStoryS = "MyStories";
    public final static String Mess = "Message";
    public final static String GroupAva = "GroupsAva";


    //App constatnts
    public final static int STATUE_LENTH = 20;
    public final static int STORY_NAME_LENTH = 10;
    public final static int FileName_LENTH = 30;
    public final static int NOTIFYTIME = 3000;
    public final static int SHAKE_UNDO_TIMEOUT = 30; //time in sec
    public static String DEFAULT_STATUE = "Hello World!!";
    public static boolean DARKSTATE = false;
    public static boolean netconnect = false;
    public static boolean local_on = true;
    public static boolean yourM = true;
    public static ArrayList<AudioWife> audiolist;
    public static ArrayList<String> btnid;


    //local vars (my data)
    public static String nameLocal = "";
    public static String statueLocal = "";
    public static String avaLocal = "";
    public static String idLocal = "";
    public static String phoneLocal = "";
    public static boolean blockedLocal = false;
    public static boolean stickerIcon = true;
    public static ArrayList<MessageIn> messG;
    public static ArrayList<MessageIn> messGGG;
    public static ArrayList<MessageFav> FavMess;
    public static ArrayList<MessageIn> retryM;
    public static ArrayList<UserData> contactsG;
    public static ArrayList<GroupIn> groupsArray;
    public static boolean myonstate;
    public static boolean myscreen;
    public static ArrayList<UserIn> diaG;
    public static ArrayList<GroupIn> diaGGG;
    public static ArrayList<StoryModel> myStoryList;
    public static ArrayList<StoryModel> ArchiveList;
    public static ArrayList<StoryListRetr> StoryList;
    public static ArrayList<calls> callList;
    public static ArrayList<String> blockList;
    public static ArrayList<UserData> tempUser;
    public static ArrayList<String> mutelist;
    public static String currentpageid = "";
    public static Message lastDeletedMessage;

    public static Activity currentactivity;
    public static Activity currentfragment;
    public static Activity chatactivity;
    public static Activity mainActivity;
    public static Activity IncAActivity = null;
    public static Activity IncVActivity = null;
    public static ArrayList<String> groupids;
    public static ArrayList<String> forwardids;
    public static Message forwardMessage;

    public static ArrayList<String> inviteNums;
    public static PowerManager.WakeLock wl;
    public static PowerManager pm;
    public static DialogsListAdapter<DefaultDialog> globalChatsAdapter;


    //app media max number chooser
    public static int photoS = 5; //photos max number to select in one time
    public static int audioS = 1; //audio max number to select in one time
    public static int videoS = 1; //video max number to select in one time
    public static int fileS = 1; //files max number to select in one time

    //storage
    public static Context conA;
    public static Context conMain;
    //friend (friend data)
    public static String currFid = "";
    public static String currAva = "";
    public static String currname = "";
    public static String currstatue = "";
    public static String currphone = "";
    public static ArrayList<String> currGUsers;
    public static ArrayList<UserData> currGUsersU;
    public static ArrayList<UserData> adminList;
    public static ArrayList<String> currGUsersAva;
    public static ArrayList<String> currGAdmins;
    public static ArrayList<String> currblockList;
    public static boolean currblocked = false;


    public static boolean onstate;
    public static boolean currscreen;
    public static long currtime = 0;


    //dialog update message
    public static MessageIn DialogM;
    public static String Dialogid = "";
    public static UserIn userrG;
    public static GroupIn groupG;
    public static ArrayList<UserIn> Dialogonelist;
    public static ArrayList<GroupIn> DialogonelistG;

    //encryption
    public static String salt = "codeslu8882888plaxsalt";
    public static String keyE = "€codeslu€8882888€plax€key€";

    //story
    public static boolean storyFramel = true;


    //check internet
    public static Boolean check_int(Context context) {
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = manager.getActiveNetworkInfo();
        if (info != null && info.isConnected()) {
            return true;
        } else
            return false;
    }

    //check if user in activity or not
    public static boolean isRunning(Context ctx) {
        ActivityManager activityManager = (ActivityManager) ctx.getSystemService(Context.ACTIVITY_SERVICE);
        if (Build.VERSION.SDK_INT > 20) {
            List<ActivityManager.RunningAppProcessInfo> tasks = activityManager.getRunningAppProcesses();
            for (ActivityManager.RunningAppProcessInfo task : tasks) {
                if (ctx.getPackageName().equalsIgnoreCase(task.processName))
                    return true;
            }
        } else {
            List<ActivityManager.RunningTaskInfo> tasks = activityManager.getRunningTasks(Integer.MAX_VALUE);
            for (ActivityManager.RunningTaskInfo task : tasks) {
                if (ctx.getPackageName().equalsIgnoreCase(task.baseActivity.getPackageName()))
                    return true;
            }
        }

        return false;
    }


}
