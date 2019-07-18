package ar.codeslu.plax.settingsitems;

import android.Manifest;
import android.net.Uri;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.kevalpatel.ringtonepicker.RingtonePickerDialog;
import com.kevalpatel.ringtonepicker.RingtonePickerListener;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.util.List;

import ar.codeslu.plax.Chat;
import ar.codeslu.plax.MainActivity;
import ar.codeslu.plax.R;

public class NotifSetting extends AppCompatActivity {

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
                                    RingtonePickerDialog.Builder ringtonePickerBuilder = new RingtonePickerDialog
                                            .Builder(NotifSetting.this, getSupportFragmentManager())
                                            .setTitle(getString(R.string.slec))
                                            .displayDefaultRingtone(true)
                                            .displaySilentRingtone(true)
                                            .setPositiveButtonText(getString(R.string.set))
                                            .setCancelButtonText(getString(R.string.cancel))
                                            .setPlaySampleWhileSelection(true)
                                            .setListener(new RingtonePickerListener() {
                                                @Override
                                                public void OnRingtoneSelected(@NonNull String ringtoneName, Uri ringtoneUri) {

                                                }
                                            });

                                    ringtonePickerBuilder.addRingtoneType(RingtonePickerDialog.Builder.TYPE_NOTIFICATION);
                                    ringtonePickerBuilder.addRingtoneType(RingtonePickerDialog.Builder.TYPE_ALARM);

//Display the dialog.
                                    ringtonePickerBuilder.show();
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


    }

}
