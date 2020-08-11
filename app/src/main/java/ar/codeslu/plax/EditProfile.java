package ar.codeslu.plax;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.ActivityCompat;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

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
import com.squareup.picasso.Target;
import com.stfalcon.chatkit.me.GroupIn;
import com.stfalcon.chatkit.me.MessageIn;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;
import com.vanniktech.emoji.EmojiEditText;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import ar.codeslu.plax.global.AppBack;
import ar.codeslu.plax.global.Global;
import ar.codeslu.plax.global.encryption;
import ar.codeslu.plax.lists.calls;
import de.hdodenhof.circleimageview.CircleImageView;
import dmax.dialog.SpotsDialog;
import id.zelory.compressor.Compressor;

public class  EditProfile extends AppCompatActivity {
    //View
    EmojiEditText name, statue;
    CircleImageView avatar;
    Button next;
    LinearLayout lyEdit;
    //Vars
    String nameS, statueS, avaS;
    Uri imgLocalpath;
    //Firebase
    FirebaseAuth mAuth;
    DatabaseReference mData, mchat, mGroup,mCalls;
    //compress
    private Bitmap compressedImageFile;
    //dialog
    AlertDialog dialog;
//encryption
    String called="";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        name = findViewById(R.id.nameE);
        statue = findViewById(R.id.statueE);
        avatar = findViewById(R.id.avatarSet);
        next = findViewById(R.id.nextS);
        lyEdit = findViewById(R.id.lyEdit);
        Global.currentactivity = this;

        //encryption



        //firebase init
        mAuth = FirebaseAuth.getInstance();
        mData = FirebaseDatabase.getInstance().getReference(Global.USERS);
        mchat = FirebaseDatabase.getInstance().getReference(Global.CHATS);
        mGroup = FirebaseDatabase.getInstance().getReference(Global.GROUPS);
        mCalls = FirebaseDatabase.getInstance().getReference(Global.CALLS);
        //dark mode init
        if (mAuth.getCurrentUser() != null) {
            if (!((AppBack) getApplication()).shared().getBoolean("dark" + mAuth.getCurrentUser().getUid(), false)) {
                getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            } else {
                getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            }
        }

