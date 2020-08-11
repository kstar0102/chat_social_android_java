package ar.codeslu.plax.settingsitems;

import android.Manifest;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
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
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ar.codeslu.plax.R;
import ar.codeslu.plax.global.AppBack;
import ar.codeslu.plax.global.Global;
import dmax.dialog.SpotsDialog;
import xyz.aprildown.ultimatemusicpicker.MusicPickerListener;
import xyz.aprildown.ultimatemusicpicker.UltimateMusicPicker;

public class CallsSetting extends AppCompatActivity implements MusicPickerListener {

    LinearLayout ringL;
    TextView ringT,mainT;
    android.app.AlertDialog dialogg;
    TextView clearT;
    LinearLayout clearL;
    DatabaseReference deleteC;
    FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calls_setting);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        Global.currentactivity = this;

        deleteC = FirebaseDatabase.getInstance().getReference(Global.CALLS);
        mAuth = FirebaseAuth.getInstance();
        clearL = findViewById(R.id.clear);
        clearT = findViewById(R.id.clearT);
        ringL = findViewById(R.id.ringL);
        ringT = findViewById(R.id.ringT);
        mainT = findViewById(R.id.mainT);
        ringT.setText(((AppBack) getApplication()).shared().getString("ringNCC", getString(R.string.defaultt)));



        //dark mode init
        if (mAuth.getCurrentUser() != null) {
            if (!((AppBack) getApplication()).shared().getBoolean("dark" + mAuth.getCurrentUser().getUid(), false)) {
                getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                mainT.setTextColor(Color.BLACK);
            } else {
                getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                mainT.setTextColor(Color.WHITE);

            }
        }
        //loader
        if (Global.DARKSTATE) {
            dialogg = new SpotsDialog.Builder()
                    .setContext(this)
                    .setMessage(R.string.pleasW)
                    .setTheme(R.style.darkDialog)
                    .setCancelable(true)
                    .build();
        } else {
            dialogg = new SpotsDialog.Builder()
                    .setContext(this)
                    .setMessage(R.string.pleasW)
                    .setCancelable(true)
                    .build();
        }

        ringL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Dexter.withActivity(CallsSetting.this)
                        .withPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE)
                        .withListener(new MultiplePermissionsListener() {
                            @Override
                            public void onPermissionsChecked(MultiplePermissionsReport report) {

                                if (report.areAllPermissionsGranted()) {
                                    new UltimateMusicPicker()
                                            // Picker activity action bar title or dialog title
                                            .windowTitle(getString(R.string.choose_ring))
                                            .streamType(AudioManager.STREAM_VOICE_CALL)
                                            .ringtone()
                                            .removeSilent()
                                            .defaultTitleAndUri(getString(R.string.defaultt),RingtoneManager.getActualDefaultRingtoneUri(CallsSetting.this, RingtoneManager.TYPE_RINGTONE))
                                            // Show music files from external storage. Requires READ_EXTERNAL_STORAGE permission.
                                            .music()
                                            .alarm()
                                            // Show a picker dialog
                                            .goWithDialog(getSupportFragmentManager());
                                } else
                                    Toast.makeText(CallsSetting.this, getString(R.string.acc_per), Toast.LENGTH_SHORT).show();


                            }

                            @Override
                            public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {

                                token.continuePermissionRequest();

                            }
                        }).check();
            }
        });
        //clear chats
        clearL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(Global.check_int(CallsSetting.this)){
                dialogg.show();
                deleteC.child(mAuth.getCurrentUser().getUid()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        dialogg.dismiss();
                        if (task.isSuccessful()) {
                            ((AppBack) getApplication()).getCalls();
                            Global.callList.clear();
                            ((AppBack) getApplication()).setCalls();

                            Toast.makeText(CallsSetting.this, R.string.call_dee, Toast.LENGTH_SHORT).show();

                        } else
                            Toast.makeText(CallsSetting.this, R.string.error, Toast.LENGTH_SHORT).show();
                    }
                });
            }
            else
                    Toast.makeText(CallsSetting.this, R.string.check_int, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onMusicPick(@NotNull Uri uri, @NotNull String s) {
        ((AppBack) getApplication()).editSharePrefs().putString("ringNCC", s);
        ((AppBack) getApplication()).editSharePrefs().apply();
        ((AppBack) getApplication()).editSharePrefs().putString("ringUCC", String.valueOf(uri));
        ((AppBack) getApplication()).editSharePrefs().apply();
        ringT.setText(((AppBack) getApplication()).shared().getString("ringNCC", getString(R.string.defaultt)));
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
