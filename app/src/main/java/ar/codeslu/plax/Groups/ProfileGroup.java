package ar.codeslu.plax.Groups;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.stfalcon.chatkit.me.GroupIn;
import com.theartofdev.edmodo.cropper.CropImage;

import net.glxn.qrgen.android.QRCode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import ar.codeslu.plax.LockScreen;
import ar.codeslu.plax.MainActivity;
import ar.codeslu.plax.R;
import ar.codeslu.plax.adapters.GroupAdapter;
import ar.codeslu.plax.custom.EditProfile;
import ar.codeslu.plax.custom.ReactCustom;
import ar.codeslu.plax.global.AppBack;
import ar.codeslu.plax.global.Global;
import ar.codeslu.plax.global.encryption;
import ar.codeslu.plax.lists.UserData;
import dmax.dialog.SpotsDialog;
import it.auron.library.mecard.MeCard;


public class ProfileGroup extends AppCompatActivity {

    Toolbar toolbar;
    TextView name;
    ImageView profile, addContact;
    FloatingActionButton fab;
    String friendid;
    Uri imgLocalpath;
    EditProfile cdd;
    FirebaseAuth mAuth;
    DatabaseReference mData, mUser;

    RecyclerView admins, user;
    int before = 0;
    AlertDialog dialog;
    Button exit;

    //adapters
    GroupAdapter adminsA;
    GroupAdapter userA;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_group);
        mAuth = FirebaseAuth.getInstance();
        mData = FirebaseDatabase.getInstance().getReference(Global.GROUPS);
        mUser = FirebaseDatabase.getInstance().getReference(Global.USERS);
        Global.currentactivity = this;

        //loader
        if (Global.DARKSTATE) {
            dialog = new SpotsDialog.Builder()
                    .setContext(this)
                    .setMessage(R.string.pleasW)
                    .setTheme(R.style.darkDialog)
                    .setCancelable(true)
                    .build();
        } else {
            dialog = new SpotsDialog.Builder()
                    .setContext(this)
                    .setMessage(R.string.pleasW)
                    .setCancelable(true)
                    .build();
        }
        dialog.show();

