package ar.codeslu.plax.global;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.media.AudioManager;
import android.os.AsyncTask;

import androidx.multidex.MultiDex;

import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.sinch.android.rtc.ClientRegistration;
import com.sinch.android.rtc.PushPair;
import com.sinch.android.rtc.Sinch;
import com.sinch.android.rtc.SinchClient;
import com.sinch.android.rtc.SinchClientListener;
import com.sinch.android.rtc.SinchError;
import com.sinch.android.rtc.calling.Call;
import com.sinch.android.rtc.calling.CallClient;
import com.sinch.android.rtc.calling.CallClientListener;
import com.sinch.android.rtc.calling.CallListener;
import com.vanniktech.emoji.EmojiManager;
import com.vanniktech.emoji.ios.IosEmojiProvider;

import java.io.IOException;
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
import ar.codeslu.plax.calls.SinchService;
import ar.codeslu.plax.db.TinyDB;
import in.myinnos.customfontlibrary.TypefaceUtil;


/**
 * Created by mostafa on 29/10/18.
 */

public class AppBack extends Application implements SinchService.StartFailedListener {
    //Vars
    private Timer mActivityTransitionTimer;
    private TimerTask mActivityTransitionTimerTask;
    public boolean wasInBackground;
    private final long MAX_ACTIVITY_TRANSITION_TIME_MS = 2000;
    //Firebase
    FirebaseAuth mAuth;
    DatabaseReference mData;
    //shared
    private SharedPreferences settings;
    private SharedPreferences.Editor editor;
    //vars
    String choosenFont, choosenLang;
    TinyDB tinydb;
    boolean lockState;
    //calls
    private Call call;
    private SinchClient sinchClient;
    private String callerId;
    private String recipientId;


