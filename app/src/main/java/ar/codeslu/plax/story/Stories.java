package ar.codeslu.plax.story;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;

import android.provider.ContactsContract;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.fxn.pix.Options;
import com.fxn.pix.Pix;
import com.fxn.utility.PermUtil;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.functions.FirebaseFunctions;
import com.google.firebase.functions.HttpsCallableResult;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.stfalcon.chatkit.me.UserIn;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import ar.codeslu.plax.Chat;
import ar.codeslu.plax.Contacts;
import ar.codeslu.plax.R;
import ar.codeslu.plax.adapters.StoryAdapter;
import ar.codeslu.plax.global.AppBack;
import ar.codeslu.plax.global.GetTime;
import ar.codeslu.plax.global.Global;
import ar.codeslu.plax.lists.CountryToPhonePrefix;
import ar.codeslu.plax.lists.StoryList;
import ar.codeslu.plax.lists.StoryListRetr;
import ar.codeslu.plax.lists.UserData;
import se.simbio.encryption.Encryption;
import xute.storyview.StoryModel;
import xute.storyview.StoryView;

public class Stories extends AppCompatActivity {
    RecyclerView storyList;
    StoryAdapter adapter;
    ArrayList<StoryList> array;
    ImageView addStory;
    StoryView storyView;
    //Firebase
    FirebaseAuth mAuth;
    DatabaseReference myData, mUserDB, mOtherData;


    //vars
    ArrayList<StoryModel> myS, otherS;
    ArrayList<StoryListRetr> getlistS;
    ArrayList<String> localContacts;
    ArrayList<UserData> userList, contactList, searchL;
    String name = "", ava = "", id = "";
    int enter = 0, enterM = 0;

    //enc
    Encryption encryption;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stories);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        storyList = findViewById(R.id.storylist);
        addStory = findViewById(R.id.newS);

        storyView = findViewById(R.id.storyView);
        //arrays init
        myS = new ArrayList<>();
        otherS = new ArrayList<>();
        getlistS = new ArrayList<>();
        contactList = new ArrayList<>();
        userList = new ArrayList<>();
        localContacts = new ArrayList<>();
        searchL = new ArrayList<>();

        //enc
        //encryption
        byte[] iv = new byte[16];
        encryption = Encryption.getDefault(Global.keyE, Global.salt, iv);

