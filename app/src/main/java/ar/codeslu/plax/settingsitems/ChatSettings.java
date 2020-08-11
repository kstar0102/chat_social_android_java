package ar.codeslu.plax.settingsitems;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.ActivityCompat;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.nightonke.jellytogglebutton.JellyToggleButton;
import com.stfalcon.chatkit.me.UserIn;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ar.codeslu.plax.Chat;
import ar.codeslu.plax.Contacts;
import ar.codeslu.plax.R;
import ar.codeslu.plax.auth.DataSet;
import ar.codeslu.plax.fragments.Chats;
import ar.codeslu.plax.global.AppBack;
import ar.codeslu.plax.global.Global;
import dmax.dialog.SpotsDialog;

public class ChatSettings extends AppCompatActivity {

    FirebaseAuth mAuth;
    TextView fontT, wallT, clearT,main1,main2,main3;
    JellyToggleButton soundT, enterT,shakeT;
    LinearLayout fontL, wallL, clearL;
    AlertDialog.Builder dialog;
    String choosenFont;
    ImageView delete;
    DatabaseReference mData, deleteC;
    android.app.AlertDialog dialogg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_settings);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        Global.currentactivity = this;

        soundT = findViewById(R.id.soundT);
        enterT = findViewById(R.id.enterT);
        shakeT = findViewById(R.id.shakeT);
        fontT = findViewById(R.id.fontT);
        wallT = findViewById(R.id.wallT);
        fontL = findViewById(R.id.fontL);
        wallL = findViewById(R.id.wallL);
        delete = findViewById(R.id.delete);
        clearL = findViewById(R.id.clear);
        clearT = findViewById(R.id.clearT);
        main1 = findViewById(R.id.main1);
        main2 = findViewById(R.id.main2);
        main3 = findViewById(R.id.main3);
        mAuth = FirebaseAuth.getInstance();


        //loader
        if (Global.DARKSTATE) {
            dialogg = new SpotsDialog.Builder()
                    .setContext(this)
                    .setMessage(R.string.pleasW)
                    .setTheme(R.style.darkDialog)
                    .setCancelable(true)
                    .build();
        } else {
            dialogg = new SpotsDialog.Builder()
                    .setContext(this)
                    .setMessage(R.string.pleasW)
                    .setCancelable(true)
                    .build();
        }

        //dark mode init
        if (mAuth.getCurrentUser() != null) {
            if (!((AppBack) getApplication()).shared().getBoolean("dark" + mAuth.getCurrentUser().getUid(), false)) {
                getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                main1.setTextColor(Color.BLACK);
                main2.setTextColor(Color.BLACK);
                main3.setTextColor(Color.BLACK);
            } else {
                getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                main1.setTextColor(Color.WHITE);
                main2.setTextColor(Color.WHITE);
                main3.setTextColor(Color.WHITE);
            }
        }

        if (((AppBack) getApplication()).shared().getString("wall", "no").equals("no"))
            delete.setVisibility(View.GONE);
        else
            delete.setVisibility(View.VISIBLE);


        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((AppBack) getApplication()).editSharePrefs().putString("wall", "no");
                ((AppBack) getApplication()).editSharePrefs().apply();
                Toast.makeText(ChatSettings.this, getResources().getString(R.string.walldsucc), Toast.LENGTH_SHORT).show();
                finish();
            }
        });

        mData = FirebaseDatabase.getInstance().getReference(Global.USERS);
        deleteC = FirebaseDatabase.getInstance().getReference(Global.CHATS);
        if (mAuth.getCurrentUser() != null) {
            if (!((AppBack) getApplication()).shared().getBoolean("dark" + mAuth.getCurrentUser().getUid(), false)) {
                wallT.setTextColor( getResources().getColor(R.color.black));
                fontT.setTextColor( getResources().getColor(R.color.black));

            } else {
                wallT.setTextColor( getResources().getColor(R.color.white));
                fontT.setTextColor( getResources().getColor(R.color.white));

            }

        }
        fontL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog = new AlertDialog.Builder(ChatSettings.this);
                dialog.setTitle(R.string.plz);
                //checked item is the default checked item when dialog open
                choosenFont = ((AppBack) getApplication()).shared().getString("font", "8");
                dialog.setSingleChoiceItems(getResources().getStringArray(R.array.font), (Integer.parseInt(choosenFont) - 1), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //set choosen lang id
                        choosenFont = String.valueOf(i + 1);
                        ((AppBack) getApplication()).changefont((Integer.parseInt(choosenFont)));
                        dialogInterface.dismiss();
                        Intent restart = getBaseContext().getPackageManager().getLaunchIntentForPackage(getBaseContext().getPackageName());
                        restart.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        restart.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(restart);
                        finish();

                    }
                });
                dialog.show();
            }
        });
        wallL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Dexter.withActivity(ChatSettings.this)
                        .withPermissions(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA)
                        .withListener(new MultiplePermissionsListener() {
                            @Override
                            public void onPermissionsChecked(MultiplePermissionsReport report) {

                                if (report.areAllPermissionsGranted()) {
                                    CropImage.activity()
                                            .setGuidelines(CropImageView.Guidelines.ON)
                                            .start(ChatSettings.this);
                                } else
                                    Toast.makeText(ChatSettings.this, getString(R.string.acc_per), Toast.LENGTH_SHORT).show();


                            }

                            @Override
                            public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {

                                token.continuePermissionRequest();

                            }
                        }).check();
            }
        });
        //toggles
        soundT.setChecked(((AppBack) getApplication()).shared().getBoolean("sound", false));
        enterT.setChecked(((AppBack) getApplication()).shared().getBoolean("enter", false));
        shakeT.setChecked(((AppBack) getApplication()).shared().getBoolean("shake", true));

        soundT.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    ((AppBack) getApplication()).editSharePrefs().putBoolean("sound", true);
                    ((AppBack) getApplication()).editSharePrefs().apply();
                } else {
                    ((AppBack) getApplication()).editSharePrefs().putBoolean("sound", false);
                    ((AppBack) getApplication()).editSharePrefs().apply();
                }
            }
        });
        enterT.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    ((AppBack) getApplication()).editSharePrefs().putBoolean("enter", true);
                    ((AppBack) getApplication()).editSharePrefs().apply();
                } else {
                    ((AppBack) getApplication()).editSharePrefs().putBoolean("enter", false);
                    ((AppBack) getApplication()).editSharePrefs().apply();
                }
            }
        });
        shakeT.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    ((AppBack) getApplication()).editSharePrefs().putBoolean("shake", true);
                    ((AppBack) getApplication()).editSharePrefs().apply();
                } else {
                    ((AppBack) getApplication()).editSharePrefs().putBoolean("shake", false);
                    ((AppBack) getApplication()).editSharePrefs().apply();
                }
            }
        });
