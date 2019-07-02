package ar.codeslu.plax.custom;

import android.app.Activity;
import android.app.Dialog;
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
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import ar.codeslu.plax.R;
import ar.codeslu.plax.fragments.Chats;
import ar.codeslu.plax.global.AppBack;
import ar.codeslu.plax.global.Global;

/**
 * Created by mostafa on 05/03/19.
 */


public class ChatCelect extends Dialog {

    private Activity c;
    public Dialog d;
    private Button delete, mute, block;
    private FirebaseAuth mAuth;
    private DatabaseReference mData;
    private String friendid;
    LinearLayout dialogM;
    public ChatCelect(Activity a, String friendid) {
        super(a);
        // TODO Auto-generated constructor stub
        this.c = a;
        this.friendid = friendid;
    }

    public ChatCelect(Context context, Activity c) {
        super(context);
        this.c = c;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.select_dialog_chats);
        mute = findViewById(R.id.mute);
        delete = findViewById(R.id.deletechat);
        block = findViewById(R.id.block);
        dialogM = findViewById(R.id.dialogM);

        if (Global.DARKSTATE)
            dialogM.setBackground(ContextCompat.getDrawable(c, R.drawable.dialog_d));
        else
            dialogM.setBackground(ContextCompat.getDrawable(c, R.drawable.dialog_w));

        //firebase
        mAuth = FirebaseAuth.getInstance();
        mData = FirebaseDatabase.getInstance().getReference(Global.CHATS);
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mData.child(mAuth.getCurrentUser().getUid()).child(friendid).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Chats.refreshL();
                            ((AppBack) c.getApplication()).getdialogdb(mAuth.getCurrentUser().getUid());
                            ((AppBack) c.getApplication()).getchatsdb(friendid);

                            Global.messG.clear();
                            deletebyId(friendid);

                            //local store
                            ((AppBack) c.getApplication()).setchatsdb(friendid);
                            ((AppBack) c.getApplication()).setdialogdb(mAuth.getCurrentUser().getUid());
                            Toast.makeText(c, R.string.chat_dee, Toast.LENGTH_SHORT).show();
                        } else
                            Toast.makeText(c, R.string.error, Toast.LENGTH_SHORT).show();
                    }
                });
                dismiss();
            }
        });
        mute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        block.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }
    public void deletebyId(String Fid) {
        int i = 0;
        for (i = 0; i < Global.diaG.size(); i++) {
            if (Global.diaG.get(i).getId().equals(Fid)) {
                Global.diaG.remove(i);
                break;
            }

        }
    }
}