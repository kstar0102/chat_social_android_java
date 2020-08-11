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
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.Continuation;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import ar.codeslu.plax.Contacts;
import ar.codeslu.plax.Groups.AddGroup;
import ar.codeslu.plax.Groups.Group;
import ar.codeslu.plax.Groups.ProfileGroup;
import ar.codeslu.plax.R;
import ar.codeslu.plax.auth.DataSet;
import ar.codeslu.plax.global.Global;
import ar.codeslu.plax.global.encryption;
import ar.codeslu.plax.lists.Tokens;
import ar.codeslu.plax.notify.FCM;
import ar.codeslu.plax.notify.FCMresp;
import ar.codeslu.plax.notify.Sender;
import de.hdodenhof.circleimageview.CircleImageView;
import dmax.dialog.SpotsDialog;
import id.zelory.compressor.Compressor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * Created by CodeSlu on 01/11/18.
 */


public class EditProfile extends Dialog {

    private Activity c;
    private FirebaseAuth mAuth;
    private DatabaseReference mData, mUser;
    private String friendid;

    private LinearLayout reactMenu;
    private Button next;
    private EmojiTextView txt;
    private EmojiEditText nameE;
    private CircleImageView ava;
    AlertDialog dialog;
    byte[] thumbData;
    private Bitmap compressedImageFile;
    int before = 0, beforeT = 0;


    public EditProfile(Activity a, String friendid) {
        super(a);
        // TODO Auto-generated constructor stub
        this.c = a;
        this.friendid = friendid;

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //view init
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.profile_edit_menu);
        reactMenu = findViewById(R.id.edit_menu);
        next = findViewById(R.id.nextS);
        txt = findViewById(R.id.name);
        nameE = findViewById(R.id.nameE);
        ava = findViewById(R.id.avatarSet);

        //firebase init
        mAuth = FirebaseAuth.getInstance();
        mData = FirebaseDatabase.getInstance().getReference(Global.GROUPS);
        mUser = FirebaseDatabase.getInstance().getReference(Global.USERS);

        //loader
        if (Global.DARKSTATE) {
            dialog = new SpotsDialog.Builder()
                    .setContext(c)
                    .setMessage(R.string.pleasW)
                    .setTheme(R.style.darkDialog)
                    .setCancelable(true)
                    .build();
        } else {
            dialog = new SpotsDialog.Builder()
                    .setContext(c)
                    .setMessage(R.string.pleasW)
                    .setCancelable(true)
                    .build();
        }

        if (Global.DARKSTATE) {
            reactMenu.setBackground(ContextCompat.getDrawable(Global.conA, R.drawable.react_bg_d));
            next.setBackground(ContextCompat.getDrawable(Global.conA, R.drawable.btn_bg));
            nameE.setBackground(ContextCompat.getDrawable(Global.conA, R.drawable.et_bg));
            nameE.setTextColor(Color.WHITE);
            txt.setTextColor(Color.WHITE);
            next.setTextColor(Color.WHITE);

        } else {
            reactMenu.setBackground(ContextCompat.getDrawable(Global.conA, R.drawable.react_bg));
            next.setBackground(ContextCompat.getDrawable(Global.conA, R.drawable.btn_bg_b));
            nameE.setBackground(ContextCompat.getDrawable(Global.conA, R.drawable.et_bg_b));
            nameE.setTextColor(Color.BLACK);
            txt.setTextColor(Color.BLACK);
            next.setTextColor(Color.BLACK);

        }
//set data
        if (String.valueOf(Global.currAva).equals("no")) {
            Picasso.get()
                    .load(R.drawable.group)
                    .placeholder(R.drawable.placeholder_gray) .error(R.drawable.errorimg)

                    .into(ava);
        } else {
            Picasso.get()
                    .load(Global.currAva)
                    .placeholder(R.drawable.placeholder_gray) .error(R.drawable.errorimg)

                    .into(ava);
        }
        nameE.setText(Global.currname);


        //encrypt

