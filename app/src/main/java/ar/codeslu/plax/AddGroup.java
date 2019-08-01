package ar.codeslu.plax;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

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
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;
import com.vanniktech.emoji.EmojiEditText;
import com.vanniktech.emoji.EmojiTextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.provider.ContactsContract;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ar.codeslu.plax.adapters.GroupsContacts;
import ar.codeslu.plax.fragments.Groups;
import ar.codeslu.plax.global.AppBack;
import ar.codeslu.plax.global.Global;
import ar.codeslu.plax.lists.CountryToPhonePrefix;
import ar.codeslu.plax.lists.Tokens;
import ar.codeslu.plax.lists.UserData;
import ar.codeslu.plax.notify.FCM;
import ar.codeslu.plax.notify.FCMresp;
import ar.codeslu.plax.notify.Sender;
import de.hdodenhof.circleimageview.CircleImageView;
import dmax.dialog.SpotsDialog;
import id.zelory.compressor.Compressor;
import in.myinnos.alphabetsindexfastscrollrecycler.IndexFastScrollRecyclerView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import se.simbio.encryption.Encryption;

public class AddGroup extends AppCompatActivity {
    //view
    private IndexFastScrollRecyclerView mUserList;
    private GroupsContacts mUserListAdapter;
    private ImageView sora;
    private EmojiTextView contact, hint;
    private TextView contactNum;
    EmojiEditText groupName;
    CircleImageView groupAva;
    //firebase
    FirebaseAuth mAuth;
    DatabaseReference mUserDB;
    DatabaseReference group;

