package ar.codeslu.plax.adapters;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.downloader.Error;
import com.downloader.OnCancelListener;
import com.downloader.OnDownloadListener;
import com.downloader.OnPauseListener;
import com.downloader.OnProgressListener;
import com.downloader.OnStartOrResumeListener;
import com.downloader.PRDownloader;
import com.downloader.Progress;
import com.google.android.flexbox.FlexboxLayout;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;
import com.stfalcon.chatkit.link.AutoLinkTextView;
import com.stfalcon.chatkit.me.MessageFav;
import com.stfalcon.chatkit.me.MessageFav;
import com.vanniktech.emoji.EmojiTextView;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ar.codeslu.plax.R;
import ar.codeslu.plax.custom.FavSelect;
import ar.codeslu.plax.custom.GroupCelect;
import ar.codeslu.plax.global.AppBack;
import ar.codeslu.plax.global.Global;
import ar.codeslu.plax.global.encryption;
import ar.codeslu.plax.lists.UserData;


/**
 * Created by CodeSlu
 */
public class FavA extends RecyclerView.Adapter<FavA.Holder> {
    Context context;
    FirebaseAuth mAuth;
    ArrayList<MessageFav> array;

    //vars
    String[] location;
    String lat, lng, url;

    //shared
    //Shared pref
    SharedPreferences preferences;
    SharedPreferences.Editor editor;
DatabaseReference mFav;

