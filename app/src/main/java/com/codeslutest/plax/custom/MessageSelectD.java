package com.codeslutest.plax.custom;

import android.app.Activity;
import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;

import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.stfalcon.chatkit.me.Message;
import com.stfalcon.chatkit.me.MessageFav;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import com.codeslutest.plax.Chat;
import com.codeslutest.plax.Groups.Group;
import com.codeslutest.plax.R;
import com.codeslutest.plax.global.AppBack;
import com.codeslutest.plax.global.Global;
import com.codeslutest.plax.global.encryption;
import com.codeslutest.plax.lists.Tokens;
import com.codeslutest.plax.notify.FCM;
import com.codeslutest.plax.notify.FCMresp;
import com.codeslutest.plax.notify.Sender;
import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * Created by CodeSlu on 05/03/19.
 */


public class MessageSelectD extends Dialog {

    private Activity c;
    public Dialog d;
    private ImageView copy, delete, translate, edit;
    CircleImageView avatar;
    // private boolean deleted;
    private String messId, friendid, messagetxt, type;
    public long time;
    Message message;
    private int position;
    private String lastM = "Message Deleted";

    //0 for me 1 for others
    private int indicat;
    private boolean deletedChat;
    //delete chat
    boolean deleted = false;
    FirebaseAuth mAuth;
    DatabaseReference mData, userD, mTime, mFav, mMess, mGroup;
    Map<String, Object> map;
    Map<String, Object> mapd;
    LinearLayout dialogM;
    boolean favB = false;
    FCM fcm;

    //indicate 1 other // 0 me
    public MessageSelectD(Activity a, Message message, String friendid, int indicat, int position) {
        super(a);
        // TODO Auto-generated constructor stub
        this.c = a;
        this.friendid = friendid;
        this.message = message;
        this.indicat = indicat;
        this.position = position;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_imcoming_select);
        copy = findViewById(R.id.copy);
        delete = findViewById(R.id.delete);
        translate = findViewById(R.id.translate);
        edit = findViewById(R.id.edit);
//        avatar = findViewById(R.id.avatarSet);
//        retry = findViewById(R.id.retry);
//        fav = findViewById(R.id.fav);
//        forward = findViewById(R.id.forward);
//        reply = findViewById(R.id.reply);
//        deleteall = findViewById(R.id.deleteall);
        dialogM = findViewById(R.id.dialogM);
//        if (Global.DARKSTATE)
//            dialogM.setBackground(ContextCompat.getDrawable(Global.conA, R.drawable.react_bg_d));
//        else
//            dialogM.setBackground(ContextCompat.getDrawable(Global.conA, R.drawable.react_bg));

        fcm = Global.getFCMservies();
        mAuth = FirebaseAuth.getInstance();
        mMess = FirebaseDatabase.getInstance().getReference(Global.CHATS);
        mFav = FirebaseDatabase.getInstance().getReference(Global.FAV);
        mGroup = FirebaseDatabase.getInstance().getReference(Global.GROUPS);
        userD = FirebaseDatabase.getInstance().getReference(Global.USERS);

//set data
        messId = message.getMessid();
        messagetxt = message.getText();
        type = message.getType();
        time = message.getCreatedAt().getTime();
        deleted = message.isDeleted();


        if (!type.equals("text") || deleted)
            copy.setVisibility(View.GONE);


        if (message.getStatus().equals("..") || !message.isChat()) {
            delete.setVisibility(View.GONE);

        } else if (message.getStatus().equals("X")) {
            delete.setVisibility(View.VISIBLE);
        }