        ava.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ActivityCompat.checkSelfPermission(c, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(c, android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(c, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

                    ActivityCompat.requestPermissions(c, new String[]{android.Manifest.permission.CAMERA, android.Manifest.permission.READ_EXTERNAL_STORAGE, android.Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            801);
                } else {
                    CropImage.activity()
                            .setGuidelines(CropImageView.Guidelines.ON)
                            .setMinCropResultSize(400, 400)
                            .setAspectRatio(1, 1)
                            .start(c);
                }
            }
        });

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.show();
                if (!nameE.getText().toString().trim().isEmpty()) {
                    Map<String, Object> map = new HashMap<>();
                    map.put("name", nameE.getText().toString().trim());
                    mData.child(friendid).updateChildren(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            putIndBText(map);
                        }
                    })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    dialog.dismiss();
                                    Toast.makeText(c, R.string.error, Toast.LENGTH_SHORT).show();
                                }
                            });

                } else
                    Toast.makeText(c, R.string.name_empty, Toast.LENGTH_SHORT).show();
            }
        });


    }

    public void update(String imgpathL) {
        dialog.show();
        if (imgpathL == null) {
            Picasso.get()
                    .load(R.drawable.group)
                    .placeholder(R.drawable.placeholder_gray) .error(R.drawable.errorimg)

                    .into(ava);
        } else {
            Picasso.get()
                    .load(imgpathL)
                    .placeholder(R.drawable.placeholder_gray) .error(R.drawable.errorimg)

                    .into(ava);
        }
        compress(Uri.parse(imgpathL));
        uploadprofile(friendid);

    }

    public void uploadprofile(String groupid) {
        StorageReference mStorageRef = FirebaseStorage.getInstance().getReference();
        StorageReference riversRef = mStorageRef.child(Global.GroupAva + "/Ava_" + groupid + ".jpg");
        Map<String, Object> map = new HashMap<>();
        if (thumbData != null) {
            UploadTask uploadTask = riversRef.putBytes(thumbData);
            Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }

                    // Continue with the task to get the download URL
                    return riversRef.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        Uri downloadUrl = task.getResult();
                        Global.currAva = String.valueOf(downloadUrl);
                        map.put("avatar", encryption.encryptOrNull(String.valueOf(downloadUrl)));
                        mData.child(groupid).updateChildren(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                putIndB(map);

                            }
                        })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        dialog.dismiss();
                                        Toast.makeText(c, R.string.error, Toast.LENGTH_SHORT).show();
                                    }
                                });


                    }
                }
            });
        } else {
            Global.currAva = "no";
            map.put("avatar", encryption.encryptOrNull("no"));
            mData.child(groupid).updateChildren(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    putIndB(map);
                }
            })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            dialog.dismiss();
                            Toast.makeText(c, R.string.error, Toast.LENGTH_SHORT).show();
                        }
                    });


        }
    }

    public void compress(Uri imgLocalpath) {
        //compress the photo
        File newImageFile = new File(imgLocalpath.getPath());
        try {

            compressedImageFile = new Compressor(c)
                    .setMaxHeight(500)
                    .setMaxWidth(500)
                    .setQuality(50)
                    .compressToBitmap(newImageFile);

        } catch (IOException e) {
            e.printStackTrace();
        }

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        compressedImageFile.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        thumbData = baos.toByteArray();
    }

    public void putIndB(Map<String, Object> map2) {
        if (before < Global.currGUsers.size()) {
            mUser.child(Global.currGUsers.get(before)).child(Global.GROUPS).child(friendid).updateChildren(map2).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    if (before < Global.currGUsers.size() - 1) {
                        before += 1;
                        putIndB(map2);
                    } else {
                        Global.currAva = map2.get("avatar").toString();
                        before = 0;
                        dialog.dismiss();
                    }

                }
            });
        }
    }

    public void putIndBText(Map<String, Object> map2) {
        if (beforeT < Global.currGUsers.size()) {
            mUser.child(Global.currGUsers.get(beforeT)).child(Global.GROUPS).child(friendid).updateChildren(map2).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    if (beforeT < Global.currGUsers.size() - 1) {
                        beforeT += 1;
                        putIndBText(map2);
                    } else {
                        Global.currname = map2.get("name").toString();
                        beforeT = 0;
                        dialog.dismiss();
                        dismiss();
                    }
                }
            });
        }
    }

}