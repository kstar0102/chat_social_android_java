package ar.codeslu.plax.holders;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;

import androidx.core.app.ActivityCompat;

import android.util.DisplayMetrics;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
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

import com.stfalcon.chatkit.me.Message;

import ar.codeslu.plax.R;
import ar.codeslu.plax.custom.MessageSelectD;
import ar.codeslu.plax.global.Global;
import ar.codeslu.plax.mediachat.Photoa;

public class CustomOutcomingImageMessageViewHolder
        extends MessageHolders.OutcomingImageMessageViewHolder<Message> {

    private ImageView retry;
    private ProgressBar sending;

    public CustomOutcomingImageMessageViewHolder(View itemView, Object payload) {
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


        if (message.isDeleted()) {
            react.setVisibility(View.GONE);
        } else {
            react.setVisibility(View.VISIBLE);
        }
        switch (message.getReact()) {
            case "like":
                react.setVisibility(View.VISIBLE);
                react.setImageDrawable(Global.conA.getResources().getDrawable(R.drawable.like));
                break;
            case "funny":
                react.setVisibility(View.VISIBLE);
                react.setImageDrawable(Global.conA.getResources().getDrawable(R.drawable.funny));
                break;
            case "love":
                react.setVisibility(View.VISIBLE);
                react.setImageDrawable(Global.conA.getResources().getDrawable(R.drawable.love));
                break;
            case "sad":
                react.setVisibility(View.VISIBLE);
                react.setImageDrawable(Global.conA.getResources().getDrawable(R.drawable.sad));
                break;
            case "angry":
                react.setVisibility(View.VISIBLE);
                react.setImageDrawable(Global.conA.getResources().getDrawable(R.drawable.angry));
                break;
            case "no":
                react.setVisibility(View.GONE);
                react.setImageDrawable(Global.conA.getResources().getDrawable(R.drawable.emoji_blue));
                break;
        }

        Date date = message.getCreatedAt();
        DateFormat format = new SimpleDateFormat("hh:mm aa");
        String timee = format.format(date);
        if(!message.isChat())
            time.setText("  " + timee);
        else
            time.setText(" " + timee + " (" + message.getStatus() + ")");
        time.setTextSize(10);

        //retry adapt
        retry = itemView.findViewById(R.id.retry);
        sending = itemView.findViewById(R.id.sending);
        if (message.getStatus().equals("X")) {
            retry.setVisibility(View.VISIBLE);
            sending.setVisibility(View.GONE);

        } else if (message.getStatus().equals("..")) {
            retry.setVisibility(View.GONE);
            sending.setVisibility(View.VISIBLE);
        } else {
            retry.setVisibility(View.GONE);
            sending.setVisibility(View.GONE);
        }

        //dialog on long select
        image.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                MessageSelectD cdd = new MessageSelectD(Global.chatactivity, message, Global.currFid, 0, getAdapterPosition());
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
                                    intent.putExtra("ava", Global.avaLocal);
                                    intent.putExtra("name", Global.nameLocal);
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

    //Override this method to have ability to pass custom data in ImageLoader for loading image(not avatar).
    @Override
    protected Object getPayloadForImageLoader(Message message) {
        //For example you can pass size of placeholder before loading
        return new Pair<>(100, 100);
    }
}