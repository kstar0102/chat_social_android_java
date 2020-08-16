package ar.codeslu.plax.global;

import android.Manifest;
import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ShortcutInfo;
import android.content.pm.ShortcutManager;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.drawable.Icon;
import android.media.AudioManager;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;
import androidx.multidex.MultiDex;

import android.os.Build;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.stfalcon.chatkit.me.MessageFav;
import com.vanniktech.emoji.EmojiManager;
import com.vanniktech.emoji.ios.IosEmojiProvider;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import ar.codeslu.plax.LockScreen;
import ar.codeslu.plax.MainActivity;
import ar.codeslu.plax.R;
import ar.codeslu.plax.auth.Login;
import ar.codeslu.plax.db.TinyDB;
import ar.codeslu.plax.lists.BlockL;
import ar.codeslu.plax.lists.CountryToPhonePrefix;
import ar.codeslu.plax.lists.UserData;
import ar.codeslu.plax.lists.UserDetailsStory;
import ar.codeslu.plax.lists.idIdentifier;
import ar.codeslu.plax.story.AddStory;
import in.myinnos.customfontlibrary.TypefaceUtil;
import me.leolin.shortcutbadger.ShortcutBadger;

import static com.jaiselrahman.filepicker.activity.FilePickerActivity.TAG;


/**
 * Created by CodeSlu on 29/10/18.
 */

public class AppBack extends Application {
    //Vars
    private Timer mActivityTransitionTimer;
    private TimerTask mActivityTransitionTimerTask;
    public boolean wasInBackground;
    private final long MAX_ACTIVITY_TRANSITION_TIME_MS = 2000;
    //Firebase
    FirebaseAuth mAuth;
    DatabaseReference mData, mBlock, mMute, mFav;
    //shared
    private SharedPreferences settings;
    private SharedPreferences.Editor editor;
    //vars
    String choosenFont, choosenLang;
    TinyDB tinydb;
    boolean lockState;
    ArrayList<String> localContacts, tempcontacts;
    String name = "", ava = "", id = "", phone = "";
    //enc
    //getTime
    private static final int SECOND_MILLIS = 1000;
    private static final int MINUTE_MILLIS = 60 * SECOND_MILLIS;
    private static final int HOUR_MILLIS = 60 * MINUTE_MILLIS;
    private static final int DAY_MILLIS = 24 * HOUR_MILLIS;
    DatabaseReference mUserDB;
    int JOBID = 0;
    Context conn;