    ArrayList<UserData> contactList, searchL;
    ArrayList<String> localContacts;
    int secN = 0;
    AlertDialog dialog;
    String imgLocalpath = "no";
    byte[] thumbData;
    //compress
    private Bitmap compressedImageFile;
    Encryption encryption;
    //fcm
    FCM fcm;
    int before = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_group);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        FloatingActionButton fab = findViewById(R.id.fab);
        //encryption
        byte[] iv = new byte[16];
        encryption = Encryption.getDefault(Global.keyE, Global.salt, iv);
        //loader
        //fcm notify
        fcm = Global.getFCMservies();
        if (Global.DARKSTATE) {
            dialog = new SpotsDialog.Builder()
                    .setContext(AddGroup.this)
                    .setMessage(R.string.pleasW)
                    .setTheme(R.style.darkDialog)
                    .setCancelable(true)
                    .build();
        } else {
            dialog = new SpotsDialog.Builder()
                    .setContext(AddGroup.this)
                    .setMessage(R.string.pleasW)
                    .setCancelable(true)
                    .build();
        }


        dialog.show();


        sora = findViewById(R.id.sora);
        groupAva = findViewById(R.id.groupAva);
        groupName = findViewById(R.id.groupName);
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
        mUserList.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
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
        Global.currentactivity = this;
        Dexter.withActivity(this)
                .withPermissions(Manifest.permission.READ_CONTACTS)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {

                        if (report.areAllPermissionsGranted()) {
                            if (Global.check_int(AddGroup.this))
                                Global.contactsG.clear();

                            getContactList();
                        } else
                            Toast.makeText(AddGroup.this, getString(R.string.acc_per), Toast.LENGTH_SHORT).show();


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
                groupName.setBackgroundDrawable(ContextCompat.getDrawable(this, R.drawable.et_bg_b));

            } else {
                contact.setTextColor(Global.conMain.getResources().getColor(R.color.white));
                hint.setTextColor(Global.conMain.getResources().getColor(R.color.light_mid_grey));
                contactNum.setTextColor(Global.conMain.getResources().getColor(R.color.light_mid_grey));
                groupName.setBackgroundDrawable(ContextCompat.getDrawable(this, R.drawable.et_bg));
            }
        }
        groupAva.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dexter.withActivity(AddGroup.this)
                        .withPermissions(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA)
                        .withListener(new MultiplePermissionsListener() {
                            @Override
                            public void onPermissionsChecked(MultiplePermissionsReport report) {

                                if (report.areAllPermissionsGranted()) {
                                    CropImage.activity()
                                            .setGuidelines(CropImageView.Guidelines.ON)
                                            .setMinCropResultSize(400, 400)
                                            .setAspectRatio(1, 1)
                                            .start(AddGroup.this);
                                } else
                                    Toast.makeText(AddGroup.this, getString(R.string.acc_per), Toast.LENGTH_SHORT).show();


                            }

                            @Override
                            public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {

                                token.continuePermissionRequest();

                            }
                        }).check();
            }
        });
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Global.check_int(AddGroup.this)) {
                    if (Global.groupids.size() == 0)
                        Snackbar.make(view, getString(R.string.plz_chs_per), Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                    else {
                        if (!groupName.getText().toString().trim().isEmpty()) {
                            Global.groupids.add(mAuth.getCurrentUser().getUid());
                            ArrayList<String> admin = new ArrayList<>();
                            admin.add(mAuth.getCurrentUser().getUid());
                            String groupId = "groups_" + mAuth.getCurrentUser().getUid() + System.currentTimeMillis();
                            Map<String, Object> map = new HashMap<>();
                            map.put("name", groupName.getText().toString().trim());
                            map.put("id", groupId);
                            map.put("created", ServerValue.TIMESTAMP);
                            map.put("messDate",ServerValue.TIMESTAMP);
                            map.put("users", Global.groupids);
                            map.put("lastmessage", encryption.encryptOrNull(String.valueOf("777default099////ar.codeslu.plax//")));
                            map.put("admins", admin);
                            map.put("lastsenderava", Global.avaLocal);
                            map.put("lastsender",mAuth.getCurrentUser().getUid());
                            map.put("noOfUnread",0);
                            uploadprofile(Uri.parse(imgLocalpath), groupId, map,map);


                        } else
                            Snackbar.make(view, getString(R.string.cannot_l_g), Snackbar.LENGTH_LONG)
                                    .setAction("Action", null).show();
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

            if (String.valueOf(phone.charAt(0)).equals("0"))
                phone = String.valueOf(phone).replaceFirst("0", "");

            if (!String.valueOf(phone.charAt(0)).equals("+"))
                phone = ISOPrefix + phone;
            if (!Global.phoneLocal.equals(phone) && !localContacts.contains(phone)) {
                localContacts.add(phone);
            }
        }

        runOnUiThread(new Runnable() {

            @Override
            public void run() {
                if (Global.check_int(AddGroup.this)) {
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
                        name = getContactName(phone, AddGroup.this);
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
                        UserData mUser = new UserData(childSnapshot.getKey(), ava, name, statue, phone, online, screen, nameP);
                        if (name.equals(phone))
                            for (UserData mContactIterator : contactList) {
                                if (mContactIterator.getPhone().equals(mUser.getPhone())) {
                                    mUser.setNameL(getContactName(mUser.getPhone(), AddGroup.this));
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
                    Log.wtf("keyyyy", secN + "ee" + Global.contactsG.size());
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


    public void uploadprofile(Uri imgLocalpath, String groupid, Map<String, Object> map,Map<String, Object> map2) {
        dialog.show();

        StorageReference mStorageRef = FirebaseStorage.getInstance().getReference();
        StorageReference riversRef = mStorageRef.child(Global.GroupAva + "/Ava_" + groupid + ".jpg");

        if (thumbData != null) {
            UploadTask uploadTask = riversRef.putBytes(thumbData);
            Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }

                    // Continue with the task to get the download URL
                    return riversRef.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        Uri downloadUrl = task.getResult();
                        map.put("avatar", encryption.encryptOrNull(String.valueOf(downloadUrl)));
                        group.child(groupid).updateChildren(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                map2.put("avatar", encryption.encryptOrNull(String.valueOf(downloadUrl)));
                                map2.remove("users");
                                map2.remove("admins");
                                putIndB(map2);

                            }
                        })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        dialog.dismiss();
                                        Toast.makeText(AddGroup.this, R.string.error, Toast.LENGTH_SHORT).show();
                                    }
                                });


                    }
                }
            });
        } else {
            map.put("avatar", encryption.encryptOrNull("no"));
            group.child(groupid).updateChildren(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    map2.put("avatar", encryption.encryptOrNull("no"));
                    map2.remove("users");
                    map2.remove("admins");
                    putIndB(map2);
                }
            })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            dialog.dismiss();
                            Toast.makeText(AddGroup.this, R.string.error, Toast.LENGTH_SHORT).show();
                        }
                    });


        }
    }

    public void compress(Uri imgLocalpath) {
        //compress the photo
        File newImageFile = new File(imgLocalpath.getPath());
        try {

            compressedImageFile = new Compressor(AddGroup.this)
                    .setMaxHeight(500)
                    .setMaxWidth(500)
                    .setQuality(50)
                    .compressToBitmap(newImageFile);

        } catch (IOException e) {
            e.printStackTrace();
        }

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        compressedImageFile.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        thumbData = baos.toByteArray();
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                imgLocalpath = String.valueOf(result.getUri());
                Picasso.get()
                        .load(imgLocalpath)
                        .error(R.drawable.errorimg)
                        .into(groupAva);
                compress(Uri.parse(imgLocalpath));
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
                imgLocalpath = "no";
            }
        }
    }

    private void sendMessNotify(String friendId, Map<String, Object> map2) {
        String Message = encryption.encryptOrNull(Global.nameLocal + getString(R.string.add_you) + groupName.getText().toString().trim());
        String messidL = mAuth.getCurrentUser().getUid() + "_" + friendId + "_" + String.valueOf(System.currentTimeMillis());

        DatabaseReference mTokenget = FirebaseDatabase.getInstance().getReference(Global.tokens);
        mTokenget.child(friendId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Tokens tokens = dataSnapshot.getValue(Tokens.class);
                Map<String, String> map = new HashMap<>();
                map.put("title", tokens + "#" + mAuth.getCurrentUser().getUid() + "#" + Global.nameLocal + "#" + Global.avaLocal + "#" + messidL + "#" + "group990");
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
                                    Intent intent = new Intent(Global.mainActivity, Group.class);
                                    intent.putExtra("id", String.valueOf(map2.get("id")));
                                    intent.putExtra("name", String.valueOf(map2.get("name")));
                                    intent.putExtra("ava", encryption.decryptOrNull(String.valueOf(map2.get("avatar"))));
                                    Global.mainActivity.startActivity(intent);
                                    before =0;
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

        if (before < Global.groupids.size())
        {
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
                            Intent intent = new Intent(Global.mainActivity, Group.class);
                            intent.putExtra("id", String.valueOf(map2.get("id")));
                            intent.putExtra("name", String.valueOf(map2.get("name")));
                            intent.putExtra("ava", encryption.decryptOrNull(String.valueOf(map2.get("avatar"))));
                            Global.mainActivity.startActivity(intent);
                            before =0;
                            dialog.dismiss();
                            finish();

                        }
                    }
                }
            });



        }








    }

}
