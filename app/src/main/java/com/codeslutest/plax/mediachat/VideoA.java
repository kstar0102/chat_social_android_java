package com.codeslutest.plax.mediachat;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Environment;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

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
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.squareup.picasso.Picasso;
import com.universalvideoview.UniversalMediaController;
import com.universalvideoview.UniversalVideoView;
import com.vanniktech.emoji.EmojiTextView;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.codeslutest.plax.profile.Profile;
import com.codeslutest.plax.R;
import com.codeslutest.plax.global.AppBack;
import com.codeslutest.plax.global.Global;
import de.hdodenhof.circleimageview.CircleImageView;
import dmax.dialog.SpotsDialog;

public class VideoA extends AppCompatActivity implements UniversalVideoView.VideoViewCallback {
    //toolbar
    Toolbar toolbar;
    CircleImageView ava;
    ImageView download,share;
    EmojiTextView name;
    //data
    String urlS, fromS, avaS, nameS, MidS, downloadUrlOfImage, filename, DIR_NAME;
    long downloadID;
    File direct;
    //firebase
    FirebaseAuth mAuth;
    DatabaseReference mData;
    //video
    View mVideoLayout;
    UniversalVideoView mVideoView;
    UniversalMediaController mMediaController;
    private static final String SEEK_POSITION_KEY = "SEEK_POSITION_KEY";
    private int mSeekPosition;
    private int cachedHeight;
    boolean isFullscreen;
//loader
AlertDialog dialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_videoa);
        mAuth = FirebaseAuth.getInstance();
        mData = FirebaseDatabase.getInstance().getReference(Global.USERS);
        Global.currentactivity = this;
        PRDownloader.initialize(getApplicationContext());
        //toolbar
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

        //Actionbar init
        toolbar = (Toolbar) findViewById(R.id.mediabar);
        mVideoView = (UniversalVideoView) findViewById(R.id.videoView);
        mVideoLayout = findViewById(R.id.video_layout);
        mMediaController = (UniversalMediaController) findViewById(R.id.media_controller);
        mMediaController.show();
        mVideoView.setMediaController(mMediaController);
        mVideoView.setVideoViewCallback(this);
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
                        .placeholder(R.drawable.placeholder_gray) .error(R.drawable.errorimg)

                        .into(ava);
            } else {
                Picasso.get()
                        .load(avaS)
                        .placeholder(R.drawable.placeholder_gray) .error(R.drawable.errorimg)

                        .into(ava);
            }

        }

        DIR_NAME = getResources().getString(R.string.app_name);
        filename = mAuth.getCurrentUser().getUid() + Global.currentpageid + MidS + ".mp4";
        direct =
                new File(Environment
                        .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
                        .getAbsolutePath() + "/" + DIR_NAME + "/");
        File file = new File(direct.getPath() + "/" + filename);

        if (file.exists()) {
            setVideoAreaSize(direct.getPath() + "/" + filename);

        } else {
            if (Global.check_int(VideoA.this)) {

                Dexter.withActivity(VideoA.this)
                        .withPermissions(Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.WAKE_LOCK)
                        .withListener(new MultiplePermissionsListener() {
                            @Override public void onPermissionsChecked(MultiplePermissionsReport report) {

                                if(report.areAllPermissionsGranted())
                                {
                                    downloadP();
                                    setVideoAreaSize(urlS);
                                }

                                else
                                    Toast.makeText(VideoA.this, getString(R.string.acc_per), Toast.LENGTH_SHORT).show();


                            }
                            @Override public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {

                                token.continuePermissionRequest();

                            }
                        }).check();
            } else
                Toast.makeText(VideoA.this, R.string.check_conn, Toast.LENGTH_SHORT).show();


        }
        //init video player
        mVideoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {

            }
        });
        //download photo
        download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Dexter.withActivity(VideoA.this)
                        .withPermissions(Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.WAKE_LOCK)
                        .withListener(new MultiplePermissionsListener() {
                            @Override public void onPermissionsChecked(MultiplePermissionsReport report) {

                                if(report.areAllPermissionsGranted())
                                {
                                    downloadPD();
                                }

                                else
                                    Toast.makeText(VideoA.this, getString(R.string.acc_per), Toast.LENGTH_SHORT).show();


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

                Dexter.withActivity(VideoA.this)
                        .withPermissions(Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.WAKE_LOCK)
                        .withListener(new MultiplePermissionsListener() {
                            @Override public void onPermissionsChecked(MultiplePermissionsReport report) {

                                if(report.areAllPermissionsGranted())
                                {
                                    DIR_NAME = getResources().getString(R.string.app_name);
                                    filename = mAuth.getCurrentUser().getUid() + Global.currentpageid + MidS + ".mp4";
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
                                        Intent share = new Intent(Intent.ACTION_SEND);
                                        share.setType("video/mp4");
                                        share.putExtra(Intent.EXTRA_STREAM, Uri.parse(direct.getPath() + "/" + filename));
                                        startActivity(Intent.createChooser(share, getString(R.string.shareimage)));
                                    }
                                    else
                                        Toast.makeText(VideoA.this,getString(R.string.download),Toast.LENGTH_LONG).show();
                                }

                                else
                                    Toast.makeText(VideoA.this, getString(R.string.acc_per), Toast.LENGTH_SHORT).show();


                            }
                            @Override public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {

                                token.continuePermissionRequest();

                            }
                        }).check();

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
    }

    @Override
    public void onPause() {
        super.onPause();
        ((AppBack) this.getApplication()).startActivityTransitionTimer();
        if (mVideoView != null && mVideoView.isPlaying()) {
            mSeekPosition = mVideoView.getCurrentPosition();
            mVideoView.pause();
        }
        Global.currentactivity = null;


    }

    private void downloadP() {
        if (Global.check_int(VideoA.this)) {
            //Get things
            DIR_NAME = getResources().getString(R.string.app_name);
            filename = mAuth.getCurrentUser().getUid() + Global.currentpageid + MidS + ".mp4";
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


                        }

                        @Override
                        public void onError(Error error) {
                        }


                    });
        }

    }

    private void downloadPD() {
        if (Global.check_int(VideoA.this)) {
            //Get things
            DIR_NAME = getResources().getString(R.string.app_name);
            filename = mAuth.getCurrentUser().getUid() + Global.currentpageid + MidS + ".mp4";
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
            Toast.makeText(VideoA.this, R.string.check_conn, Toast.LENGTH_SHORT).show();
    }

    private void setVideoAreaSize(final String VIDEO_URL) {
        mVideoLayout.post(new Runnable() {
            @Override
            public void run() {
                int width = mVideoLayout.getWidth();
                cachedHeight = (int) (width * 405f / 720f);
//                cachedHeight = (int) (width * 3f / 4f);
//                cachedHeight = (int) (width * 9f / 16f);
                ViewGroup.LayoutParams videoLayoutParams = mVideoLayout.getLayoutParams();
                videoLayoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
                videoLayoutParams.height = cachedHeight;
                mVideoLayout.setLayoutParams(videoLayoutParams);
                mVideoView.setVideoPath(VIDEO_URL);
                mVideoView.requestFocus();
                mVideoView.start();
                dialog.dismiss();
            }
        });
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(SEEK_POSITION_KEY, mSeekPosition);
    }

    @Override
    protected void onRestoreInstanceState(Bundle outState) {
        super.onRestoreInstanceState(outState);
        mSeekPosition = outState.getInt(SEEK_POSITION_KEY);
    }


    @Override
    public void onScaleChange(boolean isFullscreen) {
        this.isFullscreen = isFullscreen;
        if (isFullscreen) {
            ViewGroup.LayoutParams layoutParams = mVideoLayout.getLayoutParams();
            layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
            layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT;
            mVideoLayout.setLayoutParams(layoutParams);

        } else {
            ViewGroup.LayoutParams layoutParams = mVideoLayout.getLayoutParams();
            layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
            layoutParams.height = this.cachedHeight;
            mVideoLayout.setLayoutParams(layoutParams);
        }

        switchTitleBar(!isFullscreen);
    }

    private void switchTitleBar(boolean show) {
        androidx.appcompat.app.ActionBar supportActionBar = getSupportActionBar();
        if (supportActionBar != null) {
            if (show) {
                supportActionBar.show();
            } else {
                supportActionBar.hide();
            }
        }
    }

    @Override
    public void onPause(MediaPlayer mediaPlayer) {
    }

    @Override
    public void onStart(MediaPlayer mediaPlayer) {
    }

    @Override
    public void onBufferingStart(MediaPlayer mediaPlayer) {
        Toast.makeText(this, R.string.loading, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onBufferingEnd(MediaPlayer mediaPlayer) {
    }

    @Override
    public void onBackPressed() {
        if (this.isFullscreen) {
            mVideoView.setFullscreen(false);
        } else {
            super.onBackPressed();
        }
    }

    public void goP(View view) {
        Intent intent = new Intent(this,Profile.class);
        intent.putExtra("idP",fromS);
        startActivity(intent);
    }
}
