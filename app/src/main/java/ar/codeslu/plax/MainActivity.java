package ar.codeslu.plax;


import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ShortcutManager;
import android.graphics.Bitmap;

import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;

import com.google.android.gms.ads.InterstitialAd;

import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;

import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.squareup.picasso.Picasso;
import com.vanniktech.emoji.EmojiTextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ar.codeslu.plax.Groups.Group;
import ar.codeslu.plax.adapters.MainAdapter;
import ar.codeslu.plax.adapters.ProfileAdapter;
import ar.codeslu.plax.adapters.Vpadapter;
import ar.codeslu.plax.auth.DataSet;
import ar.codeslu.plax.auth.Login;
import ar.codeslu.plax.auth.PolicyActivity;
import ar.codeslu.plax.fragments.Calls;
import ar.codeslu.plax.fragments.Chats;
import ar.codeslu.plax.fragments.Groups;
import ar.codeslu.plax.global.AppBack;
import ar.codeslu.plax.global.Global;
import ar.codeslu.plax.lists.UserData;
import ar.codeslu.plax.profile.Profile;
import ar.codeslu.plax.story.AddStory;
import de.hdodenhof.circleimageview.CircleImageView;
import me.leolin.shortcutbadger.ShortcutBadger;


/**
 * Created by CodeSlu
 */
public class MainActivity extends AppCompatActivity{
    //Firebase
    FirebaseAuth mAuth;
    DatabaseReference mData, mChats, mGroups, mCalls;
    //views
    ViewPager vp;
    CircleImageView userPic;
    LinearLayout userinfo;
    Button editBNav;
    ImageView hlal, searchbtn;
    TextView userPhoneNum;
    //Fragments
    Calls calls;
    Chats chats;
    Groups groups;
    //Shared pref
    SharedPreferences preferences;
    SharedPreferences.Editor editor;
    //Vars
    Uri imgLocalpath;
    //Application class
    private AppBack appback;
    //compress
    private Bitmap compressedImageFile;
    //adpaters
    Vpadapter Adapter;
    //encryption

    String called = "";
    android.app.AlertDialog dialog;

    private InterstitialAd mInterstitialAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Global.currentactivity = this;
        setContentView(R.layout.activity_main);
        //firebase init
        Global.conMain = this;
        Global.mainActivity = this;
        mAuth = FirebaseAuth.getInstance();
        mData = FirebaseDatabase.getInstance().getReference(Global.USERS);
        mChats = FirebaseDatabase.getInstance().getReference(Global.CHATS);
        mGroups = FirebaseDatabase.getInstance().getReference(Global.GROUPS);
        mCalls = FirebaseDatabase.getInstance().getReference(Global.CALLS);
        userPhoneNum = findViewById(R.id.userPhoneNum);
        userPic = findViewById(R.id.userImage);
        searchbtn = findViewById(R.id.search_icon_main);
        userinfo = findViewById(R.id.userInfo);
        vp = findViewById(R.id.Vp);

        TabLayout tabLayout = findViewById(R.id.main_tab_layout);
        tabLayout.addTab(tabLayout.newTab().setText("Chat"));
        tabLayout.addTab(tabLayout.newTab().setText("Friend"));
        tabLayout.addTab(tabLayout.newTab().setText("Notification"));
        tabLayout.addTab(tabLayout.newTab().setText("Follow"));
        tabLayout.getTabAt(0).setIcon(R.drawable.selected1);
        tabLayout.getTabAt(1).setIcon(R.drawable.friend_icon);
        tabLayout.getTabAt(2).setIcon(R.drawable.notification_icon);
        tabLayout.getTabAt(3).setIcon(R.drawable.follow_icon);
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        MainAdapter mainAdapter = new MainAdapter(getSupportFragmentManager(), tabLayout.getTabCount());
        vp.setAdapter(mainAdapter);

