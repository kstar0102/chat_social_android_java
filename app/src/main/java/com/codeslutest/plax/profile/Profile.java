package com.codeslutest.plax.profile;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

import com.codeslutest.plax.R;
import com.codeslutest.plax.Setting;
import com.codeslutest.plax.global.AppBack;
import com.codeslutest.plax.global.Global;
import com.codeslutest.plax.lists.UserData;
/**
 * Created by CodeSlu
 */
public class Profile extends AppCompatActivity {
    //view
    ImageView userPic, backBtn, menuBtn;
    LinearLayout savebtn, accountbtn, helpbtn;
//    EmojiTextView nameE, phoneE, last_onlineE, statueE;
    EditText nameE, phoneE, homePhone,profiletitle, profileplace, officephone, email;
    TextView profileBtn, contactBtn;
    //firebase
    DatabaseReference mData, mlogs;
    FirebaseAuth mAuth;
    //vars
    String nameS, phoneS, avaS, id, last_onlineS;
    Query query;
    ValueEventListener child;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ownprofile);

        Global.currentactivity = this;

        mlogs = FirebaseDatabase.getInstance().getReference(Global.CALLS);
        nameE = findViewById(R.id.nameP);
        phoneE = findViewById(R.id.phoneNum);
        homePhone = findViewById(R.id.homePhone);
        userPic = findViewById(R.id.userPic);
        savebtn = findViewById(R.id.profile_save);
        accountbtn = findViewById(R.id.profile_account);
        helpbtn = findViewById(R.id.profile_help);
        profiletitle = findViewById(R.id.profile_title);
        profileplace = findViewById(R.id.profile_place);
        officephone = findViewById(R.id.officePhoneNum);
        email = findViewById(R.id.contact_email);
        profileBtn = findViewById(R.id.profile_txt);
        contactBtn = findViewById(R.id.contact_txt);
        backBtn = findViewById(R.id.back_btn);
        menuBtn = findViewById(R.id.profile_menu);
//        bg = findViewById(R.id.bgP);
//        last_onlineE = findViewById(R.id.lastonlineP);
//        statueE = findViewById(R.id.statueP);

        profileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                profileplace.setEnabled(true);
                profiletitle.setEnabled(true);
            }
        });

        contactBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                homePhone.setEnabled(true);
                officephone.setEnabled(true);
                email.setEnabled(true);
            }
        });

        menuBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater layoutInflater = LayoutInflater.from(Profile.this);
                View promptView = layoutInflater.inflate(R.layout.dialog_profilemenu, null);
                final AlertDialog dialog = new AlertDialog.Builder(Profile.this,R.style.CustomAlertDialog).create();
                dialog.setView(promptView);
                Button profileSecretBtn = promptView.findViewById(R.id.profile_secret);
                Button profileSettingBtn = promptView.findViewById(R.id.profile_setting);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                WindowManager.LayoutParams abc= dialog.getWindow().getAttributes();
                abc.gravity = Gravity.TOP | Gravity.RIGHT;
                abc.x = 20;   //x position
                abc.y = 50;   //y position
                dialog.show();
                Window window = dialog.getWindow();
                window.setLayout(600, LinearLayout.LayoutParams.WRAP_CONTENT);

                profileSettingBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(Profile.this, Setting.class);
                        startActivity(intent);
                    }
                });
            }
        });

        accountbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Profile.this, AccountProfile.class);
                startActivity(intent);
            }
        });

        savebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Profile.this, SavedProfile.class);
                startActivity(intent);
            }
        });

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

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

                            if (userData.getPhone() != null) {
                                String phone = userData.getPhone();
                                if (!phone.equals("t88848992hisuseri9483828snothereri9949ghtnow009933")){
                                    phoneE.setText(phone);
                                    homePhone.setText(phone);
                                }
                                else
                                    phoneE.setText(getString(R.string.account_deleted));
                            }

                            if (userData.getAvatar() != null) {
                                String ava = userData.getAvatar();
                                Global.avaLocal = ava;
                                if (ava.equals("no")) {
                                    Picasso.get()
                                            .load(R.drawable.profile)
                                            .placeholder(R.drawable.placeholder_gray).error(R.drawable.errorimg)
                                            .into(userPic);
                                } else {
                                    Picasso.get()
                                            .load(ava)
                                            .placeholder(R.drawable.placeholder_gray).error(R.drawable.errorimg)
                                            .into(userPic);
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
                Global.idLocal = mAuth.getCurrentUser().getUid();
                Query query = mData.child(mAuth.getCurrentUser().getUid());
                query.keepSynced(true);
                query.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (mAuth.getCurrentUser() != null) {

                            if (dataSnapshot.exists()) {
                                String statueT = "";
                                UserData userData = dataSnapshot.getValue(UserData.class);
                                if (userData.getName() != null) {
                                    String name = userData.getName();
                                    nameE.setText(name);
                                    Global.nameLocal = name;
                                }

                                if (userData.getPhone() != null) {
                                    String phone = userData.getPhone();
                                    phoneE.setText(phone);
                                    homePhone.setText(phone);
                                }

                                if (userData.getAvatar() != null) {
                                    String ava = userData.getAvatar();
                                    Global.avaLocal = ava;
                                    if (ava.equals("no")) {
                                        Picasso.get()
                                                .load(R.drawable.profile)
                                                .placeholder(R.drawable.placeholder_gray).error(R.drawable.errorimg)
                                                .into(userPic);
                                    } else {
                                        Picasso.get()
                                                .load(ava)
                                                .placeholder(R.drawable.placeholder_gray).error(R.drawable.errorimg)
                                                .into(userPic);
                                    }
                                }
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            } else {
                Global.idLocal = mAuth.getCurrentUser().getUid();
                Query query = mData.child(mAuth.getCurrentUser().getUid());
                query.keepSynced(true);
                query.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (mAuth.getCurrentUser() != null) {

                            if (dataSnapshot.exists()) {
                                String statueT = "";
                                UserData userData = dataSnapshot.getValue(UserData.class);
                                if (userData.getName() != null) {
                                    String name = userData.getName();
                                    nameE.setText(name);
                                    Global.nameLocal = name;
                                }
                                if (userData.getPhone() != null) {
                                    String phone = userData.getPhone();
                                    phoneE.setText(phone);
                                    homePhone.setText(phone);
                                }
                                if (userData.getAvatar() != null) {
                                    String ava = userData.getAvatar();
                                    Global.avaLocal = ava;
                                    if (ava.equals("no")) {
                                        Picasso.get()
                                                .load(R.drawable.profile)
                                                .placeholder(R.drawable.placeholder_gray).error(R.drawable.errorimg)
                                                .into(userPic);
                                    } else {
                                        Picasso.get()
                                                .load(ava)
                                                .placeholder(R.drawable.placeholder_gray).error(R.drawable.errorimg)
                                                .into(userPic);
                                    }
                                }
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
