package ar.codeslu.plax;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.stfalcon.chatkit.me.GroupIn;
import com.stfalcon.chatkit.me.Message;
import com.stfalcon.chatkit.me.MessageIn;
import com.stfalcon.chatkit.me.UserIn;
import com.vanniktech.emoji.EmojiTextView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ar.codeslu.plax.Groups.Group;
import ar.codeslu.plax.adapters.ForwardAdapter;
import ar.codeslu.plax.adapters.ForwardAdapterG;
import ar.codeslu.plax.datasetters.MessageData;
import ar.codeslu.plax.fragments.Chats;
import ar.codeslu.plax.fragments.Groups;
import ar.codeslu.plax.global.AppBack;
import ar.codeslu.plax.global.Global;
import ar.codeslu.plax.global.encryption;
import ar.codeslu.plax.lists.CountryToPhonePrefix;
import ar.codeslu.plax.lists.Tokens;
import ar.codeslu.plax.lists.UserData;
import ar.codeslu.plax.lists.calls;
import ar.codeslu.plax.notify.FCM;
import ar.codeslu.plax.notify.FCMresp;
import ar.codeslu.plax.notify.Sender;
import dmax.dialog.SpotsDialog;
import id.zelory.compressor.Compressor;
import in.myinnos.alphabetsindexfastscrollrecycler.IndexFastScrollRecyclerView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class Forward extends AppCompatActivity {

    //view
    private IndexFastScrollRecyclerView mUserList, grouplist;
    private ForwardAdapter mUserListAdapter;
    private ImageView sora;
    private EmojiTextView contact, hint, groups;
    private TextView contactNum;
    //firebase
    FirebaseAuth mAuth;
    DatabaseReference mUserDB;
    DatabaseReference group;

    ArrayList<UserData> contactList, searchL;
    ArrayList<String> localContacts;
    int secN = 0;
    AlertDialog dialog;
    //compress

    //fcm
    FCM fcm;
    String id, messidNew;
    Message forwardM;
    DatabaseReference mData, userD, mMess;

    ArrayList<String> groupids;
    ForwardAdapterG groupAdapter;
    MessageIn messageLocal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_user_fav);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        FloatingActionButton fab = findViewById(R.id.fab);
        Global.currentactivity = this;
        //encryption
        //loader
        //fcm notify
        fcm = Global.getFCMservies();
        if (Global.DARKSTATE) {
            dialog = new SpotsDialog.Builder()
                    .setContext(Forward.this)
                    .setMessage(R.string.pleasW)
                    .setTheme(R.style.darkDialog)
                    .setCancelable(true)
                    .build();
        } else {
            dialog = new SpotsDialog.Builder()
                    .setContext(Forward.this)
                    .setMessage(R.string.pleasW)
                    .setCancelable(true)
                    .build();
        }


        forwardM = Global.forwardMessage;

        mMess = FirebaseDatabase.getInstance().getReference(Global.CHATS);
        userD = FirebaseDatabase.getInstance().getReference(Global.USERS);
        mData = FirebaseDatabase.getInstance().getReference(Global.GROUPS);

        Global.forwardids = new ArrayList<>();
        dialog.show();
        sora = findViewById(R.id.sora);
        mUserList = findViewById(R.id.userlist);
        grouplist = findViewById(R.id.grouplist);
        contactNum = findViewById(R.id.contactNum);
        contact = findViewById(R.id.contacts);
        groups = findViewById(R.id.groups);
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
        mUserListAdapter = new ForwardAdapter(Global.contactsG);
        mUserList.setAdapter(mUserListAdapter);
        //custom recyclerview
        mUserList.setIndexBarTextColor(R.color.white);
        mUserList.setIndexBarColor(R.color.colorPrimaryDark2awy);


        ((AppBack) getApplication()).getGroupsM();
        groupids = new ArrayList<>();
        grouplist.setHasFixedSize(true);
        grouplist.setNestedScrollingEnabled(true);
        grouplist.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        groupAdapter = new ForwardAdapterG(Global.groupsArray);
        grouplist.setAdapter(groupAdapter);
        //custom recyclerview
        grouplist.setIndexBarTextColor(R.color.white);
        grouplist.setIndexBarColor(R.color.colorPrimaryDark2awy);


        if (mAuth.getCurrentUser() != null) {
            userD.child(mAuth.getCurrentUser().getUid()).child(Global.GROUPS).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        for (DataSnapshot data : dataSnapshot.getChildren()) {
                            groupids.add(data.getKey());
                        }

                        Global.groupsArray.clear();

                        for (int i = 0; i < groupids.size(); i++) {
                            mData.child(groupids.get(i)).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    GroupIn groupIn = dataSnapshot.getValue(GroupIn.class);
                                    Global.groupsArray.add(groupIn);
                                    ((AppBack) getApplication()).setGroupsM();
                                    groupAdapter.notifyDataSetChanged();
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

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


        //dark mode init
        if (mAuth.getCurrentUser() != null) {
            if (!((AppBack) getApplication()).shared().getBoolean("dark" + mAuth.getCurrentUser().getUid(), false)) {
                getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            } else {
                getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            }
        }

        Global.currentactivity = this;
        Dexter.withActivity(this)
                .withPermissions(Manifest.permission.READ_CONTACTS)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {

                        if (report.areAllPermissionsGranted()) {
                            if (Global.check_int(Forward.this))
                                Global.contactsG.clear();

                            getContactList();
                        } else
                            Toast.makeText(Forward.this, getString(R.string.acc_per), Toast.LENGTH_SHORT).show();


                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {

                        token.continuePermissionRequest();

                    }
                }).check();
        if (mAuth.getCurrentUser() != null) {
            if (!((AppBack) getApplication()).shared().getBoolean("dark" + mAuth.getCurrentUser().getUid(), false)) {
                contact.setTextColor(this.getResources().getColor(R.color.black));
                groups.setTextColor(this.getResources().getColor(R.color.black));
                hint.setTextColor(this.getResources().getColor(R.color.mid_grey));
                contactNum.setTextColor(this.getResources().getColor(R.color.mid_grey));

            } else {
                contact.setTextColor(this.getResources().getColor(R.color.white));
                groups.setTextColor(this.getResources().getColor(R.color.white));
                hint.setTextColor(this.getResources().getColor(R.color.light_mid_grey));
                contactNum.setTextColor(this.getResources().getColor(R.color.light_mid_grey));
            }
        }
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Global.forwardids.size() == 0)
                    Snackbar.make(view, getString(R.string.plz_chs_per), Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                else {

                    dialog.show();
                    goForward();


                }

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
            if (phone.length() > 0) {

                if (String.valueOf(phone.charAt(0)).equals("0"))
                    phone = String.valueOf(phone).replaceFirst("0", "");
                if (phone.length() > 0) {
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
                if (Global.check_int(Forward.this)) {
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
                if (mAuth.getCurrentUser() != null) {
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
                            name = getContactName(phone, Forward.this);
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
                                            mUser.setNameL(getContactName(mUser.getPhone(), Forward.this));
                                            mUser.setName(mContactIterator.getName());
                                            mUser.setAvatar(mContactIterator.getAvatar());
                                            mUser.setStatue(mContactIterator.getStatue());
                                            mUser.setScreen(mContactIterator.isScreen());

                                        }
                                    }
                                Global.contactsG.add(mUser);
                                ((AppBack) getApplication()).setContacts();
                                mUserListAdapter.notifyDataSetChanged();

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
            if (mAuth.getCurrentUser() != null)
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


    public void goForward() {
        {

            Map<String, Object> map = new HashMap<>();

            Object currT = ServerValue.TIMESTAMP;


            switch (forwardM.getType()) {
                case "text":
                    map.put("message", encryption.encryptOrNull(forwardM.getText()));
                    map.put("time", currT);
                    map.put("react", "no");
                    map.put("avatar", encryption.encryptOrNull(Global.avaLocal));
                    map.put("seen", false);
                    map.put("type", "text");
                    map.put("deleted", false);
                    map.put("statue", "✔");
                    map.put("reply", encryption.encryptOrNull(forwardM.getReply()));
                    map.put("forw", true);
                    map.put("call", forwardM.isCall());
                    map.put("from", mAuth.getCurrentUser().getUid());

                    for (int i = 0; i < Global.forwardids.size(); i++) {
                        messidNew = mAuth.getCurrentUser().getUid() + "_" + Global.forwardids.get(i) + "_" + System.currentTimeMillis();
                        map.put("messId", messidNew);
                        messageLocal = new MessageIn(encryption.encryptOrNull(forwardM.getText()), "text", "..", mAuth.getCurrentUser().getUid(), System.currentTimeMillis(), false, false, messidNew, "no", encryption.encryptOrNull(Global.avaLocal), !Global.forwardids.get(i).contains("groups-"), false, false, encryption.encryptOrNull(forwardM.getReply()));

                        map.put("chat", !Global.forwardids.get(i).contains("groups-"));
                        updateData(encryption.encryptOrNull(forwardM.getText()), messidNew, currT, map, Global.forwardids.get(i), !Global.forwardids.get(i).contains("groups-"));

                    }
                    break;
                case "map":

                    map.put("location", encryption.encryptOrNull(forwardM.getMap().getLocation()));
                    map.put("time", currT);
                    map.put("react", "no");
                    map.put("avatar", encryption.encryptOrNull(Global.avaLocal));
                    map.put("seen", false);
                    map.put("type", "map");
                    map.put("deleted", false);
                    map.put("statue", "✔");
                    map.put("reply", encryption.encryptOrNull(forwardM.getReply()));
                    map.put("forw", true);
                    map.put("call", forwardM.isCall());
                    map.put("from", mAuth.getCurrentUser().getUid());

                    for (int i = 0; i < Global.forwardids.size(); i++) {
                        messidNew = mAuth.getCurrentUser().getUid() + "_" + Global.forwardids.get(i) + "_" + System.currentTimeMillis();
                        map.put("messId", messidNew);
                        messageLocal = new MessageIn(encryption.encryptOrNull(forwardM.getMap().getLocation()), "..", mAuth.getCurrentUser().getUid(), System.currentTimeMillis(), false, "no", false, messidNew, "map", encryption.encryptOrNull(Global.avaLocal), !Global.forwardids.get(i).contains("groups-"), false, false, encryption.encryptOrNull(forwardM.getReply()));

                        map.put("chat", !Global.forwardids.get(i).contains("groups-"));
                        updateData(encryption.encryptOrNull(getString(R.string.map_location)), messidNew, currT, map, Global.forwardids.get(i), !Global.forwardids.get(i).contains("groups-"));

                    }


                    break;
                case "voice":
                    map.put("linkV", encryption.encryptOrNull(forwardM.getVoice().getUrl()));
                    map.put("time", currT);
                    map.put("react", "no");
                    map.put("avatar", encryption.encryptOrNull(Global.avaLocal));
                    map.put("duration", forwardM.getVoice().getDuration());
                    map.put("seen", false);
                    map.put("type", "voice");
                    map.put("deleted", false);
                    map.put("statue", "✔");
                    map.put("reply", encryption.encryptOrNull(forwardM.getReply()));
                    map.put("forw", true);
                    map.put("call", forwardM.isCall());
                    map.put("from", mAuth.getCurrentUser().getUid());

                    for (int i = 0; i < Global.forwardids.size(); i++) {
                        messidNew = mAuth.getCurrentUser().getUid() + "_" + Global.forwardids.get(i) + "_" + System.currentTimeMillis();
                        map.put("messId", messidNew);
                        messageLocal = new MessageIn(encryption.encryptOrNull(forwardM.getVoice().getUrl()), "..", mAuth.getCurrentUser().getUid(), System.currentTimeMillis(), false, false, messidNew, "voice", "no", forwardM.getVoice().getDuration(), encryption.encryptOrNull(Global.avaLocal), !Global.forwardids.get(i).contains("groups-"), false, false, encryption.encryptOrNull(forwardM.getReply()));

                        map.put("chat", !Global.forwardids.get(i).contains("groups-"));
                        updateData(encryption.encryptOrNull("Voice " + forwardM.getVoice().getDuration()), messidNew, currT, map, Global.forwardids.get(i), !Global.forwardids.get(i).contains("groups-"));

                    }


                    break;
                case "video":

                    map.put("linkVideo", encryption.encryptOrNull(forwardM.getVideo().getUrl()));
                    map.put("time", currT);
                    map.put("react", "no");
                    map.put("avatar", encryption.encryptOrNull(Global.avaLocal));
                    map.put("thumb", forwardM.getVideo().getThumb());
                    map.put("duration", forwardM.getVideo().getDuration());
                    map.put("seen", false);
                    map.put("type", "video");
                    map.put("deleted", false);
                    map.put("statue", "✔");
                    map.put("reply", encryption.encryptOrNull(forwardM.getReply()));
                    map.put("forw", true);
                    map.put("call", forwardM.isCall());
                    map.put("from", mAuth.getCurrentUser().getUid());

                    for (int i = 0; i < Global.forwardids.size(); i++) {
                        messidNew = mAuth.getCurrentUser().getUid() + "_" + Global.forwardids.get(i) + "_" + System.currentTimeMillis();
                        map.put("messId", messidNew);
                        messageLocal = new MessageIn(encryption.encryptOrNull(forwardM.getVideo().getUrl()), "..", mAuth.getCurrentUser().getUid(), System.currentTimeMillis(), false, false, messidNew, forwardM.getVideo().getDuration(), forwardM.getVideo().getThumb(), "no", encryption.encryptOrNull(Global.avaLocal), !Global.forwardids.get(i).contains("groups-"), false, false, encryption.encryptOrNull(forwardM.getReply()));

                        map.put("chat", !Global.forwardids.get(i).contains("groups-"));
                        updateData(encryption.encryptOrNull("Video " + forwardM.getVideo().getDuration()), messidNew, currT, map, Global.forwardids.get(i), !Global.forwardids.get(i).contains("groups-"));

                    }


                    break;
                case "file":

                    map.put("linkF", encryption.encryptOrNull(forwardM.getFile().getUrl()));
                    map.put("time", currT);
                    map.put("react", "no");
                    map.put("avatar", encryption.encryptOrNull(Global.avaLocal));
                    map.put("filename", forwardM.getFile().getFilename());
                    map.put("seen", false);
                    map.put("type", "file");
                    map.put("deleted", false);
                    map.put("statue", "✔");
                    map.put("reply", encryption.encryptOrNull(forwardM.getReply()));
                    map.put("forw", true);
                    map.put("call", forwardM.isCall());
                    map.put("from", mAuth.getCurrentUser().getUid());

                    for (int i = 0; i < Global.forwardids.size(); i++) {
                        messidNew = mAuth.getCurrentUser().getUid() + "_" + Global.forwardids.get(i) + "_" + System.currentTimeMillis();
                        map.put("messId", messidNew);
                        messageLocal = new MessageIn(encryption.encryptOrNull(forwardM.getFile().getUrl()), "..", System.currentTimeMillis(), false, false, messidNew, "file", forwardM.getFile().getFilename(), mAuth.getCurrentUser().getUid(), "no", encryption.encryptOrNull(Global.avaLocal), !Global.forwardids.get(i).contains("groups-"), false, false, encryption.encryptOrNull(forwardM.getReply()));

                        map.put("chat", !Global.forwardids.get(i).contains("groups-"));
                        updateData(encryption.encryptOrNull("File " + forwardM.getFile().getFilename()), messidNew, currT, map, Global.forwardids.get(i), !Global.forwardids.get(i).contains("groups-"));

                    }


                    break;
                case "image":

                    map.put("linkI", encryption.encryptOrNull(forwardM.getImageUrl()));
                    map.put("time", currT);
                    map.put("react", "no");
                    map.put("avatar", encryption.encryptOrNull(Global.avaLocal));
                    map.put("seen", false);
                    map.put("type", "image");
                    map.put("deleted", false);
                    map.put("statue", "✔");
                    map.put("reply", encryption.encryptOrNull(forwardM.getReply()));
                    map.put("forw", true);
                    map.put("call", forwardM.isCall());
                    map.put("from", mAuth.getCurrentUser().getUid());

                    for (int i = 0; i < Global.forwardids.size(); i++) {
                        messidNew = mAuth.getCurrentUser().getUid() + "_" + Global.forwardids.get(i) + "_" + System.currentTimeMillis();
                        map.put("messId", messidNew);
                        map.put("chat", !Global.forwardids.get(i).contains("groups-"));
                        messageLocal = new MessageIn(encryption.encryptOrNull(forwardM.getImageUrl()), "image", messidNew, "..", mAuth.getCurrentUser().getUid(), System.currentTimeMillis(), false, false, "no", encryption.encryptOrNull(Global.avaLocal), !Global.forwardids.get(i).contains("groups-"), false, false, encryption.encryptOrNull(forwardM.getReply()));

                        if (!forwardM.getImageUrl().contains(".png"))
                            updateData(encryption.encryptOrNull("Image"), messidNew, currT, map, Global.forwardids.get(i), !Global.forwardids.get(i).contains("groups-"));
                        else
                            updateData(encryption.encryptOrNull("Sticker"), messidNew, currT, map, Global.forwardids.get(i), !Global.forwardids.get(i).contains("groups-"));

                    }

                    break;


            }

            dialog.dismiss();
            finish();
        }

    }


    public void updateData(String mess, String messid, Object currTime, Map<String, Object> MessMap, String friendid, boolean ischat) {
        if (mAuth.getCurrentUser() != null) {
            if (ischat) {

                Map<String, Object> map = new HashMap<>();
                Map<String, Object> map2 = new HashMap<>();

                map.put("avatar", Global.avaLocal);
                map.put("name", Global.nameLocal);
                map.put("nameL", Global.nameLocal);
                map.put("phone", Global.phoneLocal);
                map.put("id", mAuth.getCurrentUser().getUid());
                map.put("screen", Global.myscreen);
                map.put("lastmessage", mess);
                map.put("lastsender", mAuth.getCurrentUser().getUid());
                map.put("lastsenderava", Global.avaLocal);
                map.put("messDate", currTime);


                map2.put("avatar", Global.contactsG.get(halbine(Global.contactsG, friendid)).getAvatar());
                map2.put("name", Global.contactsG.get(halbine(Global.contactsG, friendid)).getName());
                map2.put("nameL", Global.contactsG.get(halbine(Global.contactsG, friendid)).getName());
                map2.put("phone", Global.contactsG.get(halbine(Global.contactsG, friendid)).getPhone());
                map2.put("id", friendid);
                map2.put("screen", Global.contactsG.get(halbine(Global.contactsG, friendid)).isScreen());
                map2.put("lastmessage", mess);
                map2.put("lastsender", mAuth.getCurrentUser().getUid());
                map2.put("lastsenderava", Global.avaLocal);
                map2.put("messDate", currTime);


                mMess.child(mAuth.getCurrentUser().getUid()).child(friendid).updateChildren(map2).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        mMess.child(friendid).child(mAuth.getCurrentUser().getUid()).updateChildren(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                mMess.child(mAuth.getCurrentUser().getUid()).child(friendid).child(Global.Messages).child(messidNew).updateChildren(MessMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        mMess.child(friendid).child(mAuth.getCurrentUser().getUid()).child(Global.Messages).child(messidNew).updateChildren(MessMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {

                                                sendMessNotify(mess, messid, friendid);

                                            }
                                        });
                                    }
                                });
                            }
                        });
                    }
                });

            } else {
                mData.child(friendid).child(Global.Messages).child(messid)
                        .updateChildren(MessMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Map<String, Object> map2 = new HashMap<>();
                        map2.put("lastmessage", mess);
                        map2.put("lastsender", mAuth.getCurrentUser().getUid());
                        map2.put("lastsenderava", Global.avaLocal);
                        map2.put("messDate", currTime);

                        mData.child(friendid).updateChildren(map2).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {

                                userD.child(mAuth.getCurrentUser().getUid()).child(Global.GROUPS).child(friendid).updateChildren(map2).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        sendMessNotifyG(mess, messid, friendid);
                                        for (int i = 0; i < Global.groupsArray.get(halbineG(Global.groupsArray, friendid)).getUsers().size(); i++) {
                                            int j = i;
                                            if (!Global.groupsArray.get(halbineG(Global.groupsArray, friendid)).getUsers().get(i).equals(mAuth.getCurrentUser().getUid())) {

                                                userD.child(Global.groupsArray.get(halbineG(Global.groupsArray, friendid)).getUsers().get(i)).child(Global.GROUPS).child(friendid).updateChildren(map2).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {

                                                    }
                                                });
                                            }

                                        }
                                    }
                                });
                            }
                        });
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });


            }


        }
    }

    private void sendMessNotify(final String message, final String Mid, String friendid) {

        DatabaseReference mTokenget = FirebaseDatabase.getInstance().getReference(Global.tokens);
        mTokenget.child(friendid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (mAuth.getCurrentUser() != null) {

                    Tokens tokens = dataSnapshot.getValue(Tokens.class);
                    Map<String, String> map = new HashMap<>();
                    map.put("nType","message");
                    map.put("tokens",tokens.toString());
                    map.put("senderId",mAuth.getCurrentUser().getUid());
                    map.put("senderName",Global.nameLocal);
                    map.put("senderAva",Global.avaLocal);
                    map.put("Mid",Mid);
                    map.put("to", friendid);
                    map.put("message", message);
                    Sender sender = new Sender(tokens.getTokens(), map);
                    fcm.send(sender)
                            .enqueue(new Callback<FCMresp>() {
                                @Override
                                public void onResponse(Call<FCMresp> call, Response<FCMresp> response) {
                                }

                                @Override
                                public void onFailure(Call<FCMresp> call, Throwable t) {

                                }
                            });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void sendMessNotifyG(final String message, final String Mid, String friendid) {

        for (int i = 0; i < Global.groupsArray.get(halbineG(Global.groupsArray, friendid)).getUsers().size(); i++) {
            if (!Global.groupsArray.get(halbineG(Global.groupsArray, friendid)).getUsers().get(i).equals(mAuth.getCurrentUser().getUid())) {
                DatabaseReference mTokenget = FirebaseDatabase.getInstance().getReference(Global.tokens);
                int finalI = i;
                mTokenget.child(Global.groupsArray.get(halbineG(Global.groupsArray, friendid)).getUsers().get(i)).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Tokens tokens = dataSnapshot.getValue(Tokens.class);
                        Map<String, String> map = new HashMap<>();
                        map.put("nType","messageGroup");
                        map.put("tokens",tokens.toString());
                        map.put("senderId",friendid);
                        map.put("senderName",Global.currname);
                        map.put("senderAva",Global.currAva);
                        map.put("Mid",Mid);
                        map.put("to", Global.groupsArray.get(halbineG(Global.groupsArray, friendid)).getUsers().get(finalI));
                        map.put("message", message);
                        Sender sender = new Sender(tokens.getTokens(), map);
                        fcm.send(sender)
                                .enqueue(new Callback<FCMresp>() {
                                    @Override
                                    public void onResponse(Call<FCMresp> call, Response<FCMresp> response) {
                                    }

                                    @Override
                                    public void onFailure(Call<FCMresp> call, Throwable t) {
                                    }
                                });

                    }


                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        }


    }

    public int halbine(ArrayList<UserData> ml, String id) {
        int j = 0, i = 0;
        for (i = 0; i < ml.size(); i++) {
            if (ml.get(i).getId().equals(id)) {
                j = 1;
                break;
            }

        }
        if (j == 1)
            return i;
        else
            return -1;

    }

    public int halbineG(ArrayList<GroupIn> ml, String id) {
        int j = 0, i = 0;
        for (i = 0; i < ml.size(); i++) {
            if (ml.get(i).getId().equals(id)) {
                j = 1;
                break;
            }

        }
        if (j == 1)
            return i;
        else
            return -1;

    }


}

