package ar.codeslu.plax.settingsitems;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.google.firebase.auth.FirebaseAuth;
import com.nightonke.jellytogglebutton.JellyToggleButton;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.ArrayList;
import java.util.Arrays;

import ar.codeslu.plax.Chat;
import ar.codeslu.plax.EditProfile;
import ar.codeslu.plax.R;
import ar.codeslu.plax.Setting;
import ar.codeslu.plax.global.AppBack;
import ar.codeslu.plax.global.Global;
import ar.codeslu.plax.settings.SettingAdapter;

public class ChatSettings extends AppCompatActivity {

    FirebaseAuth mAuth;
    TextView fontT,wallT;
    JellyToggleButton soundT,enterT;
    LinearLayout fontL,wallL;
    AlertDialog.Builder dialog;
String choosenFont;
ImageView delete;
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
                if (ActivityCompat.checkSelfPermission(ChatSettings.this, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(ChatSettings.this, android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(ChatSettings.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

                    ActivityCompat.requestPermissions(ChatSettings.this, new String[]{android.Manifest.permission.CAMERA, android.Manifest.permission.READ_EXTERNAL_STORAGE, android.Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            801);
                } else {
                    CropImage.activity()
                            .setGuidelines(CropImageView.Guidelines.ON)
                            .start(ChatSettings.this);
                }
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
    protected void onResume() {
        super.onResume();
        if(((AppBack) getApplication()).shared().getString("wall", "no").equals("no"))
            delete.setVisibility(View.GONE);
        else
            delete.setVisibility(View.VISIBLE);
    }
}
