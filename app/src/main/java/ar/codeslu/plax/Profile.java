package ar.codeslu.plax;

import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Build;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.squareup.picasso.Picasso;
import com.vanniktech.emoji.EmojiTextView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ar.codeslu.plax.calls.CallingActivity;
import ar.codeslu.plax.calls.CallingActivityVideo;
import ar.codeslu.plax.global.AppBack;
import ar.codeslu.plax.global.Global;
import ar.codeslu.plax.lists.UserData;
/**
 * Created by CodeSlu
 */
public class Profile extends AppCompatActivity {
    //view
    ImageView bg;
    EmojiTextView nameE, phoneE, last_onlineE, statueE;
    Button message, voiceC, videoC;
    LinearLayout lyP;
    //firebase
    DatabaseReference mData, mlogs;
    FirebaseAuth mAuth;
    //vars
    String nameS, phoneS, last_onlineS, statueS, avaS, id;
    long last_onlineL;
    boolean view = false;
    Bitmap bitmap;
    Query query;
    ValueEventListener child;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        Global.currentactivity = this;

        mlogs = FirebaseDatabase.getInstance().getReference(Global.CALLS);
        bg = findViewById(R.id.bgP);
        nameE = findViewById(R.id.nameP);
        phoneE = findViewById(R.id.phoneNum);
        last_onlineE = findViewById(R.id.lastonlineP);
        statueE = findViewById(R.id.statueP);
        message = findViewById(R.id.message);
        voiceC = findViewById(R.id.voiceCP);
        lyP = findViewById(R.id.lyP);
        videoC = findViewById(R.id.videoCP);
        //disable btns
        message.setVisibility(View.GONE);
        lyP.setVisibility(View.GONE);

        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });

        if (Global.ADMOB_ENABLE) {
            AdView mAdView = findViewById(R.id.adView);
            AdRequest adRequest = new AdRequest.Builder().build();
            mAdView.loadAd(adRequest);
        }
        //Firebase init
        Global.currentactivity = this;
        mAuth = FirebaseAuth.getInstance();
        mData = FirebaseDatabase.getInstance().getReference(Global.USERS);
        //dark mode init
        if (mAuth.getCurrentUser() != null) {
            if (!((AppBack) getApplication()).shared().getBoolean("dark" + mAuth.getCurrentUser().getUid(), false)) {
                getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            } else {
                getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            }
        }
        try {
            if (getIntent() != null)
                id = getIntent().getExtras().getString("idP");
            else
                id = mAuth.getCurrentUser().getUid();
        } catch (NullPointerException e) {
            id = mAuth.getCurrentUser().getUid();

        }

        if (!Global.currblocked && !Global.blockedLocal) {
            voiceC.setEnabled(true);
            videoC.setEnabled(true);
        } else {
            voiceC.setEnabled(false);
            videoC.setEnabled(false);
        }


        if (Global.check_int(Profile.this)) {
            query = mData.child(id);
            child = query.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if(mAuth.getCurrentUser()!=null) {

                        if (dataSnapshot.exists()) {
                            String statueT = "";
                            UserData userData = dataSnapshot.getValue(UserData.class);
                            if (userData.getName() != null) {
                                String name = userData.getName();
                                nameE.setText(name);
                            }
                            if (userData.getStatue() != null) {
                                String statue = userData.getStatue();
                                if (statue.length() > Global.STATUE_LENTH)
                                    statueT = statue.substring(0, Global.STATUE_LENTH) + "...";

                                else
                                    statueT = statue;

                                statueT = "\"" + statueT + "\"";

                                statueE.setText(statueT);
                            }
                            if (userData.getPhone() != null) {
                                String phone = userData.getPhone();
                                if (!phone.equals("t88848992hisuseri9483828snothereri9949ghtnow009933"))
                                    phoneE.setText(phone);
                                else
                                    phoneE.setText(getString(R.string.account_deleted));
                            }
                            if (!userData.isOnline()) {
                                if (userData.getTime() != 0) {
                                    long last = userData.getTime();
                                    last_onlineS = AppBack.getTimeAgo(last, Profile.this);
                                    last_onlineE.setText(last_onlineS);

                                }

                            } else
                                last_onlineE.setText(R.string.online_nw);

                            if (userData.getAvatar() != null) {
                                String ava = userData.getAvatar();
                                if (ava.equals("no")) {
                                    Picasso.get()
                                            .load(R.drawable.bg)
                                            .placeholder(R.drawable.placeholder_gray).error(R.drawable.errorimg)

                                            .into(bg);
                                } else {
                                    Picasso.get()
                                            .load(ava)
                                            .placeholder(R.drawable.placeholder_gray).error(R.drawable.errorimg)

                                            .into(bg);
                                }
                            }
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        } else {
            if (id.equals(mAuth.getCurrentUser().getUid())) {
                nameS = Global.nameLocal;
                avaS = Global.avaLocal;
                statueS = Global.statueLocal;
                phoneS = Global.phoneLocal;
                statueS = "\"" + statueS + "\"";
                last_onlineE.setText(getResources().getString(R.string.check_conn));
                nameE.setText(nameS);
                phoneE.setText(phoneS);
                statueE.setText(statueS);
                if (avaS.equals("no")) {
                    Picasso.get()
                            .load(R.drawable.bg)
                            .placeholder(R.drawable.placeholder_gray).error(R.drawable.errorimg)

                            .into(bg);
                } else {
                    Picasso.get()
                            .load(avaS)
                            .placeholder(R.drawable.placeholder_gray).error(R.drawable.errorimg)

                            .into(bg);
                }
            } else {
                nameS = Global.currname;
                avaS = Global.currAva;
                statueS = Global.currstatue;
                phoneS = Global.currphone;
                statueS = "\"" + statueS + "\"";
                last_onlineE.setText(getResources().getString(R.string.check_conn));
                nameE.setText(nameS);
                phoneE.setText(phoneS);
                statueE.setText(statueS);
                if (avaS.equals("no")) {
                    Picasso.get()
                            .load(R.drawable.bg)
                            .placeholder(R.drawable.placeholder_gray).error(R.drawable.errorimg)

                            .into(bg);
                } else {
                    Picasso.get()
                            .load(avaS)
                            .placeholder(R.drawable.placeholder_gray).error(R.drawable.errorimg)

                            .into(bg);
                }
            }
        }
        //this view will apear only when current user != the user from the the list
        //if the current == user from list or go from intent it will be false
        if (id.equals(mAuth.getCurrentUser().getUid())) {
            message.setVisibility(View.VISIBLE);
            message.setText(getString(R.string.edit));
            lyP.setVisibility(View.GONE);
            message.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(Profile.this, EditProfile.class);
                    intent.putExtra("name", Global.nameLocal);
                    intent.putExtra("ava", Global.avaLocal);
                    intent.putExtra("statue", Global.statueLocal);
                    startActivity(intent);

                }
            });
        } else {
            message.setVisibility(View.GONE);
            lyP.setVisibility(View.VISIBLE);
        }
        voiceC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dexter.withActivity(Global.mainActivity)
                        .withPermissions(Manifest.permission.RECORD_AUDIO, Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE)
                        .withListener(new MultiplePermissionsListener() {
                            @Override
                            public void onPermissionsChecked(MultiplePermissionsReport report) {
                                if (report.areAllPermissionsGranted()) {
                                    try {
                                        if (Global.check_int(Profile.this)) {


                                            try {
                                                voiceC.setEnabled(false);

                                                if (Global.check_int(Profile.this)) {
                                                    if (!Global.currblocked && !Global.blockedLocal && !Global.currphone.equals("t88848992hisuseri9483828snothereri9949ghtnow009933")) {
                                                        voiceC.setEnabled(true);

                                                        String callid = mAuth.getCurrentUser().getUid() + System.currentTimeMillis();
                                                        Intent jumptocall = new Intent(Profile.this, CallingActivity.class);
                                                        jumptocall.putExtra("name", Global.currname);
                                                        jumptocall.putExtra("ava", Global.currAva);
                                                        jumptocall.putExtra("out", true);
                                                        jumptocall.putExtra("UserId", id);
                                                        jumptocall.putExtra("channel_id", callid);
                                                        startActivity(jumptocall);

                                                    } else
                                                        Toast.makeText(Profile.this, R.string.cannot_user, Toast.LENGTH_SHORT).show();

                                                } else
                                                    Toast.makeText(Profile.this, R.string.check_int, Toast.LENGTH_SHORT).show();
                                            } catch (NullPointerException e) {
                                                Toast.makeText(Profile.this, getString(R.string.error), Toast.LENGTH_SHORT).show();
                                                voiceC.setEnabled(true);

                                            }

                                        } else
                                            Toast.makeText(Profile.this, R.string.check_int, Toast.LENGTH_SHORT).show();


                                    } catch (NullPointerException e) {
                                        Toast.makeText(Profile.this, getString(R.string.error), Toast.LENGTH_SHORT).show();
                                    }
                                } else
                                    Toast.makeText(Profile.this, getString(R.string.acc_per), Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {

                                token.continuePermissionRequest();

                            }
                        }).check();


            }
        });
        videoC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dexter.withActivity(Global.mainActivity)
                        .withPermissions(Manifest.permission.RECORD_AUDIO, Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE)
                        .withListener(new MultiplePermissionsListener() {
                            @Override
                            public void onPermissionsChecked(MultiplePermissionsReport report) {
                                if (report.areAllPermissionsGranted()) {

                                    try {
                                        videoC.setEnabled(false);

                                        if (Global.check_int(Profile.this)) {
                                            if (!Global.currblocked && !Global.blockedLocal && !Global.currphone.equals("t88848992hisuseri9483828snothereri9949ghtnow009933")) {
                                                videoC.setEnabled(true);

                                                String callid = mAuth.getCurrentUser().getUid() + System.currentTimeMillis();
                                                Intent jumptocall = new Intent(Profile.this, CallingActivityVideo.class);
                                                jumptocall.putExtra("UserId", id);
                                                jumptocall.putExtra("name", Global.currname);
                                                jumptocall.putExtra("ava", Global.currAva);
                                                jumptocall.putExtra("id", id);
                                                jumptocall.putExtra("out", true);
                                                jumptocall.putExtra("channel_id", callid);
                                                startActivity(jumptocall);

                                            } else
                                                Toast.makeText(Profile.this, R.string.cannot_user, Toast.LENGTH_SHORT).show();

                                        } else
                                            Toast.makeText(Profile.this, R.string.check_int, Toast.LENGTH_SHORT).show();
                                    } catch (NullPointerException e) {
                                        Toast.makeText(Profile.this, getString(R.string.error), Toast.LENGTH_SHORT).show();
                                        videoC.setEnabled(true);
                                    }
                                } else
                                    Toast.makeText(Profile.this, getString(R.string.acc_per), Toast.LENGTH_SHORT).show();


                            }

                            @Override
                            public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {

                                token.continuePermissionRequest();

                            }
                        }).check();

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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (child != null && query != null)
            query.removeEventListener(child);
    }
}
