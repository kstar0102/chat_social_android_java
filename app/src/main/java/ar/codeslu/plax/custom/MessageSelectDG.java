package ar.codeslu.plax.custom;

import android.app.Activity;
import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

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
import com.stfalcon.chatkit.me.Message;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import ar.codeslu.plax.R;
import ar.codeslu.plax.global.Global;
import ar.codeslu.plax.lists.Tokens;
import ar.codeslu.plax.notify.FCM;
import ar.codeslu.plax.notify.FCMresp;
import ar.codeslu.plax.notify.Sender;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import se.simbio.encryption.Encryption;

/**
 * Created by mostafa on 05/03/19.
 */


public class MessageSelectDG extends Dialog {

    private Activity c;
    public Dialog d;
    private Button copy,delete, deleteall,retry;
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
//indicate 1 other // 0 me
    public MessageSelectDG(Activity a, Message message, String friendid, int indicat, int position) {
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
        retry = findViewById(R.id.retry);
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

        retry.setVisibility(View.GONE);
        delete.setVisibility(View.GONE);

        //encryption
        byte[] iv = new byte[16];
        encryption = Encryption.getDefault(Global.keyE, Global.salt, iv);
        if (!type.equals("text") || deleted)
            copy.setVisibility(View.GONE);

        if (message.getStatus().equals(".."))
            delete.setVisibility(View.GONE);
        else if(message.getStatus().equals("X"))
        {
            retry.setVisibility(View.VISIBLE);
        }


        if (!Global.check_int(Global.conA)) {
            delete.setVisibility(View.GONE);
            deleteall.setVisibility(View.GONE);
            retry.setVisibility(View.GONE);

        } else {
            deleteall.setVisibility(View.GONE);
            FirebaseFunctions.getInstance().getHttpsCallable("getTime")
                    .call().addOnSuccessListener(new OnSuccessListener<HttpsCallableResult>() {
                @Override
                public void onSuccess(HttpsCallableResult httpsCallableResult) {
                    long now = (long) httpsCallableResult.getData();
                    int hours = (int) TimeUnit.MILLISECONDS.toHours(now - time);
                    if (hours >= 24 || indicat != 0|| message.getStatus().equals("..")||message.getStatus().equals("X")||deleted)
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

        retry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(Global.conA, c.getString(R.string.we_will_soon), Toast.LENGTH_SHORT).show();

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

//    public void sendText()
//    {
//            editor.putString("chatM_" + friendId + "_" + mAuth.getCurrentUser().getUid(), message.getText().toString());
//            editor.apply();
//            Global.yourM = false;
//            encrypM = String.valueOf(input[0]).trim();
//            encrypM = encryption.encryptOrNull(encrypM);
//            currTime = ServerValue.TIMESTAMP;
//            messidL = mAuth.getCurrentUser().getUid() + "_" + friendId + "_" + String.valueOf(System.currentTimeMillis());
//            //send owner data to friend
//            mAuth = FirebaseAuth.getInstance();
//            Map<String, Object> map = new HashMap<>();
//            map.put("avatar",Global.avaLocal);
//            map.put("name",Global.nameLocal);
//            map.put("nameL",Global.nameLocal);
//            map.put("phone",Global.phoneLocal);
//            map.put("id", mAuth.getCurrentUser().getUid());
//            map.put("screen", Global.myscreen);
//            map.put("lastmessage", encrypM);
//            map.put("lastsender", mAuth.getCurrentUser().getUid());
//            map.put("lastsenderava",Global.avaLocal);
//            map.put("messDate", currTime);
//
//            //local message
//            messagesAdapter.clear();
//            messageLocal = new MessageIn(encrypM, "text", "..", mAuth.getCurrentUser().getUid(), System.currentTimeMillis(), false, false, messidL, "no");
//            try {
//                Global.messG.add(messageLocal);
//                //local store
//                ((AppBack) getApplication()).setchatsdb(friendId);
//            } catch (NullPointerException e) {
//                Global.messG = new ArrayList<>();
//                Global.messG.add(messageLocal);
//                //local store
//                ((AppBack) getApplication()).setchatsdb(friendId);
//            }
//
//            //update last message if dialog exist
//            Chats chat = new Chats();
//            //update dialog if not exist
//            UserIn dialog = new UserIn(Global.currname, Global.currstatue, Global.currAva, Global.currphone, friendId, messageLocal.getMessage(), mAuth.getCurrentUser().getUid(), Global.avaLocal, messageLocal.getTime(), 0, Global.currscreen);
//            ArrayList<UserIn> tempoo = new ArrayList<>();
//            tempoo.clear();
//            tempoo.add(dialog);
//            Global.userrG = dialog;
//            Global.Dialogonelist = tempoo;
//            Global.Dialogid = friendId;
//            Global.DialogM = messageLocal;
//            //    ((AppBack) getApplication()).getdialogdb(mAuth.getCurrentUser().getUid());
//            chat.onNewMessage();
//
//
//            messagesAdapter.addToEnd(MessageData.getMessages(), true);
//            messagesAdapter.notifyDataSetChanged();
//            messagesList.getLayoutManager().smoothScrollToPosition(messagesList, null, 0);
//            ///////
//
//            mData.child(friendId).child(mAuth.getCurrentUser().getUid()).updateChildren(map).addOnSuccessListener(new OnSuccessListener<Void>() {
//                @Override
//                public void onSuccess(Void aVoid) {
//                    sendM();
//                }
//            });
//       
//    }





}