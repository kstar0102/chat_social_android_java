package com.codeslutest.plax.holders;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioDeviceInfo;
import android.media.AudioManager;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;

import androidx.core.content.ContextCompat;

import android.os.PowerManager;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.downloader.Error;
import com.downloader.OnCancelListener;
import com.downloader.OnDownloadListener;
import com.downloader.OnPauseListener;
import com.downloader.OnProgressListener;
import com.downloader.OnStartOrResumeListener;
import com.downloader.PRDownloader;
import com.downloader.Progress;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.squareup.picasso.Picasso;
import com.stfalcon.chatkit.link.AutoLinkMode;
import com.stfalcon.chatkit.link.AutoLinkOnClickListener;
import com.stfalcon.chatkit.link.AutoLinkTextView;
import com.stfalcon.chatkit.messages.MessageHolders;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import com.codeslutest.plax.Map;
import com.codeslutest.plax.R;
import com.codeslutest.plax.custom.MessageSelectD;
import com.codeslutest.plax.global.Global;

import com.codeslutest.plax.mediachat.VideoA;
import nl.changer.audiowife.AudioWife;

import com.stfalcon.chatkit.me.Message;
import com.makeramen.roundedimageview.RoundedImageView;

import static android.content.Context.POWER_SERVICE;


/**
 * Created by CodeSlu on 03/02/19.
 */

