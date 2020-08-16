package ar.codeslu.plax.holders;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Callback;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.stfalcon.chatkit.link.AutoLinkMode;
import com.stfalcon.chatkit.link.AutoLinkOnClickListener;
import com.stfalcon.chatkit.link.AutoLinkTextView;
import com.stfalcon.chatkit.messages.MessagesListAdapter;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import ar.codeslu.plax.R;
import ar.codeslu.plax.custom.MessageSelectD;
import ar.codeslu.plax.custom.ReactCustom;
import ar.codeslu.plax.global.Global;
import ar.codeslu.plax.mediachat.Photoa;

import com.stfalcon.chatkit.me.Message;

/**
 * Created by CodeSlu on 01/02/19.
 */

public class IncomeHolder extends MessagesListAdapter.IncomingMessageViewHolder<Message> {

    AutoLinkTextView autoLinkTextView;
    RoundedImageView userava;
    ImageView forward, call;
    LinearLayout replyb;
    private  AutoLinkTextView replyText;
    public IncomeHolder(View itemView, Object payload) {
        super(itemView);
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
        userava = itemView.findViewById(R.id.messAva);
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
                            .placeholder(R.drawable.placeholder_gray)
                            .error(R.drawable.errorimg)
                            .into(userava);

                } else {
                    userava.setImageResource(0);
                    Picasso.get()
                            .load(message.getAvatar())
                            //  .networkPolicy(NetworkPolicy.NO_CACHE).memoryPolicy(MemoryPolicy.NO_CACHE)
                            .placeholder(R.drawable.placeholder_gray).error(R.drawable.errorimg)
                            .into(userava);
                }
            }
        }

        //react
//        ImageView react = itemView.findViewById(R.id.react);


//        if (message.isDeleted() || !message.isChat()) {
//            react.setVisibility(View.GONE);
//        } else {
//            if (!Global.currblocked && !Global.blockedLocal)
//                react.setVisibility(View.VISIBLE);
//            else
//                react.setVisibility(View.GONE);
//        }

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

        autoLinkTextView = (AutoLinkTextView) itemView.findViewById(R.id.messageText);

        if (message.isDeleted()) {
            time.setVisibility(View.GONE);
            bubble.setBackground(ContextCompat.getDrawable(Global.conA, R.drawable.shape_incoming_deleted));
            text.setTextColor(Global.conA.getResources().getColor(R.color.white));

        } else {
            time.setVisibility(View.VISIBLE);

            //dark init
            if (Global.DARKSTATE) {
                autoLinkTextView.enableUnderLine();
                autoLinkTextView.setPhoneModeColor(ContextCompat.getColor(Global.conA, R.color.white));
                autoLinkTextView.setUrlModeColor(ContextCompat.getColor(Global.conA, R.color.white));
                autoLinkTextView.setEmailModeColor(ContextCompat.getColor(Global.conA, R.color.white));
                autoLinkTextView.setSelectedStateColor(ContextCompat.getColor(Global.conA, R.color.white));
                bubble.setBackground(ContextCompat.getDrawable(Global.conA, R.drawable.shape_incoming_message_d));
                text.setTextColor(Global.conA.getResources().getColor(R.color.white));
            } else {
                autoLinkTextView.enableUnderLine();
                autoLinkTextView.setPhoneModeColor(ContextCompat.getColor(Global.conA, R.color.black));
                autoLinkTextView.setUrlModeColor(ContextCompat.getColor(Global.conA, R.color.black));
                autoLinkTextView.setEmailModeColor(ContextCompat.getColor(Global.conA, R.color.black));
                autoLinkTextView.setSelectedStateColor(ContextCompat.getColor(Global.conA, R.color.black));
                bubble.setBackground(ContextCompat.getDrawable(Global.conA, R.drawable.shape_incoming_message));
                text.setTextColor(Global.conA.getResources().getColor(R.color.black));
            }
        }
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
        autoLinkTextView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                MessageSelectD cdd = new MessageSelectD(Global.chatactivity, message, Global.currFid, 1, getAdapterPosition());
                cdd.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                cdd.getWindow().getAttributes().windowAnimations = R.style.CustomDialogAnimation;
                cdd.requestWindowFeature(Window.FEATURE_NO_TITLE);
                WindowManager.LayoutParams abc= cdd.getWindow().getAttributes();
                abc.gravity = Gravity.BOTTOM | Gravity.RIGHT;
                abc.x = 20;   //x position
                abc.y = itemView.getScrollY() + 200;   //y position
                cdd.show();
                return true;
            }
        });


        autoLinkTextView.addAutoLinkMode(
                AutoLinkMode.MODE_PHONE,
                AutoLinkMode.MODE_URL, AutoLinkMode.MODE_EMAIL);
        if (!message.getText().isEmpty())
        autoLinkTextView.setAutoLinkText(message.getText());
        autoLinkTextView.setAutoLinkOnClickListener(new AutoLinkOnClickListener() {
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
        Date date = message.getCreatedAt();
        DateFormat format = new SimpleDateFormat("hh:mm aa");
        String timee = format.format(date);
        time.setText("  " + timee);
        time.setTextSize(10);
        time.setTextColor(Global.conA.getResources().getColor(R.color.mess_time));


    }
}