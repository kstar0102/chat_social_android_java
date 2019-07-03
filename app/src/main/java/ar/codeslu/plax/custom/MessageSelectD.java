package ar.codeslu.plax.custom;

import android.app.Activity;
import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import android.view.View;
import android.view.Window;
import android.widget.Button;
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
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.functions.FirebaseFunctions;
import com.google.firebase.functions.HttpsCallableResult;
import com.google.firebase.iid.FirebaseInstanceId;
import com.instacart.library.truetime.TrueTime;
import com.stfalcon.chatkit.me.Message;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import ar.codeslu.plax.R;
import ar.codeslu.plax.global.GetTime;
import ar.codeslu.plax.global.Global;
import ar.codeslu.plax.lists.Tokens;
import ar.codeslu.plax.notify.FCM;
import ar.codeslu.plax.notify.FCMresp;
import ar.codeslu.plax.notify.Sender;
import ar.codeslu.plax.story.Stories;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import se.simbio.encryption.Encryption;
import xute.storyview.StoryModel;

/**
 * Created by mostafa on 05/03/19.
 */


public class MessageSelectD extends Dialog {

    private Activity c;
    public Dialog d;
    private Button copy, delete, deleteall;
    // private boolean deleted;
    private String messId, friendid, messagetxt, type;
    public long time;
    Message message;
    private int position;
    private String lastM = "Message Deleted";
    private Encryption encryption;
    //0 for me 1 for others
    private int indicat;
    private boolean deletedChat;
    //delete chat
    boolean deleted = false;
    FirebaseAuth mAuth;
    DatabaseReference mData;
    Map<String, Object> map;
    Map<String, Object> mapd;
    LinearLayout dialogM;

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
        setContentView(R.layout.select_dialog);
        copy = findViewById(R.id.copy);
        delete = findViewById(R.id.delete);
        deleteall = findViewById(R.id.deleteall);
        dialogM = findViewById(R.id.dialogM);
        if (Global.DARKSTATE)
            dialogM.setBackground(ContextCompat.getDrawable(Global.conA, R.drawable.dialog_d));
        else
            dialogM.setBackground(ContextCompat.getDrawable(Global.conA, R.drawable.dialog_w));


//set data
        messId = message.getMessid();
        messagetxt = message.getText();
        type = message.getType();
        time = message.getCreatedAt().getTime();
        deleted = message.isDeleted();

        //encryption
        byte[] iv = new byte[16];
        encryption = Encryption.getDefault(Global.keyE, Global.salt, iv);
        if (!type.equals("text"))
            copy.setVisibility(View.GONE);

        if (!Global.check_int(Global.conA)) {
            delete.setVisibility(View.GONE);
            deleteall.setVisibility(View.GONE);
        } else {
            deleteall.setVisibility(View.GONE);

            FirebaseFunctions.getInstance().getHttpsCallable("getTime")
                    .call().addOnSuccessListener(new OnSuccessListener<HttpsCallableResult>() {
                @Override
                public void onSuccess(HttpsCallableResult httpsCallableResult) {
                    long now = (long) httpsCallableResult.getData();
                    int hours = (int) TimeUnit.MILLISECONDS.toHours(now - time);
                    if (hours >= 24)
                        deleteall.setVisibility(View.GONE);
                    else
                        deleteall.setVisibility(View.VISIBLE);


                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(c, c.getResources().getString(R.string.timeerror) + " " + c.getResources().getString(R.string.check_int), Toast.LENGTH_SHORT).show();

                }
            });

            if (deleted) {
                deleteall.setVisibility(View.GONE);
                copy.setVisibility(View.GONE);

            }

            if (indicat != 0) {
                delete.setVisibility(View.GONE);
                deleteall.setVisibility(View.GONE);
            }
        }
        if (indicat != 0) {
            delete.setVisibility(View.GONE);
            deleteall.setVisibility(View.GONE);
        }
        if (message.getStatus().equals("..") || message.getStatus().equals("X")) {
            delete.setVisibility(View.GONE);
            deleteall.setVisibility(View.GONE);
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

                dismiss();

            }
        });
        deleteall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAuth = FirebaseAuth.getInstance();
                mData = FirebaseDatabase.getInstance().getReference(Global.CHATS);
                map = new HashMap<String, Object>();
                map.put("deleted", true);
                map.put("message", lastM);
                map.put("type", "text");
                mData.child(mAuth.getCurrentUser().getUid()).child(friendid).child(Global.Messages).child(messId).updateChildren(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        mData.child(friendid).child(mAuth.getCurrentUser().getUid()).child(Global.Messages).child(messId).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()) {
                                    deletedChat = false;
                                    kamelDelete();
                                } else {
                                    deletedChat = true;
                                    kamelDelete();
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }
                });
                dismiss();
            }
        });
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
                                            sendMessNotify();

                                        }
                                    });
                                }
                            }
                        });
                    } else
                        sendMessNotify();

                }
            });
        } else {
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
    }

    private void sendMessNotify() {
        //fcm notify
        final FCM fcm = Global.getFCMservies();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference(Global.tokens);
        databaseReference.orderByKey().equalTo(friendid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Tokens tokens = snapshot.getValue(Tokens.class);
                    String Ptoken = FirebaseInstanceId.getInstance().getToken();
                    Map<String, String> map = new HashMap<>();
                    map.put("title", Ptoken + "#" + "ID" + "#" + "name" + "#" + "AVAU" + "#" + messId);
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
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}