package ar.codeslu.plax.mediachat;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.davemorrissey.labs.subscaleview.ImageSource;
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;
import com.downloader.Error;
import com.downloader.OnCancelListener;
import com.downloader.OnDownloadListener;
import com.downloader.OnPauseListener;
import com.downloader.OnProgressListener;
import com.downloader.OnStartOrResumeListener;
import com.downloader.PRDownloader;
import com.downloader.Progress;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.karumi.dexter.listener.single.PermissionListener;
import com.squareup.picasso.Picasso;
import com.vanniktech.emoji.EmojiTextView;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ar.codeslu.plax.Profile;
import ar.codeslu.plax.R;
import ar.codeslu.plax.global.AppBack;
import ar.codeslu.plax.global.Global;
import de.hdodenhof.circleimageview.CircleImageView;
import dmax.dialog.SpotsDialog;

public class Photoa extends AppCompatActivity {
    //toolbar
    Toolbar toolbar;
    CircleImageView ava;
    ImageView download,share;
    EmojiTextView name;
    //view
    SubsamplingScaleImageView full;
    //data
    String urlS, fromS, avaS, nameS, MidS, filename, DIR_NAME;
    File direct;
    //firebase
    FirebaseAuth mAuth;
    DatabaseReference mData;
    //loader
    AlertDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photoa);
        PRDownloader.initialize(getApplicationContext());
        mAuth = FirebaseAuth.getInstance();
        mData = FirebaseDatabase.getInstance().getReference(Global.USERS);
        Global.currentactivity = this;
        //dark mode init
        if (mAuth.getCurrentUser() != null) {
            if (!((AppBack) getApplication()).shared().getBoolean("dark" + mAuth.getCurrentUser().getUid(), false)) {
                getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            } else {
                getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            }
        }
        //loader
        if (Global.DARKSTATE)
        {
            dialog = new SpotsDialog.Builder()
                    .setContext(this)
                    .setMessage(R.string.pleasW)
                    .setTheme(R.style.darkDialog)
                    .setCancelable(true)
                    .build();
        }
        else
        {
            dialog = new SpotsDialog.Builder()
                    .setContext(this)
                    .setMessage(R.string.pleasW)
                    .setCancelable(true)
                    .build();
        }

        dialog.show();
        //toolbar
        //Actionbar init
        toolbar = (Toolbar) findViewById(R.id.mediabar);
        full = findViewById(R.id.fullimage);
        toolbar.setPadding(0, 0, 0, 0);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayShowCustomEnabled(true);
        //Action bar design
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View viewS = inflater.inflate(R.layout.media_bar, null);
        actionBar.setCustomView(viewS);
        name = viewS.findViewById(R.id.nameC);
        ava = viewS.findViewById(R.id.avaC);
        download = viewS.findViewById(R.id.download);
        share = viewS.findViewById(R.id.share);

        if (getIntent() != null) {
            Intent intent = getIntent();
            urlS = intent.getExtras().getString("url");
            fromS = intent.getExtras().getString("from");
            MidS = intent.getExtras().getString("Mid");
            avaS = intent.getExtras().getString("ava");
            nameS = intent.getExtras().getString("name");
            //load name
            name.setText(nameS);
            //load ava
            if (avaS.equals("no")) {
                Picasso.get()
                        .load(R.drawable.profile)
                        .error(R.drawable.errorimg)
                        .into(ava);
            } else {
                Picasso.get()
                        .load(avaS)
                        .error(R.drawable.errorimg)
                        .into(ava);
            }

            if(urlS.contains("file://"))
            {
                full.setImage(ImageSource.uri(urlS));
                dialog.dismiss();

            }else
            {
                Dexter.withActivity(Photoa.this)
                        .withPermissions(Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.WAKE_LOCK)
                        .withListener(new MultiplePermissionsListener() {
                            @Override public void onPermissionsChecked(MultiplePermissionsReport report) {

                                if(report.areAllPermissionsGranted())
                                    downloadP();

                                else
                                    Toast.makeText(Photoa.this, getString(R.string.acc_per), Toast.LENGTH_SHORT).show();


                            }
                            @Override public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {

                                token.continuePermissionRequest();

                            }
                        }).check();
            }


        }
        //download photo
        download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Dexter.withActivity(Photoa.this)
                        .withPermissions(Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.WAKE_LOCK)
                        .withListener(new MultiplePermissionsListener() {
                            @Override public void onPermissionsChecked(MultiplePermissionsReport report) {

                                if(report.areAllPermissionsGranted())
                                    downloadPD();
                                else
                                    Toast.makeText(Photoa.this, getString(R.string.acc_per), Toast.LENGTH_SHORT).show();



                            }
                            @Override public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {

                                token.continuePermissionRequest();

                            }
                        }).check();
            }
        });

        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Dexter.withActivity(Photoa.this)
                        .withPermissions(Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.WAKE_LOCK)
                        .withListener(new MultiplePermissionsListener() {
                            @Override public void onPermissionsChecked(MultiplePermissionsReport report) {

                                if(report.areAllPermissionsGranted())
                                {
                                    DIR_NAME = getResources().getString(R.string.app_name);
                                    filename = mAuth.getCurrentUser().getUid() + Global.currentpageid + MidS + ".png";
                                    direct =
                                            new File(Environment
                                                    .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
                                                    .getAbsolutePath() + "/" + DIR_NAME + "/");
                                    if (!direct.exists()) {
                                        direct.mkdir();
                                    }

                                    File file = new File(direct.getPath() + "/" + filename);
                                    if (file.exists())
                                    {
                                        full.setImage(ImageSource.uri(direct.getPath() + "/" + filename));
                                        Intent share = new Intent(Intent.ACTION_SEND);
                                        share.setType("image/jpeg");
                                        share.putExtra(Intent.EXTRA_STREAM, Uri.parse(direct.getPath() + "/" + filename));
                                        startActivity(Intent.createChooser(share, getString(R.string.shareimage)));
                                    }
                                    else
                                        Toast.makeText(Photoa.this,getString(R.string.download),Toast.LENGTH_LONG).show();
                                }
                                else
                                    Toast.makeText(Photoa.this, getString(R.string.acc_per), Toast.LENGTH_SHORT).show();



                            }
                            @Override public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {

                                token.continuePermissionRequest();

                            }
                        }).check();


            }
        });


    }


    private void downloadP() {
        //Get things
        DIR_NAME = getResources().getString(R.string.app_name);
        filename = mAuth.getCurrentUser().getUid() + Global.currentpageid + MidS + ".png";
        direct =
                new File(Environment
                        .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
                        .getAbsolutePath() + "/" + DIR_NAME + "/");
        if (!direct.exists()) {
            direct.mkdir();
        }

        File file = new File(direct.getPath() + "/" + filename);
        if (file.exists())
        {
            full.setImage(ImageSource.uri(direct.getPath() + "/" + filename));
            dialog.dismiss();
        }
        else {
            int downloadId = PRDownloader.download(urlS, direct.getPath(), filename)
                    .build()
                    .setOnStartOrResumeListener(new OnStartOrResumeListener() {
                        @Override
                        public void onStartOrResume() {

                        }
                    })
                    .setOnPauseListener(new OnPauseListener() {
                        @Override
                        public void onPause() {

                        }
                    })
                    .setOnCancelListener(new OnCancelListener() {
                        @Override
                        public void onCancel() {

                        }
                    })
                    .setOnProgressListener(new OnProgressListener() {
                        @Override
                        public void onProgress(Progress progress) {

                        }
                    })
                    .start(new OnDownloadListener() {
                        @Override
                        public void onDownloadComplete() {
                            full.setImage(ImageSource.uri(direct.getPath() + "/" + filename));
                            dialog.dismiss();
                        }

                        @Override
                        public void onError(Error error) {
                            Toast.makeText(Global.conA, Global.conA.getResources().getString(R.string.error), Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                        }


                    });
        }

    }


    private void downloadPD() {
        if (Global.check_int(Photoa.this)) {
            //Get things
            DIR_NAME = getResources().getString(R.string.app_name);
            filename = mAuth.getCurrentUser().getUid() + Global.currentpageid + MidS + ".png";
            direct =
                    new File(Environment
                            .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
                            .getAbsolutePath() + "/" + DIR_NAME + "/");
            if (!direct.exists()) {
                direct.mkdir();
            }
            int downloadId = PRDownloader.download(urlS, direct.getPath(), filename)
                    .build()
                    .setOnStartOrResumeListener(new OnStartOrResumeListener() {
                        @Override
                        public void onStartOrResume() {
                            Toast.makeText(Global.conA, Global.conA.getResources().getString(R.string.downloadstart), Toast.LENGTH_SHORT).show();

                        }
                    })
                    .setOnPauseListener(new OnPauseListener() {
                        @Override
                        public void onPause() {

                        }
                    })
                    .setOnCancelListener(new OnCancelListener() {
                        @Override
                        public void onCancel() {

                        }
                    })
                    .setOnProgressListener(new OnProgressListener() {
                        @Override
                        public void onProgress(Progress progress) {

                        }
                    })
                    .start(new OnDownloadListener() {
                        @Override
                        public void onDownloadComplete() {
                            Toast.makeText(Global.conA, Global.conA.getResources().getString(R.string.downloadcomplete), Toast.LENGTH_SHORT).show();


                        }

                        @Override
                        public void onError(Error error) {
                            Toast.makeText(Global.conA, Global.conA.getResources().getString(R.string.error), Toast.LENGTH_SHORT).show();
                        }


                    });
        } else
            Toast.makeText(Photoa.this, R.string.check_conn, Toast.LENGTH_SHORT).show();
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


    public void goP(View view) {
        Intent intent = new Intent(this, Profile.class);
        intent.putExtra("idP", fromS);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        dialog.dismiss();
        super.onBackPressed();
    }
}
