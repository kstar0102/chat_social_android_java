package ar.codeslu.plax.holders;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;


import com.stfalcon.chatkit.link.AutoLinkMode;
import com.stfalcon.chatkit.link.AutoLinkOnClickListener;
import com.stfalcon.chatkit.link.AutoLinkTextView;
import com.stfalcon.chatkit.messages.MessagesListAdapter;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.stfalcon.chatkit.me.Message;

import ar.codeslu.plax.R;
import ar.codeslu.plax.custom.MessageSelectD;
import ar.codeslu.plax.global.Global;

/**
 * Created by mostafa on 01/02/19.
 */

public class OutcomeHolder
        extends MessagesListAdapter.OutcomingMessageViewHolder<Message> {
    private ImageView retry;
    private ProgressBar sending;

    public OutcomeHolder(View itemView, Object payload) {
        super(itemView);

    }

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
        //retry adapt
        retry = itemView.findViewById(R.id.retry);
        sending = itemView.findViewById(R.id.sending);
        if (message.getStatus().equals("X"))
        {
            retry.setVisibility(View.VISIBLE);
            sending.setVisibility(View.GONE);

        }
        else if(message.getStatus().equals(".."))
        {
            retry.setVisibility(View.GONE);
            sending.setVisibility(View.VISIBLE);
        }
        else
        {
            retry.setVisibility(View.GONE);
            sending.setVisibility(View.GONE);
        }

        AutoLinkTextView autoLinkTextView = (AutoLinkTextView) itemView.findViewById(R.id.messageText);
        autoLinkTextView.addAutoLinkMode(
                AutoLinkMode.MODE_PHONE,
                AutoLinkMode.MODE_URL, AutoLinkMode.MODE_EMAIL);
        autoLinkTextView.enableUnderLine();
        autoLinkTextView.setPhoneModeColor(ContextCompat.getColor(Global.conA, R.color.white));
        autoLinkTextView.setUrlModeColor(ContextCompat.getColor(Global.conA, R.color.white));
        autoLinkTextView.setEmailModeColor(ContextCompat.getColor(Global.conA, R.color.white));
        autoLinkTextView.setSelectedStateColor(ContextCompat.getColor(Global.conA, R.color.white));
        if (message.getText() != null) {
            autoLinkTextView.setAutoLinkText(message.getText());
            autoLinkTextView.setAutoLinkOnClickListener(new AutoLinkOnClickListener() {
                @Override
                public void onAutoLinkTextClick(AutoLinkMode autoLinkMode, String matchedText) {
                    switch (autoLinkMode) {
                        case MODE_URL:
                            if (matchedText.startsWith("w"))
                                matchedText = "http://" + matchedText;
                           // new FinestWebView.Builder(Global.conA).show(matchedText);

//                            Intent intent = new Intent(Intent.ACTION_VIEW);
//                            intent.setData(Uri.parse(matchedText));
//                            String title = matchedText;
//                            Intent chooser = Intent.createChooser(intent, title);
//                            Global.conA.startActivity(chooser);
                            break;
                        case MODE_PHONE:
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                if (ActivityCompat.checkSelfPermission(Global.conA, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(Global.conA, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                                    ActivityCompat.requestPermissions((Activity) Global.conA, new String[]{Manifest.permission.RECORD_AUDIO, Manifest.permission.READ_PHONE_STATE},
                                            804);
                                } else {
                                    Intent intent2 = new Intent(Intent.ACTION_DIAL);
                                    intent2.setData(Uri.parse("tel:" + matchedText));
                                    Global.conA.startActivity(intent2);
                                }
                            } else {
                                Intent intent2 = new Intent(Intent.ACTION_DIAL);
                                intent2.setData(Uri.parse("tel:" + matchedText));
                                Global.conA.startActivity(intent2);
                            }


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
            time.setText("  " + timee + " (" + message.getStatus() + ")");
            time.setTextSize(10);
            if (message.isDeleted()) {
                time.setVisibility(View.GONE);
                final int sdk = android.os.Build.VERSION.SDK_INT;
                if (sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                    bubble.setBackgroundDrawable(ContextCompat.getDrawable(Global.conA, R.drawable.shape_outcoming_deleted));
                } else {
                    bubble.setBackground(ContextCompat.getDrawable(Global.conA, R.drawable.shape_outcoming_deleted));
                }

            } else {
                time.setVisibility(View.VISIBLE);
                final int sdk = android.os.Build.VERSION.SDK_INT;
                if (sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                    bubble.setBackgroundDrawable(ContextCompat.getDrawable(Global.conA, R.drawable.shape_outcoming_message));
                } else {
                    bubble.setBackground(ContextCompat.getDrawable(Global.conA, R.drawable.shape_outcoming_message));
                }
            }

            bubble.setOnLongClickListener(new View.OnLongClickListener() {
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
    }
}

