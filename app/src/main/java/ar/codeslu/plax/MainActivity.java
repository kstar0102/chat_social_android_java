package ar.codeslu.plax;


import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;

import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatDelegate;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

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
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;
import com.vanniktech.emoji.EmojiTextView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ar.codeslu.plax.adapters.Vpadapter;
import ar.codeslu.plax.auth.DataSet;
import ar.codeslu.plax.auth.Login;
import ar.codeslu.plax.custom.BadgedDrawerArrowDrawable;
import ar.codeslu.plax.fragments.Calls;
import ar.codeslu.plax.fragments.Chats;
import ar.codeslu.plax.fragments.Groups;
import ar.codeslu.plax.global.AppBack;
import ar.codeslu.plax.global.Global;
import ar.codeslu.plax.lists.Tokens;
import ar.codeslu.plax.lists.UserData;
import de.hdodenhof.circleimageview.CircleImageView;
import eu.long1.spacetablayout.SpaceTabLayout;
import id.zelory.compressor.Compressor;
import me.leolin.shortcutbadger.ShortcutBadger;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    //Firebase
    FirebaseAuth mAuth;
    DatabaseReference mData;
    //views
    DrawerLayout drawer;
    ActionBarDrawerToggle toggle;
    ViewPager vp;
    SpaceTabLayout tabs;
    EmojiTextView nameNav, statueNav;
    CircleImageView avatarNav;
    Button editBNav;
    ImageView hlal;
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
    Vpadapter vpa;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //firebase init
        Global.currentactivity = this;
        Global.conMain = this;
        Global.mainActivity = this;
        mAuth = FirebaseAuth.getInstance();
        mData = FirebaseDatabase.getInstance().getReference(Global.USERS);
        if(mAuth.getCurrentUser() != null)
        checkData();

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
         drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
         toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);

        toggle.setDrawerArrowDrawable(new BadgedDrawerArrowDrawable(MainActivity.this));
        drawer.addDrawerListener(toggle);
        toggle.syncState();

//Tab layout init
        //Initializing..
        vp = findViewById(R.id.Vp);
        vpa = new Vpadapter(getSupportFragmentManager(), this);
        tabs = findViewById(R.id.spaceTabLayout);
        vp.setAdapter(vpa);
        chats = new Chats();
        groups = new Groups();
        calls = new Calls();
        List<Fragment> fragmentList = new ArrayList<>();
        fragmentList.add(calls);
        fragmentList.add(chats);
        fragmentList.add(groups);
        tabs.initialize(vp, getSupportFragmentManager(),
                fragmentList, savedInstanceState);
        //redirect
        if (mAuth.getCurrentUser() == null) {
            startActivity(new Intent(MainActivity.this, Login.class));
            finish();
        } else {

            if (Global.check_int(this)) {
                updateTokens();
                sharedadv(Global.check_int(this));
            }
            //main data init
            SharedPreferences preferences = getSharedPreferences("profile", Context.MODE_PRIVATE);
            String phone = preferences.getString("phone_" + mAuth.getCurrentUser().getUid(), null);
            Global.phoneLocal = phone;
        }

        Global.currentpageid = "";
        ///////////Navigation Inflate/////////////
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View hView = navigationView.getHeaderView(0);

        nameNav = hView.findViewById(R.id.nameNav);
        statueNav = hView.findViewById(R.id.statueNav);
        avatarNav = hView.findViewById(R.id.profimgNav);
        editBNav = hView.findViewById(R.id.editNav);
        hlal = hView.findViewById(R.id.hlal);
        //dark mode init
        if (mAuth.getCurrentUser() != null) {
            if (!((AppBack) getApplication()).shared().getBoolean("dark" + mAuth.getCurrentUser().getUid(), false)) {
                hlal.setImageDrawable(getResources().getDrawable(R.drawable.hlal));
                getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                Global.DARKSTATE = false;
            } else {
                hlal.setImageDrawable(getResources().getDrawable(R.drawable.hlal_fill));
                getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                Global.DARKSTATE = true;
            }
        }
        //change  profile pic
        avatarNav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ActivityCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{android.Manifest.permission.CAMERA, android.Manifest.permission.READ_EXTERNAL_STORAGE, android.Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            801);
                } else {
                    CropImage.activity()
                            .setGuidelines(CropImageView.Guidelines.ON)
                            .setMinCropResultSize(400, 400)
                            .setAspectRatio(1, 1)
                            .start(MainActivity.this);
                }
            }
        });
        //dark mode
        hlal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (((AppBack) getApplication()).shared().getBoolean("dark" + mAuth.getCurrentUser().getUid(), false)) {
                    hlal.setImageDrawable(getResources().getDrawable(R.drawable.hlal));
                    //store choice
                    ((AppBack) getApplication()).editSharePrefs().putBoolean("dark" + mAuth.getCurrentUser().getUid(), false);
                    ((AppBack) getApplication()).editSharePrefs().apply();
                    getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                } else {
                    hlal.setImageDrawable(getResources().getDrawable(R.drawable.hlal_fill));
                    //store choice
                    ((AppBack) getApplication()).editSharePrefs().putBoolean("dark" + mAuth.getCurrentUser().getUid(), true);
                    ((AppBack) getApplication()).editSharePrefs().apply();
                    getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                }

            }
        });
        editBNav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, Profile.class);
                SharedPreferences preferences = getSharedPreferences("profile", Context.MODE_PRIVATE);
                String ava = preferences.getString("ava_" + mAuth.getCurrentUser().getUid(), null);
                String name = preferences.getString("name_" + mAuth.getCurrentUser().getUid(), null);
                String statue = preferences.getString("statue_" + mAuth.getCurrentUser().getUid(), null);
                long last = preferences.getLong("laston_" + mAuth.getCurrentUser().getUid(), 0);
                String phone = preferences.getString("phone_" + mAuth.getCurrentUser().getUid(), null);
                Global.phoneLocal = phone;
                //nulled intent
