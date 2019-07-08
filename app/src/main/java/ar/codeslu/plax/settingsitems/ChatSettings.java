package ar.codeslu.plax.settingsitems;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import androidx.core.app.ActivityCompat;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.nightonke.jellytogglebutton.JellyToggleButton;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ar.codeslu.plax.R;
import ar.codeslu.plax.auth.DataSet;
import ar.codeslu.plax.global.AppBack;
import ar.codeslu.plax.global.Global;

public class ChatSettings extends AppCompatActivity {

    FirebaseAuth mAuth;
    TextView fontT,wallT;
    JellyToggleButton soundT,enterT;
    LinearLayout fontL,wallL;
    AlertDialog.Builder dialog;
String choosenFont;
ImageView delete;
    DatabaseReference mData;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_settings);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        soundT = findViewById(R.id.soundT);
        enterT = findViewById(R.id.enterT);
        fontT = findViewById(R.id.fontT);
        wallT = findViewById(R.id.wallT);
        fontL = findViewById(R.id.fontL);
        wallL = findViewById(R.id.wallL);
delete = findViewById(R.id.delete);
if(((AppBack) getApplication()).shared().getString("wall", "no").equals("no"))
delete.setVisibility(View.GONE);
else
    delete.setVisibility(View.VISIBLE);


delete.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        ((AppBack) getApplication()).editSharePrefs().putString("wall", "no");
        ((AppBack) getApplication()).editSharePrefs().apply();
        Toast.makeText(ChatSettings.this, getResources().getString(R.string.walldsucc), Toast.LENGTH_SHORT).show();
        finish();
    }
});
        mAuth = FirebaseAuth.getInstance();
        mData = FirebaseDatabase.getInstance().getReference(Global.USERS);
        if (mAuth.getCurrentUser() != null) {
            if (!((AppBack) Global.mainActivity.getApplication()).shared().getBoolean("dark" + mAuth.getCurrentUser().getUid(), false))
            {
                wallT.setTextColor(Global.conMain.getResources().getColor(R.color.black));
                fontT.setTextColor(Global.conMain.getResources().getColor(R.color.black));

            }
            else
            {
                wallT.setTextColor(Global.conMain.getResources().getColor(R.color.white));
                fontT.setTextColor(Global.conMain.getResources().getColor(R.color.white));

            }

        }
fontL.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        dialog = new AlertDialog.Builder(ChatSettings.this);
        dialog.setTitle(R.string.plz);
        //checked item is the default checked item when dialog open
        choosenFont = ((AppBack) getApplication()).shared().getString("font", "8");
        dialog.setSingleChoiceItems(getResources().getStringArray(R.array.font), (Integer.parseInt(choosenFont)-1), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //set choosen lang id
                choosenFont = String.valueOf(i+1);
                ((AppBack) getApplication()).changefont((Integer.parseInt(choosenFont)));
                dialogInterface.dismiss();
                Intent restart = getBaseContext().getPackageManager().getLaunchIntentForPackage(getBaseContext().getPackageName());
                restart.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                restart.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(restart);
                finish();

            }
        });
        dialog.show();
    }
});
        wallL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Dexter.withActivity(ChatSettings.this)
                        .withPermissions(Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.CAMERA)
                        .withListener(new MultiplePermissionsListener() {
                            @Override public void onPermissionsChecked(MultiplePermissionsReport report) {

                                if(report.areAllPermissionsGranted())
                                {
                                    CropImage.activity()
                                            .setGuidelines(CropImageView.Guidelines.ON)
                                            .start(ChatSettings.this);
                                }
                                else
                                    Toast.makeText(ChatSettings.this, getString(R.string.acc_per), Toast.LENGTH_SHORT).show();


                            }
                            @Override public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {

                                token.continuePermissionRequest();

                            }
                        }).check();
            }
        });
        //toggles
soundT.setChecked(((AppBack) getApplication()).shared().getBoolean("sound", false));
        enterT.setChecked(((AppBack) getApplication()).shared().getBoolean("enter", false));

        soundT.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    ((AppBack) getApplication()).editSharePrefs().putBoolean("sound", true);
                    ((AppBack) getApplication()).editSharePrefs().apply();
                } else {
                    ((AppBack) getApplication()).editSharePrefs().putBoolean("sound", false);
                    ((AppBack) getApplication()).editSharePrefs().apply();
                }
            }
        });
        enterT.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    ((AppBack) getApplication()).editSharePrefs().putBoolean("enter", true);
                    ((AppBack) getApplication()).editSharePrefs().apply();
                } else {
                    ((AppBack) getApplication()).editSharePrefs().putBoolean("enter", false);
                    ((AppBack) getApplication()).editSharePrefs().apply();
                }
            }
        });



    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
             Uri imgLocalpath = result.getUri();
             Intent intent = new Intent(this,TestWall.class);
             intent.putExtra("wall334",String.valueOf(imgLocalpath));
             startActivity(intent);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }


    @Override
    public void onResume() {
        super.onResume();
        if(((AppBack) getApplication()).shared().getString("wall", "no").equals("no"))
            delete.setVisibility(View.GONE);
        else
            delete.setVisibility(View.VISIBLE);
        Global.currentactivity = this;
        AppBack myApp = (AppBack) this.getApplication();
        if (myApp.wasInBackground) {
            //init data
            Map<String, Object> map = new HashMap<>();
            map.put(Global.Online, true);
            mData.child(mAuth.getCurrentUser().getUid()).updateChildren(map);
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
}
