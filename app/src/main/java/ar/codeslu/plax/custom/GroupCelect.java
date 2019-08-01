package ar.codeslu.plax.custom;

import android.app.Activity;
import android.app.Dialog;
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


public class GroupCelect extends Dialog {

    private Activity c;
    public Dialog d;
    private Button delete, mute, block;
    private FirebaseAuth mAuth;
    private DatabaseReference mData;
    private String friendid;
    LinearLayout dialogM;
    public GroupCelect(Activity a, String friendid) {
        super(a);
        // TODO Auto-generated constructor stub
        this.c = a;
        this.friendid = friendid;
    }

    public GroupCelect(Context context, Activity c) {
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



        delete.setVisibility(View.GONE);
        block.setVisibility(View.GONE);
        mute.setVisibility(View.VISIBLE);

        mute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

    }
}