        if (!Global.check_int(Global.conA)) {
            delete.setVisibility(View.GONE);

        } else {
            mTime = FirebaseDatabase.getInstance().getReference(Global.TIME);
            Map<String, Object> map = new HashMap<>();
            map.put("time", ServerValue.TIMESTAMP);
            mTime.updateChildren(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    mTime.child("time").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            long now = dataSnapshot.getValue(Long.class);
                            int hours = (int) TimeUnit.MILLISECONDS.toHours(now - time);
                            if (hours >= 24 || indicat != 0 || message.getStatus().equals("..") || message.getStatus().equals("X") || deleted){
                                edit.setVisibility(View.GONE);
                                translate.setVisibility(View.VISIBLE);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            Toast.makeText(c, c.getResources().getString(R.string.timeerror) + " " + c.getResources().getString(R.string.check_int), Toast.LENGTH_SHORT).show();

                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(c, c.getResources().getString(R.string.timeerror) + " " + c.getResources().getString(R.string.check_int), Toast.LENGTH_SHORT).show();
                }
            });


        }

        try {
            if (messId != null) {
                ((AppBack) c.getApplication()).getFav();
                if (halbine(Global.FavMess, messId) == -1) {
                    favB = false;
                } else {
                    favB = true;
                }
            }
        } catch (NullPointerException e) {

        }

        copy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ClipboardManager clipboard = (ClipboardManager) c.getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("Message", messagetxt);
                clipboard.setPrimaryClip(clip);
                Toast.makeText(Global.conA, R.string.mssg_cpd, Toast.LENGTH_SHORT).show();
                dismiss();
            }
        });
        //add message deleted
        lastM = encryption.encryptOrNull(lastM);
        mapd = new HashMap<>();

        mapd.put("lastmessage", lastM);
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
                if (message.isChat()) {
                    final FirebaseAuth mAuth = FirebaseAuth.getInstance();
                    final DatabaseReference mData = FirebaseDatabase.getInstance().getReference(Global.CHATS);
                    final Map<String, Object> map = new HashMap<String, Object>();
                    map.put("type", "delete");

                    mData.child(mAuth.getCurrentUser().getUid()).child(friendid).child(Global.Messages).child(messId).updateChildren(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (position == 0) {
                                mData.child(mAuth.getCurrentUser().getUid()).child(friendid).updateChildren(mapd).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        Toast.makeText(Global.conA, R.string.mss_dlt, Toast.LENGTH_SHORT).show();

                                    }
                                });
                            } else
                                Toast.makeText(Global.conA, R.string.mss_dlt, Toast.LENGTH_SHORT).show();
                        }
                    });


                }
            }
        });
    }

    private int getAvasCount = 0;

    private void kameDeleteGG(String mDeleteT) {

        Map<String, Object> map = new HashMap<>();
        map.put("lastmessage", mDeleteT);
        map.put("lastsender", mAuth.getCurrentUser().getUid());
        map.put("lastsenderava", Global.avaLocal);


        for (int i = 0; i < Global.currGUsers.size(); i++) {
            int finalI = i;
            userD.child(Global.currGUsers.get(i)).child(Global.GROUPS).child(friendid).updateChildren(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {

                    if (finalI == Global.currGUsers.size() - 1) {
                        Global.lastDeletedMessage = message;
                        Group.timerForDeleted();
                        sendMessNotifyG();
                    }
                }
            });
        }
    }

    private void kamelDelete() {
        if (!deletedChat) {
            mData.child(friendid).child(mAuth.getCurrentUser().getUid()).child(Global.Messages).child(messId).updateChildren(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (position == 0) {
                        mData.child(mAuth.getCurrentUser().getUid()).child(friendid).updateChildren(mapd).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (!deletedChat) {
                                    mData.child(friendid).child(mAuth.getCurrentUser().getUid()).updateChildren(mapd).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            Global.lastDeletedMessage = message;
                                            Chat.timerForDeleted();
                                            sendMessNotify();

                                        }
                                    });
                                }
                            }
                        });
                    } else {
                        Global.lastDeletedMessage = message;

                        Chat.timerForDeleted();
                        sendMessNotify();
                    }

                }
            });
        } else {
            if (position == 0) {
                mData.child(mAuth.getCurrentUser().getUid()).child(friendid).updateChildren(mapd).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Global.lastDeletedMessage = message;

                        Chat.timerForDeleted();
                        Toast.makeText(Global.conA, R.string.mss_dlt, Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                Toast.makeText(Global.conA, R.string.mss_dlt, Toast.LENGTH_SHORT).show();
                Global.lastDeletedMessage = message;
                Chat.timerForDeleted();
            }
        }
    }


    private void sendMessNotify() {

        //fcm notify
        final FCM fcm = Global.getFCMservies();
        DatabaseReference mTokenget = FirebaseDatabase.getInstance().getReference(Global.tokens);
        mTokenget.child(friendid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Tokens tokens = dataSnapshot.getValue(Tokens.class);
                String Ptoken = FirebaseInstanceId.getInstance().getToken();

                Map<String, String> map = new HashMap<>();
                map.put("nType","deleteMess");
                map.put("tokens",Ptoken);
                map.put("Mid",messId);
                map.put("to", friendid);
                map.put("message", lastM);
                Sender sender = new Sender(tokens.getTokens(), map);
                fcm.send(sender)
                        .enqueue(new Callback<FCMresp>() {
                            @Override
                            public void onResponse(Call<FCMresp> call, Response<FCMresp> response) {
                                Toast.makeText(Global.conA, R.string.mss_dlt, Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onFailure(Call<FCMresp> call, Throwable t) {
                            }
                        });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }


    private void sendMessNotifyG() {
        //fcm notify
        final FCM fcm = Global.getFCMservies();

        for (int i = 0; i < Global.currGUsers.size(); i++) {
            if (!Global.currGUsers.get(i).equals(mAuth.getCurrentUser().getUid())) {
                DatabaseReference mTokenget = FirebaseDatabase.getInstance().getReference(Global.tokens);
                int finalI = i;
                mTokenget.child(Global.currGUsers.get(i)).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Tokens tokens = dataSnapshot.getValue(Tokens.class);


                        Map<String, String> map = new HashMap<>();
                        map.put("nType","deleteGroup");
                        map.put("tokens",tokens.toString());
                        map.put("Mid",messId);
                        map.put("to", Global.currGUsers.get(finalI));
                        map.put("message", lastM);
                        Sender sender = new Sender(tokens.getTokens(), map);
                        fcm.send(sender)
                                .enqueue(new Callback<FCMresp>() {
                                    @Override
                                    public void onResponse(Call<FCMresp> call, Response<FCMresp> response) {
                                    }

                                    @Override
                                    public void onFailure(Call<FCMresp> call, Throwable t) {
                                    }
                                });

                    }


                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        }

    }

    public int halbine(ArrayList<MessageFav> ml, String id) {
        int j = 0, i = 0;
        for (i = 0; i < ml.size(); i++) {
            if (ml.get(i).getMessId().equals(id)) {
                j = 1;
                break;
            }

        }
        if (j == 1)
            return i;
        else
            return -1;

    }

    private String getHumanTimeText(long milliseconds) {
        return String.format("%02d:%02d",
                TimeUnit.MILLISECONDS.toMinutes(milliseconds),
                TimeUnit.MILLISECONDS.toSeconds(milliseconds) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(milliseconds)));
    }

    public void updateData(String mess, String messid, Object currTime, Map<String, Object> MessMap) {
        if (mAuth.getCurrentUser() != null) {
            if (message.isChat()) {
                Map<String, Object> map = new HashMap<>();
                Map<String, Object> map2 = new HashMap<>();

                map.put("avatar", Global.avaLocal);
                map.put("name", Global.nameLocal);
                map.put("nameL", Global.nameLocal);
                map.put("phone", Global.phoneLocal);
                map.put("id", mAuth.getCurrentUser().getUid());
                map.put("screen", Global.myscreen);
                map.put("lastmessage", mess);
                map.put("lastsender", mAuth.getCurrentUser().getUid());
                map.put("lastsenderava", Global.avaLocal);
                map.put("messDate", currTime);

                map2.put("avatar", Global.currAva);
                map2.put("name", Global.currname);
                map2.put("nameL", Global.currname);
                map2.put("phone", Global.currphone);
                map2.put("id", friendid);
                map2.put("screen", Global.currscreen);
                map2.put("lastmessage", mess);
                map2.put("lastsender", mAuth.getCurrentUser().getUid());
                map2.put("lastsenderava", Global.avaLocal);
                map2.put("messDate", currTime);


                mMess.child(mAuth.getCurrentUser().getUid()).child(friendid).updateChildren(map2).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        mMess.child(friendid).child(mAuth.getCurrentUser().getUid()).updateChildren(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                mMess.child(mAuth.getCurrentUser().getUid()).child(friendid).child(Global.Messages).child(message.getMessid()).updateChildren(MessMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        mMess.child(friendid).child(mAuth.getCurrentUser().getUid()).child(Global.Messages).child(message.getMessid()).updateChildren(MessMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {

                                                sendMessNotify(mess, messid);

                                            }
                                        });
                                    }
                                });
                            }
                        });
                    }
                });

            } else {
                mData.child(friendid).child(Global.Messages).child(message.getMessid())
                        .updateChildren(MessMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Map<String, Object> map2 = new HashMap<>();
                        map2.put("lastmessage", mess);
                        map2.put("lastsender", mAuth.getCurrentUser().getUid());
                        map2.put("lastsenderava", Global.avaLocal);
                        map2.put("messDate", currTime);

                        mData.child(friendid).updateChildren(map2).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                userD.child(mAuth.getCurrentUser().getUid()).child(Global.GROUPS).child(friendid).updateChildren(map2).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        sendMessNotifyG(mess, messid);
                                        for (int i = 0; i < Global.currGUsers.size(); i++) {
                                            int j = i;
                                            if (!Global.currGUsers.get(i).equals(mAuth.getCurrentUser().getUid())) {

                                                userD.child(Global.currGUsers.get(i)).child(Global.GROUPS).child(friendid).updateChildren(map2).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                    }
                                                });
                                            }

                                        }
                                    }
                                });
                            }
                        });
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });


            }


        }
    }

    private void sendMessNotify(final String message, final String Mid) {

        DatabaseReference mTokenget = FirebaseDatabase.getInstance().getReference(Global.tokens);
        mTokenget.child(friendid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (mAuth.getCurrentUser() != null) {

                    Tokens tokens = dataSnapshot.getValue(Tokens.class);
                    Map<String, String> map = new HashMap<>();
                    map.put("nType","message");
                    map.put("tokens",tokens.toString());
                    map.put("senderId",mAuth.getCurrentUser().getUid());
                    map.put("senderName",Global.nameLocal);
                    map.put("senderAva",Global.avaLocal);
                    map.put("Mid",Mid);
                    map.put("to", friendid);
                    map.put("message", message);
                    Sender sender = new Sender(tokens.getTokens(), map);
                    fcm.send(sender)
                            .enqueue(new Callback<FCMresp>() {
                                @Override
                                public void onResponse(Call<FCMresp> call, Response<FCMresp> response) {
                                }

                                @Override
                                public void onFailure(Call<FCMresp> call, Throwable t) {

                                }
                            });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void sendMessNotifyG(final String message, final String Mid) {

        for (int i = 0; i < Global.currGUsers.size(); i++) {
            if (!Global.currGUsers.get(i).equals(mAuth.getCurrentUser().getUid())) {
                DatabaseReference mTokenget = FirebaseDatabase.getInstance().getReference(Global.tokens);
                int finalI = i;
                mTokenget.child(Global.currGUsers.get(i)).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Tokens tokens = dataSnapshot.getValue(Tokens.class);
                        Map<String, String> map = new HashMap<>();
                        map.put("nType","messageGroup");
                        map.put("tokens",tokens.toString());
                        map.put("senderId",mAuth.getCurrentUser().getUid());
                        map.put("senderName",Global.currname);
                        map.put("senderAva",Global.currAva);
                        map.put("Mid",Mid);
                        map.put("to", Global.currGUsers.get(finalI));
                        map.put("message", message);
                        Sender sender = new Sender(tokens.getTokens(), map);
                        fcm.send(sender)
                                .enqueue(new Callback<FCMresp>() {
                                    @Override
                                    public void onResponse(Call<FCMresp> call, Response<FCMresp> response) {
                                    }

                                    @Override
                                    public void onFailure(Call<FCMresp> call, Throwable t) {
                                    }
                                });

                    }


                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        }


    }


}