    @Override
    public void onCreate() {
        super.onCreate();

        //gettime
        FirebaseMessaging.getInstance().setAutoInitEnabled(true);
        // Mapbox Access token
     //   Mapbox.getInstance(getApplicationContext(), getString(R.string.mapbox_access_token));
        // FirebaseDatabase.getInstance().setPersistenceEnabled(true);
//Emoji init
        EmojiManager.install(new IosEmojiProvider());
        mAuth = FirebaseAuth.getInstance();
        mData = FirebaseDatabase.getInstance().getReference(Global.USERS);
        mUserDB = FirebaseDatabase.getInstance().getReference(Global.USERS);
        mBlock = FirebaseDatabase.getInstance().getReference(Global.BLOCK);
        mMute = FirebaseDatabase.getInstance().getReference(Global.MUTE);
        mFav = FirebaseDatabase.getInstance().getReference(Global.FAV);
        //notif counter
        settings = getSharedPreferences("Setting", MODE_PRIVATE);
        editor = settings.edit();

        localContacts = new ArrayList<>();
        tempcontacts = new ArrayList<>();

        conn = this;
        //chat saver
        tinydb = new TinyDB(this);

        //change app lang
        choosenLang = shared().getString("lang", "def");
        choosenFont = shared().getString("font", "8");
        lockState = shared().getBoolean("lock", false);
        changelang(choosenLang);
        changefont(Integer.parseInt(choosenFont));
        if (shared().getBoolean("lock", false) && mAuth.getCurrentUser() != null) {
            //lock screen
            lockscreen(shared().getBoolean("lock", false));
        }

        if (mAuth.getCurrentUser() != null) {
            getBlock();
            getMute();
            getFav();

            mBlock.child(mAuth.getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    if (dataSnapshot.exists()) {
                        BlockL blockL = dataSnapshot.getValue(BlockL.class);
                        Global.blockList.clear();
                        Global.blockList = blockL.getList();
                        try {
                            setBlock();

                        } catch (NullPointerException e) {

                        }
                    } else {
                        Global.blockList.clear();
                        try {
                            setBlock();

                        } catch (NullPointerException e) {

                        }

                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

            mMute.child(mAuth.getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        BlockL blockL = dataSnapshot.getValue(BlockL.class);
                        Global.mutelist.clear();
                        Global.mutelist = blockL.getList();
                        try {
                            setMute();

                        } catch (NullPointerException e) {

                        }
                    } else {
                        Global.mutelist.clear();
                        try {
                            setMute();

                        } catch (NullPointerException e) {

                        }
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

            mFav.child(mAuth.getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        Global.FavMess.clear();
                        for (DataSnapshot data : dataSnapshot.getChildren()) {
                            MessageFav message = data.getValue(MessageFav.class);
                            Global.FavMess.add(message);
                        }
                        try {
                            setFav();

                        } catch (NullPointerException e) {

                        }
                    } else {
                        Global.FavMess.clear();
                        if (mAuth.getCurrentUser() != null)
                            try {
                                setFav();

                            } catch (NullPointerException e) {

                            }
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });


        }

    }


    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    public void startOnline() {
        if (mAuth.getCurrentUser() != null) {
            //init data
            Map<String, Object> map = new HashMap<>();
            map.put(Global.Online, true);
            mData.child(mAuth.getCurrentUser().getUid()).updateChildren(map);
            Global.local_on = true;
            mData.child(mAuth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists() && mAuth.getCurrentUser() != null) {
                        //check data
                        Map<String, Object> map2 = new HashMap<>();
                        map2.put(Global.Online, false);
                        map2.put(Global.time, ServerValue.TIMESTAMP);
                        mData.child(mAuth.getCurrentUser().getUid()).onDisconnect().updateChildren(map2);
                        Global.local_on = false;


                        //check data
                        Map<String, Object> map3 = new HashMap<>();
                        map3.put("incall", false);
                        mData.child(mAuth.getCurrentUser().getUid()).onDisconnect().updateChildren(map3);


                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });


        }
    }

    public static String getTimeAgo(long time, Context ctx) {
        if (time < 1000000000000L) {
            // if timestamp given in seconds, convert to millis
            time *= 1000;
        }
        long now = System.currentTimeMillis();


        // TODO: localize
        final long diff = now - time;
        if (diff < MINUTE_MILLIS) {
            return ctx.getResources().getString(R.string.now);
        } else if (diff < 2 * MINUTE_MILLIS) {
            return ctx.getResources().getString(R.string.minute);
        } else if (diff < 50 * MINUTE_MILLIS) {
            return diff / MINUTE_MILLIS + " " + ctx.getResources().getString(R.string.min_ago);
        } else if (diff < 90 * MINUTE_MILLIS) {
            return ctx.getResources().getString(R.string.hour);
        } else if (diff < 24 * HOUR_MILLIS) {
            return diff / HOUR_MILLIS + " " + ctx.getResources().getString(R.string.hours);
        } else if (diff < 48 * HOUR_MILLIS) {
            return ctx.getResources().getString(R.string.yesterday);
        } else {
            return diff / DAY_MILLIS + " " + ctx.getResources().getString(R.string.days);
        }
    }

    public void startActivityTransitionTimer() {
        this.mActivityTransitionTimer = new Timer();
        this.mActivityTransitionTimerTask = new TimerTask() {
            public void run() {
                AppBack.this.wasInBackground = true;
                if (mAuth.getCurrentUser() != null) {
                    if (Global.check_int(getApplicationContext())) {
                        mData.child(mAuth.getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()) {
                                    if (mAuth.getCurrentUser() != null) {

                                        //check data
                                        Map<String, Object> map = new HashMap<>();
                                        map.put(Global.Online, false);
                                        map.put(Global.time, ServerValue.TIMESTAMP);
                                        mData.child(mAuth.getCurrentUser().getUid()).updateChildren(map);
                                        Global.local_on = false;
                                        Global.currentactivity = null;
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }

                }
            }
        };

        this.mActivityTransitionTimer.schedule(mActivityTransitionTimerTask,
                MAX_ACTIVITY_TRANSITION_TIME_MS);
    }

    public void stopActivityTransitionTimer() {
        if (this.mActivityTransitionTimerTask != null) {
            this.mActivityTransitionTimerTask.cancel();
        }

        if (this.mActivityTransitionTimer != null) {
            this.mActivityTransitionTimer.cancel();
        }

        this.wasInBackground = false;
    }


    public SharedPreferences.Editor editSharePrefs() {
        return editor;
    }

    public SharedPreferences shared() {
        return settings;
    }

    public void changelang(String choosenLang) {
        Resources res = getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        android.content.res.Configuration conf = res.getConfiguration();
        if(choosenLang.equals("def"))
        conf.setLocale(new Locale(Locale.getDefault().getDisplayLanguage().toLowerCase()));
        else
            conf.setLocale(new Locale(choosenLang.toLowerCase()));

        editSharePrefs().putString("lang", choosenLang);
        editSharePrefs().apply();
        res.updateConfiguration(conf, dm);
    }

    public void changefont(int i) {
        TypefaceUtil.overrideFont(getApplicationContext(), "SERIF", "fonts/" + i + ".ttf");
        editSharePrefs().putString("font", String.valueOf(i));
        editSharePrefs().apply();
    }

    public void setchatsdb(String friendId) {
        if(mAuth.getCurrentUser() != null)
        tinydb.putListChats(mAuth.getCurrentUser().getUid() + "/" + friendId, Global.messG);
    }

    public void getchatsdb(String friendId) {
        if(mAuth.getCurrentUser() != null)
        Global.messG = tinydb.getListChats(mAuth.getCurrentUser().getUid() + "/" + friendId);
        else
            Global.messG = new ArrayList<>();
    }

    public void setchatsdbG(String friendId) {
        if(mAuth.getCurrentUser() != null)
        tinydb.putListChats(mAuth.getCurrentUser().getUid() + "/Groups/" + friendId, Global.messGGG);
    }

    public void getchatsdbG(String friendId) {
        if(mAuth.getCurrentUser() != null)
        Global.messGGG = tinydb.getListChats(mAuth.getCurrentUser().getUid() + "/Groups/" + friendId);
        else
            Global.messGGG = new ArrayList<>();
    }


    public void setRetry(String friendId) {
        if(mAuth.getCurrentUser() != null)
        tinydb.putListRetry(mAuth.getCurrentUser().getUid() + "R" + friendId, Global.retryM);
    }

    public void getRetry(String friendId) {
        if(mAuth.getCurrentUser() != null)
        Global.retryM = tinydb.getListRetry(mAuth.getCurrentUser().getUid() + "R" + friendId);
        else
            Global.retryM = new ArrayList<>();
    }

    public void setContacts() {
        if(mAuth.getCurrentUser() != null)
        tinydb.putListContacts(mAuth.getCurrentUser().getUid() + "C", Global.contactsG);

    }

    public void getContacts() {
        if(mAuth.getCurrentUser() != null){
            Global.contactsG = tinydb.getListConatcts(mAuth.getCurrentUser().getUid() + "C");
        }
        else
            Global.contactsG = new ArrayList<>();
    }

    public void setdialogdb(String userId) {
        if(mAuth.getCurrentUser() != null)
        tinydb.putListDialog(userId, Global.diaG);
    }

    public void getdialogdb(String userId) {
        if(mAuth.getCurrentUser() != null)
        Global.diaG = tinydb.getListDialog(userId);
        else
            Global.diaG = new ArrayList<>();
    }

    public void setdialogdbG(String userId) {
        if(mAuth.getCurrentUser() != null)
        tinydb.putListDialogG(userId + "Groups", Global.diaGGG);
    }

    public void getdialogdbG(String userId) {
        if(mAuth.getCurrentUser() != null)
        Global.diaGGG = tinydb.getListDialogG(userId + "Groups");
        else
            Global.diaGGG = new ArrayList<>();
    }

    public void setMyStory() {
        if(mAuth.getCurrentUser() != null)
        tinydb.putMyStory(mAuth.getCurrentUser().getUid() + "/MyStory/", Global.myStoryList);
    }

    public void getMyStory() {
        if(mAuth.getCurrentUser() != null)
        Global.myStoryList = tinydb.getMyStory(mAuth.getCurrentUser().getUid() + "/MyStory/");
        else
            Global.myStoryList = new ArrayList<>();
    }

    public void setStory() {
        if(mAuth.getCurrentUser() != null)
        tinydb.putStory(mAuth.getCurrentUser().getUid() + "/Story/", Global.StoryList);
    }

    public void getStory() {
        if(mAuth.getCurrentUser() != null)
        Global.StoryList = tinydb.getStory(mAuth.getCurrentUser().getUid() + "/Story/");
        else
            Global.StoryList = new ArrayList<>();
    }

    //for group
    public void setGroupUsers(String userId) {
        if(mAuth.getCurrentUser() != null)
        tinydb.putListString(userId + "GroupUsers", Global.currGUsers);
    }

    public void getGroupUsers(String userId) {
        if(mAuth.getCurrentUser() != null)
        Global.currGUsers = tinydb.getListString(userId + "GroupUsers");
        else
            Global.currGUsers = new ArrayList<>();
    }

    public void setGroupUsersAva(String userId) {
        if(mAuth.getCurrentUser() != null)
        tinydb.putListString(userId + "GroupUsersAva", Global.currGUsersAva);
    }

    public void getGroupUsersAva(String userId) {
        if(mAuth.getCurrentUser() != null)
        Global.currGUsersAva = tinydb.getListString(userId + "GroupUsersAva");
        else
            Global.currGUsersAva = new ArrayList<>();
    }

    public void setGroupAdmins(String userId) {
        if(mAuth.getCurrentUser() != null)
        tinydb.putListString(userId + "GroupAdmins", Global.currGAdmins);
    }

    public void getGroupAdmins(String userId) {
        if(mAuth.getCurrentUser() != null)
        Global.currGAdmins = tinydb.getListString(userId + "GroupAdmins");
        else
            Global.currGAdmins = new ArrayList<>();
    }

    public void setGroupUA(String userId) {
        if(mAuth.getCurrentUser() != null)
        tinydb.putGroupUA(userId + "UsersandAdmins", Global.currGUsersU);
    }

    public void getGroupUA(String userId) {
        if(mAuth.getCurrentUser() != null)
        Global.currGUsersU = tinydb.getGroupUA(userId + "UsersandAdmins");
        else
            Global.currGUsersU = new ArrayList<>();
    }

    public void setCalls() {
        if(mAuth.getCurrentUser() != null)
        tinydb.putCalls(mAuth.getCurrentUser().getUid() + "calls", Global.callList);
    }

    public void getCalls() {
        if(mAuth.getCurrentUser() != null)
        Global.callList = tinydb.getCalls(mAuth.getCurrentUser().getUid() + "calls");
        else
            Global.callList = new ArrayList<>();
    }

    public void setBlock() {
        if(mAuth.getCurrentUser() != null)
        tinydb.putListString(mAuth.getCurrentUser().getUid() + "block", Global.blockList);
    }

    public void getBlock() {
        if(mAuth.getCurrentUser() != null)
        Global.blockList = tinydb.getListString(mAuth.getCurrentUser().getUid() + "block");
        else
            Global.blockList = new ArrayList<>();
    }

    public void setBlockCurr(String friendid) {
        if(mAuth.getCurrentUser() != null)
        tinydb.putListString(mAuth.getCurrentUser().getUid() + "block" + friendid, Global.currblockList);
    }

    public void getBlockCurr(String friendid) {
        if(mAuth.getCurrentUser() != null)
        Global.currblockList = tinydb.getListString(mAuth.getCurrentUser().getUid() + "block" + friendid);
        else
            Global.currblockList = new ArrayList<>();
    }

    public void setMute() {
        if(mAuth.getCurrentUser() != null)
        tinydb.putListString(mAuth.getCurrentUser().getUid() + "mute", Global.mutelist);
    }

    public void getMute() {
        if(mAuth.getCurrentUser() != null)
        Global.mutelist = tinydb.getListString(mAuth.getCurrentUser().getUid() + "mute");
        else
            Global.mutelist = new ArrayList<>();
    }

    public void setFav() {
        if(mAuth.getCurrentUser() != null)
        tinydb.putListFav(mAuth.getCurrentUser().getUid() + "/Favourite", Global.FavMess);
    }

    public void getFav() {
        if(mAuth.getCurrentUser() != null)
        Global.FavMess = tinydb.getListFav(mAuth.getCurrentUser().getUid() + "/Favourite");
        else
            Global.FavMess = new ArrayList<>();
    }

    public void setStoryArch() {
        if(mAuth.getCurrentUser() != null)
        tinydb.putMyStory(mAuth.getCurrentUser().getUid() + "/StoryArch/", Global.ArchiveList);
    }

    public void getStoryArch() {
        if(mAuth.getCurrentUser() != null)
        Global.ArchiveList = tinydb.getMyStory(mAuth.getCurrentUser().getUid() + "/StoryArch/");
        else
            Global.ArchiveList = new ArrayList<>();
    }
    public void setGroupsM() {
        if(mAuth.getCurrentUser() != null)
        tinydb.putListDialogG(mAuth.getCurrentUser().getUid() + "///Groups///forward", Global.groupsArray);
    }

    public void getGroupsM() {
        if(mAuth.getCurrentUser() != null)
        Global.groupsArray = tinydb.getListDialogG(mAuth.getCurrentUser().getUid() + "///Groups///forward");
        else
            Global.groupsArray = new ArrayList<>();
    }

    public void lockscreenE() {
        Intent intent = new Intent(this, LockScreen.class);
        intent.putExtra("typeL", 0);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    public void lockscreen(boolean locked) {
        if (locked) {
            Intent intent = new Intent(this, LockScreen.class);
            intent.putExtra("typeL", 1);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
    }

    public void oneAccVerfy() {
        final boolean[] first = {true};
        DatabaseReference mUser = FirebaseDatabase.getInstance().getReference(Global.USERS);

        mUser.child(mAuth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists() && mAuth.getCurrentUser() != null) {
                    try {
                        idIdentifier id = dataSnapshot.getValue(idIdentifier.class);
                        if (!id.getAndroidid().equals(Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID)) && first[0]) {
                            first[0] = false;
                            //clear all notifications
                            NotificationManager notificationManager = (NotificationManager) conn.getSystemService(Context.NOTIFICATION_SERVICE);
                            try {
                                if (notificationManager != null) {
                                    notificationManager.cancelAll();
                                    int count = 0;
                                    //store it again
                                    ((AppBack) conn.getApplicationContext()).editSharePrefs().putInt("numN" + mAuth.getCurrentUser().getUid(), count);
                                    ((AppBack) conn.getApplicationContext()).editSharePrefs().apply();
                                    ShortcutBadger.applyCount(conn, count);
                                }
                            } catch (NullPointerException e) {
                                //nothing
                            }
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1)
                                disableshourtcuts();
                            mAuth.signOut();
                            Intent intent = new Intent(conn, Login.class);
                            intent.putExtra("code", true);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                            Global.mainActivity.finish();
                        }
                    } catch (NullPointerException e) {

                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.N_MR1)
    public void disableshourtcuts() {
        List<String> idds = new ArrayList<>();
        idds.add("addstory");
        idds.add("group");
        idds.add("user1");
        idds.add("user2");
        ShortcutManager shortcutManager2 = this.getSystemService(ShortcutManager.class);
        shortcutManager2.disableShortcuts(idds);
    }
}

