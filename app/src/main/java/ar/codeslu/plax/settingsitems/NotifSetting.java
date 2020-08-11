package ar.codeslu.plax.settingsitems;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Bundle;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ar.codeslu.plax.Chat;
import ar.codeslu.plax.MainActivity;
import ar.codeslu.plax.R;
import ar.codeslu.plax.Setting;
import ar.codeslu.plax.global.AppBack;
import ar.codeslu.plax.global.Global;
import petrov.kristiyan.colorpicker.ColorPicker;
import xyz.aprildown.ultimatemusicpicker.MusicPickerListener;
import xyz.aprildown.ultimatemusicpicker.UltimateMusicPicker;

public class NotifSetting extends AppCompatActivity implements MusicPickerListener {

    LinearLayout soundL, ledL;
    TextView soundT,main1,main2;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notif_setting);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        Global.currentactivity = this;
        actionBar.setDisplayHomeAsUpEnabled(true);
        soundL = findViewById(R.id.soundL);
        ledL = findViewById(R.id.ledL);
        soundT = findViewById(R.id.soundT);
        main1 = findViewById(R.id.main1);
        main2 = findViewById(R.id.main2);
        mAuth = FirebaseAuth.getInstance();

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

        soundT.setText(((AppBack) getApplication()).shared().getString("ringN", getString(R.string.defaultt)));
        soundL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Dexter.withActivity(NotifSetting.this)
                        .withPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE)
                        .withListener(new MultiplePermissionsListener() {
                            @Override
                            public void onPermissionsChecked(MultiplePermissionsReport report) {

                                if (report.areAllPermissionsGranted()) {
                                    new UltimateMusicPicker()
                                            // Picker activity action bar title or dialog title
                                            .windowTitle(getString(R.string.chos_noti))
                                            .streamType(AudioManager.STREAM_NOTIFICATION)
                                            .notification()
                                            .alarm()
                                            // Show music files from external storage. Requires READ_EXTERNAL_STORAGE permission.
                                            .music()
                                            // Show a picker dialog
                                            .goWithDialog(getSupportFragmentManager());
                                } else
                                    Toast.makeText(NotifSetting.this, getString(R.string.acc_per), Toast.LENGTH_SHORT).show();


                            }

                            @Override
                            public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {

                                token.continuePermissionRequest();

                            }
                        }).check();
            }
        });

        ledL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final ColorPicker colorPicker = new ColorPicker(NotifSetting.this);
                colorPicker.setOnFastChooseColorListener(new ColorPicker.OnFastChooseColorListener() {
                    @Override
                    public void setOnFastChooseColorListener(int position, int color) {
                        ((AppBack) getApplication()).editSharePrefs().putInt("colorN", color);
                        ((AppBack) getApplication()).editSharePrefs().apply();
                    }

                    @Override
                    public void onCancel() {

                    }
                })
                        .disableDefaultButtons(true)
                        .setRoundColorButton(true)
                        .setColumns(5)
                        .show();

            }
        });


    }

    @Override
    public void onMusicPick(@NotNull Uri uri, @NotNull String s) {
        ((AppBack) getApplication()).editSharePrefs().putString("ringN", s);
        ((AppBack) getApplication()).editSharePrefs().apply();
        ((AppBack) getApplication()).editSharePrefs().putString("ringU", String.valueOf(uri));
        ((AppBack) getApplication()).editSharePrefs().apply();
        soundT.setText(((AppBack) getApplication()).shared().getString("ringN", getString(R.string.defaultt)));
    }

    @Override
    public void onPickCanceled() {

    }

    @Override
    protected void onResume() {
        super.onResume();
        Global.currentactivity = this;
        DatabaseReference mData = FirebaseDatabase.getInstance().getReference(Global.USERS);
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
    }

    @Override
    protected void onPause() {
        super.onPause();
        ((AppBack) this.getApplication()).startActivityTransitionTimer();
        Global.currentactivity = null;


    }
}
