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
import com.stfalcon.chatkit.me.GroupIn;
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
import androidx.recyclerview.widget.RecyclerView;

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
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ar.codeslu.plax.R;
import ar.codeslu.plax.adapters.InviteAdapter;
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

public class Invite extends AppCompatActivity {

    //view
    private IndexFastScrollRecyclerView mUserList;
    private InviteAdapter mUserListAdapter;
    private ImageView sora;
    private EmojiTextView contact, hint;
    private TextView contactNum;
    //firebase
    FirebaseAuth mAuth;
    DatabaseReference mUserDB;
    DatabaseReference group;

    ArrayList<UserData> contactList, searchL;
    ArrayList<String> localContacts;
    AlertDialog dialog;
    //fcm
    FCM fcm;
    String id;
    String phones = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_user_group);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        FloatingActionButton fab = findViewById(R.id.fab);
        Global.currentactivity = this;

        //loader
        //fcm notify
        fcm = Global.getFCMservies();
        if (Global.DARKSTATE) {
            dialog = new SpotsDialog.Builder()
                    .setContext(Invite.this)
                    .setMessage(R.string.pleasW)
                    .setTheme(R.style.darkDialog)
                    .setCancelable(true)
                    .build();
        } else {
            dialog = new SpotsDialog.Builder()
                    .setContext(Invite.this)
                    .setMessage(R.string.pleasW)
                    .setCancelable(true)
                    .build();
        }

        Global.inviteNums = new ArrayList<>();
        dialog.show();
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
        mUserList.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        Global.inviteNums = new ArrayList<>();
        mUserListAdapter = new InviteAdapter(contactList);
        mUserList.setHasFixedSize(true);
        mUserList.setNestedScrollingEnabled(true);
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
                            if (Global.check_int(Invite.this))
                                contactList.clear();

                            getContactList();
                        } else
                            Toast.makeText(Invite.this, getString(R.string.acc_per), Toast.LENGTH_SHORT).show();


                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {

                        token.continuePermissionRequest();

                    }
                }).check();
        if (mAuth.getCurrentUser() != null) {
            if (!((AppBack)  getApplication()).shared().getBoolean("dark" + mAuth.getCurrentUser().getUid(), false)) {
                contact.setTextColor(Invite.this.getResources().getColor(R.color.black));
                hint.setTextColor(Invite.this.getResources().getColor(R.color.mid_grey));
                contactNum.setTextColor(Invite.this.getResources().getColor(R.color.mid_grey));

            } else {
                contact.setTextColor(Invite.this.getResources().getColor(R.color.white));
                hint.setTextColor(Invite.this.getResources().getColor(R.color.light_mid_grey));
                contactNum.setTextColor(Invite.this.getResources().getColor(R.color.light_mid_grey));
            }
        }
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (Global.check_int(Invite.this)) {
                    if (Global.inviteNums.size() == 0)
                        Snackbar.make(view, getString(R.string.plz_chs_per), Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                    else {
                        dialog.show();
                        phones = Global.inviteNums.get(0);
                        for (int i = 0; i < Global.inviteNums.size() -1; i++)
                        phones = phones.concat(";" + Global.inviteNums.get(i+1));


                        Intent smsIntent = new Intent(Intent.ACTION_SENDTO,Uri.parse("smsto:"+phones));
                        smsIntent.putExtra("sms_body", getString(R.string.hello)+" "+getString(R.string.app_name)+
                                getString(R.string.downloadnw) +" "+ "https://play.google.com/store/apps/details?id="+getPackageName());
                        dialog.dismiss();
                        Global.inviteNums.clear();
                        startActivity(smsIntent);

                    }
                } else
                    Snackbar.make(view, getString(R.string.check_int), Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();


            }

        });


    }

    private void getContactList() {


        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Cursor phones = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC");
                while (phones.moveToNext()) {
                    String phone = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));

                    phone = phone.replace(" ", "");
                    phone = phone.replace("-", "");
                    phone = phone.replace("(", "");
                    phone = phone.replace(")", "");
                    if (phone.length() > 0) {

                        if (!Global.phoneLocal.equals(phone) && !localContacts.contains(phone) && !phone.equals("t88848992hisuseri9483828snothereri9949ghtnow009933")) {
                            if (!phone.contains(".") && !phone.contains("#") && !phone.contains("$") && !phone.contains("[") && !phone.contains("]")) {
                                localContacts.add(phone);
                                UserData mContact = new UserData("", "", getContactName(phone, Invite.this), "", phone, false, false, getContactName(phone, Invite.this));
                                contactList.add(mContact);
                            }
                        }
                    }
                }

                if (contactList.size() > 0) {
                    if (contactList.size() == 1)
                        contactNum.setText(contactList.size() + " " + getResources().getString(R.string.contact));
                    else
                        contactNum.setText(contactList.size() + " " + getResources().getString(R.string.contacts));

                }
                dialog.dismiss();
                Collections.sort(contactList, new Comparator<UserData>() {
                    @Override
                    public int compare(UserData lhs, UserData rhs) {
                        return lhs.getName().compareTo(rhs.getName());
                    }
                });
                mUserListAdapter.notifyDataSetChanged();
                contactNum.setVisibility(View.VISIBLE);

                if (contactList.size() == 0) {
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
        });




    }


    @Override
    public void onBackPressed() {
        dialog.dismiss();
        super.onBackPressed();
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


}

