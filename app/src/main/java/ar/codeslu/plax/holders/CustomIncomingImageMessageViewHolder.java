package ar.codeslu.plax.holders;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import androidx.core.app.ActivityCompat;

import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
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

    public CustomIncomingImageMessageViewHolder(View itemView, Object payload) {
        super(itemView, payload);
    }

    @Override
    public void onBind(final Message message) {
        super.onBind(message);
        //react
        ImageView react = itemView.findViewById(R.id.react);

        DisplayMetrics displaymetrics = new DisplayMetrics();
        Global.chatactivity.getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        int imagewidth = (int) Math.round(displaymetrics.widthPixels * 0.68);
        int imageheight = (int) Math.round(imagewidth * 0.6);
        image.getLayoutParams().width = imagewidth;
        image.getLayoutParams().height = imageheight;

        int profileWH = (int) Math.round(displaymetrics.widthPixels * 0.11);
        userAvatar.getLayoutParams().width = profileWH;
        userAvatar.getLayoutParams().height = profileWH;

        int reactWH = (int) Math.round(displaymetrics.widthPixels * 0.069);
        react.getLayoutParams().width = reactWH;
        react.getLayoutParams().height = reactWH;

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

        Date date = message.getCreatedAt();
        DateFormat format = new SimpleDateFormat("hh:mm aa");
        String timee = format.format(date);
        time.setText("  " + timee);
        time.setTextSize(10);
        time.setTextColor(Global.conA.getResources().getColor(R.color.mess_time));
        //dialog on long select
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

                Dexter.withActivity(Global.chatactivity)
                        .withPermissions(Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.WAKE_LOCK)
                        .withListener(new MultiplePermissionsListener() {
                            @Override public void onPermissionsChecked(MultiplePermissionsReport report) {

                                if(report.areAllPermissionsGranted())
                                {
                                    Intent intent = new Intent(Global.conA, Photoa.class);
                                    intent.putExtra("url", message.getImageUrl());
                                    intent.putExtra("from", message.getId());
                                    intent.putExtra("Mid", message.getMessid());
                                    intent.putExtra("ava", Global.currAva);
                                    intent.putExtra("name", Global.currname);
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

    }
}