    public FavA(ArrayList<MessageFav> array) {
        this.array = array;

    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.fav_row, null, false);
        context = parent.getContext();
        mAuth = FirebaseAuth.getInstance();
        mFav = FirebaseDatabase.getInstance().getReference(Global.FAV);
        return new Holder(layoutView);
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        try {
        if (array.get(position).getFrom().equals(mAuth.getCurrentUser().getUid())) {
            holder.TItext.setVisibility(View.GONE);
            holder.bubbleFI.setVisibility(View.GONE);
            holder.lyIall.setVisibility(View.GONE);
            holder.playerI.setVisibility(View.GONE);
            holder.videoViewI.setVisibility(View.GONE);
            holder.mapI.setVisibility(View.GONE);
            holder.imageI.setVisibility(View.GONE);
            holder.bubble.setVisibility(View.GONE);
            holder.messageTextI.setVisibility(View.GONE);
            holder.messageUserAvatarC.setVisibility(View.GONE);
            holder.messageUserAvatar.setVisibility(View.GONE);

            if (!array.get(position).isDeleted()) {

                switch (array.get(position).getType()) {
                    case "text":
                        holder.bubbleO.setBackground(ContextCompat.getDrawable(context, R.drawable.shape_outcoming_message));
                        holder.messageText.setTextColor(Color.WHITE);


                        holder.bubbleO.setVisibility(View.VISIBLE);
                        holder.messageText.setVisibility(View.VISIBLE);
                        holder.bubbleFO.setVisibility(View.GONE);
                        holder.lyOall.setVisibility(View.GONE);
                        holder.playerO.setVisibility(View.GONE);
                        holder.videoViewO.setVisibility(View.GONE);
                        holder.mapO.setVisibility(View.GONE);
                        holder.imageO.setVisibility(View.GONE);
                        holder.messageText.setText(encryption.decryptOrNull(array.get(position).getMessage()));
                        break;
                    case "image":
                        holder.bubbleO.setVisibility(View.GONE);
                        holder.messageText.setVisibility(View.GONE);
                        holder.bubbleFO.setVisibility(View.GONE);
                        holder.lyOall.setVisibility(View.VISIBLE);
                        holder.playerO.setVisibility(View.GONE);
                        holder.videoViewO.setVisibility(View.GONE);
                        holder.mapO.setVisibility(View.GONE);
                        holder.imageO.setVisibility(View.VISIBLE);
                        Picasso.get()
                                .load(encryption.decryptOrNull(array.get(position).getLinkI()))
                                .placeholder(R.drawable.placeholder_gray).error(R.drawable.profile)

                                .into(holder.imageO);

                        break;
                    case "voice":
                        holder.bubbleO.setVisibility(View.GONE);
                        holder.messageText.setVisibility(View.GONE);
                        holder.bubbleFO.setVisibility(View.GONE);
                        holder.lyOall.setVisibility(View.VISIBLE);
                        holder.playerO.setVisibility(View.GONE);
                        holder.videoViewO.setVisibility(View.GONE);
                        holder.mapO.setVisibility(View.GONE);
                        holder.imageO.setVisibility(View.GONE);

                        break;
                    case "video":
//not now
                        break;
                    case "file":
                        if (Global.DARKSTATE) {
                            holder.bubbleFO.setBackground(ContextCompat.getDrawable(context, R.drawable.shape_outcoming_message));
                            holder.recorddurationO.setTextColor(Color.WHITE);

                        } else {
                            holder.bubbleFO.setBackground(ContextCompat.getDrawable(context, R.drawable.shape_outcoming_message));
                            holder.recorddurationO.setTextColor(Color.BLACK);

                        }


                        holder.messageText.setVisibility(View.GONE);
                        holder.bubbleFO.setVisibility(View.VISIBLE);
                        holder.lyOall.setVisibility(View.VISIBLE);
                        holder.playerO.setVisibility(View.GONE);
                        holder.videoViewO.setVisibility(View.GONE);
                        holder.mapO.setVisibility(View.GONE);
                        holder.imageO.setVisibility(View.GONE);

                        String filename = array.get(position).getFilename();

                        if (filename.length() > Global.FileName_LENTH)
                            filename = filename.substring(0, Global.STATUE_LENTH) + "...";

                        holder.recorddurationO.setVisibility(View.VISIBLE);
                        holder.recorddurationO.setTextColor(Color.WHITE);
                        holder.recorddurationO.setText(filename);
                        holder.recorddurationO.setTextSize(13);
                        //Shared pref
                        preferences = context.getSharedPreferences("file", Context.MODE_PRIVATE);
                        editor = preferences.edit();
                        //dark init
                        holder.playVO.setImageResource(R.drawable.download_w);


                        holder.playVO.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                Dexter.withActivity(Global.chatactivity)
                                        .withPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE)
                                        .withListener(new MultiplePermissionsListener() {
                                            @Override
                                            public void onPermissionsChecked(MultiplePermissionsReport report) {

                                                if (report.areAllPermissionsGranted()) {
                                                    SharedPreferences preferences = context.getSharedPreferences("file", Context.MODE_PRIVATE);
                                                    String pathL = preferences.getString("file_" + encryption.decryptOrNull(array.get(position).getLinkF()), "not");
                                                    if (Global.check_int(context)) {
                                                        File file = new File(pathL);
                                                        if (pathL.equals("not") || !file.exists()) {
                                                            String url = encryption.decryptOrNull(array.get(position).getLinkF());
                                                            pathL = Environment.getExternalStorageDirectory().getAbsolutePath().toString()
                                                                    + "/" + context.getResources().getString(R.string.app_name) + "/Files/";
                                                            String fileName = array.get(position).getFilename();
                                                            final String finalPathL = pathL;
                                                            int downloadId = PRDownloader.download(url, pathL, fileName)
                                                                    .build()
                                                                    .setOnStartOrResumeListener(new OnStartOrResumeListener() {
                                                                        @Override
                                                                        public void onStartOrResume() {
                                                                            Toast.makeText(context, context.getResources().getString(R.string.downloadstart), Toast.LENGTH_SHORT).show();

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
                                                                            editor.putString("file_" + encryption.decryptOrNull(array.get(position).getLinkF()), finalPathL + fileName);
                                                                            editor.apply();
                                                                            //  here is open local
                                                                            Toast.makeText(context, context.getResources().getString(R.string.downloadcomplete), Toast.LENGTH_SHORT).show();

                                                                        }

                                                                        @Override
                                                                        public void onError(Error error) {
                                                                            Toast.makeText(context, context.getResources().getString(R.string.cannot_open), Toast.LENGTH_SHORT).show();

                                                                        }

                                                                    });
                                                        } else {
                                                            //  here is open local

                                                        }

                                                    } else {
                                                        if (pathL.equals("not")) {
                                                            Toast.makeText(context, context.getResources().getString(R.string.cannot_open), Toast.LENGTH_SHORT).show();
                                                        } else {
                                                            //  here is open local
                                                        }
                                                    }
                                                } else
                                                    Toast.makeText(context, context.getString(R.string.acc_per), Toast.LENGTH_SHORT).show();


                                            }

                                            @Override
                                            public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {

                                                token.continuePermissionRequest();

                                            }
                                        }).check();

                            }
                        });