public class IncomeOther
        extends MessageHolders.BaseIncomingMessageViewHolder<Message> implements SensorEventListener {

    public IncomeOther(View itemView, Object payload) {
        super(itemView, payload);
    }

    boolean playb = false;
    //Shared pref
    SharedPreferences preferences;
    SharedPreferences.Editor editor;
    //view
    ImageView play;
    //download
    String fileName;
    private static String url, lat, lng;
    private String location[];
    //play new
    private LinearLayout lyFullV, bubble;
    private ImageView mPlayMedia, mPauseMedia;
    private SeekBar mMediaSeekBar;
    private TextView mRunTime, duration, mTotalTime;
    private RelativeLayout jzvdStd;
    private RoundedImageView image, map;
    RoundedImageView userava;
    private String btnid;
    private ProgressBar wait;
    private SensorManager mSensorManager;
    private Sensor mProximity;
    private static final int SENSOR_SENSITIVITY = 4;
    ImageView forward, call;
    LinearLayout replyb;
    private AutoLinkTextView replyText;
    private AudioManager m_amAudioManager;
    @Override
    public void onBind(final Message message) {
        super.onBind(message);
        forward = itemView.findViewById(R.id.forward);
        call = itemView.findViewById(R.id.call);

        ////reply

        replyb = itemView.findViewById(R.id.replyb);
        replyText = itemView.findViewById(R.id.replytext);

        replyb.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                MessageSelectD cdd = new MessageSelectD(Global.chatactivity, message, Global.currFid, 1, getAdapterPosition());
                cdd.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                cdd.getWindow().getAttributes().windowAnimations = R.style.CustomDialogAnimation;
                cdd.show();
                return true;
            }
        });
        try {
            if(!message.getReply().isEmpty() && !message.isDeleted())
            {
                replyb.setVisibility(View.VISIBLE);
                replyText.addAutoLinkMode(
                        AutoLinkMode.MODE_PHONE,
                        AutoLinkMode.MODE_URL, AutoLinkMode.MODE_EMAIL);
                replyText.enableUnderLine();
                replyText.setPhoneModeColor(ContextCompat.getColor(Global.conA, R.color.white));
                replyText.setUrlModeColor(ContextCompat.getColor(Global.conA, R.color.white));
                replyText.setEmailModeColor(ContextCompat.getColor(Global.conA, R.color.white));
                replyText.setSelectedStateColor(ContextCompat.getColor(Global.conA, R.color.white));
                if (message.getReply() != null) {
                    replyText.setAutoLinkText(message.getReply());
                    replyText.setAutoLinkOnClickListener(new AutoLinkOnClickListener() {
                        @Override
                        public void onAutoLinkTextClick(AutoLinkMode autoLinkMode, String matchedText) {
                            switch (autoLinkMode) {
                                case MODE_URL:
                                    if (matchedText.toLowerCase().startsWith("w"))
                                        matchedText = "http://" + matchedText;

                                    Intent intent = new Intent(Intent.ACTION_VIEW);
                                    intent.setData(Uri.parse(matchedText));
                                    String title = matchedText;
                                    Intent chooser = Intent.createChooser(intent, title);
                                    Global.conA.startActivity(chooser);
                                    break;
                                case MODE_PHONE:

                                    String finalMatchedText = matchedText;
                                    Dexter.withActivity(Global.chatactivity)
                                            .withPermissions(Manifest.permission.CALL_PHONE, Manifest.permission.READ_PHONE_STATE)
                                            .withListener(new MultiplePermissionsListener() {
                                                @Override
                                                public void onPermissionsChecked(MultiplePermissionsReport report) {

                                                    if (report.areAllPermissionsGranted()) {
                                                        Intent intent2 = new Intent(Intent.ACTION_DIAL);
                                                        intent2.setData(Uri.parse("tel:" + finalMatchedText));
                                                        Global.conA.startActivity(intent2);
                                                    } else
                                                        Toast.makeText(Global.conA, Global.conA.getString(R.string.acc_per), Toast.LENGTH_SHORT).show();


                                                }

                                                @Override
                                                public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {

                                                    token.continuePermissionRequest();

                                                }
                                            }).check();

                                    break;
                                case MODE_EMAIL:
                                    final Intent emailIntent = new Intent(Intent.ACTION_VIEW);
                                    emailIntent.setData(Uri.parse("mailto:"));
                                    emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[]{matchedText});
                                    try {
                                        Global.conA.startActivity(emailIntent);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                    break;
                            }


                        }
                    });
                }
            }
            else
            {
                replyb.setVisibility(View.GONE);

            }
        }
        catch (NullPointerException e)
        {
            replyb.setVisibility(View.GONE);

        }

        ////

        if (!message.isDeleted()) {
            if (message.isCall())
                call.setVisibility(View.VISIBLE);
            else
                call.setVisibility(View.GONE);

            if (message.isForw())
                forward.setVisibility(View.VISIBLE);
            else
                forward.setVisibility(View.GONE);

        } else {
            forward.setVisibility(View.GONE);
            call.setVisibility(View.GONE);

        }

        mSensorManager = (SensorManager) Global.conA.getSystemService(Context.SENSOR_SERVICE);
        mProximity = mSensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);

        mSensorManager
                .registerListener(this, mProximity, SensorManager.SENSOR_DELAY_NORMAL);


        userava = itemView.findViewById(R.id.messAva);
        if (message.isChat()) {
            if (String.valueOf(Global.currAva).equals("no")) {
                Picasso.get()
                        .load(R.drawable.profile)
                        .placeholder(R.drawable.placeholder_gray) .error(R.drawable.errorimg)

                        .into(userava);
            } else {
                Picasso.get()
                        .load(Global.currAva)
                        .placeholder(R.drawable.placeholder_gray) .error(R.drawable.errorimg)

                        .into(userava);
            }
        } else {
            if (Global.currGUsersAva.size() > 0 && Global.currGUsers.size() > 0) {

                if (message.getAvatar().equals("no")) {
                    Picasso.get()
                            .load(R.drawable.profile)
                            .placeholder(R.drawable.placeholder_gray) .error(R.drawable.errorimg)

                            .into(userava);
                } else {
                    userava.setImageResource(0);
                    Picasso.get()
                            .load(message.getAvatar())
                            .placeholder(R.drawable.placeholder_gray) .error(R.drawable.errorimg)

                            .into(userava);
                }
            }
        }

        //react
