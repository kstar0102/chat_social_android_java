package ar.codeslu.plax.holders;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.stfalcon.chatkit.messages.MessageHolders;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import ar.codeslu.plax.Map;
import ar.codeslu.plax.R;
import ar.codeslu.plax.custom.MessageSelectD;
import ar.codeslu.plax.custom.ReactCustom;
import ar.codeslu.plax.global.Global;

import ar.codeslu.plax.mediachat.Photoa;
import ar.codeslu.plax.mediachat.VideoA;
import nl.changer.audiowife.AudioWife;

import com.stfalcon.chatkit.me.Message;
import com.makeramen.roundedimageview.RoundedImageView;


/**
 * Created by mostafa on 03/02/19.
 */

public class IncomeOther
        extends MessageHolders.BaseIncomingMessageViewHolder<Message> {

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

    @Override
    public void onBind(final Message message) {
        super.onBind(message);
        //react
        ImageView react = itemView.findViewById(R.id.react);
        if (message.isDeleted()) {
            react.setVisibility(View.GONE);
        } else {
            react.setVisibility(View.VISIBLE);
        }
        switch (message.getReact()) {
            case "like":
                react.setImageDrawable(Global.conA.getResources().getDrawable(R.drawable.like));
                break;
            case "funny":
                react.setImageDrawable(Global.conA.getResources().getDrawable(R.drawable.funny));
                break;
            case "love":
                react.setImageDrawable(Global.conA.getResources().getDrawable(R.drawable.love));
                break;
            case "sad":
                react.setImageDrawable(Global.conA.getResources().getDrawable(R.drawable.sad));
                break;
            case "angry":
                react.setImageDrawable(Global.conA.getResources().getDrawable(R.drawable.angry));
                break;
            case "no":
                react.setImageDrawable(Global.conA.getResources().getDrawable(R.drawable.emoji_blue));
                break;
        }

        react.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Global.check_int(Global.conA)) {
                    String text = "";
                    try {
                        if (message.getText() != null)
                            text = message.getText();


                    } catch (NullPointerException e) {
                        text = "";
                    }

                    ReactCustom cdd = new ReactCustom(Global.chatactivity, message.getId(), message.getMessid(), text);
                    cdd.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    cdd.getWindow().getAttributes().windowAnimations = R.style.CustomDialogAnimation;
                    cdd.show();
                } else
                    Toast.makeText(Global.conA, Global.conA.getResources().getString(R.string.check_int), Toast.LENGTH_SHORT).show();
            }
        });
        react.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {

                if (Global.check_int(Global.conA)) {
                    String text = "";
                    try {
                        if (message.getText() != null)
                            text = message.getText();


                    } catch (NullPointerException e) {
                        text = "";
                    }

                    ReactCustom cdd = new ReactCustom(Global.chatactivity, message.getId(), message.getMessid(), text);
                    cdd.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    cdd.getWindow().getAttributes().windowAnimations = R.style.CustomDialogAnimation;
                    cdd.show();
                } else
                    Toast.makeText(Global.conA, Global.conA.getResources().getString(R.string.check_int), Toast.LENGTH_SHORT).show();

                return true;
            }
        });
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
        if (Global.DARKSTATE) {
            play.setImageResource(R.drawable.download_w);
            mPlayMedia.setImageResource(R.drawable.play_w);
            mPauseMedia.setImageResource(R.drawable.pause_w);
            mRunTime.setTextColor(Global.conA.getResources().getColor(R.color.white));
            mTotalTime.setTextColor(Global.conA.getResources().getColor(R.color.white));
            duration.setTextColor(Global.conA.getResources().getColor(R.color.white));
            lyFullV.setBackground(ContextCompat.getDrawable(Global.conA, R.drawable.shape_incoming_message_d));
            bubble.setBackground(ContextCompat.getDrawable(Global.conA, R.drawable.shape_incoming_message_d));
        } else {
            play.setImageResource(R.drawable.download_b);
            mPlayMedia.setImageResource(R.drawable.play_b);
            mPauseMedia.setImageResource(R.drawable.pause_b);
            mRunTime.setTextColor(Global.conA.getResources().getColor(R.color.black));
            mTotalTime.setTextColor(Global.conA.getResources().getColor(R.color.black));
            duration.setTextColor(Global.conA.getResources().getColor(R.color.black));
            lyFullV.setBackground(ContextCompat.getDrawable(Global.conA, R.drawable.shape_incoming_message));
            bubble.setBackground(ContextCompat.getDrawable(Global.conA, R.drawable.shape_incoming_message));

        }

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
            map.setVisibility(View.GONE);
            bubble.setVisibility(View.GONE);
            Global.DEFAULT_STATUE = message.getVoice().getUrl();
            bubble.setBackgroundResource(0);
            play.setVisibility(View.GONE);
            lyFullV.setVisibility(View.VISIBLE);
            duration.setVisibility(View.GONE);
            jzvdStd.setVisibility(View.GONE);
            mTotalTime.setText(message.getVoice().getDuration());
            //Shared pref
            preferences = Global.conA.getSharedPreferences("voice", Context.MODE_PRIVATE);
            editor = preferences.edit();
            mPlayMedia.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Dexter.withActivity(Global.chatactivity)
                            .withPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE)
                            .withListener(new MultiplePermissionsListener() {
                                @Override public void onPermissionsChecked(MultiplePermissionsReport report) {

                                    if(report.areAllPermissionsGranted())
                                    {
                                        SharedPreferences preferences = Global.conA.getSharedPreferences("voice", Context.MODE_PRIVATE);
                                        String pathL = preferences.getString("voice_" + message.getVoice().getUrl(), "not");
                                        if (Global.check_int(Global.conA)) {
                                            File file = new File(pathL);
                                            if (pathL.equals("not") || !file.exists()) {
                                                String url = message.getVoice().getUrl();
                                                pathL = Environment.getExternalStorageDirectory().getAbsolutePath().toString()
                                                        + "/" + Global.conA.getResources().getString(R.string.app_name) + "/Voice Notes/";
                                                fileName = message.getVoice().getUrl() + ".m4a";
                                                final String finalPathL = pathL;
                                                //     play(message.getVoice().getUrl());
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
                                                Toast.makeText(Global.conA, Global.conA.getResources().getString(R.string.cannot_play), Toast.LENGTH_SHORT).show();
                                            } else {
                                                play(pathL);
                                            }
                                        }

                                    }

                                    else
                                        Toast.makeText(Global.conA, Global.conA.getString(R.string.acc_per), Toast.LENGTH_SHORT).show();


                                }
                                @Override public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {

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
            duration.setVisibility(View.VISIBLE);
            duration.setText(message.getFile().getFilename());
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
                            .withPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE)
                            .withListener(new MultiplePermissionsListener() {
                                @Override public void onPermissionsChecked(MultiplePermissionsReport report) {

                                    if(report.areAllPermissionsGranted())
                                    {
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
                                    }

                                    else
                                        Toast.makeText(Global.conA, Global.conA.getString(R.string.acc_per), Toast.LENGTH_SHORT).show();


                                }
                                @Override public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {

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
                    .error(R.drawable.errorimg)
                    .placeholder(R.drawable.loading)
                    .into(image);
            image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Dexter.withActivity(Global.chatactivity)
                            .withPermissions(Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.WAKE_LOCK)
                            .withListener(new MultiplePermissionsListener() {
                                @Override public void onPermissionsChecked(MultiplePermissionsReport report) {

                                    if(report.areAllPermissionsGranted())
                                    {
                                        Intent intent = new Intent(Global.conA, VideoA.class);
                                        intent.putExtra("Mid", message.getMessid());
                                        intent.putExtra("ava", Global.currAva);
                                        intent.putExtra("name", Global.currname);
                                        intent.putExtra("url", message.getVideo().getUrl());
                                        intent.putExtra("from", message.getId());
                                        intent.putExtra("duration", message.getVideo().getDuration());
                                        Global.conA.startActivity(intent);
                                    }

                                    else
                                        Toast.makeText(Global.conA, Global.conA.getString(R.string.acc_per), Toast.LENGTH_SHORT).show();


                                }
                                @Override public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {

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
            lat = location[0];
            lng = location[1];

            url = "https://maps.googleapis.com/maps/api/staticmap?center=" + Double.parseDouble(lat) + "," + Double.parseDouble(lng) + "&zoom=15&size=300x300&maptype=roadmap&format=png&visual_refresh=true&key=" + Global.conA.getResources().getString(R.string.google_maps_key) + "&signature=BASE64_SIGNATURE";
            Picasso.get()
                    .load(url)
                    .error(R.drawable.errorimg)
                    .placeholder(R.drawable.loading)
                    .into(map);

            map.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(Global.conA, Map.class);
                    intent.putExtra("lat", lat);
                    intent.putExtra("lng", lng);
                    intent.putExtra("from", message.getId());
                    intent.putExtra("Mid", message.getMessid());
                    intent.putExtra("ava", Global.avaLocal);
                    intent.putExtra("name", Global.nameLocal);
                    Global.conA.startActivity(intent);

                }
            });
            map.setOnLongClickListener(new View.OnLongClickListener() {
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
                .withPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(new MultiplePermissionsListener() {
                    @Override public void onPermissionsChecked(MultiplePermissionsReport report) {

                        if(report.areAllPermissionsGranted())
                        {
                            AudioWife.getInstance()
                                    .init(Global.conA, Uri.parse(file))
                                    .setPlayView(mPlayMedia)
                                    .setPauseView(mPauseMedia)
                                    .setSeekBar(mMediaSeekBar)
                                    .setRuntimeView(mRunTime)
                                    .setTotalTimeView(mTotalTime);
                            //   AudioWife.getInstance().release();
                        }

                        else
                            Toast.makeText(Global.conA, Global.conA.getString(R.string.acc_per), Toast.LENGTH_SHORT).show();


                    }
                    @Override public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {

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
}
