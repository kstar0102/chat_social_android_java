package ar.codeslu.plax.custom;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;
import com.vanniktech.emoji.EmojiEditText;
import com.vanniktech.emoji.EmojiTextView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import ar.codeslu.plax.Chat;
import ar.codeslu.plax.Qr;
import ar.codeslu.plax.R;
import ar.codeslu.plax.global.Global;
import de.hdodenhof.circleimageview.CircleImageView;
import dmax.dialog.SpotsDialog;
import id.zelory.compressor.Compressor;


/**
 * Created by CodeSlu on 01/11/18.
 */


public class QrResult extends Dialog {

    private Activity c;
    private FirebaseAuth mAuth;
    private LinearLayout reactMenu;
    private Button next;
    private EmojiTextView txt;
    private CircleImageView ava;
    private String name, avatar, id, phone;
    private boolean screen;


    public QrResult(Activity a, String avatar, String name, String id, String phone, boolean screen) {
        super(a);
        // TODO Auto-generated constructor stub
        this.c = a;
        this.avatar = avatar;
        this.name = name;
        this.id = id;
        this.phone = phone;
        this.screen = screen;
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
                Intent intent = new Intent(c, Chat.class);
                intent.putExtra("id", id);
                intent.putExtra("name", name);
                intent.putExtra("ava", avatar);
                intent.putExtra("phone", phone);
                intent.putExtra("screen", screen);
                Global.currphone = phone;
                c.startActivity(intent);
            }
        });


    }

}