//firebase

        mAuth = FirebaseAuth.getInstance();
        myData = FirebaseDatabase.getInstance().getReference(Global.USERS);
        mOtherData = FirebaseDatabase.getInstance().getReference(Global.USERS);
        mUserDB = FirebaseDatabase.getInstance().getReference().child(Global.USERS);
        mUserDB.keepSynced(true);
        //dark mode init
        if (mAuth.getCurrentUser() != null) {
            if (!((AppBack) getApplication()).shared().getBoolean("dark" + mAuth.getCurrentUser().getUid(), false)) {
                getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            } else {
                getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            }
        }


        Dexter.withActivity(Stories.this)
                .withPermissions(Manifest.permission.READ_CONTACTS)
                .withListener(new MultiplePermissionsListener() {
                    @Override public void onPermissionsChecked(MultiplePermissionsReport report) {

                        if(report.areAllPermissionsGranted())
                            getContactList();

                        else
                            Toast.makeText(Stories.this, getString(R.string.acc_per), Toast.LENGTH_SHORT).show();


                    }
                    @Override public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {

                        token.continuePermissionRequest();

                    }
                }).check();


        Query query = myData.child(mAuth.getCurrentUser().getUid()).child(Global.StoryS);
        query.keepSynced(true);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                myS.clear();
                if (Global.check_int(Stories.this))
                    enterM = 0;

                storyView.setVisibility(View.GONE);
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    StoryList post = postSnapshot.getValue(StoryList.class);
                    FirebaseFunctions.getInstance().getHttpsCallable("getTime")
                            .call().addOnSuccessListener(new OnSuccessListener<HttpsCallableResult>() {
                        @Override
                        public void onSuccess(HttpsCallableResult httpsCallableResult) {
                            long now = (long) httpsCallableResult.getData();

                            int hours = (int) TimeUnit.MILLISECONDS.toHours(now - post.getTime());
                            if (hours < 24) {
                                try {
                                    if (post.getLink() != null) {
                                        myS.add(new StoryModel(encryption.decryptOrNull(post.getLink()), getString(R.string.me), GetTime.getTimeAgo(post.getTime(), Stories.this),post.getTime()));
                                        enterM = enterM + 1;
                                        if (enterM == dataSnapshot.getChildrenCount() - 1) {
                                            storyView.setImageUris(myS, getApplicationContext());
                                            if (myS.size() > 0)
                                                storyView.setVisibility(View.VISIBLE);

                                            else
                                                storyView.setVisibility(View.GONE);

                                            enterM = 0;
                                        }

                                    }
                                } catch (NullPointerException e) {
                                    storyView.setVisibility(View.GONE);
                                }
                            }
                            /////


                        }
                    });
                }
                if (myS.size() == 0)
                    storyView.setVisibility(View.GONE);


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        array = new ArrayList<>();
        adapter = new StoryAdapter(getlistS);
        storyList.setAdapter(adapter);
        storyList.setLayoutManager(new LinearLayoutManager(this));
        addStory.setOnClickListener(v -> startActivity(new Intent(Stories.this, AddStory.class)));

    }

    private void getContactList() {
        String ISOPrefix = getCountryISO();
        Cursor phones = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC");
        while (phones.moveToNext()) {
            String phone = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));

            phone = phone.replace(" ", "");
            phone = phone.replace("-", "");
            phone = phone.replace("(", "");
            phone = phone.replace(")", "");

            if (String.valueOf(phone.charAt(0)).equals("0"))
                phone = String.valueOf(phone).replaceFirst("0", "");

            if (!String.valueOf(phone.charAt(0)).equals("+"))
                phone = ISOPrefix + phone;
            if (!Global.phoneLocal.equals(phone) && !localContacts.contains(phone)) {
                localContacts.add(phone);
            }
        }
        new Thread(
                new Runnable() {
                    @Override
                    public void run() {
                        for (int i = 0; i < localContacts.size(); i++) {
                            UserData mContact = new UserData("", "", "", "", localContacts.get(i), false, false);
                            contactList.add(mContact);
                            getUserDetails(mContact);
                        }
                    }
                }
        ).start();


    }

    private void getUserDetails(UserData mContact) {
        Query query = mUserDB.orderByChild("phone").equalTo(mContact.getPhone());
        query.keepSynced(true);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                        if (childSnapshot.child("name").getValue() != null)
                            name = childSnapshot.child("name").getValue().toString();

                        if (childSnapshot.child("avatar").getValue() != null)
                            ava = childSnapshot.child("avatar").getValue().toString();
                        if (childSnapshot.child("id").getValue() != null)
                            id = childSnapshot.child("id").getValue().toString();

                        Query query = myData.child(id).child(Global.StoryS);
                        query.keepSynced(true);
                        query.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                otherS.clear();
                                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                                    StoryList post = postSnapshot.getValue(StoryList.class);
                                    FirebaseFunctions.getInstance().getHttpsCallable("getTime")
                                            .call().addOnSuccessListener(new OnSuccessListener<HttpsCallableResult>() {
                                        @Override
                                        public void onSuccess(HttpsCallableResult httpsCallableResult) {
                                            long now = (long) httpsCallableResult.getData();
                                            int hours = (int) TimeUnit.MILLISECONDS.toHours(now - post.getTime());
                                            if (hours < 24) {
                                                try {
                                                    if (post.getLink() != null) {

                                                        otherS.add(new StoryModel(encryption.decryptOrNull(post.getLink()), name, GetTime.getTimeAgo(post.getTime(), Stories.this),post.getTime()));

                                                        if (enter == dataSnapshot.getChildrenCount() - 1) {
                                                            for(int i=0;i<getlistS.size();i++)
                                                            {
                                                                if(getlistS.get(i).getUID().equals(id))
                                                                {
                                                                 getlistS.remove(i);
                                                                 setStory(otherS,name,ava,id,otherS.get(otherS.size()-1).timeL);
                                                                    break;
                                                                }



                                                            }
                                                            enter = 0;
                                                        }
                                                        else
                                                        enter = enter + 1;


                                                    }
                                                } catch (NullPointerException e) {

                                                }
                                            }
                                        }
                                    });
                                }


                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });


                    }


                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private String getCountryISO() {
        String iso = null;

        TelephonyManager telephonyManager = (TelephonyManager) getApplicationContext().getSystemService(getApplicationContext().TELEPHONY_SERVICE);
        if (telephonyManager.getNetworkCountryIso() != null)
            if (!telephonyManager.getNetworkCountryIso().toString().equals(""))
                iso = telephonyManager.getNetworkCountryIso().toString();

        return CountryToPhonePrefix.getPhone(iso);
    }

    private  void setStory(ArrayList<StoryModel> list ,String name,String ava,String id,long lastTime)
    {
        getlistS.add(new StoryListRetr(otherS, name, ava,id,lastTime));
        adapter.notifyDataSetChanged();
        arrange();
    }

    private void arrange()
    {
        StoryListRetr temp;
        for(int i=0;i<getlistS.size();i++) {
            if (i != getlistS.size() - 1)
            {
                if (getlistS.get(i).getLastTime() < getlistS.get(i + 1).getLastTime()) {
                    temp = getlistS.get(i);
                    getlistS.set(i, getlistS.get(i + 1));
                    getlistS.set(i + 1, temp);
                    arrange();
                    break;
                }
            }

        }

        adapter.notifyDataSetChanged();

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
            myData.child(mAuth.getCurrentUser().getUid()).updateChildren(map);
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
