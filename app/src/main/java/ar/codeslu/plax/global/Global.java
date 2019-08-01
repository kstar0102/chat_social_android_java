package ar.codeslu.plax.global;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;

import java.util.ArrayList;
import java.util.List;

import com.stfalcon.chatkit.me.GroupIn;
import com.stfalcon.chatkit.me.MessageIn;
import com.stfalcon.chatkit.me.UserIn;

import ar.codeslu.plax.lists.UserData;
import ar.codeslu.plax.notify.FCM;
import ar.codeslu.plax.notify.FCMclient;
import nl.changer.audiowife.AudioWife;

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
    public final static String GROUPS = "Groups";
    public final static String Phones = "Phones";
    public final static String Online = "online";
    public final static String avatar = "avatar";
    public final static String time = "time";
    public final static String Messages = "messages";
    public final static String tokens = "Tokens";


    //Storage Constants
    public final static String AvatarS = "Avatar";
    public final static String StoryS = "Stories";
    public final static String Mess = "Message";
    public final static String GroupAva = "GroupsAva";


    //App constatnts
    public final static int STATUE_LENTH = 20;
    public final static int FileName_LENTH = 30;
    public final static int NOTIFYTIME = 3000;
    public static String DEFAULT_STATUE = "Hello World!!";
    public static boolean DARKSTATE = false;
    public static boolean netconnect = false;
    public static boolean local_on = true;
    public static boolean yourM = true;
public  static ArrayList<AudioWife> audiolist;
    public  static ArrayList<String> btnid;


    //local vars (my data)
    public static String nameLocal = "";
    public static String statueLocal = "";
    public static String avaLocal = "";
    public static String idLocal = "";
    public static String phoneLocal = "";
    public static ArrayList<MessageIn> messG;
    public static ArrayList<MessageIn> retryM;
    public static ArrayList<UserData> contactsG;
    public static boolean myonstate;
    public static boolean myscreen;
    public static ArrayList<UserIn> diaG;
    public static ArrayList<GroupIn> diaGGG;
    public static String currentpageid = "";
    public static Activity currentactivity;
    public static Activity chatactivity;
    public static Activity mainActivity;
    public static ArrayList<String> groupids;

    //app media max number chooser
    public static int photoS = 5; //photos max number to select in one time
    public static int audioS = 1; //audio max number to select in one time
    public static int videoS = 1; //video max number to select in one time
    public static int fileS = 5; //files max number to select in one time

    //storage
    public static Context conA;
    public static Context conMain;
    //friend (friend data)
    public static String currFid = "";
    public static String currAva = "";
    public static String currname = "";
    public static String currstatue = "";
    public static String currphone = "";
    public static ArrayList<String> currGUsers ;
    public static ArrayList<String> currGAdmins;

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
    public static String salt = "cryp2code8882888plaxsalt";
    public static String keyE = "€cryp2code€8882888€plax€key€";




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
