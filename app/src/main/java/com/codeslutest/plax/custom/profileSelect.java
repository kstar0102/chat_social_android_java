package com.codeslutest.plax.custom;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

import com.codeslutest.plax.R;
import com.codeslutest.plax.global.Global;

/**
 * Created by CodeSlu on 05/03/19.
 */


public class profileSelect extends Dialog {

    private Context c;
    public Dialog d;
    private Button addA, rmvA, rmvU;
    private FirebaseAuth mAuth;
    private DatabaseReference mData, mUser;
    private String friendid, groupid;
    LinearLayout dialogM;
    boolean adminList, admin, isitadmin;


    public profileSelect(Context a, String groupid, String friendid, boolean adminList) {
        super(a);
        // TODO Auto-generated constructor stub
        this.c = a;
        this.friendid = friendid;
        this.groupid = groupid;
        this.adminList = adminList;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.select_dialog_group);
        addA = findViewById(R.id.mkadmin);
        rmvA = findViewById(R.id.rmvadmin);
        rmvU = findViewById(R.id.deletuser);
        dialogM = findViewById(R.id.dialogM);

        if (Global.DARKSTATE)
            dialogM.setBackground(ContextCompat.getDrawable(c, R.drawable.dialog_d));
        else
            dialogM.setBackground(ContextCompat.getDrawable(c, R.drawable.dialog_w));

        //firebase
        mAuth = FirebaseAuth.getInstance();
        mData = FirebaseDatabase.getInstance().getReference(Global.GROUPS);
        mUser = FirebaseDatabase.getInstance().getReference(Global.USERS);


        admin = Global.currGAdmins.contains(mAuth.getCurrentUser().getUid());
        isitadmin = Global.currGAdmins.contains(friendid);


        if (adminList && admin) {
            addA.setVisibility(View.GONE);
            rmvA.setVisibility(View.VISIBLE);
            rmvU.setVisibility(View.GONE);

        } else if (!adminList && admin) {
            if (isitadmin)
                addA.setVisibility(View.GONE);
            else
                addA.setVisibility(View.VISIBLE);

            rmvA.setVisibility(View.GONE);
            rmvU.setVisibility(View.VISIBLE);
        } else {
            addA.setVisibility(View.GONE);
            rmvA.setVisibility(View.GONE);
            rmvU.setVisibility(View.GONE);
        }


        if (adminList && admin && Global.adminList.size() <= 1) {
            addA.setVisibility(View.GONE);
            rmvA.setVisibility(View.GONE);
            rmvU.setVisibility(View.GONE);
        }


        addA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                if(Global.currGAdmins.size() < 25) {
                    if (Global.currGAdmins.indexOf(friendid) == -1) {
                        Map<String, Object> map = new HashMap<>();
                        Global.currGAdmins.add(friendid);
                        map.put("admins", Global.currGAdmins);
                        mData.child(groupid).updateChildren(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                            }
                        });

                    } else
                        Toast.makeText(Global.mainActivity, R.string.this_usr_adm, Toast.LENGTH_SHORT).show();

                }
                else
                    Toast.makeText(Global.mainActivity, R.string.maxadmin, Toast.LENGTH_SHORT).show();


            }
        });

        rmvA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                Map<String, Object> map = new HashMap<>();
                Global.currGAdmins.remove(friendid);
                map.put("admins", Global.currGAdmins);
                mData.child(groupid).updateChildren(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                    }
                });

            }
        });

        rmvU.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                mUser.child(friendid).child(Global.GROUPS).child(groupid).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Map<String, Object> map = new HashMap<>();
                        Global.currGUsers.remove(friendid);
                        map.put("users", Global.currGUsers);
                        mData.child(groupid).updateChildren(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                if (Global.currGAdmins.contains(friendid)) {
                                    Map<String, Object> map2 = new HashMap<>();
                                    Global.currGAdmins.remove(friendid);
                                    map2.put("admins", Global.currGAdmins);
                                    mData.child(groupid).updateChildren(map2).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                        }
                                    });
                                }

                            }
                        });
                    }
                });
            }
        });


    }
}