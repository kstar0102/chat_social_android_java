package com.andrew.link.notify;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.database.Cursor;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import android.provider.MediaStore;
import android.util.Log;
import android.view.View;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import com.andrew.link.Chat;
import com.andrew.link.Groups.Group;
import com.andrew.link.R;
import com.andrew.link.calls.IncAudioActivity;
import com.andrew.link.calls.IncCallActivity;
import com.andrew.link.global.AppBack;
import com.andrew.link.global.Global;
import com.andrew.link.global.encryption;
import com.andrew.link.lists.OnlineGetter;
import com.andrew.link.lists.Usercalldata;
import me.leolin.shortcutbadger.ShortcutBadger;


import com.stfalcon.chatkit.me.GroupIn;
import com.stfalcon.chatkit.me.UserIn;
import com.tapadoo.alerter.Alerter;

/**
 * Created by CodeSlu on 9/4/2018.
 */

public class FCMR extends FirebaseMessagingService {


    String notificationId[];
    boolean deleted,online;
    int oneTimeId;
    Context conn;
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    DatabaseReference mlogs;
    String senderId;
    String senderName;
    String senderAva;
    String Mid;
    String to;
    String message;
    String channelId;
    String callerId;
    String callerName;
    String callerAva,type;
    int[] noUnread = {0};

    PendingIntent pIntent;
    TaskStackBuilder stackBuilder;
    //notifi id
    String notifiID[];