//        ImageView react = itemView.findViewById(R.id.react);
//
//        if (message.isDeleted() || !message.isChat()) {
//            react.setVisibility(View.GONE);
//        } else {
//            if (!Global.currblocked && !Global.blockedLocal)
//                react.setVisibility(View.VISIBLE);
//            else
//                react.setVisibility(View.GONE);
//        }
//
//        switch (message.getReact()) {
//            case "like":
//                react.setImageDrawable(Global.conA.getResources().getDrawable(R.drawable.like));
//                break;
//            case "funny":
//                react.setImageDrawable(Global.conA.getResources().getDrawable(R.drawable.funny));
//                break;
//            case "love":
//                react.setImageDrawable(Global.conA.getResources().getDrawable(R.drawable.love));
//                break;
//            case "sad":
//                react.setImageDrawable(Global.conA.getResources().getDrawable(R.drawable.sad));
//                break;
//            case "angry":
//                react.setImageDrawable(Global.conA.getResources().getDrawable(R.drawable.angry));
//                break;
//            case "no":
//                react.setImageDrawable(Global.conA.getResources().getDrawable(R.drawable.emoji_blue));
//                break;
//        }
//
//        react.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if (Global.check_int(Global.conA)) {
//                    String text = "";
//                    try {
//                        if (message.getText() != null)
//                            text = message.getText();
//
//
//                    } catch (NullPointerException e) {
//                        text = "";
//                    }
//
//                    ReactCustom cdd = new ReactCustom(Global.chatactivity, message.getId(), message.getMessid(), text);
//                    cdd.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//                    cdd.getWindow().getAttributes().windowAnimations = R.style.CustomDialogAnimation;
//                    cdd.show();
//                } else
//                    Toast.makeText(Global.conA, Global.conA.getResources().getString(R.string.check_int), Toast.LENGTH_SHORT).show();
//            }
//        });
//        react.setOnLongClickListener(new View.OnLongClickListener() {
//            @Override
//            public boolean onLongClick(View view) {
//
//                if (Global.check_int(Global.conA)) {
//                    String text = "";
//                    try {
//                        if (message.getText() != null)
//                            text = message.getText();
//
//
//                    } catch (NullPointerException e) {
//                        text = "";
//                    }
//
//                    ReactCustom cdd = new ReactCustom(Global.chatactivity, message.getId(), message.getMessid(), text);
//                    cdd.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//                    cdd.getWindow().getAttributes().windowAnimations = R.style.CustomDialogAnimation;
//                    cdd.show();
//                } else
//                    Toast.makeText(Global.conA, Global.conA.getResources().getString(R.string.check_int), Toast.LENGTH_SHORT).show();
//
//                return true;
//            }
//        });

        //view init
        mPlayMedia = itemView.findViewById(R.id.play);
        mPauseMedia = itemView.findViewById(R.id.pause);
        mMediaSeekBar = (SeekBar) itemView.findViewById(R.id.media_seekbar);
        mRunTime = (TextView) itemView.findViewById(R.id.run_time);
        mTotalTime = (TextView) itemView.findViewById(R.id.total_time);
        play = itemView.findViewById(R.id.playV);
        lyFullV = itemView.findViewById(R.id.playerFull);
        jzvdStd = (RelativeLayout) itemView.findViewById(R.id.videoView);
        image = itemView.findViewById(R.id.image);
        map = itemView.findViewById(R.id.map);
        wait = itemView.findViewById(R.id.wait);
        bubble = itemView.findViewById(R.id.bubblely);
        duration = itemView.findViewById(R.id.recordduration);

        //resize
        DisplayMetrics displaymetrics = new DisplayMetrics();
        Global.chatactivity.getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        int imagewidth = (int) Math.round(displaymetrics.widthPixels * 0.68);
        int imageheight = (int) Math.round(imagewidth * 0.6);
        image.getLayoutParams().width = imagewidth;
        image.getLayoutParams().height = imageheight;
        map.getLayoutParams().width = imagewidth;
        map.getLayoutParams().height = imageheight;


        //dark init
