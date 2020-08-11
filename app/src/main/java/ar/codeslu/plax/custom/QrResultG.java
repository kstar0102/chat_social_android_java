package ar.codeslu.plax.custom;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.stfalcon.chatkit.me.GroupIn;
import com.vanniktech.emoji.EmojiTextView;

import ar.codeslu.plax.Chat;
import ar.codeslu.plax.Groups.Group;
import ar.codeslu.plax.R;
import ar.codeslu.plax.global.Global;
import de.hdodenhof.circleimageview.CircleImageView;


/**
 * Created by CodeSlu on 01/11/18.
 */


public class QrResultG extends Dialog {

    private Activity c;
    private FirebaseAuth mAuth;
    private LinearLayout reactMenu;
    private Button next;
    private EmojiTextView txt;
    private CircleImageView ava;
    private String name, avatar, id;
    private DatabaseReference groupQr;


    public QrResultG(Activity a, String avatar, String name, String id) {
        super(a);
        // TODO Auto-generated constructor stub
        this.c = a;
        this.avatar = avatar;
        this.name = name;
        this.id = id;

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //view init
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.qrprofile);
        reactMenu = findViewById(R.id.edit_menu);
        next = findViewById(R.id.nextS);
        txt = findViewById(R.id.name);
        ava = findViewById(R.id.avatarSet);
        //firebase init
        mAuth = FirebaseAuth.getInstance();
        next.setVisibility(View.GONE);

        groupQr = FirebaseDatabase.getInstance().getReference(Global.GROUPS).child(id);

        groupQr.addListenerForSingleValueEvent(new ValueEventListener() {
    @Override
    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
        if (dataSnapshot.exists()){
            GroupIn groupIn = dataSnapshot.getValue(GroupIn.class);

            if (groupIn.getUsers().contains(mAuth.getCurrentUser().getUid()))
            {
                next.setVisibility(View.VISIBLE);

            }
            else
            {
                next.setVisibility(View.INVISIBLE);

            }

        }else{
            next.setVisibility(View.INVISIBLE);

        }
    }

    @Override
    public void onCancelled(@NonNull DatabaseError databaseError) {

    }
});

        if (Global.DARKSTATE) {
            reactMenu.setBackground(ContextCompat.getDrawable(c, R.drawable.react_bg_d));
            next.setBackground(ContextCompat.getDrawable(c, R.drawable.btn_bg));
            txt.setTextColor(Color.WHITE);
            next.setTextColor(Color.WHITE);

        } else {
            reactMenu.setBackground(ContextCompat.getDrawable(c, R.drawable.react_bg));
            next.setBackground(ContextCompat.getDrawable(c, R.drawable.btn_bg_b));
            txt.setTextColor(Color.BLACK);
            next.setTextColor(Color.BLACK);

        }

//set data
        if (String.valueOf(avatar).equals("no")) {
            Picasso.get()
                    .load(R.drawable.profile)
                    .placeholder(R.drawable.placeholder_gray).error(R.drawable.errorimg)
                    .into(ava);
        } else {
            Picasso.get()
                    .load(avatar)
                    .placeholder(R.drawable.placeholder_gray).error(R.drawable.errorimg)
                    .into(ava);
        }




        txt.setText(name);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(c, Group.class);
                intent.putExtra("id", id);
                intent.putExtra("name", name);
                intent.putExtra("ava", avatar);
                c.startActivity(intent);
            }
        });


    }

}