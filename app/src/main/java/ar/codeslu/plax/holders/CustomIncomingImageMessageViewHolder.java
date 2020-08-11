package ar.codeslu.plax.holders;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.net.Uri;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;
import com.stfalcon.chatkit.link.AutoLinkMode;
import com.stfalcon.chatkit.link.AutoLinkOnClickListener;
import com.stfalcon.chatkit.link.AutoLinkTextView;
import com.stfalcon.chatkit.messages.MessageHolders;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import ar.codeslu.plax.Contacts;
import ar.codeslu.plax.R;
import ar.codeslu.plax.custom.MessageSelectD;
import ar.codeslu.plax.custom.ReactCustom;
import ar.codeslu.plax.global.Global;
import ar.codeslu.plax.mediachat.Photoa;

import com.stfalcon.chatkit.me.Message;

public class CustomIncomingImageMessageViewHolder
        extends MessageHolders.IncomingImageMessageViewHolder<Message> {

    private View onlineIndicator;
    RoundedImageView userava;
    ImageView forward, call;
    LinearLayout replyb;
    private AutoLinkTextView replyText;
    public CustomIncomingImageMessageViewHolder(View itemView, Object payload) {
        super(itemView, payload);
    }

    @Override
    public void onBind(final Message message) {
        super.onBind(message);
        ////reply

        replyb = itemView.findViewById(R.id.replyb);
        replyText = itemView.findViewById(R.id.replytext);

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
        userava = itemView.findViewById(R.id.messAva);

        forward = itemView.findViewById(R.id.forward);
        call = itemView.findViewById(R.id.call);

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


        if (message.isChat()) {
            if (String.valueOf(Global.currAva).equals("no")) {
                Picasso.get()
                        .load(R.drawable.profile)
                        .placeholder(R.drawable.placeholder_gray).error(R.drawable.errorimg)

                        .into(userava);
            } else {
                Picasso.get()
                        .load(Global.currAva)
                        .placeholder(R.drawable.placeholder_gray).error(R.drawable.errorimg)

                        .into(userava);
            }
        } else {
            if (Global.currGUsersAva.size() > 0 && Global.currGUsers.size() > 0) {
                if (message.getAvatar().equals("no")) {
                    Picasso.get()
                            .load(R.drawable.profile)
                            .placeholder(R.drawable.placeholder_gray).error(R.drawable.errorimg)

                            .into(userava);
                } else {
                    userava.setImageResource(0);
                    Picasso.get()
                            .load(message.getAvatar())
                            .placeholder(R.drawable.placeholder_gray).error(R.drawable.errorimg)

                            .into(userava);
                }
            }
        }
        //react
        ImageView react = itemView.findViewById(R.id.react);

        if (message.isDeleted() || !message.isChat()) {
            react.setVisibility(View.GONE);
        } else {
            if (!Global.currblocked && !Global.blockedLocal)
                react.setVisibility(View.VISIBLE);
            else
                react.setVisibility(View.GONE);
        }

        DisplayMetrics displaymetrics = new DisplayMetrics();
        Global.chatactivity.getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        int imagewidth = (int) Math.round(displaymetrics.widthPixels * 0.68);
        int imageheight = (int) Math.round(imagewidth * 0.6);
        image.getLayoutParams().width = imagewidth;
        image.getLayoutParams().height = imageheight;

        int profileWH = (int) Math.round(displaymetrics.widthPixels * 0.11);
        userava.getLayoutParams().width = profileWH;
        userava.getLayoutParams().height = profileWH;

        int reactWH = (int) Math.round(displaymetrics.widthPixels * 0.069);
        react.getLayoutParams().width = reactWH;
        react.getLayoutParams().height = reactWH;


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

        Date date = message.getCreatedAt();
        DateFormat format = new SimpleDateFormat("hh:mm aa");
        String timee = format.format(date);
        time.setText("  " + timee);
        time.setTextSize(10);
        time.setTextColor(Global.conA.getResources().getColor(R.color.mess_time));
        //dialog on long select
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
        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!message.getImageUrl().contains(".png")) {

                    Dexter.withActivity(Global.chatactivity)
                            .withPermissions(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.WAKE_LOCK)
                            .withListener(new MultiplePermissionsListener() {
                                @Override
                                public void onPermissionsChecked(MultiplePermissionsReport report) {

                                    if (report.areAllPermissionsGranted()) {
                                        Intent intent = new Intent(Global.conA, Photoa.class);
                                        intent.putExtra("url", message.getImageUrl());
                                        intent.putExtra("from", message.getId());
                                        intent.putExtra("Mid", message.getMessid());
                                        intent.putExtra("ava", Global.currAva);
                                        intent.putExtra("name", Global.currname);
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
            }
        });


    }
}