package com.codeslutest.plax;

import android.Manifest;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import com.budiyev.android.codescanner.CodeScanner;
import com.budiyev.android.codescanner.CodeScannerView;
import com.budiyev.android.codescanner.DecodeCallback;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.zxing.Result;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.makeramen.roundedimageview.RoundedImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.View;
import android.widget.Toast;


import net.glxn.qrgen.android.QRCode;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.codeslutest.plax.custom.QrResult;
import com.codeslutest.plax.custom.QrResultG;
import com.codeslutest.plax.global.AppBack;
import com.codeslutest.plax.global.Global;
import it.auron.library.mecard.MeCard;
import it.auron.library.mecard.MeCardParser;

public class Qr extends AppCompatActivity {
    RoundedImageView qr;
    FirebaseAuth mAuth;
    private CodeScanner mCodeScanner;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        Global.currentactivity = this;
        qr = findViewById(R.id.qr);
        mAuth = FirebaseAuth.getInstance();
        if (mAuth.getCurrentUser() != null)
            generateQr();

        Dexter.withActivity(Qr.this)
                .withPermissions(Manifest.permission.CAMERA)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        //scan
                        CodeScannerView scannerView = findViewById(R.id.scanner_view);
                        mCodeScanner = new CodeScanner(Qr.this, scannerView);
                        mCodeScanner.setDecodeCallback(new DecodeCallback() {
                            @Override
                            public void onDecoded(@NonNull final Result result) {
                                Qr.this.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        parseQr(result.getText());
                                    }
                                });
                            }
                        });
                        scannerView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                mCodeScanner.startPreview();

                            }
                        });
                        mCodeScanner.startPreview();


                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();

                    }
                }).check();


    }

    private void generateQr() {
        MeCard meCard = new MeCard();
        meCard.setName(Global.nameLocal);
        meCard.setAddress(mAuth.getCurrentUser().getUid());
        meCard.setUrl(Global.avaLocal);
        meCard.setNote(Global.statueLocal);
        meCard.addTelephone(Global.phoneLocal);
        meCard.setOrg(String.valueOf(Global.myscreen));
        String meCardContent = meCard.buildString();
        qr.setImageBitmap(QRCode.from(meCardContent).withSize(250, 250).bitmap());

    }

    private void parseQr(String meCardString) {
        try {
            MeCard meCard = MeCardParser.parse(meCardString);
            Log.wtf("oooo",meCard.getAddress()+"");
            Log.wtf("oooo",meCard.getName()+"");
            Log.wtf("oooo",meCard.getUrl()+"");
            if (meCard.getAddress() != null)
            {

                if (meCard.getAddress().contains("groups-"))
                {
                    if (meCard.getAddress() != null && meCard.getName() != null &&  meCard.getUrl() != null) {
                        QrResultG cdd = new QrResultG(Qr.this, meCard.getUrl(), meCard.getName(), meCard.getAddress());
                        cdd.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        cdd.getWindow().getAttributes().windowAnimations = R.style.CustomDialogAnimation;
                        cdd.show();
                    }
                    else

                    {
                        Toast.makeText(this, getString(R.string.invaled), Toast.LENGTH_SHORT).show();

                    }
                }
                else
                {
                    if (meCard.getAddress() != null && meCard.getName() != null && meCard.getOrg() != null && meCard.getTelephones() != null && meCard.getUrl() != null) {
                        QrResult cdd = new QrResult(Qr.this, meCard.getUrl(), meCard.getName(), meCard.getAddress(), meCard.getTelephones().get(0), Boolean.parseBoolean(meCard.getOrg()));
                        cdd.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        cdd.getWindow().getAttributes().windowAnimations = R.style.CustomDialogAnimation;
                        cdd.show();
                    }
                    else
                    {
                        Toast.makeText(this, getString(R.string.invaled), Toast.LENGTH_SHORT).show();

                    }
                }
            }
            else
            {
                Toast.makeText(this, getString(R.string.invaled), Toast.LENGTH_SHORT).show();
            }


        } catch (NullPointerException e) {
            Toast.makeText(this, getString(R.string.invaled), Toast.LENGTH_SHORT).show();
        }
    }
    @Override
    protected void onResume() {
        super.onResume();

        DatabaseReference mData = FirebaseDatabase.getInstance().getReference(Global.USERS);
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
    }

    @Override
    protected void onPause() {

        ((AppBack) this.getApplication()).startActivityTransitionTimer();
        Global.currentactivity = null;
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        try {
            if(mCodeScanner != null)
                mCodeScanner.releaseResources();
        }catch (NullPointerException e)
        {

        }
        super.onDestroy();
    }
}
