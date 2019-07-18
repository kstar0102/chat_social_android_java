package ar.codeslu.plax;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.recyclerview.widget.LinearLayoutManager;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.widget.Toolbar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.vanniktech.emoji.EmojiEditText;
import com.vanniktech.emoji.EmojiTextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ar.codeslu.plax.adapters.ContactsU;
import ar.codeslu.plax.global.AppBack;
import ar.codeslu.plax.global.Global;
import ar.codeslu.plax.lists.CountryToPhonePrefix;
import ar.codeslu.plax.lists.UserData;
import ar.codeslu.plax.story.Stories;
import dmax.dialog.SpotsDialog;
import in.myinnos.alphabetsindexfastscrollrecycler.IndexFastScrollRecyclerView;

public class Contacts extends AppCompatActivity {
    //view
    private IndexFastScrollRecyclerView mUserList;
    private ContactsU mUserListAdapter;
    private ImageView sora, refresh;
    private EmojiEditText search;
    private EmojiTextView contact, hint;
    private TextView contactNum;
    Toolbar toolbar;

    //firebase
    FirebaseAuth mAuth;
    DatabaseReference mUserDB;

    ArrayList<UserData> userList, contactList, searchL;
    ArrayList<String> localContacts;
    int secN = 0;
    AlertDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);
        sora = findViewById(R.id.sora);
        mUserList = findViewById(R.id.userlist);
        contactNum = findViewById(R.id.contactNum);
        contact = findViewById(R.id.contacts);
        hint = findViewById(R.id.hintC);
        sora.setVisibility(View.GONE);
        contactList = new ArrayList<>();
        userList = new ArrayList<>();
        localContacts = new ArrayList<>();
        searchL = new ArrayList<>();
        mUserDB = FirebaseDatabase.getInstance().getReference().child(Global.USERS);
        mUserDB.keepSynced(true);
        mAuth = FirebaseAuth.getInstance();
        mUserList.setHasFixedSize(true);
        mUserList.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        mUserListAdapter = new ContactsU(userList);
        mUserList.setAdapter(mUserListAdapter);
        //custom recyclerview
        mUserList.setIndexBarTextColor(R.color.white);
        mUserList.setIndexBarColor(R.color.colorPrimaryDark2awy);
        //dark mode init
        if (mAuth.getCurrentUser() != null) {
            if (!((AppBack) getApplication()).shared().getBoolean("dark" + mAuth.getCurrentUser().getUid(), false)) {
                getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            } else {
                getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            }
        }
        //Actionbar init
        toolbar = (Toolbar) findViewById(R.id.search_toolbar);
        toolbar.setPadding(0, 0, 0, 0);

        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayShowCustomEnabled(true);

        //Action bar design
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View viewS = inflater.inflate(R.layout.custom_bar, null);
        actionBar.setCustomView(viewS);
        search = viewS.findViewById(R.id.searchE);
        refresh = viewS.findViewById(R.id.refreshC);
        refresh.setFocusableInTouchMode(false);
        refresh.setFocusable(false);
        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Global.check_int(Contacts.this)) {
                    dialog.show();
                    contactList.clear();
                    userList.clear();
                    localContacts.clear();
                    mUserListAdapter.notifyDataSetChanged();
                    getContactList();

                } else
                    Toast.makeText(Contacts.this, R.string.check_int, Toast.LENGTH_SHORT).show();
            }
        });
        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                filter(editable.toString());

            }
        });

        Global.currentactivity = this;
        Dexter.withActivity(this)
                .withPermissions(Manifest.permission.READ_CONTACTS)
                .withListener(new MultiplePermissionsListener() {
                    @Override public void onPermissionsChecked(MultiplePermissionsReport report) {

                        if(report.areAllPermissionsGranted())
                            getContactList();

                        else
                            Toast.makeText(Contacts.this, getString(R.string.acc_per), Toast.LENGTH_SHORT).show();


                    }
                    @Override public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {

                        token.continuePermissionRequest();

                    }
                }).check();
        if (mAuth.getCurrentUser() != null) {
            if (!((AppBack) Global.mainActivity.getApplication()).shared().getBoolean("dark" + mAuth.getCurrentUser().getUid(), false)) {
                contact.setTextColor(Global.conMain.getResources().getColor(R.color.black));
                hint.setTextColor(Global.conMain.getResources().getColor(R.color.mid_grey));
                contactNum.setTextColor(Global.conMain.getResources().getColor(R.color.mid_grey));

            } else {
                contact.setTextColor(Global.conMain.getResources().getColor(R.color.white));
                hint.setTextColor(Global.conMain.getResources().getColor(R.color.light_mid_grey));
                contactNum.setTextColor(Global.conMain.getResources().getColor(R.color.light_mid_grey));
            }
        }

        //loader
        if (Global.DARKSTATE)
        {
            dialog = new SpotsDialog.Builder()
                    .setContext(this)
                    .setMessage(R.string.pleasW)
                    .setTheme(R.style.darkDialog)
                    .setCancelable(true)
                    .build();
        }
        else
        {
            dialog = new SpotsDialog.Builder()
                    .setContext(this)
                    .setMessage(R.string.pleasW)
                    .setCancelable(true)
                    .build();
        }


        dialog.show();

    }

    private void getContactList() {
        String ISOPrefix = getCountryISO();
        Cursor phones = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME+" ASC");
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
                            UserData mContact = new UserData("", "", "", "", localContacts.get(i), false,false);
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
                    String phone = "",
                            name = "", ava = "",
                            statue = "";
                    boolean online=false,screen = false;
                    secN++;
                    if (secN == contactList.size())
                        dialog.dismiss();
                    for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                        if (childSnapshot.child("phone").getValue() != null) {
                            phone = childSnapshot.child("phone").getValue().toString();
                            dialog.dismiss();
                        }
                        name = getContactName(phone, Contacts.this);
                        if (TextUtils.isEmpty(name) || name == null) {
                            if (childSnapshot.child("name").getValue() != null)
                                phone = childSnapshot.child("name").getValue().toString();
                        }
                        if (childSnapshot.child("avatar").getValue() != null)
                            ava = childSnapshot.child("avatar").getValue().toString();
                        if (childSnapshot.child("statue").getValue() != null)
                            statue = childSnapshot.child("statue").getValue().toString();
                        if (childSnapshot.child("Onstatue").getValue() != null)
                            online = (boolean) childSnapshot.child("Onstatue").getValue();
                        if (childSnapshot.child("screen").getValue() != null)
                            screen = (boolean) childSnapshot.child("screen").getValue();
                        UserData mUser = new UserData(childSnapshot.getKey(), ava, name, statue, phone, online,screen);
                        if (name.equals(phone))
                            for (UserData mContactIterator : contactList) {
                                if (mContactIterator.getPhone().equals(mUser.getPhone())) {
                                    mUser.setNameL(getContactName(mUser.getPhone(), Contacts.this));
                                    mUser.setAvatar(mContactIterator.getAvatar());
                                    mUser.setStatue(mContactIterator.getStatue());
                                    mUser.setScreen(mContactIterator.isScreen());

                                }
                            }

                        userList.add(mUser);
                        mUserListAdapter.notifyDataSetChanged();
                        if (userList.size() == 1)
                            contactNum.setText(userList.size() + " " + getResources().getString(R.string.contact));
                        else
                            contactNum.setText(userList.size() + " " + getResources().getString(R.string.contacts));
                        sora.setVisibility(View.GONE);
                        contactNum.setVisibility(View.VISIBLE);
                        mUserList.setVisibility(View.VISIBLE);


                        return;
                    }


                } else {
                    if (userList.size() == 0 && secN == contactList.size()) {
                        sora.setVisibility(View.VISIBLE);
                        contactNum.setVisibility(View.GONE);
                        mUserList.setVisibility(View.GONE);
                        dialog.dismiss();
                    }

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onBackPressed() {
        dialog.dismiss();
        super.onBackPressed();
    }

    private String getCountryISO() {
        String iso = null;

        TelephonyManager telephonyManager = (TelephonyManager) getApplicationContext().getSystemService(getApplicationContext().TELEPHONY_SERVICE);
        if (telephonyManager.getNetworkCountryIso() != null)
            if (!telephonyManager.getNetworkCountryIso().toString().equals(""))
                iso = telephonyManager.getNetworkCountryIso().toString();

        return CountryToPhonePrefix.getPhone(iso);
    }

    public String getContactName(final String phoneNumber, Context context) {
        Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(phoneNumber));

        String[] projection = new String[]{ContactsContract.PhoneLookup.DISPLAY_NAME};

        String contactName = "";
        Cursor cursor = context.getContentResolver().query(uri, projection, null, null, null);

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                contactName = cursor.getString(0);
            }
            cursor.close();
        }

        return contactName;
    }

    //    public static int calculateNoOfColumns(Context context) {
//        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
//        float dpWidth = displayMetrics.widthPixels / displayMetrics.density;
//        int scalingFactor = 110; // You can vary the value held by the scalingFactor
//        // variable. The smaller it is the more no. of columns you can display, and the
//        // larger the value the less no. of columns will be calculated. It is the scaling
//        // factor to tweak to your needs.
//        int columnCount = (int) (dpWidth / scalingFactor);
//        return (columnCount>=2?columnCount:2); // if column no. is less than 2, we still display 2 columns
//    }
    public static int calcspace(Context context) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        float dpWidth = displayMetrics.widthPixels / displayMetrics.density;
        int space = (int) (dpWidth - (80 * 3)) / 3;
        return (space);
    }

    private void filter(String text) {
        ArrayList<UserData> filteredList = new ArrayList<>();

        for (UserData item : userList) {
            if (item.getNameL().toLowerCase().contains(text.toLowerCase()))
                filteredList.add(item);

            else if (item.getPhone().toLowerCase().contains(text.toLowerCase()))
                filteredList.add(item);


        }

        mUserListAdapter.filterList(filteredList);
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
            mUserDB.child(mAuth.getCurrentUser().getUid()).updateChildren(map);
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