//                intent.putExtra("name", name);
//                intent.putExtra("ava", ava);
//                intent.putExtra("statue", statue);
//                intent.putExtra("last", last);
//                intent.putExtra("phone", phone);
                startActivity(intent);

            }
        });
//        Global.diaG = new ArrayList<>();
//Global.diaG.clear();
//        ((AppBack) getApplication()).setdialogdb(mAuth.getCurrentUser().getUid());


        ///////////Navigation Inflate/////////////

        //data init from shared pref
        if (mAuth.getCurrentUser() != null)
            getshared();
        //online checker
        ((AppBack) this.getApplication()).startOnline();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        tabs.saveState(outState);
        super.onSaveInstanceState(outState);
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

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_stories) {
            startActivity(new Intent(MainActivity.this,Stories.class));

        } else if (id == R.id.nav_favM) {

        } else if (id == R.id.nav_muted) {

        } else if (id == R.id.nav_blocked) {

        } else if (id == R.id.nav_setting) {
            startActivity(new Intent(MainActivity.this,Setting.class));

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_signout) {
            if (Global.check_int(this)) {
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
                mAuth.signOut();
                startActivity(new Intent(MainActivity.this,Login.class));
                finish();
            } else
                Toast.makeText(appback, R.string.check_int, Toast.LENGTH_SHORT).show();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
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

    public void sharedadv(boolean check) {
        if (check) {
            Global.idLocal = mAuth.getCurrentUser().getUid();

            mData.child(mAuth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        String statueT = "";
                        UserData userData = dataSnapshot.getValue(UserData.class);
                        if (userData.getName() != null) {
                            String name = userData.getName();
                            editor.putString("name_" + mAuth.getCurrentUser().getUid(), name);
                            editor.apply();
                            nameNav.setText(name);
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

                            statueNav.setText(statueT);
                        }
                        if (userData.getPhone() != null) {
                            String phone = userData.getPhone();
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
                                        .error(R.drawable.errorimg)
                                        .into(avatarNav);
                            } else {
                                Picasso.get()
                                        .load(ava)
                                        .error(R.drawable.errorimg)
                                        .into(avatarNav);
                            }
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

        } else {
            getshared();
        }

    }


    @Override
    public void onResume() {
        super.onResume();
        Global.currentactivity = this;
        if(mAuth.getCurrentUser() != null)
            checkData();
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

    @Override
    public void onPause() {
        super.onPause();
        ((AppBack) this.getApplication()).startActivityTransitionTimer();
    }

    @Override
    protected void onDestroy() {
        ((AppBack) this.getApplication()).startActivityTransitionTimer();
        super.onDestroy();

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                imgLocalpath = result.getUri();
                uploadprofile(imgLocalpath);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }

    public void uploadprofile(Uri imgLocalpath) {
        //compress the photo
        File newImageFile = new File(imgLocalpath.getPath());
        try {

            compressedImageFile = new Compressor(MainActivity.this)
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
                        Map<String, Object> map = new HashMap<>();
                        map.put(Global.avatar, String.valueOf(downloadUrl));
                        mData.child(mAuth.getCurrentUser().getUid()).updateChildren(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    if (String.valueOf(downloadUrl).equals("no")) {
                                        Picasso.get()
                                                .load(R.drawable.profile)
                                                .error(R.drawable.errorimg)
                                                .into(avatarNav);
                                    } else {
                                        Picasso.get()
                                                .load(downloadUrl)
                                                .error(R.drawable.errorimg)
                                                .into(avatarNav);
                                    }
                                    editor.putString("ava_" + mAuth.getCurrentUser().getUid(), String.valueOf(downloadUrl));
                                    editor.apply();
                                    Toast.makeText(MainActivity.this, R.string.image_update, Toast.LENGTH_SHORT).show();
                                } else
                                    Toast.makeText(MainActivity.this, R.string.image_fail, Toast.LENGTH_SHORT).show();
                            }
                        })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {

                                    }
                                });

                    }

                })
                .addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                    }
                });

    }

    private void getshared() {
        String statueT = "";
        SharedPreferences preferences = getSharedPreferences("profile", Context.MODE_PRIVATE);
        String ava = preferences.getString("ava_" + mAuth.getCurrentUser().getUid(), "not");
        String name = preferences.getString("name_" + mAuth.getCurrentUser().getUid(), "not");
        String statue = preferences.getString("statue_" + mAuth.getCurrentUser().getUid(), "not");
        if (!ava.equals("not") && !name.equals("not") && !statue.equals("not")) {
            Global.avaLocal = ava;
            Global.nameLocal = name;
            Global.statueLocal = statue;

            nameNav.setText(name);
            if (statue.length() > Global.STATUE_LENTH)
                statueT = statue.substring(0, Global.STATUE_LENTH) + "...";

            else
                statueT = statue;
            statueT = "\"" + statueT + "\"";
            statueNav.setText(statueT);
            if (ava.equals("no")) {
                Picasso.get()
                        .load(R.drawable.profile)
                        .error(R.drawable.errorimg)
                        .into(avatarNav);
            } else {
                Picasso.get()
                        .load(ava)
                        .error(R.drawable.errorimg)
                        .into(avatarNav);
            }
        }
    }

    private void updateTokens() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference tokens = database.getReference(Global.tokens);
        Tokens tk = new Tokens(FirebaseInstanceId.getInstance().getToken());
        tokens.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(tk);
    }
    private void checkData() {
        mData.child(mAuth.getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
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