//        if (Global.DARKSTATE) {
//            play.setImageResource(R.drawable.download_w);
//            mPlayMedia.setImageResource(R.drawable.play_w);
//            mPauseMedia.setImageResource(R.drawable.pause_w);
//            mRunTime.setTextColor(Global.conA.getResources().getColor(R.color.white));
//            mTotalTime.setTextColor(Global.conA.getResources().getColor(R.color.white));
//            duration.setTextColor(Global.conA.getResources().getColor(R.color.white));
//            lyFullV.setBackground(ContextCompat.getDrawable(Global.conA, R.drawable.shape_incoming_message_d));
//            bubble.setBackground(ContextCompat.getDrawable(Global.conA, R.drawable.shape_incoming_message_d));
//        } else {
            play.setImageResource(R.drawable.download_b);
            mPlayMedia.setImageResource(R.drawable.voice_play_icon);
            mPauseMedia.setImageResource(R.drawable.pause_b);
            mRunTime.setTextColor(Global.conA.getResources().getColor(R.color.black));
            mTotalTime.setTextColor(Global.conA.getResources().getColor(R.color.black));
            duration.setTextColor(Global.conA.getResources().getColor(R.color.black));
            lyFullV.setBackground(ContextCompat.getDrawable(Global.conA, R.drawable.shape_incoming_message));
            bubble.setBackground(ContextCompat.getDrawable(Global.conA, R.drawable.shape_incoming_message));

