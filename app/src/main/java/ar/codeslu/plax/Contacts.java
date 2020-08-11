package ar.codeslu.plax;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
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
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ar.codeslu.plax.adapters.ContactsU;
import ar.codeslu.plax.global.AppBack;
import ar.codeslu.plax.global.Global;
import ar.codeslu.plax.lists.CountryToPhonePrefix;
import ar.codeslu.plax.lists.UserData;
import dmax.dialog.SpotsDialog;
import in.myinnos.alphabetsindexfastscrollrecycler.IndexFastScrollRecyclerView;
/**
 * Created by CodeSlu
 */
public class Contacts extends AppCompatActivity {
    //view
    private IndexFastScrollRecyclerView mUserList;
    private ContactsU mUserListAdapter;
    private ImageView sora, refresh;
    private EmojiEditText search;
    private EmojiTextView contact, hint,invitecontactT;
    private LinearLayout invitecontact;
    private TextView contactNum;
    Toolbar toolbar;

    //firebase
    FirebaseAuth mAuth;
    DatabaseReference mUserDB, myData;

    ArrayList<UserData> contactList, searchL;
    ArrayList<String> localContacts;
    int secN = 0;
    AlertDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);
        Global.currentactivity = this;

        mAuth = FirebaseAuth.getInstance();


        myData = FirebaseDatabase.getInstance().getReference(Global.USERS);
        //loader
        if (Global.DARKSTATE) {
            dialog = new SpotsDialog.Builder()
                    .setContext(Contacts.this)
                    .setMessage(R.string.pleasW)
                    .setTheme(R.style.darkDialog)
                    .setCancelable(true)
                    .build();
        } else {
            dialog = new SpotsDialog.Builder()
                    .setContext(Contacts.this)
                    .setMessage(R.string.pleasW)
                    .setCancelable(true)
                    .build();
        }


        dialog.show();
        Global.currentactivity = this;


        sora = findViewById(R.id.sora);
        mUserList = findViewById(R.id.userlist);
        invitecontact = findViewById(R.id.invitecontact);
        invitecontactT = findViewById(R.id.invitecontactT);
        contactNum = findViewById(R.id.contactNum);
        contact = findViewById(R.id.contacts);
        hint = findViewById(R.id.hintC);
        sora.setVisibility(View.GONE);
        contactList = new ArrayList<>();

        contactNum.setVisibility(View.GONE);

if(mAuth.getCurrentUser() != null)
        ((AppBack) getApplication()).getContacts();
