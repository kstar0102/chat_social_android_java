package com.codeslutest.plax.Groups;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
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
import com.stfalcon.chatkit.me.GroupIn;
import com.vanniktech.emoji.EmojiTextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.ContactsContract;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import net.glxn.qrgen.android.QRCode;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.codeslutest.plax.R;
import com.codeslutest.plax.adapters.GroupsContacts;
import com.codeslutest.plax.global.AppBack;
import com.codeslutest.plax.global.Global;
import com.codeslutest.plax.global.encryption;
import com.codeslutest.plax.lists.CountryToPhonePrefix;
import com.codeslutest.plax.lists.Tokens;
import com.codeslutest.plax.lists.UserData;
import com.codeslutest.plax.notify.FCM;
import com.codeslutest.plax.notify.FCMresp;
import com.codeslutest.plax.notify.Sender;

import dmax.dialog.SpotsDialog;
import in.myinnos.alphabetsindexfastscrollrecycler.IndexFastScrollRecyclerView;
import it.auron.library.mecard.MeCard;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class AddUserGroup extends AppCompatActivity {

    //view
    private IndexFastScrollRecyclerView mUserList;
    private GroupsContacts mUserListAdapter;
    private ImageView sora;
    private EmojiTextView contact, hint;
    private TextView contactNum;
    //firebase
    FirebaseAuth mAuth;
    DatabaseReference mUserDB;
    DatabaseReference group;

    ArrayList<UserData> contactList, searchL;
    ArrayList<String> localContacts;
    int secN = 0;
    AlertDialog dialog;
    String imgLocalpath = "no";
    String groupid;
    byte[] thumbData;
    //compress
    private Bitmap compressedImageFile;
    ImageView qr;
    //fcm
    FCM fcm;
    int before = 0;
    String id,name,ava;

    private void generateQr() {
        MeCard meCard = new MeCard();
        meCard.setName(Global.currname);
        meCard.setAddress(groupid);
        meCard.setUrl(Global.currAva);
        meCard.setNote(getResources().getString(R.string.groups));
        String meCardContent = meCard.buildString();
        qr.setImageBitmap(QRCode.from(meCardContent).withSize(250, 250).bitmap());

    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_user_group);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        FloatingActionButton fab = findViewById(R.id.fab);
        Global.currentactivity = this;
        //loader
        //fcm notify
        fcm = Global.getFCMservies();
        if (Global.DARKSTATE) {
            dialog = new SpotsDialog.Builder()
                    .setContext(AddUserGroup.this)
                    .setMessage(R.string.pleasW)
                    .setTheme(R.style.darkDialog)
                    .setCancelable(true)
                    .build();
        } else {
            dialog = new SpotsDialog.Builder()
                    .setContext(AddUserGroup.this)
                    .setMessage(R.string.pleasW)
                    .setCancelable(true)
                    .build();
        }

        Global.groupids = new ArrayList<>();
        dialog.show();
        qr = findViewById(R.id.qr);
        sora = findViewById(R.id.sora);
        mUserList = findViewById(R.id.userlist);
        contactNum = findViewById(R.id.contactNum);
        contact = findViewById(R.id.contacts);
        hint = findViewById(R.id.hintC);
        sora.setVisibility(View.GONE);
        contactList = new ArrayList<>();
        ((AppBack) getApplication()).getContacts();
        localContacts = new ArrayList<>();
        searchL = new ArrayList<>();
        mUserDB = FirebaseDatabase.getInstance().getReference().child(Global.USERS);
        group = FirebaseDatabase.getInstance().getReference().child(Global.GROUPS);
        mUserDB.keepSynced(true);
        mAuth = FirebaseAuth.getInstance();
        mUserList.setHasFixedSize(true);
        mUserList.setNestedScrollingEnabled(true);
        mUserList.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        mUserListAdapter = new GroupsContacts(Global.contactsG);
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

        if (getIntent() != null) {
            groupid = getIntent().getExtras().getString("id");
            generateQr();

        }
        Global.currentactivity = this;
        Dexter.withActivity(this)
                .withPermissions(Manifest.permission.READ_CONTACTS)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {

                        if (report.areAllPermissionsGranted()) {
                            if (Global.check_int(AddUserGroup.this))
                                Global.contactsG.clear();

                            getContactList();
                        } else
                            Toast.makeText(AddUserGroup.this, getString(R.string.acc_per), Toast.LENGTH_SHORT).show();


                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {

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

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Global.check_int(AddUserGroup.this)) {
                    if (Global.groupids.size() == 0)
                        Snackbar.make(view, getString(R.string.plz_chs_per), Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                    else {
                        dialog.show();
                        Global.currGUsers.addAll(Global.groupids);
                        mUserDB.child(mAuth.getCurrentUser().getUid()).child(Global.GROUPS).child(groupid).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                GroupIn groupIn = dataSnapshot.getValue(GroupIn.class);
                                id = groupIn.getId();
                                ava = encryption.decryptOrNull(groupIn.getAvatar());
                                name = groupIn.getName();
                                Map<String, Object> map2 = new HashMap<>();
                                map2.put("users",  Global.currGUsers);
                                map2.put("admins", Global.currGAdmins);
                                Map<String, Object> map = new HashMap<>();
                                map.put("name", groupIn.getName());
                                map.put("id", groupIn.getId());
                                map.put("avatar", groupIn.getAvatar());
                                map.put("created", groupIn.getCreated());
                                map.put("messDate", groupIn.getMessDate());
                                map.put("lastmessage", groupIn.getLastmessage());
                                map.put("lastsenderava", groupIn.getLastsenderava());
                                map.put("lastsender", groupIn.getLastsender());
                                map.put("noOfUnread", 0);

                                group.child(groupid).updateChildren(map2).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        putIndB(map);
                                    }
                                });
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });

                    }
                } else
                    Snackbar.make(view, getString(R.string.check_int), Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
            }

        });


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

                    if (phone.length() > 0) {
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
                if (Global.check_int(AddUserGroup.this)) {
                    for (int i = 0; i < localContacts.size(); i++) {
                        UserData mContact = new UserData("", "", "", "", localContacts.get(i), false, false, "");
                        contactList.add(mContact);
                        getUserDetails(mContact);
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
                        name = getContactName(phone, AddUserGroup.this);
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
                                        mUser.setNameL(getContactName(mUser.getPhone(), AddUserGroup.this));
                                        mUser.setName(mContactIterator.getName());
                                        mUser.setAvatar(mContactIterator.getAvatar());
                                        mUser.setStatue(mContactIterator.getStatue());
                                        mUser.setScreen(mContactIterator.isScreen());

                                    }
                                }
                            if (!Global.currGUsers.contains(mUser.getId())) {
                                Global.contactsG.add(mUser);
                                ((AppBack) getApplication()).setContacts();
                                mUserListAdapter.notifyDataSetChanged();
                            }
                            if (Global.contactsG.size() == 1)
                                contactNum.setText(Global.contactsG.size() + " " + getResources().getString(R.string.contact));
                            else
                                contactNum.setText(Global.contactsG.size() + " " + getResources().getString(R.string.contacts));

                            sora.setVisibility(View.GONE);
                            contactNum.setVisibility(View.VISIBLE);
                            mUserList.setVisibility(View.VISIBLE);
                        }

                    }
                    if (secN == Global.contactsG.size()) {
                        dialog.dismiss();
                    }

                } else {
                    if (Global.contactsG.size() == 0 && secN == Global.contactsG.size()) {
                        sora.setVisibility(View.VISIBLE);
                        contactNum.setVisibility(View.GONE);
                        mUserList.setVisibility(View.GONE);
                        dialog.dismiss();
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


    private void sendMessNotify(String friendId, Map<String, Object> map2) {
        String Message = encryption.encryptOrNull(Global.nameLocal + getString(R.string.add_you) + name);
        String messidL = mAuth.getCurrentUser().getUid() + "_" + friendId + "_" + String.valueOf(System.currentTimeMillis());

        DatabaseReference mTokenget = FirebaseDatabase.getInstance().getReference(Global.tokens);
        mTokenget.child(friendId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Tokens tokens = dataSnapshot.getValue(Tokens.class);

                Map<String, String> map = new HashMap<>();
                map.put("nType","addG");
                map.put("tokens",tokens.toString());
                map.put("senderId",id);
                map.put("senderName",name);
                map.put("senderAva",ava);
                map.put("Mid",messidL);
                map.put("to",friendId);

                map.put("message", Message);
                Sender sender = new Sender(tokens.getTokens(), map);
                fcm.send(sender)
                        .enqueue(new Callback<FCMresp>() {
                            @Override
                            public void onResponse(Call<FCMresp> call, Response<FCMresp> response) {

                                if (before < Global.groupids.size() - 1) {
                                    before += 1;
                                    putIndB(map2);
                                } else {
//go
                                    finish();
                                    before = 0;
                                    dialog.dismiss();
                                    finish();

                                }

                            }

                            @Override
                            public void onFailure(Call<FCMresp> call, Throwable t) {
                                dialog.dismiss();
                            }
                        });

            }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });






    }

    public void putIndB(Map<String, Object> map2) {

        if (before < Global.groupids.size()) {
            mUserDB.child(Global.groupids.get(before)).child("Groups").child(map2.get("id").toString()).updateChildren(map2).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    if (!Global.groupids.get(before).equals(mAuth.getCurrentUser().getUid())) {
                        sendMessNotify(Global.groupids.get(before), map2);
                    } else {
                        if (before < Global.groupids.size() - 1) {
                            before += 1;
                            putIndB(map2);
                        } else {
//go
                            finish();
                            before = 0;
                            dialog.dismiss();
                            finish();

                        }
                    }
                }
            });


        }

    }

}