                        break;
                    case "map":

                        holder.messageText.setVisibility(View.GONE);
                        holder.bubbleFO.setVisibility(View.GONE);
                        holder.lyOall.setVisibility(View.VISIBLE);
                        holder.playerO.setVisibility(View.GONE);
                        holder.videoViewO.setVisibility(View.GONE);
                        holder.mapO.setVisibility(View.VISIBLE);
                        holder.imageO.setVisibility(View.GONE);


                        location = encryption.decryptOrNull(array.get(position).getLocation()).split(",");
                        if (location.length == 2) {
                            lat = location[0];
                            lng = location[1];
                        }


                        url = "https://maps.googleapis.com/maps/api/staticmap?center=" + Double.parseDouble(lat) + "," + Double.parseDouble(lng) + "&zoom=15&size=300x300&maptype=roadmap&format=png&visual_refresh=true&key=" + context.getResources().getString(R.string.google_maps_key) + "&signature=BASE64_SIGNATURE";
                        Picasso.get()
                                .load(url)
                                .placeholder(R.drawable.placeholder_gray).error(R.drawable.profile)

                                .into(holder.mapO);

                        break;


                }
            } else {
                holder.bubbleO.setBackground(ContextCompat.getDrawable(context, R.drawable.shape_outcoming_deleted));
                holder.messageText.setTextColor(Color.WHITE);


                holder.bubbleO.setVisibility(View.VISIBLE);
                holder.messageText.setVisibility(View.VISIBLE);
                holder.bubbleFO.setVisibility(View.GONE);
                holder.lyOall.setVisibility(View.GONE);
                holder.playerO.setVisibility(View.GONE);
                holder.videoViewO.setVisibility(View.GONE);
                holder.mapO.setVisibility(View.GONE);
                holder.imageO.setVisibility(View.GONE);
                holder.messageText.setText(encryption.decryptOrNull(array.get(position).getMessage()));


            }


            ///////
        } else {
            holder.bubbleO.setVisibility(View.GONE);
            holder.messageText.setVisibility(View.GONE);
            holder.bubbleFO.setVisibility(View.GONE);
            holder.playerFO.setVisibility(View.GONE);
            holder.lyOall.setVisibility(View.GONE);
            holder.videoViewO.setVisibility(View.GONE);
            holder.mapO.setVisibility(View.GONE);
            holder.imageO.setVisibility(View.GONE);
            if (!array.get(position).isDeleted()) {

                if (array.get(position).getType().equals("text")) {
                    if (encryption.decryptOrNull(array.get(position).getAvatar()).equals("no")) {
                        Picasso.get()
                                .load(R.drawable.profile)
                                .placeholder(R.drawable.placeholder_gray)
                                .error(R.drawable.profile)

                                .into(holder.messageUserAvatar);
                    } else {
                        Picasso.get()
                                .load(encryption.decryptOrNull(array.get(position).getAvatar()))
                                .placeholder(R.drawable.placeholder_gray)
                                .error(R.drawable.profile)

                                .into(holder.messageUserAvatar);
                    }
                } else {
                    if (encryption.decryptOrNull(array.get(position).getAvatar()).equals("no")) {
                        Picasso.get()
                                .load(R.drawable.profile)
                                .placeholder(R.drawable.placeholder_gray)
                                .error(R.drawable.profile)

                                .into(holder.messageUserAvatarC);
                    } else {
                        Picasso.get()
                                .load(encryption.decryptOrNull(array.get(position).getAvatar()))
                                .placeholder(R.drawable.placeholder_gray)
                                .error(R.drawable.profile)

                                .into(holder.messageUserAvatarC);
                    }
                }

                switch (array.get(position).getType()) {
                    case "text":
                        if (Global.DARKSTATE) {
                            holder.bubble.setBackground(ContextCompat.getDrawable(context, R.drawable.shape_incoming_message_d));
                            holder.messageTextI.setTextColor(Color.WHITE);

                        } else {
                            holder.bubble.setBackground(ContextCompat.getDrawable(context, R.drawable.shape_incoming_message));
                            holder.messageTextI.setTextColor(Color.BLACK);
                        }
                        holder.TItext.setVisibility(View.VISIBLE);
                        holder.bubble.setVisibility(View.VISIBLE);
                        holder.messageTextI.setVisibility(View.VISIBLE);
                        holder.messageUserAvatar.setVisibility(View.VISIBLE);
                        holder.messageUserAvatarC.setVisibility(View.GONE);
                        holder.messageText.setVisibility(View.GONE);
                        holder.bubbleFI.setVisibility(View.GONE);
                        holder.lyIall.setVisibility(View.GONE);
                        holder.playerI.setVisibility(View.GONE);
                        holder.videoViewI.setVisibility(View.GONE);
                        holder.mapI.setVisibility(View.GONE);
                        holder.imageI.setVisibility(View.GONE);
                        holder.messageTextI.setText(encryption.decryptOrNull(array.get(position).getMessage()));
                        break;
                    case "image":
                        holder.TItext.setVisibility(View.GONE);
                        holder.bubble.setVisibility(View.GONE);
                        holder.messageTextI.setVisibility(View.GONE);
                        holder.messageUserAvatar.setVisibility(View.GONE);
                        holder.messageUserAvatarC.setVisibility(View.VISIBLE);
                        holder.messageText.setVisibility(View.GONE);
                        holder.bubbleFI.setVisibility(View.GONE);
                        holder.lyIall.setVisibility(View.VISIBLE);
                        holder.playerI.setVisibility(View.GONE);
                        holder.videoViewI.setVisibility(View.GONE);
                        holder.mapI.setVisibility(View.GONE);
                        holder.imageI.setVisibility(View.VISIBLE);
                        Picasso.get()
                                .load(encryption.decryptOrNull(array.get(position).getLinkI()))
                                .placeholder(R.drawable.placeholder_gray).error(R.drawable.profile)

                                .into(holder.imageI);

                        break;
                    case "voice":
                        holder.TItext.setVisibility(View.GONE);
                        holder.bubble.setVisibility(View.GONE);
                        holder.messageTextI.setVisibility(View.GONE);
                        holder.messageUserAvatar.setVisibility(View.GONE);
                        holder.messageUserAvatarC.setVisibility(View.VISIBLE);
                        holder.messageText.setVisibility(View.GONE);
                        holder.bubbleFI.setVisibility(View.GONE);
                        holder.lyIall.setVisibility(View.VISIBLE);
                        holder.playerI.setVisibility(View.VISIBLE);
                        holder.videoViewI.setVisibility(View.GONE);
                        holder.mapI.setVisibility(View.GONE);
                        holder.imageI.setVisibility(View.GONE);

                        break;
                    case "video":
//not now
                        break;
                    case "file":
                        if (Global.DARKSTATE) {
                            holder.bubbleFI.setBackground(ContextCompat.getDrawable(context, R.drawable.shape_incoming_message_d));
                            holder.recordduration.setTextColor(Color.WHITE);

                        } else {
                            holder.bubbleFI.setBackground(ContextCompat.getDrawable(context, R.drawable.shape_incoming_message));
                            holder.recordduration.setTextColor(Color.BLACK);

                        }

                        holder.TItext.setVisibility(View.GONE);
                        holder.bubble.setVisibility(View.GONE);
                        holder.messageTextI.setVisibility(View.GONE);
                        holder.messageUserAvatar.setVisibility(View.GONE);
                        holder.messageUserAvatarC.setVisibility(View.VISIBLE);
                        holder.messageText.setVisibility(View.GONE);
                        holder.bubbleFI.setVisibility(View.VISIBLE);
                        holder.lyIall.setVisibility(View.VISIBLE);
                        holder.playerI.setVisibility(View.GONE);
                        holder.videoViewI.setVisibility(View.GONE);
                        holder.mapI.setVisibility(View.GONE);
                        holder.imageI.setVisibility(View.GONE);

                        String filename = array.get(position).getFilename();

                        if (filename.length() > Global.FileName_LENTH)
                            filename = filename.substring(0, Global.STATUE_LENTH) + "...";

                        holder.recordduration.setVisibility(View.VISIBLE);
                        holder.recordduration.setText(filename);
                        holder.recordduration.setTextSize(13);
                        //Shared pref
                        preferences = context.getSharedPreferences("file", Context.MODE_PRIVATE);
                        editor = preferences.edit();
                        //dark init
                        if (Global.DARKSTATE)
                            holder.playV.setImageResource(R.drawable.download_w);
                        else
                            holder.playV.setImageResource(R.drawable.download_b);

                        holder.playV.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                Dexter.withActivity(Global.chatactivity)
                                        .withPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE)
                                        .withListener(new MultiplePermissionsListener() {
                                            @Override
                                            public void onPermissionsChecked(MultiplePermissionsReport report) {

                                                if (report.areAllPermissionsGranted()) {
                                                    SharedPreferences preferences = context.getSharedPreferences("file", Context.MODE_PRIVATE);
                                                    String pathL = preferences.getString("file_" + encryption.decryptOrNull(array.get(position).getLinkF()), "not");
                                                    if (Global.check_int(context)) {
                                                        File file = new File(pathL);
                                                        if (pathL.equals("not") || !file.exists()) {
                                                            String url = encryption.decryptOrNull(array.get(position).getLinkF());
                                                            pathL = Environment.getExternalStorageDirectory().getAbsolutePath().toString()
                                                                    + "/" + context.getResources().getString(R.string.app_name) + "/Files/";
                                                            String fileName = array.get(position).getFilename();
                                                            final String finalPathL = pathL;
                                                            int downloadId = PRDownloader.download(url, pathL, fileName)
                                                                    .build()
                                                                    .setOnStartOrResumeListener(new OnStartOrResumeListener() {
                                                                        @Override
                                                                        public void onStartOrResume() {
                                                                            Toast.makeText(context, context.getResources().getString(R.string.downloadstart), Toast.LENGTH_SHORT).show();

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
                                                                            editor.putString("file_" + encryption.decryptOrNull(array.get(position).getLinkF()), finalPathL + fileName);
                                                                            editor.apply();
                                                                            //  here is open local
                                                                            Toast.makeText(context, context.getResources().getString(R.string.downloadcomplete), Toast.LENGTH_SHORT).show();

                                                                        }

                                                                        @Override
                                                                        public void onError(Error error) {
                                                                            Toast.makeText(context, context.getResources().getString(R.string.cannot_open), Toast.LENGTH_SHORT).show();

                                                                        }

                                                                    });
                                                        } else {
                                                            //  here is open local

                                                        }

                                                    } else {
                                                        if (pathL.equals("not")) {
                                                            Toast.makeText(context, context.getResources().getString(R.string.cannot_open), Toast.LENGTH_SHORT).show();
                                                        } else {
                                                            //  here is open local
                                                        }
                                                    }
                                                } else
                                                    Toast.makeText(context, context.getString(R.string.acc_per), Toast.LENGTH_SHORT).show();


                                            }

                                            @Override
                                            public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {

                                                token.continuePermissionRequest();

                                            }
                                        }).check();

                            }
                        });


                        break;
                    case "map":
                        holder.TItext.setVisibility(View.GONE);
                        holder.bubble.setVisibility(View.GONE);
                        holder.messageTextI.setVisibility(View.GONE);
                        holder.messageUserAvatar.setVisibility(View.GONE);
                        holder.messageUserAvatarC.setVisibility(View.VISIBLE);
                        holder.messageText.setVisibility(View.GONE);
                        holder.bubbleFI.setVisibility(View.GONE);
                        holder.lyIall.setVisibility(View.VISIBLE);
                        holder.playerI.setVisibility(View.GONE);
                        holder.videoViewI.setVisibility(View.GONE);
                        holder.mapI.setVisibility(View.VISIBLE);
                        holder.imageI.setVisibility(View.GONE);


                        location = encryption.decryptOrNull(array.get(position).getLocation()).split(",");
                        if (location.length == 2) {
                            lat = location[0];
                            lng = location[1];
                        }


                        url = "https://maps.googleapis.com/maps/api/staticmap?center=" + Double.parseDouble(lat) + "," + Double.parseDouble(lng) + "&zoom=15&size=300x300&maptype=roadmap&format=png&visual_refresh=true&key=" + context.getResources().getString(R.string.google_maps_key) + "&signature=BASE64_SIGNATURE";
                        Picasso.get()
                                .load(url)
                                .placeholder(R.drawable.placeholder_gray).error(R.drawable.profile)
                                .into(holder.mapI);

                        break;


                }
            } else {
                holder.TItext.setVisibility(View.VISIBLE);
                holder.bubble.setVisibility(View.VISIBLE);
                holder.messageTextI.setVisibility(View.VISIBLE);
                holder.messageUserAvatar.setVisibility(View.VISIBLE);
                holder.messageUserAvatarC.setVisibility(View.GONE);
                holder.messageText.setVisibility(View.GONE);
                holder.bubbleFI.setVisibility(View.GONE);
                holder.lyIall.setVisibility(View.GONE);
                holder.playerI.setVisibility(View.GONE);
                holder.videoViewI.setVisibility(View.GONE);
                holder.mapI.setVisibility(View.GONE);
                holder.imageI.setVisibility(View.GONE);
                holder.messageTextI.setText(encryption.decryptOrNull(array.get(position).getMessage()));
                holder.bubble.setBackground(ContextCompat.getDrawable(context, R.drawable.shape_incoming_deleted));
                holder.messageTextI.setTextColor(Color.WHITE);


            }
        }
        holder.lyallall.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                unstar(array.get(position).getMessId());
                return false;
            }
        });


    }
        catch (NullPointerException e) {
        }

    }

    @Override
    public int getItemCount() {
        return array.size();
    }

    class Holder extends RecyclerView.ViewHolder {

        AutoLinkTextView messageText, messageTextI;
        FlexboxLayout bubble, bubbleFO, bubbleFI, bubbleO;
        LinearLayout TItext, playerFO, lyIall, lyOall, playerI,playerO;
        RelativeLayout videoViewO, videoViewI,lyallall;
        RoundedImageView mapO, imageO, mapI, imageI, messageUserAvatar, messageUserAvatarC;
        ImageView playV,playVO;
        TextView recordduration,recorddurationO;

        Holder(View view) {
            super(view);
            messageTextI = view.findViewById(R.id.messageTextI);
            messageText = view.findViewById(R.id.messageText);

            playV = view.findViewById(R.id.playV);
            recordduration = view.findViewById(R.id.recordduration);

            playVO = view.findViewById(R.id.playVO);
            recorddurationO = view.findViewById(R.id.recorddurationO);
            playerO = view.findViewById(R.id.playerFullO);

            bubble = view.findViewById(R.id.bubble);
            TItext = view.findViewById(R.id.InLy);

            bubbleFO = view.findViewById(R.id.bubbleFO);
            bubbleO = view.findViewById(R.id.bubbleO);
            playerFO = view.findViewById(R.id.playerFullO);
            lyIall = view.findViewById(R.id.lyIall);
            lyOall = view.findViewById(R.id.lyOall);
            videoViewO = view.findViewById(R.id.videoViewO);
            mapO = view.findViewById(R.id.mapO);
            imageO = view.findViewById(R.id.imageO);


            bubbleFI = view.findViewById(R.id.bubbleFI);
            playerI = view.findViewById(R.id.playerFull);
            videoViewI = view.findViewById(R.id.videoView);
            mapI = view.findViewById(R.id.mapI);
            imageI = view.findViewById(R.id.imageI);

            messageUserAvatar = view.findViewById(R.id.messageUserAvatar);
            messageUserAvatarC = view.findViewById(R.id.messageUserAvatarC);

            lyallall= view.findViewById(R.id.lyallall);
        }
        }


    private void unstar(String id)
    {
        if (Global.check_int(context)) {
            FavSelect cdd = new FavSelect(context, id);
            cdd.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            cdd.getWindow().getAttributes().windowAnimations = R.style.CustomDialogAnimation;
            cdd.show();
        } else
            Toast.makeText(context, R.string.check_int, Toast.LENGTH_SHORT).show();
    }

}
