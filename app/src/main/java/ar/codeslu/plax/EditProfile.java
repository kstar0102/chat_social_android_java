package ar.codeslu.plax;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

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
import com.squareup.picasso.Target;
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
import de.hdodenhof.circleimageview.CircleImageView;
import dmax.dialog.SpotsDialog;
import id.zelory.compressor.Compressor;

public class EditProfile extends AppCompatActivity {
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
    DatabaseReference mData, mchat;
    //compress
    private Bitmap compressedImageFile;
    //dialog
    AlertDialog dialog;

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

        //firebase init
        mAuth = FirebaseAuth.getInstance();
        mData = FirebaseDatabase.getInstance().getReference(Global.USERS);
        mchat = FirebaseDatabase.getInstance().getReference(Global.CHATS);


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
                        .error(R.drawable.errorimg)
                        .into(avatar);
                Picasso.get()
                        .load(R.drawable.bg)
                        .error(R.drawable.errorimg)
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
                        .error(R.drawable.errorimg)
                        .into(avatar);
                Picasso.get()
                        .load(avaS)
                        .error(R.drawable.errorimg)
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
                    }
                    catch (NullPointerException e)
                    {
                        finish();
                        Toast.makeText(EditProfile.this, R.string.prfl_updt, Toast.LENGTH_SHORT).show();

                    }
                } else {
                    next.setEnabled(true);
                    Toast.makeText(EditProfile.this, R.string.plz_name, Toast.LENGTH_SHORT).show();
                }

            }
        });
        //dialog
        dialog = new SpotsDialog.Builder()
                .setContext(this)
                .setMessage(R.string.pleasW)
                .setCancelable(false)
                .build();
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
                        .error(R.drawable.errorimg)
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
        riversRef.putBytes(thumbData)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        final Uri downloadUrl = taskSnapshot.getDownloadUrl();
                        final Map<String, Object> map = new HashMap<>();
                        map.put(Global.avatar, String.valueOf(downloadUrl));
                        mData.child(mAuth.getCurrentUser().getUid()).updateChildren(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    if (String.valueOf(downloadUrl).equals("no")) {
                                        Picasso.get()
                                                .load(R.drawable.profile)
                                                .error(R.drawable.errorimg)
                                                .into(avatar);
                                    } else {
                                        Picasso.get()
                                                .load(downloadUrl)
                                                .error(R.drawable.errorimg)
                                                .into(avatar);
                                    }
                                    if(Global.diaG != null) {
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

                })
                .addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
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
            mData.child(mAuth.getCurrentUser().getUid()).updateChildren(map);
            Global.local_on = true;
        }

        myApp.stopActivityTransitionTimer();
    }

    @Override
    public void onPause() {
        super.onPause();
        ((AppBack) this.getApplication()).startActivityTransitionTimer();
    }

    public void deletephoto(View view) {
        final Map<String, Object> map = new HashMap<>();
        map.put("avatar", "no");
        mData.child(mAuth.getCurrentUser().getUid()).updateChildren(map).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
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
                    Global.avaLocal = "no";
                    Picasso.get()
                            .load(R.drawable.profile)
                            .error(R.drawable.errorimg)
                            .into(avatar);
                    Picasso.get()
                            .load(R.drawable.bg)
                            .error(R.drawable.errorimg)
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
