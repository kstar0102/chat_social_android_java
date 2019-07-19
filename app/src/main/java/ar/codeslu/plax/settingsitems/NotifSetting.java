package ar.codeslu.plax.settingsitems;

import android.Manifest;
import android.content.DialogInterface;
import android.graphics.Color;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Bundle;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import ar.codeslu.plax.Chat;
import ar.codeslu.plax.MainActivity;
import ar.codeslu.plax.R;
import petrov.kristiyan.colorpicker.ColorPicker;
import xyz.aprildown.ultimatemusicpicker.MusicPickerListener;
import xyz.aprildown.ultimatemusicpicker.UltimateMusicPicker;

public class NotifSetting extends AppCompatActivity implements MusicPickerListener {

    LinearLayout soundL,ledL;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notif_setting);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        soundL = findViewById(R.id.soundL);
        ledL = findViewById(R.id.ledL);

        soundL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Dexter.withActivity(NotifSetting.this)
                        .withPermissions(Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE)
                        .withListener(new MultiplePermissionsListener() {
                            @Override
                            public void onPermissionsChecked(MultiplePermissionsReport report) {

                                if (report.areAllPermissionsGranted()) {

                                    new UltimateMusicPicker()
                                            // Picker activity action bar title or dialog title
                                            .windowTitle("UltimateMusicPicker")
                                            // Music preview stream type(AudioManager.STREAM_MUSIC is used by default)
                                            .streamType(AudioManager.STREAM_NOTIFICATION)

                                            // Show different kinds of system ringtones. Calling order determines their display order.
                                            .notification()
                                            .alarm()
                                            // Show a picker dialog
                                            .goWithDialog(getSupportFragmentManager());
                                    // Or show a picker activity
                                    //.goWithActivity(this, 0, MusicPickerActivity::class.java)
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
        Log.d("kkk",s+"////"+String.valueOf(uri));

    }

    @Override
    public void onPickCanceled() {
        Log.d("kkk","cancellll");


    }
}