//clear chats
        clearL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(Global.check_int(ChatSettings.this)){
                ((AppBack) getApplication()).getdialogdb(mAuth.getCurrentUser().getUid());

                Global.messG = new ArrayList<>();
                dialogg.show();

                if (Global.diaG.size() == 0)
                    dialogg.dismiss();

                for (int i = 0; i < Global.diaG.size(); i++) {
                    Global.messG.clear();
                    ((AppBack) getApplication()).setchatsdb(Global.diaG.get(i).getId());

                    if (i == Global.diaG.size() - 1) {
                        deleteC.child(mAuth.getCurrentUser().getUid()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                dialogg.dismiss();
                                if (task.isSuccessful()) {
                                    Global.diaG.clear();
                                    ((AppBack) getApplication()).setdialogdb(mAuth.getCurrentUser().getUid());
                                    Toast.makeText(ChatSettings.this, R.string.chat_dee, Toast.LENGTH_SHORT).show();

                                } else
                                    Toast.makeText(ChatSettings.this, R.string.error, Toast.LENGTH_SHORT).show();
                            }
                        });
                    }


                }
            }
            else
                    Toast.makeText(ChatSettings.this, R.string.check_int, Toast.LENGTH_SHORT).show();


            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri imgLocalpath = result.getUri();
                Intent intent = new Intent(this, TestWall.class);
                intent.putExtra("wall334", String.valueOf(imgLocalpath));
                startActivity(intent);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }


    @Override
    public void onResume() {
        super.onResume();
        if (((AppBack) getApplication()).shared().getString("wall", "no").equals("no"))
            delete.setVisibility(View.GONE);
        else
            delete.setVisibility(View.VISIBLE);
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
}