    @Override
    public void onCreate() {
        super.onCreate();
        //gettime
        FirebaseMessaging.getInstance().setAutoInitEnabled(true);
        // FirebaseDatabase.getInstance().setPersistenceEnabled(true);
//Emoji init
        EmojiManager.install(new IosEmojiProvider());
        mAuth = FirebaseAuth.getInstance();
        mData = FirebaseDatabase.getInstance().getReference(Global.USERS);
        //notif counter
        settings = getSharedPreferences("Setting", MODE_PRIVATE);
        editor = settings.edit();

        //chat saver
        tinydb = new TinyDB(this);

        //change app lang
        choosenLang = shared().getString("lang", "en");
        choosenFont = shared().getString("font", "8");
        lockState = shared().getBoolean("lock", false);
        changelang(choosenLang);
        changefont(Integer.parseInt(choosenFont));
        if (shared().getBoolean("lock", false) && mAuth.getCurrentUser() != null) {
            //lock screen
            lockscreen(shared().getBoolean("lock", false));
        }

        if (mAuth.getCurrentUser() != null)
            sinchInit();

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
                    if (dataSnapshot.exists()) {
                        if (Global.check_int(getApplicationContext()) && !wasInBackground && !Global.local_on) {
                            Map<String, Object> map = new HashMap<>();
                            map.put(Global.Online, true);
                            mData.child(mAuth.getCurrentUser().getUid()).updateChildren(map);
                            Global.local_on = true;
                        }
                        //check data
                        Map<String, Object> map2 = new HashMap<>();
                        map2.put(Global.Online, false);
                        map2.put(Global.time, ServerValue.TIMESTAMP);
                        mData.child(mAuth.getCurrentUser().getUid()).onDisconnect().updateChildren(map2);
                        Global.local_on = false;

                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });


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
                                    //check data
                                    Map<String, Object> map = new HashMap<>();
                                    map.put(Global.Online, false);
                                    map.put(Global.time, ServerValue.TIMESTAMP);
                                    mData.child(mAuth.getCurrentUser().getUid()).updateChildren(map);
                                    Global.local_on = false;
                                    Global.currentactivity = null;
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
        tinydb.putListChats(mAuth.getCurrentUser().getUid() + "/" + friendId, Global.messG);
    }

    public void getchatsdb(String friendId) {
        Global.messG = tinydb.getListChats(mAuth.getCurrentUser().getUid() + "/" + friendId);
    }

    public void setchatsdbG(String friendId) {
        tinydb.putListChats(mAuth.getCurrentUser().getUid() + "/Groups/" + friendId, Global.messGGG);
    }

    public void getchatsdbG(String friendId) {
        Global.messGGG = tinydb.getListChats(mAuth.getCurrentUser().getUid() + "/Groups/" + friendId);
    }


    public void setRetry(String friendId) {
        tinydb.putListRetry(mAuth.getCurrentUser().getUid() + "R" + friendId, Global.retryM);
    }

    public void getRetry(String friendId) {
        Global.retryM = tinydb.getListRetry(mAuth.getCurrentUser().getUid() + "R" + friendId);
    }

    public void setContacts() {
        tinydb.putListContacts(mAuth.getCurrentUser().getUid() + "C", Global.contactsG);
    }

    public void getContacts() {
        Global.contactsG = tinydb.getListConatcts(mAuth.getCurrentUser().getUid() + "C");
    }

    public void setdialogdb(String userId) {
        tinydb.putListDialog(userId, Global.diaG);
    }

    public void getdialogdb(String userId) {
        Global.diaG = tinydb.getListDialog(userId);
    }

    public void setdialogdbG(String userId) {
        tinydb.putListDialogG(userId + "Groups", Global.diaGGG);
    }

    public void getdialogdbG(String userId) {
        Global.diaGGG = tinydb.getListDialogG(userId + "Groups");
    }

    //for group
    public void setGroupUsers(String userId) {
        tinydb.putListString(userId + "GroupUsers", Global.currGUsers);
    }

    public void getGroupUsers(String userId) {
        Global.currGUsers = tinydb.getListString(userId + "GroupUsers");
    }

    public void setGroupUsersAva(String userId) {
        tinydb.putListString(userId + "GroupUsersAva", Global.currGUsersAva);
    }

    public void getGroupUsersAva(String userId) {
        Global.currGUsersAva = tinydb.getListString(userId + "GroupUsersAva");
    }

    public void setGroupAdmins(String userId) {
        tinydb.putListString(userId + "GroupAdmins", Global.currGAdmins);
    }

    public void getGroupAdmins(String userId) {
        Global.currGAdmins = tinydb.getListString(userId + "GroupAdmins");
    }

    public void setGroupUA(String userId) {
        tinydb.putGroupUA(userId + "UsersandAdmins", Global.currGUsersU);
    }

    public void getGroupUA(String userId) {
        Global.currGUsersU = tinydb.getGroupUA(userId + "UsersandAdmins");
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

    public void sinchInit() {
        sinchClient = Sinch.getSinchClientBuilder().context(getApplicationContext())
                .applicationKey(getString(R.string.sich_app_key))
                .applicationSecret(getString(R.string.sich_app_secret))
                .environmentHost(getString(R.string.sich_hostname))
                .userId(mAuth.getCurrentUser().getUid())
                .build();
        sinchClient.setSupportCalling(true);
        sinchClient.setSupportActiveConnectionInBackground(true);
        sinchClient.startListeningOnActiveConnection();
        sinchClient.start();
        sinchClient.getCallClient().addCallClientListener(new SinchCallClientListener());

    }


    public void callUserA(String friendid) {

        if (call != null) {
            call.hangup();
            call = null;
        }


        call = sinchClient.getCallClient().callUser(friendid);
        call.addCallListener(new SinchCallListener());
        Toast.makeText(AppBack.this, "calling Audio", Toast.LENGTH_SHORT).show();

    }

    public void callUserV(String friendid) {
        if (call != null) {
            call.hangup();
            call = null;
        }

        call = sinchClient.getCallClient().callUserVideo(friendid);
        call.addCallListener(new SinchCallListener());
        Toast.makeText(AppBack.this, "calling Video", Toast.LENGTH_SHORT).show();

    }

    public void signOutStop() {
        sinchClient.stopListeningOnActiveConnection();
        sinchClient.terminate();
    }

    @Override
    public void onStartFailed(SinchError error) {

    }

    @Override
    public void onStarted() {

    }

    private class SinchCallListener implements CallListener {
        @Override
        public void onCallEnded(Call endedCall) {
            call.hangup();
            call = null;
            SinchError a = endedCall.getDetails().getError();
            Toast.makeText(AppBack.this, "stop" + a, Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onCallEstablished(Call establishedCall) {
            Toast.makeText(AppBack.this, "connected", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onCallProgressing(Call progressingCall) {
            Toast.makeText(AppBack.this, "ringing", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onShouldSendPushNotification(Call call, List<PushPair> pushPairs) {
        }
    }

    private class SinchCallClientListener implements CallClientListener {
        @Override
        public void onIncomingCall(CallClient callClient, Call incomingCall) {


            Toast.makeText(AppBack.this, "Video offered " + incomingCall.getDetails().isVideoOffered(), Toast.LENGTH_SHORT).show();

            call = incomingCall;
            // call.answer();
            call.addCallListener(new SinchCallListener());
            Log.wtf("keyyyyy", "calling3");
            Toast.makeText(AppBack.this, "calling3", Toast.LENGTH_SHORT).show();
        }


    }


}

