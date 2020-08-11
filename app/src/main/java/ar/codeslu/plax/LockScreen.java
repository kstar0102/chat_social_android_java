package ar.codeslu.plax;

import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.pm.ShortcutManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.andrognito.patternlockview.PatternLockView;
import com.andrognito.patternlockview.listener.PatternLockViewListener;
import com.andrognito.patternlockview.utils.PatternLockUtils;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ar.codeslu.plax.auth.Login;
import ar.codeslu.plax.global.AppBack;
import ar.codeslu.plax.global.Global;
import ar.codeslu.plax.global.encryption;
import me.leolin.shortcutbadger.ShortcutBadger;


public class LockScreen extends AppCompatActivity {
    //view
    PatternLockView mPatternLockView;
    PatternLockViewListener mPatternLockViewListener;
    ImageView profile_image;
    Button clear, forgot;
    //Firebase
    FirebaseAuth mAuth;
    DatabaseReference mData;

    //var
    int typeL; //0 is set //1 is get
    String pattren;
    int num = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lock_screen);
        //firebase init
        mAuth = FirebaseAuth.getInstance();
        mData = FirebaseDatabase.getInstance().getReference(Global.USERS);
//encryption
        //dark mode init
        if (mAuth.getCurrentUser() != null) {
            if (!((AppBack) getApplication()).shared().getBoolean("dark" + mAuth.getCurrentUser().getUid(), false)) {
                getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            } else {
                getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            }
        }
        //encryption
        Global.currentactivity = null;

        mPatternLockView = (PatternLockView) findViewById(R.id.patter_lock_view);
        profile_image = findViewById(R.id.profile_image);
        clear = findViewById(R.id.clear);
        forgot = findViewById(R.id.forgot);
        if (Global.avaLocal.equals("no")) {
            Picasso.get()
                    .load(R.drawable.profile)
                    .placeholder(R.drawable.placeholder_gray) .error(R.drawable.errorimg)

                    .into(profile_image);
        } else {
            if(!Global.avaLocal.isEmpty()) {
                Picasso.get()
                        .load(Global.avaLocal)
                        .placeholder(R.drawable.placeholder_gray).error(R.drawable.errorimg)

                        .into(profile_image);
            }
            else
            {
                Picasso.get()
                        .load(R.drawable.profile)
                        .placeholder(R.drawable.placeholder_gray).error(R.drawable.errorimg)
                        .into(profile_image);
            }
        }

        if (getIntent() != null) {
            typeL = getIntent().getExtras().getInt("typeL");
        }

        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPatternLockView.clearPattern();
            }
        });
        mPatternLockViewListener = new PatternLockViewListener() {
            @Override
            public void onStarted() {
            }

            @Override
            public void onProgress(List<PatternLockView.Dot> progressPattern) {
            }

            @Override
            public void onComplete(List<PatternLockView.Dot> pattern) {
                switch (typeL) {
                    case 0:
                        forgot.setVisibility(View.GONE);
                        mPatternLockView.setViewMode(PatternLockView.PatternViewMode.CORRECT);
                        num = PatternLockUtils.patternToString(mPatternLockView, pattern).length();

                        if (num >= 4) {
                            pattren = encryption.encryptOrNull(PatternLockUtils.patternToString(mPatternLockView, pattern));
                            ((AppBack) getApplication()).editSharePrefs().putString("lockP", pattren);
                            ((AppBack) getApplication()).editSharePrefs().apply();
                            mPatternLockView.clearPattern();
                            ((AppBack) getApplication()).editSharePrefs().putBoolean("lock", true);
                            ((AppBack) getApplication()).editSharePrefs().apply();
                            AlertDialog.Builder builder = new AlertDialog.Builder(LockScreen.this);
                            builder.setMessage(R.string.chnge_pattern);
                            builder.setTitle(R.string.patt_set);
                            builder.setIcon(R.drawable.ic_lock_outline_black_24dp);
                            builder.setNeutralButton(R.string.ok, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    finish();
                                }
                            });
                            builder.show();
                        } else {
                            AlertDialog.Builder builder = new AlertDialog.Builder(LockScreen.this);
                            builder.setMessage(getString(R.string.patt_err_mess));
                            builder.setTitle(getString(R.string.weak_pp));
                            builder.setIcon(R.drawable.ic_lock_outline_black_24dp);
                            builder.setNeutralButton(R.string.ok, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    num = 0;
                                    mPatternLockView.clearPattern();
                                    dialog.dismiss();
                                }
                            });
                            builder.show();
                        }

                        break;
                    case 1:
                        forgot.setVisibility(View.VISIBLE);
                        pattren = encryption.decryptOrNull(((AppBack) getApplication()).shared().getString("lockP", "no"));
                        if (pattren.equals(PatternLockUtils.patternToString(mPatternLockView, pattern))) {
                            mPatternLockView.setViewMode(PatternLockView.PatternViewMode.CORRECT);
                            mPatternLockView.clearPattern();
                            finish();
                        } else
                            mPatternLockView.setViewMode(PatternLockView.PatternViewMode.WRONG);


                        break;
                }
            }

            @Override
            public void onCleared() {
            }
        };
        mPatternLockView.addPatternLockListener(mPatternLockViewListener);

        forgot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(LockScreen.this);
                builder.setMessage(R.string.logout_forgot);
                builder.setTitle(R.string.forgot_password);
                builder.setIcon(R.drawable.ic_lock_outline_black_24dp);
                builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (Global.check_int(LockScreen.this)) {
                            //clear all notifications
                            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                            try {
                                if (notificationManager != null) {
                                    notificationManager.cancelAll();
                                    int count = 0;
                                    //store it again
                                    ((AppBack) getApplication()).editSharePrefs().putInt("numN" + mAuth.getCurrentUser().getUid(), count);
                                    ((AppBack) getApplication()).editSharePrefs().apply();
                                    ShortcutBadger.applyCount(LockScreen.this, count);
                                }
                            } catch (NullPointerException e) {
                                //nothing
                            }
                            ((AppBack) getApplication()).editSharePrefs().putBoolean("lock", false);
                            ((AppBack) getApplication()).editSharePrefs().apply();

                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1)
                                disableshourtcuts();

                            mAuth.signOut();
                            startActivity(new Intent(LockScreen.this, Login.class));
                            finish();
                        } else
                            Toast.makeText(LockScreen.this, R.string.check_int, Toast.LENGTH_SHORT).show();
                    }
                });
                builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.show();
            }
        });

    }

    @Override
    public void onResume() {
        super.onResume();
        Global.currentactivity = null;
        AppBack myApp = (AppBack) this.getApplication();
        if (myApp.wasInBackground) {
            //init data
            Map<String, Object> map = new HashMap<>();
            map.put(Global.Online, true);
            if(mAuth.getCurrentUser() != null)
                mData.child(mAuth.getCurrentUser().getUid()).updateChildren(map);
            Global.local_on = true;
        }

        myApp.stopActivityTransitionTimer();
    }

    @Override
    public void onPause() {
        super.onPause();
        ((AppBack) this.getApplication()).startActivityTransitionTimer();
        Global.currentactivity = null;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        //lock screen
        if (typeL == 1) {
            finish();
            ((AppBack) getApplication()).lockscreen(((AppBack) getApplication()).shared().getBoolean("lock", false));
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N_MR1)
    public void disableshourtcuts()
    {
        List<String> idds = new ArrayList<>();
        idds.add("addstory");
        idds.add("group");
        idds.add("user1");
        idds.add("user2");
        ShortcutManager shortcutManager2 = this.getSystemService(ShortcutManager.class);
        shortcutManager2.disableShortcuts(idds);
    }
}
