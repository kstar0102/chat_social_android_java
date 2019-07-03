package ar.codeslu.plax.story;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
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
import com.instacart.library.truetime.TrueTime;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import ar.codeslu.plax.Chat;
import ar.codeslu.plax.Contacts;
import ar.codeslu.plax.R;
import ar.codeslu.plax.adapters.StoryAdapter;
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
    DatabaseReference myData,mUserDB,mOtherData;

    //vars
    ArrayList<StoryModel> myS,otherS;
    ArrayList<StoryListRetr> getlistS;
    ArrayList<String> localContacts;
    ArrayList<UserData> userList, contactList, searchL;
    String phone = "",
            name = "", ava = "",id="";
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

        getContactList();



        Query query = myData.child(mAuth.getCurrentUser().getUid()).child(Global.StoryS);
        query.keepSynced(true);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                myS.clear();
                storyView.setImageUris(myS, getApplicationContext());
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
                                    if (post.getLink() != null)
                                        myS.add(new StoryModel(encryption.decryptOrNull(post.getLink()), getString(R.string.me), GetTime.getTimeAgo(post.getTime(), Stories.this)));
                                } catch (NullPointerException e) {

                                }
                            }
                            storyView.setImageUris(myS, getApplicationContext());
                            if (myS.size() > 0)
                                storyView.setVisibility(View.VISIBLE);

                            else
                                storyView.setVisibility(View.GONE);


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
//            if (!Global.phoneLocal.equals(phone) && !localContacts.contains(phone)) {
//
//            }
            localContacts.add(phone);
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

                        otherS.clear();
                        Query query = myData.child(id).child(Global.StoryS);
                        query.keepSynced(true);
                        query.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
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
                                                    if (post.getLink() != null)
                                                    {
                                                        otherS.add(new StoryModel(encryption.decryptOrNull(post.getLink()), getString(R.string.me), GetTime.getTimeAgo(post.getTime(), Stories.this)));
                                                    }
                                                } catch (NullPointerException e) {

                                                }
                                            }
                                        }
                                    });
                                }
                                getlistS.add(new StoryListRetr(otherS,name,ava));
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



}