//toolbar
        //dark mode init
        if (mAuth.getCurrentUser() != null) {
            if (!((AppBack) getApplication()).shared().getBoolean("dark" + mAuth.getCurrentUser().getUid(), false)) {
                getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            } else {
                getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            }
        }
        //Actionbar init
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        profile = findViewById(R.id.profileI);
        fab = findViewById(R.id.fab);
        admins = findViewById(R.id.admins);
        user = findViewById(R.id.contacts);
        exit = findViewById(R.id.exit);
        addContact = findViewById(R.id.addcontact);

        toolbar.setPadding(0, 0, 0, 0);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayShowCustomEnabled(true);
        //Action bar design
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View viewS = inflater.inflate(R.layout.profile_bar, null);
        actionBar.setCustomView(viewS);


        if (getIntent() != null)
            friendid = getIntent().getExtras().getString("idP");


        name = viewS.findViewById(R.id.nameC);


        updateLive();

        Global.adminList = new ArrayList<>();
        //set data
        ((AppBack) getApplication()).getGroupUA(friendid);

        mData.child(friendid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (mAuth.getCurrentUser() != null) {

                    try {
                        GroupIn groupIn = dataSnapshot.getValue(GroupIn.class);
                        String ava = encryption.decryptOrNull(groupIn.getAvatar());
                        String name = groupIn.getName();

                        Global.currAva = ava;
                        Global.currname = name;

                        Global.currGAdmins.clear();
                        Global.currGUsers.clear();
                        Global.currGUsersU.clear();
                        Global.adminList.clear();

                        Global.currGAdmins = groupIn.getAdmins();
                        Global.currGUsers = groupIn.getUsers();

                        if (Global.currGAdmins.contains(mAuth.getCurrentUser().getUid()))
                            addContact.setVisibility(View.VISIBLE);
                        else
                            addContact.setVisibility(View.GONE);


                        updateLive();
                        threadUpdate();
                    } catch (NullPointerException e) {

                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Global.check_int(ProfileGroup.this)) {
                    cdd = new EditProfile(ProfileGroup.this, friendid);
                    cdd.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    cdd.getWindow().getAttributes().windowAnimations = R.style.CustomDialogAnimation;
                    cdd.show();
                } else
                    Toast.makeText(ProfileGroup.this, R.string.check_int, Toast.LENGTH_SHORT).show();
            }
        });


        //adapters
        adminsA = new GroupAdapter(Global.adminList, true, friendid);
        userA = new GroupAdapter(Global.currGUsersU, false, friendid);


        //recycler
        admins.setLayoutManager(new GridLayoutManager(this, 1, LinearLayoutManager.HORIZONTAL, false));
        admins.setAdapter(adminsA);

        user.setLayoutManager(new GridLayoutManager(this, calculateNoOfColumns(this), RecyclerView.VERTICAL, false));
        user.setAdapter(userA);

        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Global.check_int(ProfileGroup.this)) {
                    androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(ProfileGroup.this);
                    builder.setMessage(R.string.exit_group_sure);
                    builder.setTitle(R.string.Leav_grp);
                    builder.setIcon(R.drawable.ic_remove);
                    builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Global.chatactivity.finish();
                            mUser.child(mAuth.getCurrentUser().getUid()).child(Global.GROUPS).child(friendid).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Map<String, Object> map = new HashMap<>();
                                    Global.currGUsers.remove(mAuth.getCurrentUser().getUid());
                                    map.put("users", Global.currGUsers);
                                    mData.child(friendid).updateChildren(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            if (Global.currGAdmins.contains(mAuth.getCurrentUser().getUid())) {
                                                Map<String, Object> map2 = new HashMap<>();
                                                Global.currGAdmins.remove(mAuth.getCurrentUser().getUid());
                                                map2.put("admins", Global.currGAdmins);
                                                mData.child(friendid).updateChildren(map2).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        Intent intent = new Intent(ProfileGroup.this, MainActivity.class);
                                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                        startActivity(intent);
                                                    }
                                                });
                                            } else {
                                                Intent intent = new Intent(ProfileGroup.this, MainActivity.class);
                                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                startActivity(intent);
                                            }

                                        }
                                    });
                                }
                            });
                        }
                    });
                    builder.show();
                } else
                    Toast.makeText(ProfileGroup.this, R.string.check_int, Toast.LENGTH_SHORT).show();
            }
        });

        addContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Global.check_int(ProfileGroup.this)) {
                    Intent intent = new Intent(ProfileGroup.this, AddUserGroup.class);
                    intent.putExtra("id", friendid);
                    startActivity(intent);
                } else
                    Toast.makeText(ProfileGroup.this, R.string.check_int, Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                imgLocalpath = result.getUri();
                if (Global.check_int(this)) {
                    cdd.update(String.valueOf(imgLocalpath));
                } else
                    Toast.makeText(this, R.string.check_conn, Toast.LENGTH_SHORT).show();
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }

    public void updateLive() {

        name.setText(Global.currname);

        if (String.valueOf(Global.currAva).equals("no")) {
            Picasso.get()
                    .load(R.drawable.group)
                    .placeholder(R.drawable.placeholder_gray).error(R.drawable.errorimg)

                    .into(profile);
        } else {
            Picasso.get()
                    .load(Global.currAva)
                    .placeholder(R.drawable.placeholder_gray).error(R.drawable.errorimg)

                    .into(profile);
        }
    }

    public void getUsers() {
        if (before < Global.currGUsers.size()) {

            mUser.child(Global.currGUsers.get(before)).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    UserData userData = dataSnapshot.getValue(UserData.class);
                    if (!Global.currGUsersU.contains(userData))
                        Global.currGUsersU.add(userData);

                    if (before < Global.currGUsers.size() - 1) {
                        before += 1;
                        getUsers();
                    } else {
                        ((AppBack) getApplication()).setGroupUA(friendid);
                        getAdmins();
                        userA.notifyDataSetChanged();
                        before = 0;
                        dialog.dismiss();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }


    public void getAdmins() {
        dialog.dismiss();
        try {
            for (int i = 0; i < Global.currGUsersU.size(); i++) {
                if (Global.currGAdmins.indexOf(Global.currGUsersU.get(i).getId()) != -1) {
                    if (!Global.adminList.contains(Global.currGUsersU.get(i)))
                        Global.adminList.add(Global.currGUsersU.get(i));
                    adminsA.notifyDataSetChanged();
                }


            }
        } catch (NullPointerException e) {

        }

    }

    public static int calculateNoOfColumns(Context context) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        float dpWidth = displayMetrics.widthPixels / displayMetrics.density;
        int scalingFactor = 76; // You can vary the value held by the scalingFactor
        int columnCount = (int) (dpWidth / scalingFactor);
        return (columnCount >= 2 ? columnCount : 2); // if column no. is less than 2, we still display 2 columns
    }

    public void threadUpdate() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (Global.check_int(ProfileGroup.this)) {
                    Global.currGUsersU.clear();
                    getUsers();

                } else
                    getAdmins();

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        Global.currentactivity = this;
        AppBack myApp = (AppBack) this.getApplication();
        if (myApp.wasInBackground) {
            //init data
            Map<String, Object> map = new HashMap<>();
            map.put(Global.Online, true);
            if (mAuth.getCurrentUser() != null)
                mUser.child(mAuth.getCurrentUser().getUid()).updateChildren(map);
            Global.local_on = true;
            //lock screen
            ((AppBack) getApplication()).lockscreen(((AppBack) getApplication()).shared().getBoolean("lock", false));
        }

        myApp.stopActivityTransitionTimer();
    }

    @Override
    protected void onPause() {
        super.onPause();
        ((AppBack) this.getApplication()).startActivityTransitionTimer();
        Global.currentactivity = null;

    }
}
