package ar.codeslu.plax;

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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.vanniktech.emoji.EmojiTextView;

import java.util.HashMap;
import java.util.Map;

import ar.codeslu.plax.global.AppBack;
import ar.codeslu.plax.global.GetTime;
import ar.codeslu.plax.global.Global;
import ar.codeslu.plax.lists.UserData;

public class Profile extends AppCompatActivity {
    //view
    ImageView bg;
    EmojiTextView nameE, phoneE, last_onlineE, statueE;
    Button message, voiceC, videoC;
    LinearLayout lyP;
    //firebase
    DatabaseReference mData;
    FirebaseAuth mAuth;
    //vars
    String nameS, phoneS, last_onlineS, statueS, avaS, id;
    long last_onlineL;
    boolean view = false;
    Bitmap bitmap;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(1);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
        setContentView(R.layout.activity_profile);
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
                id = Global.idLocal;
        } catch (NullPointerException e) {
            id = Global.idLocal;

        }


        if (Global.check_int(Profile.this)) {
            Query query = mData.child(id);
            query.keepSynced(true);
            query.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
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
                            phoneE.setText(phone);
                        }
                        if (!userData.isOnline()) {
                            if (userData.getTime() != 0) {
                                long last = userData.getTime();
                                last_onlineS = GetTime.getTimeAgo(last, Profile.this);
                                last_onlineE.setText(last_onlineS);

                            }

                        } else
                            last_onlineE.setText(R.string.online_nw);

                        if (userData.getAvatar() != null) {
                            String ava = userData.getAvatar();
                            if (ava.equals("no")) {
                                Picasso.get()
                                        .load(R.drawable.bg)
                                        .error(R.drawable.errorimg)
                                        .into(bg);
                            } else {
                                Picasso.get()
                                        .load(ava)
                                        .error(R.drawable.errorimg)
                                        .into(bg);
                            }
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        } else {
            if (id.equals(Global.idLocal)) {
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
                            .error(R.drawable.errorimg)
                            .into(bg);
                } else {
                    Picasso.get()
                            .load(avaS)
                            .error(R.drawable.errorimg)
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
                            .error(R.drawable.errorimg)
                            .into(bg);
                } else {
                    Picasso.get()
                            .load(avaS)
                            .error(R.drawable.errorimg)
                            .into(bg);
                }
            }
        }
        //this view will apear only when current user != the user from the the list
        //if the current == user from list or go from intent it will be false
        if (id.equals(Global.idLocal)) {
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
            //lock screen
            ((AppBack) getApplication()).lockscreen(((AppBack) getApplication()).shared().getBoolean("lock", false));
        }

        myApp.stopActivityTransitionTimer();
    }

    @Override
    public void onPause() {
        super.onPause();
        ((AppBack) this.getApplication()).startActivityTransitionTimer();
    }


}