        vp.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                vp.setCurrentItem(tab.getPosition());
                switch (tab.getPosition())
                {
                    case 0:
                        tab.setIcon(R.drawable.selected1);
                        tabLayout.getTabAt(1).setIcon(R.drawable.friend_icon);
                        tabLayout.getTabAt(2).setIcon(R.drawable.notification_icon);
                        tabLayout.getTabAt(3).setIcon(R.drawable.follow_icon);

                        break;
                    case 1:
                        tab.setIcon(R.drawable.selected2);
                        tabLayout.getTabAt(0).setIcon(R.drawable.chat_icon);
                        tabLayout.getTabAt(2).setIcon(R.drawable.notification_icon);
                        tabLayout.getTabAt(3).setIcon(R.drawable.follow_icon);
                        break;
                    case 2:
                        tab.setIcon(R.drawable.selected3);
                        tabLayout.getTabAt(0).setIcon(R.drawable.chat_icon);
                        tabLayout.getTabAt(1).setIcon(R.drawable.friend_icon);
                        tabLayout.getTabAt(3).setIcon(R.drawable.follow_icon);
                        break;
                    case 3:
                        tab.setIcon(R.drawable.selected4);
                        tabLayout.getTabAt(0).setIcon(R.drawable.chat_icon);
                        tabLayout.getTabAt(1).setIcon(R.drawable.friend_icon);
                        tabLayout.getTabAt(2).setIcon(R.drawable.notification_icon);
                        break;
                }
            }
            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }
            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        if (mAuth.getCurrentUser() != null){
            checkData();
        }

        userinfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, Profile.class);
                SharedPreferences preferences = getSharedPreferences("profile", Context.MODE_PRIVATE);
                String ava = preferences.getString("ava_" + mAuth.getCurrentUser().getUid(), null);
                String name = preferences.getString("name_" + mAuth.getCurrentUser().getUid(), null);
                String statue = preferences.getString("statue_" + mAuth.getCurrentUser().getUid(), null);
                long last = preferences.getLong("laston_" + mAuth.getCurrentUser().getUid(), 0);
                String phone = preferences.getString("phone_" + mAuth.getCurrentUser().getUid(), null);
                Global.phoneLocal = phone;

                startActivity(intent);
            }
        });

        //app global
        appback = (AppBack) getApplication();
        //Shared pref
        preferences = getSharedPreferences("profile", Context.MODE_PRIVATE);
        editor = preferences.edit();
        //clear all notifications
        NotificationManager notificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
        try {
            if (notificationManager != null) {
                notificationManager.cancelAll();
                int count = 0;
                //store it again
                ((AppBack) getApplication()).editSharePrefs().putInt("numN" + mAuth.getCurrentUser().getUid(), count);
                ((AppBack) getApplication()).editSharePrefs().apply();
                ShortcutBadger.applyCount(this, count);
            }
        } catch (NullPointerException e) {
            //nothing
        }


        //redirect
        if (mAuth.getCurrentUser() == null) {
            startActivity(new Intent(MainActivity.this, PolicyActivity.class));
            finish();
        } else {
            if (Global.check_int(this)) {
                updateTokens();
                sharedadv(Global.check_int(this));
            }
            //main data init
            SharedPreferences preferences = getSharedPreferences("profile", Context.MODE_PRIVATE);
            String phone = preferences.getString("phone_" + mAuth.getCurrentUser().getUid(), null);
            if (phone != null)
                Global.phoneLocal = phone;
        }

        Global.currentpageid = "";

        //online checker
        ((AppBack) this.getApplication()).startOnline();
        try {
            if (getIntent() != null) {
                if (getIntent().getExtras().getInt("codetawgeh", 0) == 1 && mAuth.getCurrentUser() != null) {
                    Intent intent = new Intent(this, Chat.class);
                    intent.putExtra("name", getIntent().getExtras().getString("name"));
                    intent.putExtra("id", getIntent().getExtras().getString("id"));
                    intent.putExtra("ava", getIntent().getExtras().getString("ava"));
                    Global.currname = getIntent().getExtras().getString("name");
                    Global.currentpageid = getIntent().getExtras().getString("id");
                    Global.currFid = getIntent().getExtras().getString("id");
                    Global.currAva = getIntent().getExtras().getString("ava");
                    startActivity(intent);
                } else if (getIntent().getExtras().getInt("codetawgeh", 0) == 2 && mAuth.getCurrentUser() != null) {
                    Intent intent = new Intent(this, Group.class);
                    intent.putExtra("name", getIntent().getExtras().getString("name"));
                    intent.putExtra("id", getIntent().getExtras().getString("id"));
                    intent.putExtra("ava", getIntent().getExtras().getString("ava"));
                    Global.currname = getIntent().getExtras().getString("name");
                    Global.currentpageid = getIntent().getExtras().getString("id");
                    Global.currFid = getIntent().getExtras().getString("id");
                    Global.currAva = getIntent().getExtras().getString("ava");
                    startActivity(intent);
                }
                else if (getIntent().getExtras().getInt("codetawgeh", 0) == 3 && mAuth.getCurrentUser() != null) {
                    Intent intent = new Intent(this, AddStory.class);
                    startActivity(intent);
                }
            }
        } catch (NullPointerException e) {
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }


    @Override
    protected void onStart() {
        super.onStart();
        mAuth = FirebaseAuth.getInstance();
        mData = FirebaseDatabase.getInstance().getReference(Global.USERS);

        if (mAuth.getCurrentUser() == null) {
            startActivity(new Intent(MainActivity.this, Login.class));
            finish();
        }

    }

    //chat listitem set data.
    public void sharedadv(boolean check) {
        if (check) {
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
                                editor.putString("name_" + mAuth.getCurrentUser().getUid(), name);
                                editor.apply();
                                Global.nameLocal = name;
                            }

                            if (userData.getStatue() != null) {
                                String statue = userData.getStatue();
                                editor.putString("statue_" + mAuth.getCurrentUser().getUid(), statue);
                                editor.apply();
                                Global.statueLocal = statue;

                                if (statue.length() > Global.STATUE_LENTH)
                                    statueT = statue.substring(0, Global.STATUE_LENTH) + "...";

                                else
                                    statueT = statue;

                                statueT = "\"" + statueT + "\"";

                            }

                            if (userData.getPhone() != null) {
                                String phone = userData.getPhone();
                                userPhoneNum.setText(phone);
                                editor.putString("phone_" + mAuth.getCurrentUser().getUid(), phone);
                                editor.apply();
                            }
                            if (userData.getTime() != 0) {
                                long last = userData.getTime();
                                editor.putLong("laston_" + mAuth.getCurrentUser().getUid(), last);
                                editor.apply();
                            }

                            if (userData.getAvatar() != null) {
                                String ava = userData.getAvatar();
                                editor.putString("ava_" + mAuth.getCurrentUser().getUid(), ava);
                                editor.apply();
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
        }

    }


    @Override
    public void onResume() {
        super.onResume();
        Global.currentactivity = this;

        try {
            if (Global.wl != null) {
                if (Global.wl.isHeld()) {
                    Global.wl.release();
                }
                Global.wl = null;
            }
        } catch (NullPointerException e) {

        }

        if (mAuth.getCurrentUser() != null) {
            checkData();
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
            //clear all notifications
            NotificationManager notificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
            try {
                if (notificationManager != null) {
                    notificationManager.cancelAll();
                    int count = 0;
                    //store it again
                    ((AppBack) getApplication()).editSharePrefs().putInt("numN" + mAuth.getCurrentUser().getUid(), count);
                    ((AppBack) getApplication()).editSharePrefs().apply();
                    ShortcutBadger.applyCount(this, count);
                }
            } catch (NullPointerException e) {
                //nothing
            }

            myApp.stopActivityTransitionTimer();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
            ((AppBack) this.getApplication()).startActivityTransitionTimer();
            Global.currentactivity = null;
    }

    @Override
    protected void onDestroy() {
        ((AppBack) this.getApplication()).startActivityTransitionTimer();
        super.onDestroy();

    }


    private void updateTokens() {
        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (task.isSuccessful()) {
                            String token = task.getResult().getToken();
                            if (token != null) {
                                Map<String, Object> map = new HashMap<>();
                                map.put("tokens", token);
                                DatabaseReference mToken = FirebaseDatabase.getInstance().getReference(Global.tokens);
                                mToken.child(mAuth.getCurrentUser().getUid()).updateChildren(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {

                                    }
                                });
                            }

                        }


                    }
                });


    }

    private void checkData() {
        Query query = mData.child(mAuth.getCurrentUser().getUid());
        query.keepSynced(true);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                UserData userData = dataSnapshot.getValue(UserData.class);
                if (userData.getName() == null) {
                    Intent intent = new Intent(getApplicationContext(), DataSet.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

}