//        }

        Date date = message.getCreatedAt();
        DateFormat format = new SimpleDateFormat("hh:mm aa");
        String timee = format.format(date);
        time.setText("  " + timee);
        time.setTextSize(10);


        play.setVisibility(View.GONE);
        jzvdStd.setVisibility(View.GONE);
        lyFullV.setVisibility(View.GONE);
        map.setVisibility(View.GONE);
        if (message.getType().equals("voice")) {
            lyFullV.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    MessageSelectD cdd = new MessageSelectD(Global.chatactivity, message, Global.currFid, 1, getAdapterPosition());
                    cdd.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    cdd.getWindow().getAttributes().windowAnimations = R.style.CustomDialogAnimation;
                    cdd.show();
                    return true;
                }
            });
            map.setVisibility(View.GONE);
            bubble.setVisibility(View.GONE);
            Global.DEFAULT_STATUE = message.getVoice().getUrl();
            bubble.setBackgroundResource(0);
            play.setVisibility(View.GONE);
            lyFullV.setVisibility(View.VISIBLE);
            duration.setVisibility(View.GONE);
            jzvdStd.setVisibility(View.GONE);
            mTotalTime.setText(message.getVoice().getDuration());
            mPlayMedia.setTag(message.getMessid());
            wait.setVisibility(View.GONE);

            //Shared pref
            preferences = Global.conA.getSharedPreferences("voice", Context.MODE_PRIVATE);
            editor = preferences.edit();
            mPlayMedia.setFocusableInTouchMode(false);
            mPlayMedia.setFocusable(false);
            mPauseMedia.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
            mPlayMedia.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    btnid = (String) view.getTag();
                    Dexter.withActivity(Global.chatactivity)
                            .withPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE)
                            .withListener(new MultiplePermissionsListener() {
                                @Override
                                public void onPermissionsChecked(MultiplePermissionsReport report) {
                                    if (report.areAllPermissionsGranted()) {
                                        mPauseMedia.setVisibility(View.GONE);
                                        mPlayMedia.setVisibility(View.GONE);
                                        wait.setVisibility(View.VISIBLE);
                                        SharedPreferences preferences = Global.conA.getSharedPreferences("voice", Context.MODE_PRIVATE);
                                        String pathL = preferences.getString("voice_" + message.getVoice().getUrl(), "not");
                                        if (Global.check_int(Global.conA)) {
                                            File file = new File(pathL);
                                            if (pathL.equals("not") || !file.exists()) {
                                                String url = message.getVoice().getUrl();
                                                pathL = Environment.getExternalStorageDirectory().getAbsolutePath().toString()
                                                        + "/" + Global.conA.getResources().getString(R.string.app_name) + "/VoiceNotes/";
                                                fileName = System.currentTimeMillis() + "VN" + ".m4a";
                                                final String finalPathL = pathL;
                                                int downloadId = PRDownloader.download(url, pathL, fileName)
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
                                                                editor.putString("voice_" + message.getVoice().getUrl(), finalPathL + fileName);
                                                                editor.apply();
                                                                play(finalPathL + fileName);
                                                            }

                                                            @Override
                                                            public void onError(Error error) {

                                                                Toast.makeText(Global.conA, Global.conA.getResources().getString(R.string.cannot_play), Toast.LENGTH_SHORT).show();

                                                            }

                                                        });
                                            } else {
                                                play(pathL);
                                            }

                                        } else {
                                            if (pathL.equals("not")) {
                                                mPauseMedia.setVisibility(View.GONE);
                                                mPlayMedia.setVisibility(View.VISIBLE);
                                                wait.setVisibility(View.GONE);
                                                Toast.makeText(Global.conA, Global.conA.getResources().getString(R.string.cannot_play), Toast.LENGTH_SHORT).show();
                                            } else {
                                                play(pathL);
                                            }
                                        }

                                    }
                                }

                                @Override
                                public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {

                                    token.continuePermissionRequest();

                                }
                            }).check();
                }
            });

        } else if (message.getType().equals("file")) {
            //dark init
            if (Global.DARKSTATE)
                bubble.setBackground(ContextCompat.getDrawable(Global.conA, R.drawable.shape_incoming_message_d));
            else
                bubble.setBackground(ContextCompat.getDrawable(Global.conA, R.drawable.shape_incoming_message));


            map.setVisibility(View.GONE);
            bubble.setVisibility(View.VISIBLE);
            lyFullV.setVisibility(View.GONE);
            jzvdStd.setVisibility(View.GONE);
            play.setVisibility(View.VISIBLE);
            String filename = message.getFile().getFilename();

            if (filename.length() > Global.FileName_LENTH)
                fileName = fileName.substring(0, Global.STATUE_LENTH) + "...";

            duration.setVisibility(View.VISIBLE);
            duration.setText(filename);
            duration.setTextSize(13);
            LinearLayout ly = itemView.findViewById(R.id.lyV);
            ly.getLayoutParams().width = ViewGroup.LayoutParams.WRAP_CONTENT;
            ly.requestLayout();
            play = itemView.findViewById(R.id.playV);
            //Shared pref
            preferences = Global.conA.getSharedPreferences("file", Context.MODE_PRIVATE);
            editor = preferences.edit();
            //dark init
            if (Global.DARKSTATE)
                play.setImageResource(R.drawable.download_w);
            else
                play.setImageResource(R.drawable.download_b);

            play.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Dexter.withActivity(Global.chatactivity)
                            .withPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE)
                            .withListener(new MultiplePermissionsListener() {
                                @Override
                                public void onPermissionsChecked(MultiplePermissionsReport report) {

                                    if (report.areAllPermissionsGranted()) {
                                        SharedPreferences preferences = Global.conA.getSharedPreferences("file", Context.MODE_PRIVATE);
                                        String pathL = preferences.getString("file_" + message.getFile().getUrl(), "not");
                                        if (Global.check_int(Global.conA)) {
                                            File file = new File(pathL);
                                            if (pathL.equals("not") || !file.exists()) {
                                                String url = message.getFile().getUrl();
                                                pathL = Environment.getExternalStorageDirectory().getAbsolutePath().toString()
                                                        + "/" + Global.conA.getResources().getString(R.string.app_name) + "/Files/";
                                                fileName = message.getFile().getFilename();
                                                final String finalPathL = pathL;
                                                int downloadId = PRDownloader.download(url, pathL, fileName)
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
                                                                editor.putString("file_" + message.getFile().getUrl(), finalPathL + fileName);
                                                                editor.apply();
                                                                //  here is open local
                                                                Toast.makeText(Global.conA, Global.conA.getResources().getString(R.string.downloadcomplete), Toast.LENGTH_SHORT).show();

                                                            }

                                                            @Override
                                                            public void onError(Error error) {
                                                                Toast.makeText(Global.conA, Global.conA.getResources().getString(R.string.cannot_open), Toast.LENGTH_SHORT).show();

                                                            }

                                                        });
                                            } else {
                                                //  here is open local

                                            }

                                        } else {
                                            if (pathL.equals("not")) {
                                                Toast.makeText(Global.conA, Global.conA.getResources().getString(R.string.cannot_open), Toast.LENGTH_SHORT).show();
                                            } else {
                                                //  here is open local
                                            }
                                        }
                                    } else
                                        Toast.makeText(Global.conA, Global.conA.getString(R.string.acc_per), Toast.LENGTH_SHORT).show();


                                }

                                @Override
                                public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {

                                    token.continuePermissionRequest();

                                }
                            }).check();

                }
            });
            bubble.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    MessageSelectD cdd = new MessageSelectD(Global.chatactivity, message, Global.currFid, 1, getAdapterPosition());
                    cdd.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    cdd.getWindow().getAttributes().windowAnimations = R.style.CustomDialogAnimation;
                    cdd.show();
                    return true;
                }
            });
        } else if (message.getType().equals("video")) {
            map.setVisibility(View.GONE);
            bubble.setVisibility(View.GONE);
            bubble.setBackgroundResource(0);
            lyFullV.setVisibility(View.GONE);
            jzvdStd.setVisibility(View.VISIBLE);
            play.setVisibility(View.GONE);
            duration.setVisibility(View.GONE);
            Picasso.get()
                    .load(message.getVideo().getThumb())
                    .placeholder(R.drawable.placeholder_gray) .error(R.drawable.errorimg)

                    .into(image);
            image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Dexter.withActivity(Global.chatactivity)
                            .withPermissions(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.WAKE_LOCK)
                            .withListener(new MultiplePermissionsListener() {
                                @Override
                                public void onPermissionsChecked(MultiplePermissionsReport report) {

                                    if (report.areAllPermissionsGranted()) {
                                        Intent intent = new Intent(Global.conA, VideoA.class);
                                        intent.putExtra("Mid", message.getMessid());
                                        intent.putExtra("ava", Global.currAva);
                                        intent.putExtra("name", Global.currname);
                                        intent.putExtra("url", message.getVideo().getUrl());
                                        intent.putExtra("from", message.getId());
                                        intent.putExtra("duration", message.getVideo().getDuration());
                                        Global.conA.startActivity(intent);
                                    } else
                                        Toast.makeText(Global.conA, Global.conA.getString(R.string.acc_per), Toast.LENGTH_SHORT).show();


                                }

                                @Override
                                public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {

                                    token.continuePermissionRequest();

                                }
                            }).check();
                }
            });

            image.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    MessageSelectD cdd = new MessageSelectD(Global.chatactivity, message, Global.currFid, 1, getAdapterPosition());
                    cdd.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    cdd.getWindow().getAttributes().windowAnimations = R.style.CustomDialogAnimation;
                    cdd.show();
                    return true;
                }
            });
        } else if (message.getType().equals("map")) {
            jzvdStd.setVisibility(View.GONE);
            bubble.setVisibility(View.GONE);
            play.setVisibility(View.GONE);
            duration.setVisibility(View.GONE);
            lyFullV.setVisibility(View.GONE);
            map.setVisibility(View.VISIBLE);
            location = message.getMap().getLocation().split(",");

            if (location.length == 2) {
                lat = location[0];
                lng = location[1];
            }

            url = "https://api.mapbox.com/styles/v1/" + Global.conA.getResources().getString(R.string.map_username) +
                    "/" + Global.conA.getResources().getString(R.string.map_style)+
                    "/static/" + Double.parseDouble(lat) +
                    "," + Double.parseDouble(lng)+
                    "," +
                    "12.0/200x200@2x?access_token=" + Global.conA.getResources().getString(R.string.mapbox_access_token);

            Picasso.get()
                    .load(url)
                    .placeholder(R.drawable.placeholder_gray) .error(R.drawable.errorimg)
                    .into(map);

            map.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    location = message.getMap().getLocation().split(",");
                    if (location.length == 2) {
                        lat = location[0];
                        lng = location[1];
                        Intent intent = new Intent(Global.conA, Map.class);
                        intent.putExtra("lat", lat);
                        intent.putExtra("lng", lng);
                        intent.putExtra("from", message.getId());
                        intent.putExtra("Mid", message.getMessid());
                        intent.putExtra("ava", Global.currAva);
                        intent.putExtra("name", Global.currname);
                        Global.conA.startActivity(intent);
                    }

                }
            });
            map.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    MessageSelectD cdd = new MessageSelectD(Global.chatactivity, message, Global.currFid, 0, getAdapterPosition());
                    cdd.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    cdd.getWindow().getAttributes().windowAnimations = R.style.CustomDialogAnimation;
                    cdd.show();
                    return true;
                }
            });
        }

        LinearLayout ly = itemView.findViewById(R.id.lyM);
        ly.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                MessageSelectD cdd = new MessageSelectD(Global.chatactivity, message, Global.currFid, 1, getAdapterPosition());
                cdd.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                cdd.getWindow().getAttributes().windowAnimations = R.style.CustomDialogAnimation;
                cdd.show();
                return true;
            }
        });
    }

    private void play(String file) {


        Dexter.withActivity(Global.chatactivity)
                .withPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {

                        if (report.areAllPermissionsGranted()) {

                            if (Global.audiolist.size() > 0)
                                Global.audiolist.get(Global.audiolist.size() - 1).pause();


                            changeScreenBrightness();

                            if (checkdevices(Global.conA)){

                                m_amAudioManager = (AudioManager)Global.conA.getSystemService(Context.AUDIO_SERVICE);
                                m_amAudioManager.setMode(AudioManager.MODE_IN_CALL);
                                m_amAudioManager.setSpeakerphoneOn(false);
                            }else
                            {
                                m_amAudioManager = (AudioManager)Global.conA.getSystemService(Context.AUDIO_SERVICE);
                                m_amAudioManager.setMode(AudioManager.MODE_NORMAL);
                                m_amAudioManager.setSpeakerphoneOn(true);
                            }

                            AudioWife audioWife = new AudioWife();


                            mPauseMedia.setVisibility(View.VISIBLE);
                            mPlayMedia.setVisibility(View.GONE);
                            wait.setVisibility(View.GONE);

                            audioWife.init(Global.conA, Uri.parse(file))
                                    .setPlayView(mPlayMedia)
                                    .setPauseView(mPauseMedia)
                                    .setSeekBar(mMediaSeekBar)
                                    .setRuntimeView(mRunTime)
                                    .setTotalTimeView(mTotalTime);
                            Global.audiolist.add(audioWife);
                            Global.btnid.add(btnid);
                            Global.audiolist.get(Global.audiolist.size() - 1).play();

                            Global.audiolist.get(Global.audiolist.size() - 1).addOnPlayClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    String position = (String) v.getTag();
                                    changeScreenBrightness();


                                    for (int i = 0; i < Global.audiolist.size(); i++) {
                                        if (i != Global.btnid.indexOf(position))
                                            Global.audiolist.get(i).pause();
                                    }


                                }
                            });
                            Global.audiolist.get(Global.audiolist.size() - 1).addOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                                @Override
                                public void onCompletion(MediaPlayer mp) {
                                    try {
                                        if ( Global.wl!= null ) {
                                            if ( Global.wl.isHeld() ) {
                                                Global.wl.release();
                                            }
                                            Global.wl= null;
                                        }
                                    }
                                    catch (NullPointerException e)
                                    {

                                    }
                                }
                            });

                            Global.audiolist.get(Global.audiolist.size() - 1).addOnPauseClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    try {
                                        if ( Global.wl!= null ) {
                                            if ( Global.wl.isHeld() ) {
                                                Global.wl.release();
                                            }
                                            Global.wl= null;
                                        }
                                    }
                                    catch (NullPointerException e)
                                    {

                                    }
                                }
                            });



                        } else {
                            Toast.makeText(Global.conA, Global.conA.getString(R.string.acc_per), Toast.LENGTH_SHORT).show();
                            mPauseMedia.setVisibility(View.GONE);
                            mPlayMedia.setVisibility(View.VISIBLE);
                            wait.setVisibility(View.GONE);
                        }


                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {

                        token.continuePermissionRequest();

                    }
                }).check();
    }

    public static Bitmap framgetter(String videoPath) throws Throwable {
        Bitmap bitmap = null;
        MediaMetadataRetriever mediaMetadataRetriever = null;
        try {
            mediaMetadataRetriever = new MediaMetadataRetriever();
            if (Build.VERSION.SDK_INT >= 14)
                mediaMetadataRetriever.setDataSource(videoPath, new HashMap<String, String>());
            else
                mediaMetadataRetriever.setDataSource(videoPath);
            //   mediaMetadataRetriever.setDataSource(videoPath);
            bitmap = mediaMetadataRetriever.getFrameAtTime();
        } catch (Exception e) {
            e.printStackTrace();
            throw new Throwable("Exception" + e.getMessage());

        } finally {
            if (mediaMetadataRetriever != null) {
                mediaMetadataRetriever.release();
            }
        }
        return bitmap;
    }

    private void changeScreenBrightness() {
        //Set screen brightness
        Global.pm = (PowerManager) Global.conA.getSystemService(POWER_SERVICE);
        Global.wl = Global.pm.newWakeLock(PowerManager.PROXIMITY_SCREEN_OFF_WAKE_LOCK, "Dim/Light:");
        Global.wl.acquire();
    }
    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.values[0] == 0) {
            AudioManager audioManager = (AudioManager) Global.conA.getSystemService(Context.AUDIO_SERVICE);
            audioManager.setMode(AudioManager.STREAM_MUSIC);
            audioManager.setSpeakerphoneOn(false);

        } else {
            AudioManager audioManager = (AudioManager) Global.conA.getSystemService(Context.AUDIO_SERVICE);
            audioManager.setMode(AudioManager.STREAM_MUSIC);
            audioManager.setSpeakerphoneOn(true);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
    private boolean checkdevices(Context con){


        AudioManager audioManager = (AudioManager) con.getSystemService(Context.AUDIO_SERVICE);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            AudioDeviceInfo[] adi = audioManager.getDevices(AudioManager.GET_DEVICES_OUTPUTS);
            for (AudioDeviceInfo device : adi) {
                if (device.getType() == AudioDeviceInfo.TYPE_BUILTIN_SPEAKER) {
                    return true;
                }
            }
            return  false;
        }else
        {
            if (audioManager.isWiredHeadsetOn() ||audioManager.isBluetoothScoOn()){

                return true;
            }else

                return false;
        }
    }
}
