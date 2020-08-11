package ar.codeslu.plax.settingsitems;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;

import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.nightonke.jellytogglebutton.JellyToggleButton;

import java.util.HashMap;
import java.util.Map;

import ar.codeslu.plax.R;
import ar.codeslu.plax.global.AppBack;
import ar.codeslu.plax.global.Global;
import io.github.tonnyl.light.Light;

public class SecuSetting extends AppCompatActivity {
    JellyToggleButton lockT,screenT;
    Button lockB;
    boolean clicked = false;
    DatabaseReference mData,mchat;
    FirebaseAuth mAuth;
    LinearLayout ly;
    Handler mHandler;
    TextView main1,main2;
    boolean isRunning = true;
    boolean prevstate = true;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_secu_setting);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        Global.currentactivity = this;

        lockT = findViewById(R.id.lockT);
        ly = findViewById(R.id.ly);
        screenT = findViewById(R.id.screenST);
        lockB = findViewById(R.id.lockB);
        main1 = findViewById(R.id.main1);
        main2 = findViewById(R.id.main2);
        mAuth = FirebaseAuth.getInstance();
        mData = FirebaseDatabase.getInstance().getReference(Global.USERS);
        mchat = FirebaseDatabase.getInstance().getReference(Global.CHATS);
        //dark mode init
        if (mAuth.getCurrentUser() != null) {
            if (!((AppBack) getApplication()).shared().getBoolean("dark" + mAuth.getCurrentUser().getUid(), false)) {
                getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                main1.setTextColor(Color.BLACK);
                main2.setTextColor(Color.BLACK);
            } else {
                getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                main1.setTextColor(Color.WHITE);
                main2.setTextColor(Color.WHITE);
            }
        }
        lockB.setVisibility(View.GONE);

        //set data
        if(((AppBack) getApplication()).shared().getBoolean("lock", false))
        {
            //lock screen
            ((AppBack) getApplication()).lockscreen(((AppBack) getApplication()).shared().getBoolean("lock", false));
            lockT.setChecked(true);
            lockB.setVisibility(View.VISIBLE);
        }
        else
        {
            lockT.setChecked(false);
            lockB.setVisibility(View.GONE);
        }


        mHandler = new Handler();

        new Thread(new Runnable() {
            @Override
            public void run() {
                // TODO Auto-generated method stub
                while (isRunning) {
                    try {
                        Thread.sleep(500);
                        mHandler.post(new Runnable() {

                            @Override
                            public void run() {
                                if(prevstate != Global.check_int(SecuSetting.this)) {
                                    screenT.setEnabled(Global.check_int(SecuSetting.this));
                                    if (!Global.check_int(SecuSetting.this))
                                        Light.error(ly, getResources().getString(R.string.check_int), Snackbar.LENGTH_INDEFINITE).show();
                                    else
                                        Light.error(ly, getResources().getString(R.string.check_int), Snackbar.LENGTH_SHORT).show();

                                    prevstate = Global.check_int(SecuSetting.this);
                                }

                            }
                        });
                    } catch (Exception e) {
                    }
                }
            }
        }).start();

        screenT.setEnabled(Global.check_int(this));
        screenT.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if(!Global.check_int(SecuSetting.this))
                    Toast.makeText(SecuSetting.this, R.string.check_int, Toast.LENGTH_SHORT).show();

                return false;
            }
        });


        screenT.setChecked(((AppBack) getApplication()).shared().getBoolean("screenP", false));

        lockT.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, final boolean isChecked) {
        if (isChecked) {
            lockT.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (buttonView.isChecked())
                    ((AppBack) getApplication()).lockscreenE();
                    lockB.setVisibility(View.VISIBLE);

                }
            });


        } else {
            lockT.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!buttonView.isChecked())
                    {
                        lockB.setVisibility(View.GONE);
                        ((AppBack) getApplication()).editSharePrefs().putBoolean("lock", false);
                        ((AppBack) getApplication()).editSharePrefs().apply();
                    }

                }
            });
        }
        clicked = false;
    }

        });


        screenT.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    Map<String, Object> map = new HashMap<>();
                    map.put("screen", true);
                    mData.child(mAuth.getCurrentUser().getUid()).updateChildren(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                        }
                    });
                    try {
                        for (int i = 0; i < Global.diaG.size(); i++) {

                            mchat.child(Global.diaG.get(i).getId()).child(mAuth.getCurrentUser().getUid()).updateChildren(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    ((AppBack) getApplication()).editSharePrefs().putBoolean("screenP", true);
                                    ((AppBack) getApplication()).editSharePrefs().apply();

                                }
                            })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(SecuSetting.this, R.string.check_conn, Toast.LENGTH_SHORT).show();
                                        }
                                    });

                        }
                    }
                    catch (NullPointerException e)
                    {
                        Toast.makeText(SecuSetting.this, R.string.check_conn, Toast.LENGTH_SHORT).show();
                    }

                } else {
                    Map<String, Object> map = new HashMap<>();
                    map.put("screen", false);
                    mData.child(mAuth.getCurrentUser().getUid()).updateChildren(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                        }
                    });
                    try {
                        for (int i = 0; i < Global.diaG.size(); i++) {

                            mchat.child(Global.diaG.get(i).getId()).child(mAuth.getCurrentUser().getUid()).updateChildren(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    ((AppBack) getApplication()).editSharePrefs().putBoolean("screenP", false);
                                    ((AppBack) getApplication()).editSharePrefs().apply();

                                }
                            })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(SecuSetting.this, R.string.check_conn, Toast.LENGTH_SHORT).show();
                                        }
                                    });

                        }
                    }
                    catch (NullPointerException e)
                    {
                        Toast.makeText(SecuSetting.this, R.string.check_conn, Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });


        lockB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((AppBack) getApplication()).lockscreenE();
                ((AppBack) getApplication()).editSharePrefs().putBoolean("lock", true);
                ((AppBack) getApplication()).editSharePrefs().apply();
            }
        });



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
                mData.child(mAuth.getCurrentUser().getUid()).updateChildren(map);
            Global.local_on = true;
            //lock screen
            ((AppBack) getApplication()).lockscreen(((AppBack) getApplication()).shared().getBoolean("lock", false));
        }

        myApp.stopActivityTransitionTimer();

        //set data
        if(((AppBack) getApplication()).shared().getBoolean("lock", false))
        {
            //lock screen
            lockT.setChecked(true);
            lockB.setVisibility(View.VISIBLE);
        }
        else
        {
            lockT.setChecked(false);
            lockB.setVisibility(View.GONE);
        }
        screenT.setEnabled(Global.check_int(this));
    }

    @Override
    public void onPause() {
        super.onPause();
        ((AppBack) this.getApplication()).startActivityTransitionTimer();
        Global.currentactivity = null;
    }

}