        if (getIntent() != null) {
            Intent intent = getIntent();
            nameS = intent.getExtras().getString("name");
            avaS = intent.getExtras().getString("ava");
            statueS = intent.getExtras().getString("statue");
            name.setText(nameS);
            statue.setText(statueS);
            if (avaS.equals("no")) {
                Picasso.get()
                        .load(R.drawable.profile)
                        .placeholder(R.drawable.placeholder_gray) .error(R.drawable.errorimg)

                        .into(avatar);
                Picasso.get()
                        .load(R.drawable.bg)
                        .placeholder(R.drawable.placeholder_gray) .error(R.drawable.errorimg)

                        .into(new Target() {
                            @Override
                            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                                lyEdit.setBackground(new BitmapDrawable(EditProfile.this.getResources(), bitmap));
                            }

                            @Override
                            public void onBitmapFailed(Exception e, Drawable errorDrawable) {

                            }


                            @Override
                            public void onPrepareLoad(Drawable placeHolderDrawable) {

                            }
                        });
            } else {
                Picasso.get()
                        .load(avaS)
                        .placeholder(R.drawable.placeholder_gray) .error(R.drawable.errorimg)

                        .into(avatar);
                Picasso.get()
                        .load(avaS)
                        .placeholder(R.drawable.placeholder_gray) .error(R.drawable.errorimg)

                        .into(new Target() {
                            @Override
                            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                                lyEdit.setBackground(new BitmapDrawable(EditProfile.this.getResources(), bitmap));
                            }

                            @Override
                            public void onBitmapFailed(Exception e, Drawable errorDrawable) {

                            }


                            @Override
                            public void onPrepareLoad(Drawable placeHolderDrawable) {

                            }
                        });
            }

        }
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                next.setEnabled(false);
                if (!TextUtils.isEmpty(name.getText().toString().trim()) && name.getText().toString() != null) {
                    nameS = name.getText().toString().trim();
                    statueS = statue.getText().toString();
                    Map<String, Object> map = new HashMap<>();
                    map.put("name", nameS);
                    statueS = statueS.trim();
                    map.put("statue", statueS);
                    mData.child(mAuth.getCurrentUser().getUid()).updateChildren(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                        }
                    });
                    try {
                        //change calls
                        Map<String, Object> map33 = new HashMap<>();
                        map33.put("name", String.valueOf(nameS));
                        if (Global.callList != null) {
                            for (int i = 0; i < Global.callList.size(); i++) {
                                if(!Global.callList.get(i).getFrom().equals(mAuth.getCurrentUser().getUid()))
                                    called = Global.callList.get(i).getFrom();
                                else
                                    called = Global.callList.get(i).getTo();
                                mCalls.child(called).child(mAuth.getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        if(dataSnapshot.exists())
                                        {
                                            for (DataSnapshot data : dataSnapshot.getChildren()) {
                                                data.getRef().updateChildren(map33);
                                            }
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });

                            }
                        }
                        for (int i = 0; i < Global.diaG.size(); i++) {
                            Map<String, Object> map2 = new HashMap<>();
                            map2.put("name", nameS);
                            map2.put("nameL", nameS);

                            mchat.child(Global.diaG.get(i).getId()).child(mAuth.getCurrentUser().getUid()).updateChildren(map2).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    finish();
                                    Toast.makeText(EditProfile.this, R.string.prfl_updt, Toast.LENGTH_SHORT).show();

                                }
                            })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(EditProfile.this, R.string.error, Toast.LENGTH_SHORT).show();
                                        }
                                    });

                        }
                    } catch (NullPointerException e) {
                        finish();
                        Toast.makeText(EditProfile.this, R.string.prfl_updt, Toast.LENGTH_SHORT).show();

                    }
                    finish();
                } else {
                    next.setEnabled(true);
                    Toast.makeText(EditProfile.this, R.string.plz_name, Toast.LENGTH_SHORT).show();
                }

            }
        });
        //loader
        if (Global.DARKSTATE) {
            dialog = new SpotsDialog.Builder()
                    .setContext(this)
                    .setMessage(R.string.pleasW)
                    .setTheme(R.style.darkDialog)
                    .setCancelable(true)
                    .build();
        } else {
            dialog = new SpotsDialog.Builder()
                    .setContext(this)
                    .setMessage(R.string.pleasW)
                    .setCancelable(true)
                    .build();
        }
    }

    public void changeprofile(View view) {
        if (ActivityCompat.checkSelfPermission(EditProfile.this, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(EditProfile.this, android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(EditProfile.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(EditProfile.this, new String[]{android.Manifest.permission.CAMERA, android.Manifest.permission.READ_EXTERNAL_STORAGE, android.Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    801);
        } else {
            CropImage.activity()
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setMinCropResultSize(400, 400)
                    .setAspectRatio(1, 1)
                    .start(EditProfile.this);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                imgLocalpath = result.getUri();
                Picasso.get()
                        .load(imgLocalpath)
                        .placeholder(R.drawable.placeholder_gray) .error(R.drawable.errorimg)

                        .into(avatar);
                if (Global.check_int(this))
                    uploadprofile(imgLocalpath);
                else
                    Toast.makeText(this, R.string.check_conn, Toast.LENGTH_SHORT).show();
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }

    public void uploadprofile(Uri imgLocalpath) {
        dialog.show();
        //compress the photo
        File newImageFile = new File(imgLocalpath.getPath());
        try {

            compressedImageFile = new Compressor(EditProfile.this)
                    .setMaxHeight(500)
                    .setMaxWidth(500)
                    .setQuality(50)
                    .compressToBitmap(newImageFile);

        } catch (IOException e) {
            e.printStackTrace();
        }

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        compressedImageFile.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] thumbData = baos.toByteArray();
        StorageReference mStorageRef = FirebaseStorage.getInstance().getReference();
        StorageReference riversRef = mStorageRef.child(Global.AvatarS + "/Ava_" + mAuth.getCurrentUser().getUid() + ".jpg");
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
                    final Map<String, Object> map = new HashMap<>();
                    map.put(Global.avatar, String.valueOf(downloadUrl));
                    mData.child(mAuth.getCurrentUser().getUid()).updateChildren(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                if (String.valueOf(downloadUrl).equals("no")) {
                                    Picasso.get()
                                            .load(R.drawable.profile)
                                            .placeholder(R.drawable.placeholder_gray) .error(R.drawable.errorimg)

                                            .into(avatar);
                                } else {
                                    Picasso.get()
                                            .load(downloadUrl)
                                            .placeholder(R.drawable.placeholder_gray) .error(R.drawable.errorimg)

                                            .into(avatar);
                                }

                                try {
                                    if (Global.diaG != null) {
                                        for (int i = 0; i < Global.diaG.size(); i++) {
                                            mchat.child(Global.diaG.get(i).getId()).child(mAuth.getCurrentUser().getUid()).updateChildren(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                }
                                            })
                                                    .addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {
                                                            dialog.dismiss();
                                                            Toast.makeText(EditProfile.this, R.string.error, Toast.LENGTH_SHORT).show();
                                                        }
                                                    });

                                        }
                                    }
                                    //change calls
                                    Map<String, Object> map33 = new HashMap<>();
                                    map33.put("ava", String.valueOf(downloadUrl));
                                    if (Global.callList != null) {
                                        for (int i = 0; i < Global.callList.size(); i++) {
                                            if(!Global.callList.get(i).getFrom().equals(mAuth.getCurrentUser().getUid()))
                                                called = Global.callList.get(i).getFrom();
                                            else
                                                called = Global.callList.get(i).getTo();
                                            mCalls.child(called).child(mAuth.getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                    if(dataSnapshot.exists())
                                                    {
                                                        for (DataSnapshot data : dataSnapshot.getChildren()) {
                                                                data.getRef().updateChildren(map33);
                                                        }
                                                    }
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                                }
                                            });

                                        }
                                    }

                                    //change all last messages in group
                                    Map<String, Object> map2 = new HashMap<>();
                                    map2.put("lastsenderava", String.valueOf(downloadUrl));
                                    if (Global.diaGGG != null) {
                                        for (int i = 0; i < Global.diaGGG.size(); i++) {
                                            if (Global.diaGGG.get(i).getLastsender().equals(mAuth.getCurrentUser().getUid())) {
                                                mGroup.child(Global.diaGGG.get(i).getId()).updateChildren(map2).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                    }
                                                })
                                                        .addOnFailureListener(new OnFailureListener() {
                                                            @Override
                                                            public void onFailure(@NonNull Exception e) {
                                                                dialog.dismiss();
                                                                Toast.makeText(EditProfile.this, R.string.error, Toast.LENGTH_SHORT).show();
                                                            }
                                                        });
                                            }
                                        }
                                    }

                                    //update group messages

                                    Map<String, Object> map3 = new HashMap<>();
                                    map3.put("avatar", encryption.encryptOrNull(String.valueOf(downloadUrl)));
                                    if (Global.diaGGG != null) {
                                        for (int i = 0; i < Global.diaGGG.size(); i++) {
                                            mGroup.child(Global.diaGGG.get(i).getId()).child(Global.Messages).addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                    for (DataSnapshot data : dataSnapshot.getChildren()) {
                                                        MessageIn message = data.getValue(MessageIn.class);
                                                        if (message.getFrom().equals(mAuth.getCurrentUser().getUid())) {
                                                            data.getRef().updateChildren(map3);
                                                        }
                                                    }

                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                                }
                                            });
                                        }
                                    }
                                }catch (NullPointerException e)
                                {

                                }

                                Toast.makeText(EditProfile.this, R.string.image_update, Toast.LENGTH_SHORT).show();
                                dialog.dismiss();

                            } else {
                                Toast.makeText(EditProfile.this, R.string.image_fail, Toast.LENGTH_SHORT).show();
                                dialog.dismiss();

                            }
                        }
                    })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    dialog.dismiss();

                                }
                            });

                }
                dialog.dismiss();


            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        Global.currentactivity = this;
        AppBack myApp = (AppBack) this.getApplication();
        if (myApp.wasInBackground) {
            //init data
            Map<String, Object> map = new HashMap<>();
            map.put(Global.Online, true);
            if(mAuth.getCurrentUser() != null)
                mData.child(mAuth.getCurrentUser().getUid()).updateChildren(map);
            Global.local_on = true;
            //lock screen
            ((AppBack) getApplication()).lockscreen(((AppBack) getApplication()).shared().getBoolean("lock", false));
        }

        myApp.stopActivityTransitionTimer();
    }

    @Override
    public void onPause() {
        super.onPause();
        ((AppBack) this.getApplication()).startActivityTransitionTimer();
        Global.currentactivity = null;

    }

    public void deletephoto(View view) {
        final Map<String, Object> map = new HashMap<>();
        map.put("avatar", "no");
        mData.child(mAuth.getCurrentUser().getUid()).updateChildren(map).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {

try {
    for (int i = 0; i < Global.diaG.size(); i++) {

        mchat.child(Global.diaG.get(i).getId()).child(mAuth.getCurrentUser().getUid()).updateChildren(map).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {

            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(EditProfile.this, R.string.error, Toast.LENGTH_SHORT).show();
                    }
                });

    }

    //change calls
    Map<String, Object> map33 = new HashMap<>();
    map33.put("ava", String.valueOf("no"));
    if (Global.callList != null) {
        for (int i = 0; i < Global.callList.size(); i++) {
            if(!Global.callList.get(i).getFrom().equals(mAuth.getCurrentUser().getUid()))
                called = Global.callList.get(i).getFrom();
            else
                called = Global.callList.get(i).getTo();
            mCalls.child(called).child(mAuth.getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if(dataSnapshot.exists())
                    {
                        for (DataSnapshot data : dataSnapshot.getChildren()) {
                            data.getRef().updateChildren(map33);
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        }
    }

    //change all last messages in group
    Map<String, Object> map2 = new HashMap<>();
    map2.put("lastsenderava", String.valueOf("no"));
    if (Global.diaGGG != null) {
        for (int i = 0; i < Global.diaGGG.size(); i++) {
            if (Global.diaGGG.get(i).getLastsender().equals(mAuth.getCurrentUser().getUid())) {
                mGroup.child(Global.diaGGG.get(i).getId()).updateChildren(map2).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                    }
                })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                dialog.dismiss();
                                Toast.makeText(EditProfile.this, R.string.error, Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        }
    }

    //update group messages

    Map<String, Object> map3 = new HashMap<>();
    map3.put("avatar", encryption.encryptOrNull(String.valueOf("no")));
    if (Global.diaGGG != null) {
        for (int i = 0; i < Global.diaGGG.size(); i++) {
            mGroup.child(Global.diaGGG.get(i).getId()).child(Global.Messages).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot data : dataSnapshot.getChildren()) {
                        MessageIn message = data.getValue(MessageIn.class);
                        if (message.getFrom().equals(mAuth.getCurrentUser().getUid())) {
                            data.getRef().updateChildren(map3);
                        }
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }
}
catch (NullPointerException e)
{

}

                    Global.avaLocal = "no";
                    Picasso.get()
                            .load(R.drawable.profile)
                            .placeholder(R.drawable.placeholder_gray) .error(R.drawable.errorimg)

                            .into(avatar);
                    Picasso.get()
                            .load(R.drawable.bg)
                            .placeholder(R.drawable.placeholder_gray) .error(R.drawable.errorimg)

                            .into(new Target() {
                                @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
                                @Override
                                public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                                    lyEdit.setBackground(new BitmapDrawable(EditProfile.this.getResources(), bitmap));
                                }

                                @Override
                                public void onBitmapFailed(Exception e, Drawable errorDrawable) {

                                }


                                @Override
                                public void onPrepareLoad(Drawable placeHolderDrawable) {

                                }
                            });

                    Toast.makeText(EditProfile.this, R.string.photo_dlete, Toast.LENGTH_SHORT).show();
                } else
                    Toast.makeText(EditProfile.this, R.string.error, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