else
    Global.contactsG = new ArrayList<>();

        localContacts = new ArrayList<>();
        searchL = new ArrayList<>();
        mUserDB = FirebaseDatabase.getInstance().getReference().child(Global.USERS);
        mUserDB.keepSynced(true);
        mUserList.setHasFixedSize(true);
        mUserList.setNestedScrollingEnabled(true);
        mUserList.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        mUserListAdapter = new ContactsU(Global.contactsG);
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
                if (!Global.check_int(Contacts.this))
                    Toast.makeText(Contacts.this, R.string.check_int, Toast.LENGTH_SHORT).show();
                else {
                    secN = 0;
                    dialog.show();
                    contactList.clear();
                    Global.contactsG.clear();
                    localContacts.clear();
                    mUserListAdapter.notifyDataSetChanged();
                    getContactList();
                }
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
        invitecontact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(Contacts.this,Invite.class));

            }
        });

        Dexter.withActivity(this)
                .withPermissions(Manifest.permission.READ_CONTACTS)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {

                        if (report.areAllPermissionsGranted()) {
                            if (Global.check_int(Contacts.this))
                                Global.contactsG.clear();


                            if (Global.nameLocal == null || Global.nameLocal.isEmpty()) {
                                myData.child(mAuth.getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        UserData userData = dataSnapshot.getValue(UserData.class);
                                        Global.phoneLocal = userData.getPhone();
                                        getContactList();

                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {
                                        dialog.dismiss();
                                    }
                                });
                            } else
                                getContactList();
                        } else
                            Toast.makeText(Contacts.this, getString(R.string.acc_per), Toast.LENGTH_SHORT).show();


                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {

                        token.continuePermissionRequest();

                    }
                }).check();
        try {
            if (mAuth.getCurrentUser() != null) {
                if (!((AppBack) Global.mainActivity.getApplication()).shared().getBoolean("dark" + mAuth.getCurrentUser().getUid(), false)) {
                    contact.setTextColor(Global.conMain.getResources().getColor(R.color.black));
                    invitecontactT.setTextColor(Global.conMain.getResources().getColor(R.color.black));
                    hint.setTextColor(Global.conMain.getResources().getColor(R.color.mid_grey));
                    contactNum.setTextColor(Global.conMain.getResources().getColor(R.color.mid_grey));

                } else {
                    contact.setTextColor(Global.conMain.getResources().getColor(R.color.white));
                    invitecontactT.setTextColor(Global.conMain.getResources().getColor(R.color.white));
                    hint.setTextColor(Global.conMain.getResources().getColor(R.color.light_mid_grey));
                    contactNum.setTextColor(Global.conMain.getResources().getColor(R.color.light_mid_grey));
                }
            }
        } catch (NullPointerException e) {

        }

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
            if(phone.length()>0) {

                if (String.valueOf(phone.charAt(0)).equals("0"))
                    phone = String.valueOf(phone).replaceFirst("0", "");

                if(phone.length()>0) {
                    if (!String.valueOf(phone.charAt(0)).equals("+"))
                        phone = ISOPrefix + phone;
                    if(phone.length()>0) {
                        if (!Global.phoneLocal.equals(phone) && !localContacts.contains(phone) && !phone.equals("t88848992hisuseri9483828snothereri9949ghtnow009933")) {
                            if (!phone.contains(".") && !phone.contains("#") && !phone.contains("$") && !phone.contains("[") && !phone.contains("]"))
                                localContacts.add(phone);
                        }
                    }
                }
            }
        }

        runOnUiThread(new Runnable() {

            @Override
            public void run() {
                if (Global.check_int(Contacts.this)) {
                    for (int i = 0; i < localContacts.size(); i++) {
                        UserData mContact = new UserData("", "", "", "", localContacts.get(i), false, false, "");
                        if (!contactList.contains(mContact))
                            contactList.add(mContact);
                        getUserDetails(mContact);
                    }


                    if (contactList.size() == 0) {
                        sora.setVisibility(View.VISIBLE);
                        contactNum.setVisibility(View.GONE);
                        mUserList.setVisibility(View.GONE);
                        dialog.dismiss();


                    } else {

                        sora.setVisibility(View.GONE);
                        contactNum.setVisibility(View.VISIBLE);
                        mUserList.setVisibility(View.VISIBLE);

                    }

                } else {
                    ((AppBack) getApplication()).getContacts();

                    for (int i = 0; i < Global.contactsG.size(); i++) {
                        if (localContacts.indexOf(Global.contactsG.get(i).getPhone()) == -1) {
                            Global.contactsG.remove(i);
                            ((AppBack) getApplication()).setContacts();
                        }
                    }
                    dialog.dismiss();
                    if (Global.contactsG.size() == 0) {
                        sora.setVisibility(View.VISIBLE);
                        contactNum.setVisibility(View.GONE);
                        mUserList.setVisibility(View.GONE);

                    } else {
                        sora.setVisibility(View.GONE);
                        contactNum.setVisibility(View.VISIBLE);
                        mUserList.setVisibility(View.VISIBLE);
                        if (Global.contactsG.size() == 1)
                            contactNum.setText(Global.contactsG.size() + " " + getResources().getString(R.string.contact));
                        else
                            contactNum.setText(Global.contactsG.size() + " " + getResources().getString(R.string.contacts));


                    }
                    mUserListAdapter.notifyDataSetChanged();

                }

            }
        });


    }

    private void getUserDetails(UserData mContact) {
        Query query = mUserDB.orderByChild("phone").equalTo(mContact.getPhone());
        query.keepSynced(true);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(mAuth.getCurrentUser() != null)
                {
                if (dataSnapshot.exists()) {
                    String phone = "",
                            name = "",
                            nameP = "", ava = "",
                            statue = "";
                    boolean online = false, screen = false;
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
                        if (childSnapshot.child("name").getValue() != null)
                            nameP = childSnapshot.child("name").getValue().toString();
                        if (childSnapshot.child("avatar").getValue() != null)
                            ava = childSnapshot.child("avatar").getValue().toString();
                        if (childSnapshot.child("statue").getValue() != null)
                            statue = childSnapshot.child("statue").getValue().toString();
                        if (childSnapshot.child(Global.Online).getValue() != null)
                            online = (boolean) childSnapshot.child(Global.Online).getValue();
                        if (childSnapshot.child("screen").getValue() != null)
                            screen = (boolean) childSnapshot.child("screen").getValue();

                        if (!childSnapshot.getKey().equals(mAuth.getCurrentUser().getUid()) && !phone.equals("t88848992hisuseri9483828snothereri9949ghtnow009933")) {
                            UserData mUser = new UserData(childSnapshot.getKey(), ava, name, statue, phone, online, screen, nameP);
                            if (name.equals(phone))
                                for (UserData mContactIterator : contactList) {
                                    if (mContactIterator.getPhone().equals(mUser.getPhone())) {
                                        mUser.setNameL(getContactName(mUser.getPhone(), Contacts.this));
                                        mUser.setName(mContactIterator.getName());
                                        mUser.setAvatar(mContactIterator.getAvatar());
                                        mUser.setStatue(mContactIterator.getStatue());
                                        mUser.setScreen(mContactIterator.isScreen());

                                    }
                                }
                            if (!Global.contactsG.contains(mUser))
                                Global.contactsG.add(mUser);
                            ((AppBack) getApplication()).setContacts();
                            mUserListAdapter.notifyDataSetChanged();
                            if (Global.contactsG.size() == 1)
                                contactNum.setText(Global.contactsG.size() + " " + getResources().getString(R.string.contact));
                            else
                                contactNum.setText(Global.contactsG.size() + " " + getResources().getString(R.string.contacts));

                        }

                    }
                    if (secN == Global.contactsG.size()) {
                        dialog.dismiss();
                        Collections.sort(Global.contactsG, new Comparator<UserData>() {
                            @Override
                            public int compare(UserData lhs, UserData rhs) {
                                return lhs.getName().compareTo(rhs.getName());
                            }
                        });
                        mUserListAdapter.notifyDataSetChanged();
                        contactNum.setVisibility(View.VISIBLE);

                        if (Global.contactsG.size() == 0) {
                            sora.setVisibility(View.VISIBLE);
                            contactNum.setVisibility(View.GONE);
                            mUserList.setVisibility(View.GONE);
                            dialog.dismiss();


                        } else {

                            sora.setVisibility(View.GONE);
                            contactNum.setVisibility(View.VISIBLE);
                            mUserList.setVisibility(View.VISIBLE);
                            dialog.dismiss();

                        }

                    }

                } else {
                    if (secN == Global.contactsG.size()) {
                        dialog.dismiss();
                        Collections.sort(Global.contactsG, new Comparator<UserData>() {
                            @Override
                            public int compare(UserData lhs, UserData rhs) {
                                return lhs.getName().compareTo(rhs.getName());
                            }
                        });
                        mUserListAdapter.notifyDataSetChanged();
                        contactNum.setVisibility(View.VISIBLE);

                        if (Global.contactsG.size() == 0) {
                            sora.setVisibility(View.VISIBLE);
                            contactNum.setVisibility(View.GONE);
                            mUserList.setVisibility(View.GONE);
                            dialog.dismiss();


                        } else {

                            sora.setVisibility(View.GONE);
                            contactNum.setVisibility(View.VISIBLE);
                            mUserList.setVisibility(View.VISIBLE);
                            dialog.dismiss();

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

    @Override
    public void onBackPressed() {
        dialog.dismiss();
        super.onBackPressed();
    }

    private String getCountryISO() {
        String countryCode = null;

        // try to get country code from TelephonyManager service
        TelephonyManager tm = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
        if (tm != null) {
            // query first getSimCountryIso()
            countryCode = tm.getSimCountryIso();

            if (countryCode != null && countryCode.length() == 2)
                return CountryToPhonePrefix.getPhone(countryCode.toUpperCase());

            if (tm.getPhoneType() == TelephonyManager.PHONE_TYPE_CDMA) {
                // special case for CDMA Devices
                countryCode = getCDMACountryIso();
            } else {
                // for 3G devices (with SIM) query getNetworkCountryIso()
                countryCode = tm.getNetworkCountryIso();
            }

            if (countryCode != null && countryCode.length() == 2)
                return CountryToPhonePrefix.getPhone(countryCode.toUpperCase());
        }

        try {
            countryCode = CountryToPhonePrefix.getPhone(countryCode.toUpperCase());
        } catch (NullPointerException e) {
            countryCode = CountryToPhonePrefix.getPhone("US");
        }

        return countryCode;
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

    private void filter(String text) {
        ArrayList<UserData> filteredList = new ArrayList<>();

        for (UserData item : Global.contactsG) {
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
            if(mAuth.getCurrentUser() != null)
                mUserDB.child(mAuth.getCurrentUser().getUid()).updateChildren(map);
            Global.local_on = true;
            //lock screen
            ((AppBack) getApplication()).lockscreen(((AppBack) getApplication()).shared().getBoolean("lock", false));
        }

        myApp.stopActivityTransitionTimer();

        try {
            if (Global.wl != null) {
                if (Global.wl.isHeld()) {
                    Global.wl.release();
                }
                Global.wl = null;
            }
        } catch (NullPointerException e) {

        }

    }

    @Override
    public void onPause() {
        super.onPause();
        ((AppBack) this.getApplication()).startActivityTransitionTimer();
        Global.currentactivity = null;

    }

    private static String getCDMACountryIso() {
        try {
            // try to get country code from SystemProperties private class
            Class<?> systemProperties = Class.forName("android.os.SystemProperties");
            Method get = systemProperties.getMethod("get", String.class);

            // get homeOperator that contain MCC + MNC
            String homeOperator = ((String) get.invoke(systemProperties,
                    "ro.cdma.home.operator.numeric"));

            // first 3 chars (MCC) from homeOperator represents the country code
            int mcc = Integer.parseInt(homeOperator.substring(0, 3));

            // mapping just countries that actually use CDMA networks
            switch (mcc) {
                case 330:
                    return "PR";
                case 310:
                    return "US";
                case 311:
                    return "US";
                case 312:
                    return "US";
                case 316:
                    return "US";
                case 283:
                    return "AM";
                case 460:
                    return "CN";
                case 455:
                    return "MO";
                case 414:
                    return "MM";
                case 619:
                    return "SL";
                case 450:
                    return "KR";
                case 634:
                    return "SD";
                case 434:
                    return "UZ";
                case 232:
                    return "AT";
                case 204:
                    return "NL";
                case 262:
                    return "DE";
                case 247:
                    return "LV";
                case 255:
                    return "UA";
            }
        } catch (ClassNotFoundException ignored) {
        } catch (NoSuchMethodException ignored) {
        } catch (IllegalAccessException ignored) {
        } catch (InvocationTargetException ignored) {
        } catch (NullPointerException ignored) {
        }

        return null;
    }


}

