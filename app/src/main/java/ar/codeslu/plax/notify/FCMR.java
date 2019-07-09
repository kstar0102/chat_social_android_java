package ar.codeslu.plax.notify;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

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
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.HashMap;
import java.util.Map;

import ar.codeslu.plax.Chat;
import ar.codeslu.plax.R;
import ar.codeslu.plax.global.AppBack;
import ar.codeslu.plax.global.Global;
import ar.codeslu.plax.lists.OnlineGetter;
import ar.codeslu.plax.lists.Tokens;
import me.leolin.shortcutbadger.ShortcutBadger;
import se.simbio.encryption.Encryption;

import com.stfalcon.chatkit.me.UserIn;
import com.tapadoo.alerter.Alerter;
import com.tapadoo.alerter.OnHideAlertListener;
import com.tapadoo.alerter.OnShowAlertListener;

/**
 * Created by Cryp2Code on 9/4/2018.
 */

public class FCMR extends FirebaseMessagingService {
    String title, message, name, ava, id, Mid, react = "", prefixR, messageReact = "";
    String[] array;
    int[] noUnread = {0};
    Encryption encryption;
    PendingIntent pIntent;
    TaskStackBuilder stackBuilder;
    boolean online = false, deleted = false;
    Context conn;
    //notifi id
    int oneTimeID;
    String notifiID[];
    //firebase
    FirebaseAuth mAuth;
    @Override
    public void onNewToken(String token) {
        Map<String, Object> map = new HashMap<>();
        map.put("tokens", token);
        DatabaseReference mToken = FirebaseDatabase.getInstance().getReference(Global.tokens);
        mToken.child(mAuth.getCurrentUser().getUid()).updateChildren(map).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {

            }
        });
    }
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        if (remoteMessage.getData() != null) {
            //get data
            Map<String, String> map = remoteMessage.getData();
            title = map.get("title");
            message = map.get("message");
            String[] array = title.split("#");
            name = array[2];
            id = array[1];
            ava = array[3];
            Mid = array[4];
            try {
                if(array.length > 6)
                {
                    react = array[5];
                    messageReact = array[6];
                }
            } catch (NullPointerException e) {
                react = "";
                messageReact = "";
            }

            notifiID = Mid.split("_");
            oneTimeID = (int) Long.parseLong(notifiID[2]);
            mAuth = FirebaseAuth.getInstance();
            conn = this;
            if (mAuth.getCurrentUser() != null) {
                //decrypt
                byte[] iv = new byte[16];
                encryption = Encryption.getDefault(Global.keyE, Global.salt, iv);
                message = encryption.decryptOrNull(message);
                messageReact = encryption.decryptOrNull(messageReact);
                if (react.equals("react")) {
                    if (message.contains("react//like//"))
                        prefixR = getResources().getString(R.string.likeR);
                    else if (message.contains("react//funny//"))
                        prefixR = getResources().getString(R.string.funnyR);
                    else if (message.contains("react//love//"))
                        prefixR = getResources().getString(R.string.loveR);
                    else if (message.contains("react//sad//"))
                        prefixR = getResources().getString(R.string.sadR);
                    else if (message.contains("react//angry//"))
                        prefixR = getResources().getString(R.string.angryR);

                    if (messageReact.isEmpty())
                        message = name + " " + prefixR;
                    else
                        message = name + " " + prefixR + " , " + messageReact;
                }
                if (!id.equals("ID")) {
                    Log.wtf("keyyy",id);

                    //check online
                    DatabaseReference chatDelete = FirebaseDatabase.getInstance().getReference(Global.CHATS);
                    Query query = chatDelete.child(mAuth.getCurrentUser().getUid()).child(id).child(Global.Messages).child(Mid);
                    query.keepSynced(true);
                    query.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                OnlineGetter onlineGetter = dataSnapshot.getValue(OnlineGetter.class);
                                deleted = onlineGetter.isDeleted();
                                Log.wtf("keyyy",deleted+"");

                                if (!deleted) {
                                    try {
                                        if (Global.currentactivity != null) {
                                            Log.wtf("keyyy",online+"");
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

                } else {
                    //clear all notifications
                    NotificationManager notificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
                    try {
                        if (notificationManager != null) {
                            notificationManager.cancel(oneTimeID);
                            countInverse();
                        }
                    } catch (NullPointerException e) {
                        //nothing
                    }
                }
            }
        }
    }

    public void tawgeh() {
        //go activity
        Intent intent = new Intent(this, Chat.class);
        intent.putExtra("name", name);
        intent.putExtra("id", id);
        intent.putExtra("ava", ava);
        stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addNextIntentWithParentStack(intent);
        pIntent = PendingIntent.getActivity(this, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        //tawgeh
        //if chat app is not running
        if (!online) {
            if (title.contains("voicecall")) {
                openvoicecall();
            } else if (title.contains("videocall")) {
                openvideocall();
            } else {
                Log.wtf("ffff",oneTimeID+"");

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                    CustomNotAPI25(message, name, oneTimeID);
                else
                    CustomNot(message, name, oneTimeID);
                int count = 0;
                //get data
                count = ((AppBack) getApplication()).shared().getInt("numN" + mAuth.getCurrentUser().getUid(), 0);
                //increment
                count++;
                //store it again
                ((AppBack) getApplication()).editSharePrefs().putInt("numN" + mAuth.getCurrentUser().getUid(), count);
                ((AppBack) getApplication()).editSharePrefs().apply();
                ShortcutBadger.applyCount(this, count);

            }
            //Delivered
            deliver();
            //get message count
            count();
        }
        //if app is running
        else {
            if (title.contains("voicecall")) {
                insideAcall();
            } else if (title.contains("videocall")) {
                insideVcall();
            } else {
                //Delivered
                deliver();
                if (Global.currentpageid.equals(id)) {
                    if(((AppBack) getApplication()).shared().getBoolean("sound", false))
                        playNotSound();

                    seenInpage();
                    if (react.equals("react")) {
                        Alerter.create(Global.currentactivity)
                                .setTitle(name)
                                .setText(message)
                                .enableSwipeToDismiss()
                                .setBackgroundColorRes(R.color.colorPrimaryDark2awy)
                                .setDuration(Global.NOTIFYTIME)
                                .show();
                    }
                } else {
                    count();
//inside notification
                    playNotSound();
                    Alerter.create(Global.currentactivity)
                            .setTitle(name)
                            .setText(message)
                            .enableSwipeToDismiss()
                            .setBackgroundColorRes(R.color.colorPrimaryDark2awy)
                            .setDuration(Global.NOTIFYTIME)
                            .setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Alerter.hide();
                                    Intent intent = new Intent(conn, Chat.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    intent.putExtra("name", name);
                                    intent.putExtra("id", id);
                                    intent.putExtra("ava", ava);
                                    startActivity(intent);
                                }
                            })
                            .show();
                }
            }

        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void CustomNotAPI25(String body, String string, int i) {
        Uri defaultsound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationChann notificationChann = new NotificationChann(getBaseContext());
        Notification.Builder builder = notificationChann.getPLAXNot(string, body, pIntent, defaultsound);
        notificationChann.getManager().notify(i, builder.build());
    }

    private void openvideocall() {
//        Intent intent = new Intent(this, Chat.class);
//        intent.putExtra("name", array[2]);
//        intent.putExtra("id", array[1]);
//        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        startActivity(intent);
//        Intent intent = new Intent(this, RatingDriver.class);
//        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        startActivity(intent);

    }

    private void openvoicecall() {
//        Intent intent = new Intent(this, Trip.class);
//        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        startActivity(intent);

    }

    public void CustomNot(String body, String title, int id) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext());
        builder.setDefaults(NotificationCompat.DEFAULT_ALL)
                .setContentTitle(title)
                .setContentText(body)
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.drawable.logo)
                .setLights(Color.BLUE,1000,1000)
                .setContentIntent(pIntent);
        NotificationManager manager = (NotificationManager) getBaseContext().getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(id, builder.build());
    }

    public void seenInpage() {
        final FirebaseAuth mAuth = FirebaseAuth.getInstance();
        final DatabaseReference mData = FirebaseDatabase.getInstance().getReference(Global.CHATS);
        mData.child(id).child(mAuth.getCurrentUser().getUid()).child(Global.Messages).addListenerForSingleValueEvent(new ValueEventListener() {
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
        mData.child(id).child(mAuth.getCurrentUser().getUid()).child(Global.Messages).child(Mid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Map<String, Object> map = new HashMap<String, Object>();
                    map.put("statue", "D✔");
                    mData.child(id).child(mAuth.getCurrentUser().getUid()).child(Global.Messages).child(Mid).updateChildren(map).addOnCompleteListener(new OnCompleteListener<Void>() {
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
        mData.child(mAuth.getCurrentUser().getUid()).child(id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                UserIn data = dataSnapshot.getValue(UserIn.class);
                noUnread[0] = data.getNoOfUnread();
                //message count
                noUnread[0] = noUnread[0] + 1;
                Map<String, Object> map2 = new HashMap<>();
                map2.put("noOfUnread", noUnread[0]);
                mData2.child(mAuth.getCurrentUser().getUid()).child(id).updateChildren(map2).addOnSuccessListener(new OnSuccessListener<Void>() {
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

    public void countInverse() {
        int count = 0;
        //get data
        count = ((AppBack) getApplication()).shared().getInt("numN", 0);
        //increment
        count--;
        //store it again
        ((AppBack) getApplication()).editSharePrefs().putInt("numN", count);
        ((AppBack) getApplication()).editSharePrefs().apply();
        ShortcutBadger.applyCount(this, count);

        id = notifiID[0];
        final FirebaseAuth mAuth = FirebaseAuth.getInstance();
        final DatabaseReference mData = FirebaseDatabase.getInstance().getReference(Global.CHATS);
        final DatabaseReference mData2 = FirebaseDatabase.getInstance().getReference(Global.CHATS);
        mData.child(mAuth.getCurrentUser().getUid()).child(id).addListenerForSingleValueEvent(new ValueEventListener() {
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
                mData2.child(mAuth.getCurrentUser().getUid()).child(id).updateChildren(map2).addOnSuccessListener(new OnSuccessListener<Void>() {
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

    public void insideAcall() {
        Alerter.create(Global.currentactivity)
                .setTitle(name)
                .setText(message)
                .setBackgroundColorRes(R.color.colorPrimaryDark2awy)
                .setDuration(2500)
                .addButton("Accept", R.style.AlertButton, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                    }
                })
                .addButton("Cancel", R.style.AlertButton, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                    }
                })
                .setOnShowListener(new OnShowAlertListener() {
                    @Override
                    public void onShow() {

                    }
                })
                .setOnHideListener(new OnHideAlertListener() {
                    @Override
                    public void onHide() {

                    }
                })
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Alerter.hide();
                        Intent intent = new Intent(Global.currentactivity, Chat.class);
                        intent.putExtra("name", name);
                        intent.putExtra("id", id);
                        intent.putExtra("ava", ava);
                        startActivity(intent);
                    }
                })
                .show();
    }

    public void insideVcall() {
        Alerter.create(Global.currentactivity)
                .setTitle(name)
                .setText(message)
                .setBackgroundColorRes(R.color.colorPrimaryDark2awy)
                .setDuration(2500)
                .addButton("Accept", R.style.AlertButton, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                    }
                })
                .addButton("Cancel", R.style.AlertButton, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                    }
                })
                .setOnShowListener(new OnShowAlertListener() {
                    @Override
                    public void onShow() {

                    }
                })
                .setOnHideListener(new OnHideAlertListener() {
                    @Override
                    public void onHide() {

                    }
                })
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Alerter.hide();
                        Intent intent = new Intent(Global.currentactivity, Chat.class);
                        intent.putExtra("name", name);
                        intent.putExtra("id", id);
                        intent.putExtra("ava", ava);
                        startActivity(intent);
                    }
                })
                .show();
    }

    public static void playNotSound() {
        try {
            MediaPlayer mediaPlayer = new MediaPlayer();

            AssetFileDescriptor descriptor = Global.currentactivity.getAssets().openFd("notsound.mp3");
            mediaPlayer.setDataSource(descriptor.getFileDescriptor(), descriptor.getStartOffset(), descriptor.getLength());
            descriptor.close();

            mediaPlayer.prepare();
            mediaPlayer.setVolume(1f, 1f);
            mediaPlayer.setLooping(false);
            mediaPlayer.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