    @Override
    public void onNewToken(String token) {
        try {
            if (mAuth.getCurrentUser() != null) {
                Map<String, Object> map = new HashMap<>();
                map.put("tokens", token);
                DatabaseReference mToken = FirebaseDatabase.getInstance().getReference(Global.tokens);
                mToken.child(mAuth.getCurrentUser().getUid()).updateChildren(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                    }
                });
            }
        } catch (NullPointerException e) {

        }
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        if (remoteMessage.getData() != null) {
            //get data
            Map<String, String> map = remoteMessage.getData();
            newNotify(map);


        }
    }

    public void tawgeh() {
        //go activity
        Intent intent = getBaseContext().getPackageManager().getLaunchIntentForPackage(getBaseContext().getPackageName());
        intent.putExtra("name", senderName);
        intent.putExtra("id", senderId);
        intent.putExtra("ava", senderAva);
        intent.putExtra("codetawgeh", 1);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addNextIntentWithParentStack(intent);
        pIntent = PendingIntent.getActivity(this, 11, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        //tawgeh
        //if chat app is not running
        if (!online) {


            //Delivered
            deliver();

            if (!Global.mutelist.contains(senderId)) {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                    CustomNotAPI25(message, senderName, oneTimeId);
                else
                    CustomNot(message, senderName, oneTimeId);
                int count = 0;
                //get data
                count = ((AppBack) getApplication()).shared().getInt("numN" + mAuth.getCurrentUser().getUid(), 0);
                //increment
                count++;
                //store it again
                ((AppBack) getApplication()).editSharePrefs().putInt("numN" + mAuth.getCurrentUser().getUid(), count);
                ((AppBack) getApplication()).editSharePrefs().apply();
                ShortcutBadger.applyCount(this, count);


                //get message count
                count();
            }

        }
        //if app is running
        else {
                //Delivered
                deliver();

                if (Global.currentpageid.equals(senderId)) {

                    if (!Global.mutelist.contains(senderId)) {
                        if (((AppBack) getApplication()).shared().getBoolean("sound", false))

                                if (Global.currentactivity != null) {
                                    playNotSound(Global.currentactivity);
                                    Alerter.create(Global.currentactivity)
                                            .setTitle(senderName)
                                            .setText(message)
                                            .setIcon(senderAva)
                                            .enableSwipeToDismiss()
                                            .setBackgroundColorRes(Global.DARKSTATE)
                                            .setDuration(Global.NOTIFYTIME)
                                            .show();
                                } else {
                                    playNotSound(Global.currentfragment);
                                    Alerter.create(Global.currentfragment)
                                            .setTitle(senderName)
                                            .setText(message)
                                            .setIcon(senderAva)
                                            .enableSwipeToDismiss()
                                            .setBackgroundColorRes(Global.DARKSTATE)
                                            .setDuration(Global.NOTIFYTIME)
                                            .show();
                                }

                    }

                    seenInpage();


                } else {
                    if (!Global.mutelist.contains(senderId)) {
                        count();
//inside notification


                        if (Global.currentactivity != null) {
                            Log.wtf("avatar",senderAva);
                            playNotSound(Global.currentactivity);
                            Alerter.create(Global.currentactivity)
                                    .setTitle(senderName)
                                    .setText(message)
                                    .setIcon(senderAva)
                                    .enableSwipeToDismiss()
                                    .setBackgroundColorRes(Global.DARKSTATE)
                                    .setDuration(Global.NOTIFYTIME)
                                    .setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {

                                            if (Global.currentactivity == Global.chatactivity)
                                                Global.currentactivity.finish();


                                            Global.currname = senderName;
                                            Global.currentpageid = senderId;
                                            Global.currFid = senderId;
                                            Global.currAva = senderAva;
                                            Alerter.hide();
                                            Intent intent = new Intent(Global.currentactivity, Chat.class);
                                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                            intent.putExtra("name", senderName);
                                            intent.putExtra("id", senderId);
                                            intent.putExtra("ava", senderAva);
                                            startActivity(intent);
                                        }
                                    })
                                    .show();
                        } else {
                            playNotSound(Global.currentfragment);
                            Alerter.create(Global.currentfragment)
                                    .setTitle(senderName)
                                    .setText(message)
                                    .setIcon(senderAva)
                                    .enableSwipeToDismiss()
                                    .setBackgroundColorRes(Global.DARKSTATE)
                                    .setDuration(Global.NOTIFYTIME)
                                    .setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {

                                            if ((Global.currentactivity == Global.chatactivity) && Global.currentactivity != null)
                                                Global.currentactivity.finish();


                                            Global.currname = senderName;
                                            Global.currentpageid = senderId;
                                            Global.currFid = senderId;
                                            Global.currAva = senderAva;
                                            Alerter.hide();
                                            Intent intent = new Intent(Global.currentfragment, Chat.class);
                                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                            intent.putExtra("name", senderName);
                                            intent.putExtra("id", senderId);
                                            intent.putExtra("ava", senderAva);
                                            startActivity(intent);
                                        }
                                    })
                                    .show();
                        }
                    }
                }

        }
    }

    public void tawgehG() {
        //go activity
        Intent intent = getBaseContext().getPackageManager().getLaunchIntentForPackage(getBaseContext().getPackageName());
        intent.putExtra("name", senderName);
        intent.putExtra("id", senderId);
        intent.putExtra("ava", senderAva);
        intent.putExtra("codetawgeh", 2);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addNextIntentWithParentStack(intent);
        pIntent = PendingIntent.getActivity(this, 22, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        //tawgeh
        //if chat app is not running
        if (!online) {
            if (!Global.mutelist.contains(senderId)) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                    CustomNotAPI25(message, senderName, oneTimeId);
                else
                    CustomNot(message, senderName, oneTimeId);
                int count = 0;
                //get data
                count = ((AppBack) getApplication()).shared().getInt("numN" + mAuth.getCurrentUser().getUid(), 0);
                //increment
                count++;
                //store it again
                ((AppBack) getApplication()).editSharePrefs().putInt("numN" + mAuth.getCurrentUser().getUid(), count);
                ((AppBack) getApplication()).editSharePrefs().apply();
                ShortcutBadger.applyCount(this, count);

                //get message count
                countG();
            }
        }
        //if app is running
        else {
            if (Global.currentpageid.equals(senderId)) {

                if (!Global.mutelist.contains(senderId)) {
                    if (((AppBack) getApplication()).shared().getBoolean("sound", false)) {
                        if (Global.currentactivity != null)
                            playNotSound(Global.currentactivity);
                        else
                            playNotSound(Global.currentfragment);


                    }
                }


            } else {
                if (!Global.mutelist.contains(senderId)) {
                    countG();
//inside notification
                    if (Global.currentactivity != null) {
                        playNotSound(Global.currentactivity);
                        Alerter.create(Global.currentactivity)
                                .setTitle(senderName)
                                .setText(message)
                                .enableSwipeToDismiss()
                                .setBackgroundColorRes(Global.DARKSTATE)
                                .setDuration(Global.NOTIFYTIME)
                                .setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {

                                        if (Global.currentactivity == Global.chatactivity)
                                            Global.currentactivity.finish();


                                        Global.currname = senderName;
                                        Global.currentpageid = senderId;
                                        Global.currFid = senderId;
                                        Global.currAva = senderAva;
                                        Alerter.hide();
                                        Intent intent = new Intent(Global.currentactivity, Group.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                        intent.putExtra("name", senderName);
                                        intent.putExtra("id", senderId);
                                        intent.putExtra("ava", senderAva);
                                        startActivity(intent);
                                    }
                                })
                                .show();

                    } else {
                        playNotSound(Global.currentfragment);
                        Alerter.create(Global.currentfragment)
                                .setTitle(senderName)
                                .setText(message)
                                .enableSwipeToDismiss()
                                .setBackgroundColorRes(Global.DARKSTATE)
                                .setDuration(Global.NOTIFYTIME)
                                .setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {

                                        if (Global.currentactivity == Global.chatactivity)
                                            Global.currentactivity.finish();


                                        Global.currname = senderName;
                                        Global.currentpageid = senderId;
                                        Global.currFid = senderId;
                                        Global.currAva = senderAva;
                                        Alerter.hide();
                                        Intent intent = new Intent(Global.currentfragment, Group.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                        intent.putExtra("name", senderName);
                                        intent.putExtra("id", senderId);
                                        intent.putExtra("ava", senderAva);
                                        startActivity(intent);
                                    }
                                })
                                .show();
                    }
                }
            }


        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void CustomNotAPI25(String body, String string, int i) {
        int color = ((AppBack) getApplication()).shared().getInt("colorN", Color.BLUE);
        Uri sound = Uri.parse(((AppBack) getApplication()).shared().getString("ringU", "no"));
        NotificationChann notificationChann = new NotificationChann(getBaseContext(), color, sound);
        Notification.Builder builder = notificationChann.getPLAXNot(string, body, pIntent, sound);
        notificationChann.getManager().notify(i, builder.build());
    }

    public void CustomNot(String body, String title, int id) {
        int color = ((AppBack) getApplication()).shared().getInt("colorN", Color.BLUE);
        Uri sound = Uri.parse(((AppBack) getApplication()).shared().getString("ringU", "no"));
        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext());
        builder.setContentTitle(title)
                .setContentText(body)
                .setSound(sound)
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.drawable.logo)
                .setOngoing(false)
                .setLights(color, 1000, 1000)
                .setContentIntent(pIntent);
        NotificationManager manager = (NotificationManager) getBaseContext().getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(id, builder.build());
    }

    public void seenInpage() {
        final FirebaseAuth mAuth = FirebaseAuth.getInstance();
        final DatabaseReference mData = FirebaseDatabase.getInstance().getReference(Global.CHATS);
        mData.child(senderId).child(mAuth.getCurrentUser().getUid()).child(Global.Messages).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    ds.child("statue").getRef().setValue("R✔");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void deliver() {
        final FirebaseAuth mAuth = FirebaseAuth.getInstance();
        final DatabaseReference mData = FirebaseDatabase.getInstance().getReference(Global.CHATS);
        mData.child(senderId).child(mAuth.getCurrentUser().getUid()).child(Global.Messages).child(Mid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Map<String, Object> map = new HashMap<String, Object>();
                    map.put("statue", "D✔");
                    mData.child(senderId).child(mAuth.getCurrentUser().getUid()).child(Global.Messages).child(Mid).updateChildren(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void count() {
        final FirebaseAuth mAuth = FirebaseAuth.getInstance();
        final DatabaseReference mData = FirebaseDatabase.getInstance().getReference(Global.CHATS);
        final DatabaseReference mData2 = FirebaseDatabase.getInstance().getReference(Global.CHATS);
        mData.child(mAuth.getCurrentUser().getUid()).child(senderId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try {
                    UserIn data = dataSnapshot.getValue(UserIn.class);
                    noUnread[0] = data.getNoOfUnread();
                    //message count
                    noUnread[0] = noUnread[0] + 1;
                    Map<String, Object> map2 = new HashMap<>();
                    map2.put("noOfUnread", noUnread[0]);
                    mData2.child(mAuth.getCurrentUser().getUid()).child(senderId).updateChildren(map2).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {

                        }
                    });
                }catch (NullPointerException e)
                {

                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    public void countG() {
        final FirebaseAuth mAuth = FirebaseAuth.getInstance();
        final DatabaseReference mData = FirebaseDatabase.getInstance().getReference(Global.USERS);
        final DatabaseReference mData2 = FirebaseDatabase.getInstance().getReference(Global.USERS);
        mData.child(mAuth.getCurrentUser().getUid()).child(Global.GROUPS).child(senderId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try {
                    GroupIn data = dataSnapshot.getValue(GroupIn.class);
                    noUnread[0] = data.getNoOfUnread();
                    //message count
                    noUnread[0] = noUnread[0] + 1;
                    Map<String, Object> map2 = new HashMap<>();
                    map2.put("noOfUnread", noUnread[0]);
                    mData2.child(mAuth.getCurrentUser().getUid()).child(Global.GROUPS).child(senderId).updateChildren(map2).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {

                        }
                    });
                } catch (NullPointerException e) {

                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    public void countInverse() {
        int count = 0;
        //get data
        count = ((AppBack) getApplication()).shared().getInt("numN" + mAuth.getCurrentUser().getUid(), 0);
        //increment
        count = count - 1;
        //store it again
        ((AppBack) getApplication()).editSharePrefs().putInt("numN" + mAuth.getCurrentUser().getUid(), count);
        ((AppBack) getApplication()).editSharePrefs().apply();
        ShortcutBadger.applyCount(this, count);

        senderId = notifiID[0];
        final FirebaseAuth mAuth = FirebaseAuth.getInstance();
        final DatabaseReference mData = FirebaseDatabase.getInstance().getReference(Global.CHATS);
        final DatabaseReference mData2 = FirebaseDatabase.getInstance().getReference(Global.CHATS);
        mData.child(mAuth.getCurrentUser().getUid()).child(senderId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                UserIn data = dataSnapshot.getValue(UserIn.class);
                noUnread[0] = data.getNoOfUnread();
                //message count
                if (noUnread[0] > 0)
                    noUnread[0] = noUnread[0] - 1;
                else
                    noUnread[0] = 0;


                Map<String, Object> map2 = new HashMap<>();
                map2.put("noOfUnread", noUnread[0]);
                mData2.child(mAuth.getCurrentUser().getUid()).child(senderId).updateChildren(map2).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                    }
                });

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    public void countInverseG() {
        int count = 0;
        //get data
        count = ((AppBack) getApplication()).shared().getInt("numN" + mAuth.getCurrentUser().getUid(), 0);
        //increment
        count = count - 1;
        //store it again
        ((AppBack) getApplication()).editSharePrefs().putInt("numN" + mAuth.getCurrentUser().getUid(), count);
        ((AppBack) getApplication()).editSharePrefs().apply();
        ShortcutBadger.applyCount(this, count);

        final FirebaseAuth mAuth = FirebaseAuth.getInstance();
        final DatabaseReference mData = FirebaseDatabase.getInstance().getReference(Global.USERS);
        final DatabaseReference mData2 = FirebaseDatabase.getInstance().getReference(Global.USERS);
        mData.child(mAuth.getCurrentUser().getUid()).child(Global.GROUPS).child(senderName).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try {
                    UserIn data = dataSnapshot.getValue(UserIn.class);
                    noUnread[0] = data.getNoOfUnread();
                    //message count
                    if (noUnread[0] > 0)
                        noUnread[0] = noUnread[0] - 1;
                    else
                        noUnread[0] = 0;


                    Map<String, Object> map2 = new HashMap<>();
                    map2.put("noOfUnread", noUnread[0]);
                    mData2.child(mAuth.getCurrentUser().getUid()).child(Global.GROUPS).child(senderName).updateChildren(map2).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {

                        }
                    });
                } catch (NullPointerException e) {
                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }



    public void playNotSound(Activity acc) {
        try {
            AudioManager audioManager = (AudioManager) acc.getApplicationContext().getSystemService(Context.AUDIO_SERVICE);


            switch (audioManager.getRingerMode()) {
                case AudioManager.RINGER_MODE_NORMAL:
                    MediaPlayer mediaPlayer = new MediaPlayer();

                    AssetFileDescriptor descriptor = Global.currentactivity.getAssets().openFd("notsound.mp3");
                    mediaPlayer.setDataSource(descriptor.getFileDescriptor(), descriptor.getStartOffset(), descriptor.getLength());
                    descriptor.close();

                    mediaPlayer.prepare();
                    mediaPlayer.setVolume(1f, 1f);
                    mediaPlayer.setLooping(false);
                    mediaPlayer.start();
                    break;
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Uri setMyNotification(String path) {
        File sdfile = new File(path);
        ContentValues values = new ContentValues();
        values.put(MediaStore.MediaColumns.DATA, sdfile.getAbsolutePath());
        values.put(MediaStore.MediaColumns.TITLE, "38");
        values.put(MediaStore.MediaColumns.MIME_TYPE, "audio/*");
        values.put(MediaStore.Audio.Media.IS_RINGTONE, false);
        values.put(MediaStore.Audio.Media.IS_NOTIFICATION, true);
        values.put(MediaStore.Audio.Media.IS_ALARM, false);
        values.put(MediaStore.Audio.Media.IS_MUSIC, false);

        Uri uri = MediaStore.Audio.Media.getContentUriForPath(sdfile.getAbsolutePath());
        Uri newUri;
        ContentValues cv = new ContentValues();
        Cursor cursor = this.getContentResolver().query(uri, null, MediaStore.MediaColumns.DATA + "=?", new String[]{path}, null);
        if (cursor.moveToNext() && cursor.getCount() > 0) {
            String _id = cursor.getString(0);
            cv.put(MediaStore.MediaColumns.DATA, sdfile.getAbsolutePath());
            cv.put(MediaStore.MediaColumns.TITLE, "38");
            cv.put(MediaStore.MediaColumns.MIME_TYPE, "audio/*");
            cv.put(MediaStore.Audio.Media.IS_NOTIFICATION, true);
            cv.put(MediaStore.Audio.Media.IS_RINGTONE, true);
            cv.put(MediaStore.Audio.Media.IS_ALARM, false);
            cv.put(MediaStore.Audio.Media.IS_MUSIC, false);
            getContentResolver().update(uri, cv, MediaStore.Audio.Media.DATA + "=?", new String[]{path});
            newUri = ContentUris.withAppendedId(uri, Long.valueOf(_id));
        } else {
            newUri = getApplicationContext().getContentResolver().insert(uri, values);
        }
        cursor.close();

        //  RingtoneManager.setActualDefaultRingtoneUri(getApplicationContext(), RingtoneManager.TYPE_NOTIFICATION, newUri);
        return newUri;

    }


    /** New notifications*/
    void newNotify(Map<String, String> map) {
        Log.wtf("tawgeh",type);
        String type = map.get("nType");
        String senderId = map.get("senderId");

        if (!Global.blockList.contains(senderId))
        {

            String Mid = map.get("Mid");
            notificationId = Mid.split("_");
            oneTimeId = (int) Long.parseLong(notificationId[2]);

            switch (type) {
                case "message":
                    if (mAuth.getCurrentUser().getUid() != null)
                        Log.wtf("tawgeh",type);
                        GetMessage(map);
                    break;

                case "messageGroup":
                    Log.wtf("tawgeh",type);
                    GetMessageGroup(map);
                    break;
                case "addG":
                    Log.wtf("tawgeh",type);
                    AddGroup(map);
                    break;
                case "deleteMess":
                    Log.wtf("tawgeh",type);
                    DeleteMessage(map);
                    break;
                case "deleteGroup":
                    Log.wtf("tawgeh",type);
                    DeleteMessageG(map);
                    break;
                case "voiceCall":
                    Log.wtf("tawgeh",type);
                    Call(map);
                    break;
                case "videoCall":
                    Log.wtf("tawgeh",type);
                    Call(map);
                    break;


            }
        }


    }

    void GetMessage(Map<String, String> map) {

        String react;
        senderId = map.get("senderId");
        senderName = map.get("senderName");
        senderAva = map.get("senderAva");
        Mid = map.get("Mid");
        to = map.get("to");
        message = map.get("message");
        react = map.get("react");

        message = encryption.decryptOrNull(message);
        Log.wtf("react",react);
        Log.wtf("react",message);

        if (react != null){
            String prefixR = "";
            react = encryption.decryptOrNull(react);
            if (message.contains("react//like//"))
                prefixR = getResources().getString(com.andrew.link.R.string.likeR);
            else if (message.contains("react//funny//"))
                prefixR = getResources().getString(com.andrew.link.R.string.funnyR);
            else if (message.contains("react//love//"))
                prefixR = getResources().getString(com.andrew.link.R.string.loveR);
            else if (message.contains("react//sad//"))
                prefixR = getResources().getString(com.andrew.link.R.string.sadR);
            else if (message.contains("react//angry//"))
                prefixR = getResources().getString(com.andrew.link.R.string.angryR);

            try {
                if (react.isEmpty())
                    message = senderName + " " + prefixR;
                else
                    message = senderName + " " + prefixR + " , " + react;
            } catch (NullPointerException e) {

            }
            //check online
            DatabaseReference chatDelete = FirebaseDatabase.getInstance().getReference(Global.CHATS);
            Query query = chatDelete.child(mAuth.getCurrentUser().getUid()).child(senderId).child(Global.Messages).child(Mid);
            query.keepSynced(true);
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        OnlineGetter onlineGetter = dataSnapshot.getValue(OnlineGetter.class);
                        deleted = onlineGetter.isDeleted();
                        if (!deleted) {
                            try {
                                if (Global.currentactivity != null || Global.currentfragment != null) {
                                    online = true;
                                    tawgeh();
                                } else {
                                    online = false;
                                    tawgeh();

                                }

                            } catch (NullPointerException e) {
                                online = false;
                                tawgeh();
                            }
                        }
                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }else{

            //check online
            DatabaseReference chatDelete = FirebaseDatabase.getInstance().getReference(Global.CHATS);
            Query query = chatDelete.child(mAuth.getCurrentUser().getUid()).child(senderId).child(Global.Messages).child(Mid);
            query.keepSynced(true);
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        OnlineGetter onlineGetter = dataSnapshot.getValue(OnlineGetter.class);
                        deleted = onlineGetter.isDeleted();
                        if (!deleted) {
                            try {
                                if (Global.currentactivity != null || Global.currentfragment != null) {
                                    online = true;
                                    tawgeh();
                                } else {
                                    online = false;
                                    tawgeh();

                                }

                            } catch (NullPointerException e) {
                                online = false;
                                tawgeh();
                            }
                        }
                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

        }





    }

    void GetMessageGroup(Map<String, String> map) {

        senderId = map.get("senderId");
        senderName = map.get("senderName");
        senderAva = map.get("senderAva");
        Mid = map.get("Mid");
        to = map.get("to");
        message = map.get("message");
        message = encryption.decryptOrNull(message);

        DatabaseReference groupdata = FirebaseDatabase.getInstance().getReference(Global.GROUPS);
        Query query = groupdata.child(senderId).child(Global.Messages).child(Mid);
        query.keepSynced(true);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    OnlineGetter onlineGetter = dataSnapshot.getValue(OnlineGetter.class);
                    deleted = onlineGetter.isDeleted();
                    if (!deleted) {
                        try {
                            if (Global.currentactivity != null) {
                                online = true;
                                tawgehG();
                            } else {
                                online = false;
                                tawgehG();

                            }

                        } catch (NullPointerException e) {
                            online = false;
                            tawgehG();
                        }
                    }
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



    }

    void AddGroup(Map<String, String> map) {
        senderId = map.get("senderId");
        senderName = map.get("senderName");
        senderAva = map.get("senderAva");
        Mid = map.get("Mid");
        to = map.get("to");
        message = map.get("message");
        message =encryption.decryptOrNull(message);
        try {
            if (Global.currentactivity != null) {
                online = true;
                tawgehG();
            } else {
                online = false;
                tawgehG();

            }

        } catch (NullPointerException e) {
            online = false;
            tawgehG();
        }
    }

    void DeleteMessage(Map<String, String> map) {
Log.wtf("delete",oneTimeId+"");
        Mid = map.get("Mid");
        to = map.get("to");
        message = map.get("message");

        //clear all notifications
        NotificationManager notificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
        try {
            if (notificationManager != null) {
                notificationManager.cancel(oneTimeId);
                countInverse();
            }
        } catch (NullPointerException e) {
            //nothing
        }

    }

    void DeleteMessageG(Map<String, String> map) {
        Mid = map.get("Mid");
        to = map.get("to");
        message = map.get("message");
        message = encryption.decryptOrNull(message);


        //clear all notifications
        NotificationManager notificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
        try {
            if (notificationManager != null) {
                notificationManager.cancel(oneTimeId);
                countInverseG();
            }
        } catch (NullPointerException e) {
            //nothing
        }



    }

    void Call(Map<String,String> map) {


        type = map.get("nType");
        senderId = map.get("senderId");
        senderName = map.get("senderName");
        senderAva =map.get("senderAva");
        Mid = map.get("Mid");
        to = map.get("to");
        message = map.get("message");
        channelId = encryption.decryptOrNull(map.get("channelId"));
        callerId = encryption.decryptOrNull(map.get("callerId"));
        callerName = encryption.decryptOrNull(map.get("callerName"));
        callerAva = encryption.decryptOrNull(map.get("callerAva"));

//        channelId = encryption.decryptOrNull(channelId);
//        callerId = encryption.decryptOrNull(callerId);
//        callerName = encryption.decryptOrNull(callerName);
//        callerAva = encryption.decryptOrNull(callerAva);
        DatabaseReference mcall = FirebaseDatabase.getInstance().getReference(Global.CALLS);

        mlogs = FirebaseDatabase.getInstance().getReference(Global.CALLS);

        conn = this;


        if (mAuth.getCurrentUser() != null) {

            if (mAuth.getCurrentUser().getUid().equals(to)) {


                //dark mode init
                if (mAuth.getCurrentUser() != null) {
                    if (!((AppBack) getApplication()).shared().getBoolean("dark" + mAuth.getCurrentUser().getUid(), false))

                        Global.DARKSTATE = false;
                    else
                        Global.DARKSTATE = true;

                }

                ((AppBack) getApplication()).getBlock();
                ((AppBack) getApplication()).getMute();
                if (!Global.mutelist.contains(senderId)) {

                    mcall.child(mAuth.getCurrentUser().getUid()).child(callerId).child(channelId).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                            try {

                                Usercalldata usercalldata = dataSnapshot.getValue(Usercalldata.class);
                                if (usercalldata.isIncall()) {

                                    if (Global.IncAActivity == null && Global.IncVActivity == null) {
                                        if (type.equals("videoCall")) {
                                            Intent jumptocall = new Intent(conn, IncCallActivity.class);
                                            jumptocall.putExtra("name", callerName);
                                            jumptocall.putExtra("ava", callerAva);
                                            jumptocall.putExtra("out", false);
                                            jumptocall.putExtra("channel_id", channelId);
                                            jumptocall.putExtra("id", callerId);
                                            jumptocall.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                            startActivity(jumptocall);
                                        } else {
                                            Intent jumptocall = new Intent(conn, IncAudioActivity.class);
                                            jumptocall.putExtra("name", callerName);
                                            jumptocall.putExtra("ava", callerAva);
                                            jumptocall.putExtra("out", false);
                                            jumptocall.putExtra("channel_id", channelId);
                                            jumptocall.putExtra("id", callerId);
                                            jumptocall.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                            startActivity(jumptocall);
                                        }
                                    } else {
                                        Map<String, Object> map = new HashMap<>();
                                        map.put("incall", false);
                                        mlogs.child(mAuth.getCurrentUser().getUid()).child(callerId).child(channelId).updateChildren(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                mlogs.child(callerId).child(mAuth.getCurrentUser().getUid()).child(channelId).updateChildren(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                    }
                                                });
                                            }
                                        });
                                    }
                                }
                            } catch (NullPointerException e) {
                                Map<String, Object> map = new HashMap<>();
                                map.put("incall", false);
                                mlogs.child(mAuth.getCurrentUser().getUid()).child(callerId).child(channelId).updateChildren(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        mlogs.child(callerId).child(mAuth.getCurrentUser().getUid()).child(channelId).updateChildren(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                            }
                                        });
                                    }
                                });
                            }


                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });


                }

            }
        }



    }
























}
