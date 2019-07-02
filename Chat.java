package ar.codeslu.plax;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaRecorder;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import androidx.annotation.NonNull;
import com.google.android.material.snackbar.Snackbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.devlomi.record_view.OnBasketAnimationEnd;
import com.devlomi.record_view.OnRecordListener;
import com.devlomi.record_view.RecordButton;
import com.devlomi.record_view.RecordView;
import com.downloader.PRDownloader;
import com.downloader.PRDownloaderConfig;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import com.stfalcon.chatkit.commons.ImageLoader;
import com.stfalcon.chatkit.me.UserIn;
import com.stfalcon.chatkit.messages.MessageHolders;
import com.stfalcon.chatkit.messages.MessagesList;
import com.stfalcon.chatkit.messages.MessagesListAdapter;
import com.stfalcon.chatkit.utils.DateFormatter;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import ar.codeslu.plax.auth.DataSet;
import ar.codeslu.plax.custom.MessageSelectD;
import ar.codeslu.plax.datasetters.MessageData;
import ar.codeslu.plax.db.TinyDB;
import ar.codeslu.plax.global.AppBack;
import ar.codeslu.plax.global.GetTime;
import ar.codeslu.plax.global.Global;
import ar.codeslu.plax.global.NetworkStateMonitor;
import ar.codeslu.plax.holders.CustomIncomingImageMessageViewHolder;
import ar.codeslu.plax.holders.CustomOutcomingImageMessageViewHolder;
import ar.codeslu.plax.holders.IncomeHolder;
import ar.codeslu.plax.holders.IncomeOther;
import ar.codeslu.plax.holders.OutcomeHolder;
import ar.codeslu.plax.holders.OutcomeOther;
import ar.codeslu.plax.lists.OnlineGetter;
import ar.codeslu.plax.lists.Tokens;
import ar.codeslu.plax.lists.UserData;
import ar.codeslu.plax.lists.myD;
import ar.codeslu.plax.lists.senderD;

import com.stfalcon.chatkit.me.Message;
import com.stfalcon.chatkit.me.MessageIn;
import com.vanniktech.emoji.EmojiEditText;
import com.vanniktech.emoji.EmojiPopup;
import com.vanniktech.emoji.EmojiTextView;
import com.vincent.filepicker.Constant;
import com.vincent.filepicker.activity.AudioPickActivity;
import com.vincent.filepicker.activity.ImagePickActivity;
import com.vincent.filepicker.activity.NormalFilePickActivity;
import com.vincent.filepicker.activity.VideoPickActivity;
import com.vincent.filepicker.filter.entity.AudioFile;
import com.vincent.filepicker.filter.entity.ImageFile;
import com.vincent.filepicker.filter.entity.NormalFile;
import com.vincent.filepicker.filter.entity.VideoFile;

import ar.codeslu.plax.notify.FCM;
import ar.codeslu.plax.notify.FCMresp;
import ar.codeslu.plax.notify.Sender;
import de.hdodenhof.circleimageview.CircleImageView;
import id.zelory.compressor.Compressor;
import im.delight.android.location.SimpleLocation;
import io.codetail.animation.SupportAnimator;
import io.codetail.animation.ViewAnimationUtils;
import io.codetail.widget.RevealFrameLayout;
import me.leolin.shortcutbadger.ShortcutBadger;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import se.simbio.encryption.Encryption;

import static com.vincent.filepicker.activity.AudioPickActivity.IS_NEED_RECORDER;
import static com.vincent.filepicker.activity.BaseActivity.IS_NEED_FOLDER_LIST;
import static com.vincent.filepicker.activity.ImagePickActivity.IS_NEED_CAMERA;

public class Chat extends AppCompatActivity
        implements
        MessagesListAdapter.OnLoadMoreListener {

    //input
    FirebaseAuth mAuth;
    String friendId = "";
    DatabaseReference type;
    DatabaseReference mData, myData;
    ArrayList<UserData> mylist;
    senderD userData;
    myD data;
    //output
    protected ImageLoader imageLoader;
    protected MessagesListAdapter<Message> messagesAdapter;
    private static final int TOTAL_MESSAGES_COUNT = 0;
    private int selectionCount;
    private Date lastLoadedDate;
    private MessagesList messagesList;
    private ArrayList<MessageIn> messlist;
    DatabaseReference mDataget, mdatagetme;
    MessagesListAdapter.HoldersConfig holdersConfig;
    //view
    RelativeLayout ly;
    Encryption encryption;
    ImageView add, send, emoji;
    private RecordButton voice;
    RecordView recordView;
    EmojiEditText message;
    private LinearLayout attachmentLayout, messagebox;
    private boolean isHidden = true;
    private RevealFrameLayout framely;
    ImageButton btnI, btnV, btnVideo, btnF, btnL;
    private Bitmap compressedImageFile;
    private RelativeLayout relativeLayout;
    //typing
    private Timer mActivityTransitionTimer;
    private TimerTask mActivityTransitionTimerTask;
    private final long MAX_ACTIVITY_TRANSITION_TIME_MS = 1300;
    boolean typing = false;
    boolean typingR = false;
    int code = 0;
    //voice record
    private MediaRecorder mRecorder;
    private long recordTime = 0;
    private File mOutputFile;
    //map
    private SimpleLocation location;
    //all
    Object currTime;
    String messidL = "", encrypM = "", encrypI = "", encrypL = "", encrypV = "", encrypF = "", encrypVideo = "", encrypMap = "";
    //Types id
    byte Voiceid = 2;
    //toolbar
    Toolbar toolbar;
    CircleImageView ava;
    ImageView callA, callV;
    EmojiTextView name, state;
    //check online
    DatabaseReference mUserDB;
    //shared pref
    SharedPreferences preferences;
    SharedPreferences.Editor editor;
    //fcm
    FCM fcm;
    //thumb
    Bitmap thumb;
    //real time
    Handler h = new Handler();
    int TIMEUPDATE = 60 * 1000;
    Runnable runnable;
    //exist
    ImageView close;
    Button block, addcontact;
    LinearLayout existlay;
    ImageView imm;
    //get Message query
    Query query;
    //local message
    MessageIn messageLocal;
    TinyDB tinydb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        Global.conA = this;
        Global.chatactivity = this;
        //local db
        tinydb = new TinyDB(this);
        ly = findViewById(R.id.lyC);
        add = findViewById(R.id.attachmentButton);
        voice = findViewById(R.id.voice);
        send = findViewById(R.id.send);
        imm = findViewById(R.id.imm);
        emoji = findViewById(R.id.emoji);
        relativeLayout = findViewById(R.id.loadingPanel);
        messagebox = findViewById(R.id.messagebox);
        messagesList = (MessagesList) findViewById(R.id.messagesList);
        recordView = (RecordView) findViewById(R.id.record_view);
        message = findViewById(R.id.messageInput);
        framely = findViewById(R.id.menuA);
        //exist
        block = findViewById(R.id.block);
        addcontact = findViewById(R.id.addcontact);
        close = findViewById(R.id.close);
        existlay = findViewById(R.id.notcontact);
        existlay.setVisibility(View.GONE);
        mAuth = FirebaseAuth.getInstance();
        mData = FirebaseDatabase.getInstance().getReference(Global.CHATS);
        type = FirebaseDatabase.getInstance().getReference(Global.CHATS);
        mDataget = FirebaseDatabase.getInstance().getReference(Global.USERS);
        mdatagetme = FirebaseDatabase.getInstance().getReference(Global.USERS);
        mUserDB = FirebaseDatabase.getInstance().getReference().child(Global.USERS);
        //dark mode init
        if (mAuth.getCurrentUser() != null) {
            if (!((AppBack) getApplication()).shared().getBoolean("dark" + mAuth.getCurrentUser().getUid(), false)) {
                getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            } else {
                getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            }
        }
        //clear all notifications
        NotificationManager notificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
        try {
            if (notificationManager != null) {
                notificationManager.cancelAll();
                int count = 0;
                //store it again
                ((AppBack) getApplication()).editSharePrefs().putInt("numN" + mAuth.getCurrentUser().getUid(), count);
                ((AppBack) getApplication()).editSharePrefs().apply();
                ShortcutBadger.applyCount(this, count);
            }
        } catch (NullPointerException e) {
            //nothing
        }
        //loading
        relativeLayout.setVisibility(View.GONE);
        //location
        location = new SimpleLocation(this);
        //fcm notify
        fcm = Global.getFCMservies();
        Global.currentactivity = this;
        //online checker
        ((AppBack) this.getApplication()).startOnline();
//toolbar
        //Actionbar init
        toolbar = (Toolbar) findViewById(R.id.chatbar);
        toolbar.setPadding(0, 0, 0, 0);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayShowCustomEnabled(true);
        //Action bar design
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View viewS = inflater.inflate(R.layout.chat_bar, null);
        actionBar.setCustomView(viewS);
        name = viewS.findViewById(R.id.nameC);
        state = viewS.findViewById(R.id.stateC);
        ava = viewS.findViewById(R.id.avaC);
        callA = viewS.findViewById(R.id.callAC);
        callV = viewS.findViewById(R.id.callVC);
//emoji keyboard
        final EmojiPopup emojiPopup = EmojiPopup.Builder.fromRootView(ly).build(message);

//downloader
        // Enabling database for resume support even after the application is killed:
        PRDownloaderConfig config = PRDownloaderConfig.newBuilder()
                .setDatabaseEnabled(true)
                .build();
        PRDownloader.initialize(getApplicationContext(), config);
        //encryption
        byte[] iv = new byte[16];
        encryption = Encryption.getDefault(Global.keyE, Global.salt, iv);
        voice.setRecordView(recordView);
        if (getIntent() != null) {
            Intent intent = getIntent();
            friendId = intent.getExtras().getString("id");
            Global.currentpageid = friendId;
            mDataget.child(friendId).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    userData = dataSnapshot.getValue(senderD.class);
                    Global.currname = userData.getName();
                    Global.currAva = userData.getAvatar();
                    Global.onstate = userData.isOnstatue();
                    Global.currtime = userData.getTime();
                    Global.currstatue = userData.getStatue();
                    Global.currphone = userData.getPhone();
                    editInf();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
            code = intent.getExtras().getInt("ccode");
            //number for checking the contact
            Global.currphone = intent.getExtras().getString("phone");
        }
        //save message
        preferences = getSharedPreferences("messagebox", Context.MODE_PRIVATE);
        editor = preferences.edit();
//retrive message
        SharedPreferences preferences = getSharedPreferences("messagebox", Context.MODE_PRIVATE);
        message.setText(preferences.getString("chatM_" + friendId + "_" + mAuth.getCurrentUser().getUid(), ""));
        //toolbar data get
        if (!Global.check_int(this)) {
            if (getIntent() != null) {
                Intent intent = getIntent();
                friendId = intent.getExtras().getString("id");
                Global.currname = intent.getExtras().getString("name");
                Global.currAva = intent.getExtras().getString("ava");
                Global.currphone = intent.getExtras().getString("phone");
            }
            name.setText(Global.currname);
            if (String.valueOf(Global.currAva).equals("no")) {
                Picasso.get()
                        .load(R.drawable.profile)
                        .error(R.drawable.errorimg)
                        .into(ava);
            } else {
                Picasso.get()
                        .load(Global.currAva)
                        .error(R.drawable.errorimg)
                        .into(ava);
            }
            state.setVisibility(View.GONE);
        }

//typing
        Global.currFid = friendId;
        type.child(friendId).child(mAuth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                UserIn userIn = dataSnapshot.getValue(UserIn.class);
                if (userIn != null) {
                    typingR = userIn.isTyping();
                    typingit();
                } else {
                    typingR = false;
                    typingit();
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mylist = new ArrayList<>();
        myData = FirebaseDatabase.getInstance().getReference(Global.USERS);
        myData.child(mAuth.getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                data = dataSnapshot.getValue(myD.class);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        mdatagetme.child(mAuth.getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                myD userData = dataSnapshot.getValue(myD.class);
                Global.myname = userData.getNameL();
                Global.myava = userData.getAvatar();
                Global.myonstate = userData.isOnstatue();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        if (code == 0) {
            zeroCount();
            readM();
        }
        //output
        imageLoader = new ImageLoader() {

            @Override
            public void loadImage(final ImageView imageView, final String url, Object payload) {
                if (String.valueOf(url).equals("no")) {
                    Picasso.get()
                            .load(R.drawable.profile)
                            .error(R.drawable.errorimg)
                            .into(imageView);
                } else {
                    Picasso.get()
                            .load(url)
                            .placeholder(getResources().getDrawable(R.drawable.loading))
                            .error(R.drawable.errorimg)
                            .into(imageView);
                }
            }
        };
        //get messages
        getChats();
        messagesList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isHidden) {
                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP)
                        showMenuBelowLollipop();
                    else
                        showMenu();
                }
            }
        });
        //typing
        if (message.getText().toString().isEmpty()) {
            voice.setVisibility(View.VISIBLE);
            send.setEnabled(false);
            send.setVisibility(View.GONE);
            imm.setVisibility(View.VISIBLE);
        } else {
            voice.setVisibility(View.GONE);
            send.setEnabled(true);
            send.setVisibility(View.VISIBLE);
            imm.setVisibility(View.GONE);
        }
        message.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (messlist.size() > 0 && messlist != null) {
                    Map<String, Object> map = new HashMap<>();
                    map.put("typing", true);
                    type.child(mAuth.getCurrentUser().getUid()).child(friendId).updateChildren(map);
                    stopTT();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                startTT();
                if (!TextUtils.isEmpty(editable.toString())) {
                    voice.setVisibility(View.GONE);
                    send.setEnabled(true);
                    send.setVisibility(View.VISIBLE);
                    imm.setVisibility(View.GONE);
                } else {
                    voice.setVisibility(View.VISIBLE);
                    send.setEnabled(false);
                    send.setVisibility(View.GONE);
                    imm.setVisibility(View.VISIBLE);
                }
                //resize message box
                getSize();
                editor.putString("chatM_" + friendId + "_" + mAuth.getCurrentUser().getUid(), message.getText().toString());
                editor.apply();

            }
        });
        final String[] input = new String[1];


        //send message
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    input[0] = message.getText().toString();
                    if (!input[0].trim().isEmpty()) {
                        message.setText("");
                        getSize();
                        editor.putString("chatM_" + friendId + "_" + mAuth.getCurrentUser().getUid(), message.getText().toString());
                        editor.apply();
                        if (emojiPopup.isShowing()) {
                            emoji.setImageResource(R.drawable.ic_emoji);
                            emojiPopup.dismiss();
                        }

                        encrypM = String.valueOf(input[0]).trim();
                        encrypM = encryption.encryptOrNull(encrypM);
                        currTime = ServerValue.TIMESTAMP;
                        messidL = mAuth.getCurrentUser().getUid() + "_" + friendId + "_" + String.valueOf(System.currentTimeMillis());
                        //send owner data to friend
                        mAuth = FirebaseAuth.getInstance();
                        Map<String, Object> map = new HashMap<>();
                        map.put("avatar", data.getAvatar());
                        map.put("name", data.getName());
                        map.put("nameL", data.getNameL());
                        map.put("phone", data.getPhone());
                        map.put("id", mAuth.getCurrentUser().getUid());
                        map.put("lastmessage", encrypM);
                        map.put("lastsender", mAuth.getCurrentUser().getUid());
                        map.put("lastsenderava", data.getAvatar());
                        map.put("messDate", currTime);

                        //local message
                        messagesAdapter.clear();
                        messageLocal = new MessageIn(encrypM, "text", "..", mAuth.getCurrentUser().getUid(), System.currentTimeMillis(), false, false, messidL);
                        try {
                            Global.messG.add(messageLocal);
                            //local store
                            tinydb.putListObject(mAuth.getCurrentUser().getUid() + "/" + friendId, Global.messG);
                        } catch (NullPointerException e) {
                            Global.messG = new ArrayList<>();
                            Global.messG.add(messageLocal);
                            //local store
                            tinydb.putListObject(mAuth.getCurrentUser().getUid() + "/" + friendId, Global.messG);
                        }
                        messagesAdapter.addToEnd(MessageData.getMessages(), true);
                        messagesAdapter.notifyDataSetChanged();
                        messagesList.getLayoutManager().smoothScrollToPosition(messagesList, null, 0);
                        ///////

                        mData.child(friendId).child(mAuth.getCurrentUser().getUid()).updateChildren(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                sendM();
                            }
                        });
                    } else
                        Snackbar.make(ly, R.string.empty_mess, Snackbar.LENGTH_SHORT).show();
            }
        });
        //menu inti
        attachmentLayout = (LinearLayout) findViewById(R.id.attach_menu);
        btnI = (ImageButton) findViewById(R.id.gallery);
        btnV = (ImageButton) findViewById(R.id.audio);
        btnL = (ImageButton) findViewById(R.id.location);
        btnVideo = (ImageButton) findViewById(R.id.video);
        btnF = (ImageButton) findViewById(R.id.file);
        framely.setVisibility(View.GONE);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP)
                    showMenuBelowLollipop();
                else
                    showMenu();
            }
        });
        btnI.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isHidden) {
                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP)
                        showMenuBelowLollipop();
                    else
                        showMenu();
                }
                uploadI();
            }
        });
        btnF.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isHidden) {
                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP)
                        showMenuBelowLollipop();
                    else
                        showMenu();
                }
                Intent intent4 = new Intent(Chat.this, NormalFilePickActivity.class);
                intent4.putExtra(Constant.MAX_NUMBER, 5);
                intent4.putExtra(IS_NEED_FOLDER_LIST, true);
                intent4.putExtra(NormalFilePickActivity.SUFFIX, new String[]{"xlsx", "xls", "doc", "docx", "ppt", "pptx", "pdf", "txt"});
                startActivityForResult(intent4, Constant.REQUEST_CODE_PICK_FILE);
            }
        });
        btnVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isHidden) {
                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP)
                        showMenuBelowLollipop();
                    else
                        showMenu();
                }
                Intent intent2 = new Intent(Chat.this, VideoPickActivity.class);
                intent2.putExtra(IS_NEED_CAMERA, true);
                intent2.putExtra(Constant.MAX_NUMBER, 1);
                intent2.putExtra(IS_NEED_FOLDER_LIST, true);
                startActivityForResult(intent2, Constant.REQUEST_CODE_PICK_VIDEO);
            }
        });
        btnV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isHidden) {
                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP)
                        showMenuBelowLollipop();
                    else
                        showMenu();
                }
                Intent intent3 = new Intent(Chat.this, AudioPickActivity.class);
                intent3.putExtra(IS_NEED_RECORDER, true);
                intent3.putExtra(IS_NEED_FOLDER_LIST, true);
                intent3.putExtra(Constant.MAX_NUMBER, 1);
                startActivityForResult(intent3, Constant.REQUEST_CODE_PICK_AUDIO);
            }
        });
        emoji.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (emojiPopup.isShowing()) {
                    emoji.setImageResource(R.drawable.ic_emoji);
                    emojiPopup.dismiss();
                } else {
                    emoji.setImageResource(R.drawable.ic_keyboard);
                    emojiPopup.toggle();
                }
            }
        });
        btnL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!isHidden) {
                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP)
                        showMenuBelowLollipop();
                    else
                        showMenu();
                }

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (ActivityCompat.checkSelfPermission(Chat.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(Chat.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                        ActivityCompat.requestPermissions(Chat.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                                804);
                    } else {
                        // if we can't access the location yet
                        if (!location.hasLocationEnabled()) {
                            // ask the user to enable location access
                            SimpleLocation.openSettings(Chat.this);
                        } else {
                            encrypL = location.getLatitude() + "," + location.getLongitude();
                            encrypL = encryption.encryptOrNull(encrypL);
                            encrypMap = getString(R.string.map_location);
                            encrypMap = encryption.encryptOrNull(encrypMap);
                            currTime = ServerValue.TIMESTAMP;
                            messidL = mAuth.getCurrentUser().getUid() + "_" + friendId + "_" + String.valueOf(System.currentTimeMillis());
                            //send owner data to friend
                            mAuth = FirebaseAuth.getInstance();
                            Map<String, Object> map = new HashMap<>();
                            map.put("avatar", data.getAvatar());
                            map.put("name", data.getName());
                            map.put("nameL", data.getNameL());
                            map.put("phone", data.getPhone());
                            map.put("id", mAuth.getCurrentUser().getUid());
                            map.put("lastmessage", encrypMap);
                            map.put("lastsender", mAuth.getCurrentUser().getUid());
                            map.put("lastsenderava", data.getAvatar());
                            map.put("messDate", currTime);
                            //local message
                            messagesAdapter.clear();
                            messageLocal = new MessageIn(encrypL, "..", mAuth.getCurrentUser().getUid(), System.currentTimeMillis(), false, false, messidL, "map");
                            try {
                                Global.messG.add(messageLocal);
                                //local store
                                tinydb.putListObject(mAuth.getCurrentUser().getUid() + "/" + friendId, Global.messG);
                            } catch (NullPointerException e) {
                                Global.messG = new ArrayList<>();
                                Global.messG.add(messageLocal);
                                //local store
                                tinydb.putListObject(mAuth.getCurrentUser().getUid() + "/" + friendId, Global.messG);
                            }
                            messagesAdapter.addToEnd(MessageData.getMessages(), true);
                            messagesAdapter.notifyDataSetChanged();
                            messagesList.getLayoutManager().smoothScrollToPosition(messagesList, null, 0);
                            ///////
                            mData.child(friendId).child(mAuth.getCurrentUser().getUid()).updateChildren(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    sendMap();
                                }
                            });
                        }
                    }
                } else {
                    // if we can't access the location yet
                    if (!location.hasLocationEnabled()) {
                        // ask the user to enable location access
                        SimpleLocation.openSettings(Chat.this);
                    } else {
                        encrypL = location.getLatitude() + "," + location.getLongitude();
                        encrypL = encryption.encryptOrNull(encrypL);
                        encrypMap = getString(R.string.map_location);
                        encrypMap = encryption.encryptOrNull(encrypMap);
                        currTime = ServerValue.TIMESTAMP;
                        messidL = mAuth.getCurrentUser().getUid() + "_" + friendId + "_" + String.valueOf(System.currentTimeMillis());
                        //send owner data to friend
                        mAuth = FirebaseAuth.getInstance();
                        Map<String, Object> map = new HashMap<>();
                        map.put("avatar", data.getAvatar());
                        map.put("name", data.getName());
                        map.put("nameL", data.getNameL());
                        map.put("phone", data.getPhone());
                        map.put("id", mAuth.getCurrentUser().getUid());
                        map.put("lastmessage", encrypMap);
                        map.put("lastsender", mAuth.getCurrentUser().getUid());
                        map.put("lastsenderava", data.getAvatar());
                        map.put("messDate", currTime);
                        //local message
                        messagesAdapter.clear();
                        messageLocal = new MessageIn(encrypL, "..", mAuth.getCurrentUser().getUid(), System.currentTimeMillis(), false, false, messidL, "map");
                        try {
                            Global.messG.add(messageLocal);
                            //local store
                            tinydb.putListObject(mAuth.getCurrentUser().getUid() + "/" + friendId, Global.messG);
                        } catch (NullPointerException e) {
                            Global.messG = new ArrayList<>();
                            Global.messG.add(messageLocal);
                            //local store
                            tinydb.putListObject(mAuth.getCurrentUser().getUid() + "/" + friendId, Global.messG);
                        }
                        messagesAdapter.addToEnd(MessageData.getMessages(), true);
                        messagesAdapter.notifyDataSetChanged();
                        messagesList.getLayoutManager().smoothScrollToPosition(messagesList, null, 0);
                        ///////
                        mData.child(friendId).child(mAuth.getCurrentUser().getUid()).updateChildren(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                sendMap();
                            }
                        });
                    }
                }

            }
        });
        messagesList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!isHidden) {
                        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP)
                            showMenuBelowLollipop();
                        else
                            showMenu();


                }
            }
        });
        message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isHidden) {
                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP)
                        showMenuBelowLollipop();
                    else
                        showMenu();
                }
                if (emojiPopup.isShowing()) {
                    emoji.setImageResource(R.drawable.ic_emoji);
                    emojiPopup.dismiss();
                }

            }
        });

//voice record
        recordView.setVisibility(View.GONE);
        messagebox.setVisibility(View.VISIBLE);
        add.setVisibility(View.VISIBLE);
        recordView.setOnRecordListener(new OnRecordListener() {
            @Override
            public void onStart() {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (ContextCompat.checkSelfPermission(Chat.this,
                            Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED &&
                            ContextCompat.checkSelfPermission(Chat.this,
                                    Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED &&
                            ContextCompat.checkSelfPermission(Chat.this,
                                    Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

                        ActivityCompat.requestPermissions(Chat.this,
                                new String[]{Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                        Manifest.permission.READ_EXTERNAL_STORAGE},
                                220);

                    } else {
                        //Start Recording..
                        recordView.setVisibility(View.VISIBLE);
                        messagebox.setVisibility(View.GONE);
                        add.setVisibility(View.GONE);
                        startRecording();
                    }
                } else {
                    //Start Recording..
                    recordView.setVisibility(View.VISIBLE);
                    messagebox.setVisibility(View.GONE);
                    add.setVisibility(View.GONE);
                    startRecording();

                }
            }

            @Override
            public void onCancel() {
                try {
                    stopRecording(false);
                } catch (RuntimeException e) {
                    mOutputFile.delete();
                }
                setResult(RESULT_CANCELED);
            }

            @Override
            public void onFinish(long recordTime) {
                recordView.setVisibility(View.GONE);
                messagebox.setVisibility(View.VISIBLE);
                add.setVisibility(View.VISIBLE);
                try {
                    stopRecording(true);
                    Uri uri = Uri.parse("file://" + mOutputFile.getAbsolutePath());
                    setResult(Activity.RESULT_OK, new Intent().setData(uri));
                    uploadV(uri, recordTime);
                } catch (NullPointerException e) {

                }
            }

            @Override
            public void onLessThanSecond() {
                try {
                    recordView.setVisibility(View.GONE);
                    messagebox.setVisibility(View.VISIBLE);
                    add.setVisibility(View.VISIBLE);
                    try {
                        stopRecording(false);
                    } catch (RuntimeException e) {
                        mOutputFile.delete();
                    }
                    setResult(RESULT_CANCELED);
                } catch (NullPointerException e) {

                }

            }
        });
        voice.setListenForRecord(true);
        recordView.setOnBasketAnimationEndListener(new OnBasketAnimationEnd() {
            @Override
            public void onAnimationEnd() {
                recordView.setVisibility(View.GONE);
                messagebox.setVisibility(View.VISIBLE);
                add.setVisibility(View.VISIBLE);
            }
        });
        //get realtime time
        h.postDelayed(runnable = new Runnable() {
            public void run() {
                if (!Global.onstate)
                    state.setText(GetTime.getTimeAgo(Global.currtime, Chat.this));
                h.postDelayed(runnable, TIMEUPDATE);
            }
        }, TIMEUPDATE);
        //just get per
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(Chat.this, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(Chat.this, new String[]{Manifest.permission.READ_CONTACTS}, 1);
            }
        }
        //all exist layout elements
        block.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //something
            }
        });
        addcontact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent addContactIntent = new Intent(Intent.ACTION_INSERT);
                addContactIntent.setType(ContactsContract.Contacts.CONTENT_TYPE);
                addContactIntent.putExtra(ContactsContract.Intents.Insert.PHONE, Global.currphone);
                addContactIntent.putExtra(ContactsContract.Intents.Insert.NAME, Global.currname);
                startActivity(addContactIntent);
            }
        });
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                existlay.setVisibility(View.GONE);
                editor.putBoolean("close_" + friendId + "_" + mAuth.getCurrentUser().getUid(), true);
                editor.apply();
            }
        });
//check contact exist
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(Chat.this, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(Chat.this, new String[]{Manifest.permission.READ_CONTACTS}, 1);
            } else {
                contactExistsLay(contactExists(Chat.this, Global.currphone));
            }
        } else
            contactExistsLay(contactExists(Chat.this, Global.currphone));

        imm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    uploadI();
            }
        });


    }

    @Override
    protected void onStop() {
        super.onStop();
        try {
            stopRecording(false);
        } catch (RuntimeException e) {
        }
        setResult(RESULT_CANCELED);
        Global.currentpageid = "";
    }

    void showMenuBelowLollipop() {
        int cx = (attachmentLayout.getLeft() + attachmentLayout.getRight());
        int cy = attachmentLayout.getBottom();
        int radius = Math.max(attachmentLayout.getWidth(), attachmentLayout.getHeight());

        try {
            SupportAnimator animator = ViewAnimationUtils.createCircularReveal(attachmentLayout, cx, cy, 0, radius);
            animator.setInterpolator(new AccelerateDecelerateInterpolator());
            animator.setDuration(300);

            if (isHidden) {
                //Log.e(getClass().getSimpleName(), "showMenuBelowLollipop");
                attachmentLayout.setVisibility(View.VISIBLE);
                animator.start();
                isHidden = false;
                framely.setVisibility(View.VISIBLE);
            } else {
                SupportAnimator animatorReverse = animator.reverse();
                animatorReverse.start();
                animatorReverse.addListener(new SupportAnimator.AnimatorListener() {
                    @Override
                    public void onAnimationStart() {
                    }

                    @Override
                    public void onAnimationEnd() {
                        //Log.e("MainActivity", "onAnimationEnd");
                        isHidden = true;
                        framely.setVisibility(View.GONE);
                        attachmentLayout.setVisibility(View.INVISIBLE);
                    }

                    @Override
                    public void onAnimationCancel() {
                    }

                    @Override
                    public void onAnimationRepeat() {
                    }
                });
            }
        } catch (Exception e) {
            //Log.e(getClass().getSimpleName(), "try catch");
            isHidden = true;
            attachmentLayout.setVisibility(View.INVISIBLE);
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    void showMenu() {
        int cx = (attachmentLayout.getLeft() + attachmentLayout.getRight());
        int cy = attachmentLayout.getBottom();
        int radius = Math.max(attachmentLayout.getWidth(), attachmentLayout.getHeight());

        if (isHidden) {
            Animator anim = android.view.ViewAnimationUtils.createCircularReveal(attachmentLayout, cx, cy, 0, radius);
            attachmentLayout.setVisibility(View.VISIBLE);
            anim.start();
            isHidden = false;
            framely.setVisibility(View.VISIBLE);
        } else {
            Animator anim = android.view.ViewAnimationUtils.createCircularReveal(attachmentLayout, cx, cy, radius, 0);
            anim.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    attachmentLayout.setVisibility(View.INVISIBLE);
                    framely.setVisibility(View.GONE);
                    isHidden = true;
                }
            });
            anim.start();
        }
    }

    private void hideMenu() {
        attachmentLayout.setVisibility(View.GONE);
        isHidden = true;
    }

    @Override
    public void onBackPressed() {
        if (selectionCount == 0) {
            super.onBackPressed();
        } else {
            messagesAdapter.unselectAllItems();
        }
    }

    @Override
    public void onLoadMore(int page, int totalItemsCount) {
        if (totalItemsCount < TOTAL_MESSAGES_COUNT) {
            loadMessages();
        }
    }


    protected void loadMessages() {
        new Handler().postDelayed(new Runnable() { //imitation of internet connection
            @Override
            public void run() {
                ArrayList<Message> messages = MessageData.getMessages();
//                lastLoadedDate = messages.get(messages.size() - 1).getCreatedAt();
                messagesAdapter.addToEnd(messages, false);
            }
        }, 1000);
    }

//    private MessagesListAdapter.Formatter<Message> getMessageStringFormatter() {
//        return new MessagesListAdapter.Formatter<Message>() {
//            @Override
//            public String format(Message message) {
//                Date date = message.getCreatedAt();
//                if (DateFormatter.isToday(date)) {
//                    return getString(R.string.date_header_today);
//                } else if (DateFormatter.isYesterday(date)) {
//                    return getString(R.string.date_header_yesterday);
//                } else {
//                    return DateFormatter.format(date, DateFormatter.Template.STRING_DAY_MONTH_YEAR);
//                }
//            }
//        };
//    }

    private void initAdapter() {
        //todo
        holdersConfig = new MessagesListAdapter.HoldersConfig();
        holdersConfig.setIncoming(IncomeHolder.class, R.layout.item_incoming_text_message);
        holdersConfig.setOutcoming(OutcomeHolder.class, R.layout.item_outcoming_text_message);
        holdersConfig.setIncomingImageHolder(CustomIncomingImageMessageViewHolder.class, R.layout.item_incoming_image_message);
        holdersConfig.setOutcomingImageHolder(CustomOutcomingImageMessageViewHolder.class, R.layout.item_outcoming_image_message);
        holdersConfig.setDateHeaderLayout(R.layout.item_date_header);
        holdersConfig.registerContentType(Voiceid, IncomeOther.class, R.layout.item_incoming_other_message,
                OutcomeOther.class, R.layout.item_outcoming_other_message, new MessageHolders.ContentChecker() {
                    @Override
                    public boolean hasContentFor(Message message, byte type) {
                        if (type == 2) {
                            try {
                                return message.getVoice().getUrl() != null;
                            } catch (NullPointerException e) {
                                try {
                                    return message.getFile().getUrl() != null;

                                } catch (NullPointerException e1) {
                                    try {
                                        return message.getVideo().getUrl() != null;

                                    } catch (NullPointerException e2) {
                                        try {
                                            return message.getMap().getLocation() != null;

                                        } catch (NullPointerException e3) {
                                            return false;

                                        }
                                    }

                                }

                            }
                        } else
                            return false;
                    }
                });
        messagesAdapter = new MessagesListAdapter<>(mAuth.getCurrentUser().getUid(), holdersConfig, imageLoader);
        // messagesAdapter.enableSelectionMode(this);
        messagesAdapter.setLoadMoreListener(this);
        messagesAdapter.setDateHeadersFormatter(new DateFormatter.Formatter() {
            @Override
            public String format(Date date) {
                if (DateFormatter.isToday(date)) {
                    return getString(R.string.date_header_today);
                } else if (DateFormatter.isYesterday(date)) {
                    return getString(R.string.date_header_yesterday);
                } else {
                    return DateFormatter.format(date, DateFormatter.Template.STRING_DAY_MONTH_YEAR);
                }


            }
        });
        messagesAdapter.registerViewClickListener(R.id.messageUserAvatar,
                new MessagesListAdapter.OnMessageViewClickListener<Message>() {
                    @Override
                    public void onMessageViewClick(View view, Message message) {
//todo
                    }
                });
//        messagesAdapter.enableSelectionMode(new MessagesListAdapter.SelectionListener() {
//            @Override
//            public void onSelectionChanged(int count) {
//                Toast.makeText(Chat.this, count+"", Toast.LENGTH_SHORT).show();
//            }
//        });
        messagesList.setAdapter(messagesAdapter);
    }

    private void sendM() {
        final Map<String, Object> map = new HashMap<>();
        map.put("message", encrypM);
        map.put("time", currTime);
        map.put("messId", messidL);
        map.put("seen", false);
        map.put("type", "text");
        map.put("deleted", false);
        map.put("statue", "✔");
        map.put("from", mAuth.getCurrentUser().getUid());
        mData.child(mAuth.getCurrentUser().getUid()).child(friendId).child(Global.Messages).child(messidL)
                .updateChildren(map).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                mData.child(friendId).child(mAuth.getCurrentUser().getUid()).child(Global.Messages).child(messidL)
                        .updateChildren(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        //send friend data to owner
                        mAuth = FirebaseAuth.getInstance();
                        Map<String, Object> map = new HashMap<>();
                        map.put("avatar", userData.getAvatar());
                        map.put("name", userData.getName());
                        map.put("nameL", userData.getNameL());
                        map.put("phone", data.getPhone());
                        map.put("lastmessage", encrypM);
                        map.put("lastsender", mAuth.getCurrentUser().getUid());
                        map.put("lastsenderava", data.getAvatar());
                        map.put("messDate", currTime);
                        map.put("id", friendId);
                        //  ------------
                        mData.child(mAuth.getCurrentUser().getUid()).child(friendId).updateChildren(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                sendMessNotify(encrypM, messidL);

                            }
                        })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                    }
                                });
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(Chat.this, R.string.Failed, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void sendMap() {
        final Map<String, Object> map = new HashMap<>();
        map.put("location", encrypL);
        map.put("time", currTime);
        map.put("messId", messidL);
        map.put("seen", false);
        map.put("type", "map");
        map.put("deleted", false);
        map.put("statue", "✔");
        map.put("from", mAuth.getCurrentUser().getUid());
        mData.child(mAuth.getCurrentUser().getUid()).child(friendId).child(Global.Messages).child(messidL)
                .updateChildren(map).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                mData.child(friendId).child(mAuth.getCurrentUser().getUid()).child(Global.Messages).child(messidL)
                        .updateChildren(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        //send friend data to owner
                        mAuth = FirebaseAuth.getInstance();
                        Map<String, Object> map = new HashMap<>();
                        map.put("avatar", userData.getAvatar());
                        map.put("name", userData.getName());
                        map.put("nameL", userData.getNameL());
                        map.put("phone", data.getPhone());
                        map.put("lastmessage", encrypMap);
                        map.put("lastsender", mAuth.getCurrentUser().getUid());
                        map.put("lastsenderava", data.getAvatar());
                        map.put("messDate", currTime);
                        map.put("id", friendId);
                        //  ------------
                        mData.child(mAuth.getCurrentUser().getUid()).child(friendId).updateChildren(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {

                            }
                        })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                    }
                                });
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(Chat.this, R.string.Failed, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getChats() {
        messlist = new ArrayList<>();
        //getride of old data
        if (Global.messG != null)
            Global.messG.clear();
        query = mData.child(mAuth.getCurrentUser().getUid()).child(friendId).child(Global.Messages).orderByChild("time");
        query.keepSynced(true);
        initAdapter();
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    if (Global.check_int(Chat.this)) {
                        if (messlist.size() != 0) {
                            messlist.clear();
                            messagesAdapter.clear();
                        }
                        for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                            MessageIn message = childSnapshot.getValue(MessageIn.class);
                            messlist.add(message);
                        }
                        Global.messG = messlist;
                        //local store
                        tinydb.putListObject(mAuth.getCurrentUser().getUid() + "/" + friendId, messlist);
                    } else//get local messages if no internet
                        Global.messG = tinydb.getListObject(mAuth.getCurrentUser().getUid() + "/" + friendId);

                    messagesAdapter.clear();
                    messagesList.getLayoutManager().smoothScrollToPosition(messagesList, null, 0);
                    messagesAdapter.addToEnd(MessageData.getMessages(), true);
                    messagesAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }


    private void readM() {
        mData.child(friendId).child(mAuth.getCurrentUser().getUid()).child(Global.Messages).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    OnlineGetter getter = ds.getValue(OnlineGetter.class);
                    try {
                        if (getter != null && getter.getStatue().equals("D✔"))
                            ds.child("statue").getRef().setValue("R✔");
                    } catch (NullPointerException e) {
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void zeroCount() {

        Map<String, Object> map = new HashMap<>();
        map.put("noOfUnread", 0);
        mData.child(mAuth.getCurrentUser().getUid()).child(friendId).updateChildren(map).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {

            }
        });
    }

    public void startTT() {
        this.mActivityTransitionTimer = new Timer();
        this.mActivityTransitionTimerTask = new TimerTask() {
            public void run() {
                if (messlist.size() > 0 && messlist != null) {
                    Map<String, Object> map = new HashMap<>();
                    map.put("typing", false);
                    type.child(mAuth.getCurrentUser().getUid()).child(friendId).updateChildren(map);
                }
            }
        };

        this.mActivityTransitionTimer.schedule(mActivityTransitionTimerTask,
                MAX_ACTIVITY_TRANSITION_TIME_MS);
    }

    public void stopTT() {
        if (this.mActivityTransitionTimerTask != null) {
            this.mActivityTransitionTimerTask.cancel();
        }

        if (this.mActivityTransitionTimer != null) {
            this.mActivityTransitionTimer.cancel();
        }

    }

    public void uploadI() {
        if (ActivityCompat.checkSelfPermission(Chat.this, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(Chat.this, android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(Chat.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(Chat.this, new String[]{android.Manifest.permission.CAMERA, android.Manifest.permission.READ_EXTERNAL_STORAGE, android.Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    801);
        } else {
            Intent intent1 = new Intent(this, ImagePickActivity.class);
            intent1.putExtra(IS_NEED_CAMERA, true);
            intent1.putExtra(IS_NEED_FOLDER_LIST, true);
            intent1.putExtra(Constant.MAX_NUMBER, 20);
            startActivityForResult(intent1, Constant.REQUEST_CODE_PICK_IMAGE);
        }


    }

    public void uploadF(Uri linkL, final String filename, String filetype) {
        //for local
        String fileL = filename;
        relativeLayout.setVisibility(View.VISIBLE);
        if (ActivityCompat.checkSelfPermission(Chat.this, android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(Chat.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(Chat.this, Manifest.permission.WAKE_LOCK) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(Chat.this, new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE, android.Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.WAKE_LOCK},
                    804);
        } else {
            filename.replace(" ", "_");
            if (filetype.contains("/xlsx")) {
                filetype = ".xlsx";
            } else if (filetype.contains("/xls")) {
                filetype = ".xls";

            } else if (filetype.contains("/docx")) {
                filetype = ".docx";

            } else if (filetype.contains("/doc")) {
                filetype = ".doc";

            } else if (filetype.contains("/pptx")) {
                filetype = ".pptx";

            } else if (filetype.contains("/ppt")) {
                filetype = ".ppt";

            } else if (filetype.contains("/pdf")) {
                filetype = ".pdf";

            } else if (filetype.contains("/txt")) {
                filetype = ".txt";
            } else if (filetype.contains("/plain")) {
                filetype = ".txt";
            }
            //local message
            String locall = encryption.encryptOrNull(String.valueOf(linkL));
            messagesAdapter.clear();
            messageLocal = new MessageIn(locall, "..", System.currentTimeMillis(), false, false, messidL, "file", fileL + filetype, mAuth.getCurrentUser().getUid());
            try {
                Global.messG.add(messageLocal);
                //local store
                tinydb.putListObject(mAuth.getCurrentUser().getUid() + "/" + friendId, Global.messG);
            } catch (NullPointerException e) {
                Global.messG = new ArrayList<>();
                Global.messG.add(messageLocal);
                //local store
                tinydb.putListObject(mAuth.getCurrentUser().getUid() + "/" + friendId, Global.messG);
            }
            messagesAdapter.addToEnd(MessageData.getMessages(), true);
            messagesAdapter.notifyDataSetChanged();
            messagesList.getLayoutManager().smoothScrollToPosition(messagesList, null, 0);
            ///////
            //upload on server
            StorageReference mStorageRef = FirebaseStorage.getInstance().getReference();
            StorageReference riversRef = mStorageRef.child(Global.Mess + "/" + mAuth.getCurrentUser().getUid() + "/Files/" + mAuth.getCurrentUser().getUid() + friendId + System.currentTimeMillis() + filetype);
            final String finalFiletype = filetype;
            riversRef.putFile(linkL)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            final Uri downloadUrl = taskSnapshot.getDownloadUrl();
                            {
                                message.setText("");
                                encrypF = "File " + filename + finalFiletype;
                                encrypF = encryption.encryptOrNull(encrypF);
                                currTime = ServerValue.TIMESTAMP;
                                messidL = mAuth.getCurrentUser().getUid() + "_" + friendId + "_" + String.valueOf(System.currentTimeMillis());
                                //send owner data to friend
                                mAuth = FirebaseAuth.getInstance();
                                Map<String, Object> map = new HashMap<>();
                                map.put("avatar", data.getAvatar());
                                map.put("name", data.getName());
                                map.put("nameL", data.getNameL());
                                map.put("phone", data.getPhone());
                                map.put("id", mAuth.getCurrentUser().getUid());
                                map.put("lastmessage", encrypF);
                                map.put("lastsender", mAuth.getCurrentUser().getUid());
                                map.put("lastsenderava", data.getAvatar());
                                map.put("messDate", currTime);
                                mData.child(friendId).child(mAuth.getCurrentUser().getUid()).updateChildren(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {

                                        sendFpre(String.valueOf(downloadUrl), filename + finalFiletype);
                                    }
                                })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                relativeLayout.setVisibility(View.GONE);

                                            }
                                        });
                            }

                        }
                    });
        }


    }

    public void uploadV(Uri linkL, final long time) {
        String locall = encryption.encryptOrNull(String.valueOf(linkL));
        //local message
        messagesAdapter.clear();
        messageLocal = new MessageIn(locall, "..", mAuth.getCurrentUser().getUid(), System.currentTimeMillis(), false, false, messidL, "voice", getHumanTimeText(time));
        try {
            Global.messG.add(messageLocal);
            //local store
            tinydb.putListObject(mAuth.getCurrentUser().getUid() + "/" + friendId, Global.messG);
        } catch (NullPointerException e) {
            Global.messG = new ArrayList<>();
            Global.messG.add(messageLocal);
            //local store
            tinydb.putListObject(mAuth.getCurrentUser().getUid() + "/" + friendId, Global.messG);
        }
        messagesAdapter.addToEnd(MessageData.getMessages(), true);
        messagesAdapter.notifyDataSetChanged();
        messagesList.getLayoutManager().smoothScrollToPosition(messagesList, null, 0);
        ///////

        relativeLayout.setVisibility(View.VISIBLE);
        StorageReference mStorageRef = FirebaseStorage.getInstance().getReference();
        StorageReference riversRef = mStorageRef.child(Global.Mess + "/" + mAuth.getCurrentUser().getUid() + "/Audio/" + mAuth.getCurrentUser().getUid() + friendId + System.currentTimeMillis() + ".m4a");
        riversRef.putFile(linkL)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        final Uri downloadUrl = taskSnapshot.getDownloadUrl();
                        mOutputFile.delete();
                        {
                            message.setText("");
                            encrypV = "Voice " + getHumanTimeText(time);
                            encrypV = encryption.encryptOrNull(encrypV);
                            currTime = ServerValue.TIMESTAMP;
                            messidL = mAuth.getCurrentUser().getUid() + "_" + friendId + "_" + String.valueOf(System.currentTimeMillis());
                            //send owner data to friend
                            mAuth = FirebaseAuth.getInstance();
                            Map<String, Object> map = new HashMap<>();
                            map.put("avatar", data.getAvatar());
                            map.put("name", data.getName());
                            map.put("nameL", data.getNameL());
                            map.put("phone", data.getPhone());
                            map.put("id", mAuth.getCurrentUser().getUid());
                            map.put("lastmessage", encrypV);
                            map.put("lastsender", mAuth.getCurrentUser().getUid());
                            map.put("lastsenderava", data.getAvatar());
                            map.put("messDate", currTime);
                            mData.child(friendId).child(mAuth.getCurrentUser().getUid()).updateChildren(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    sendVpre(String.valueOf(downloadUrl), time);
                                }
                            });
                        }

                    }
                });


    }

    public void uploadVideo(final Uri linkL, final long time, final String local) {
        String locall = encryption.encryptOrNull(String.valueOf(linkL));
        //local message
        messagesAdapter.clear();
        messageLocal = new MessageIn(locall, "..", mAuth.getCurrentUser().getUid(), System.currentTimeMillis(), false, false, messidL, "video", getHumanTimeText(time),"file:///android_asset/loading.jpg" );
        try {
            Global.messG.add(messageLocal);
            //local store
            tinydb.putListObject(mAuth.getCurrentUser().getUid() + "/" + friendId, Global.messG);
        } catch (NullPointerException e) {
            Global.messG = new ArrayList<>();
            Global.messG.add(messageLocal);
            //local store
            tinydb.putListObject(mAuth.getCurrentUser().getUid() + "/" + friendId, Global.messG);
        }
        messagesAdapter.addToEnd(MessageData.getMessages(), true);
        messagesAdapter.notifyDataSetChanged();
        messagesList.getLayoutManager().smoothScrollToPosition(messagesList, null, 0);
        ///////
        relativeLayout.setVisibility(View.VISIBLE);
        final String videoidtemp = System.currentTimeMillis() + "";
        StorageReference mStorageRef = FirebaseStorage.getInstance().getReference();
        StorageReference riversRef = mStorageRef.child(Global.Mess + "/" + mAuth.getCurrentUser().getUid() + "/Video/" + mAuth.getCurrentUser().getUid() + friendId + videoidtemp + ".mp4");
        riversRef.putFile(linkL)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        final Uri downloadUrl = taskSnapshot.getDownloadUrl();
                        {
                            message.setText("");
                            encrypVideo = "Video " + getHumanTimeText(time);
                            encrypVideo = encryption.encryptOrNull(encrypVideo);
                            currTime = ServerValue.TIMESTAMP;
                            messidL = mAuth.getCurrentUser().getUid() + "_" + friendId + "_" + String.valueOf(System.currentTimeMillis());
                            //send owner data to friend
                            mAuth = FirebaseAuth.getInstance();
                            Map<String, Object> map = new HashMap<>();
                            map.put("avatar", data.getAvatar());
                            map.put("name", data.getName());
                            map.put("nameL", data.getNameL());
                            map.put("phone", data.getPhone());
                            map.put("id", mAuth.getCurrentUser().getUid());
                            map.put("lastmessage", encrypVideo);
                            map.put("lastsender", mAuth.getCurrentUser().getUid());
                            map.put("lastsenderava", data.getAvatar());
                            map.put("messDate", currTime);
                            mData.child(friendId).child(mAuth.getCurrentUser().getUid()).updateChildren(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    thumb = ThumbnailUtils.createVideoThumbnail(local, MediaStore.Video.Thumbnails.MINI_KIND);
                                    ByteArrayOutputStream bao = new ByteArrayOutputStream();
                                    thumb.compress(Bitmap.CompressFormat.PNG, 100, bao);
                                    byte[] byteArray = bao.toByteArray();
                                    StorageReference mStorageRef = FirebaseStorage.getInstance().getReference();
                                    StorageReference riversRef = mStorageRef.child(Global.Mess + "/" + mAuth.getCurrentUser().getUid() + "/Video/" + "Thumb/" + mAuth.getCurrentUser().getUid() + friendId + videoidtemp + ".png");
                                    riversRef.putBytes(byteArray).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                        @Override
                                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                            final Uri thumbD = taskSnapshot.getDownloadUrl();
                                            sendVideopre(String.valueOf(downloadUrl), time, String.valueOf(thumbD));

                                        }
                                    });


                                }
                            });
                        }

                    }
                });


    }

    @Override
    protected void onActivityResult(int requestCode, final int resultCode, Intent data) {
        switch (requestCode) {
            case Constant.REQUEST_CODE_PICK_IMAGE:
                if (resultCode == RESULT_OK) {
                    ArrayList<ImageFile> list = data.getParcelableArrayListExtra(Constant.RESULT_PICK_IMAGE);
                    for (int i = 0; i < list.size(); i++) {
                        String path = list.get(i).getPath();
                        afterCompress(path);
                    }
                }
                break;
            case Constant.REQUEST_CODE_PICK_VIDEO:
                if (resultCode == RESULT_OK) {
                    ArrayList<VideoFile> list = data.getParcelableArrayListExtra(Constant.RESULT_PICK_VIDEO);
                    for (int i = 0; i < list.size(); i++) {
                        uploadVideo(Uri.parse("file:///" + list.get(i).getPath()), list.get(i).getDuration(), list.get(i).getPath());
                    }
                }
                break;
            case Constant.REQUEST_CODE_PICK_AUDIO:
                if (resultCode == RESULT_OK) {
                    ArrayList<AudioFile> list = data.getParcelableArrayListExtra(Constant.RESULT_PICK_AUDIO);
                    for (int i = 0; i < list.size(); i++) {
                        uploadV(Uri.parse("file:///" + list.get(i).getPath()), list.get(i).getDuration());
                    }
                }
                break;
            case Constant.REQUEST_CODE_PICK_FILE:
                if (resultCode == RESULT_OK) {
                    ArrayList<NormalFile> list = data.getParcelableArrayListExtra(Constant.RESULT_PICK_FILE);
                    for (int i = 0; i < list.size(); i++) {
                        uploadF(Uri.parse("file:///" + list.get(i).getPath()), list.get(i).getName(), list.get(i).getMimeType());

                    }
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void afterCompress(String path) {
        String locall = encryption.encryptOrNull("file://" + path);
        //local message
        messagesAdapter.clear();
        messageLocal = new MessageIn(locall, "image", messidL, "..", mAuth.getCurrentUser().getUid(), System.currentTimeMillis(), false, false);
        try {
            Global.messG.add(messageLocal);
            //local store
            tinydb.putListObject(mAuth.getCurrentUser().getUid() + "/" + friendId, Global.messG);
        } catch (NullPointerException e) {
            Global.messG = new ArrayList<>();
            Global.messG.add(messageLocal);
            //local store
            tinydb.putListObject(mAuth.getCurrentUser().getUid() + "/" + friendId, Global.messG);
        }
        messagesAdapter.addToEnd(MessageData.getMessages(), true);
        messagesAdapter.notifyDataSetChanged();
        messagesList.getLayoutManager().smoothScrollToPosition(messagesList, null, 0);
        ///////


        //      //compress the photo
        relativeLayout.setVisibility(View.VISIBLE);
        File newImageFile = new File(path);
        try {
            compressedImageFile = new Compressor(Chat.this)
                    .setMaxHeight(500)
                    .setMaxWidth(500)
                    .setQuality(50)
                    .compressToBitmap(newImageFile);

        } catch (IOException e) {
            e.printStackTrace();
        }
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        compressedImageFile.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] thumbData = baos.toByteArray();
        StorageReference mStorageRef = FirebaseStorage.getInstance().getReference();
        StorageReference riversRef = mStorageRef.child(Global.Mess + "/" + mAuth.getCurrentUser().getUid() + "/Images/" + mAuth.getCurrentUser().getUid() + friendId + System.currentTimeMillis() + ".jpg");
        riversRef.putBytes(thumbData)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        final Uri downloadUrl = taskSnapshot.getDownloadUrl();
                        {
                            message.setText("");
                            encrypI = "Image";
                            encrypI = encryption.encryptOrNull(encrypI);
                            currTime = ServerValue.TIMESTAMP;
                            messidL = mAuth.getCurrentUser().getUid() + "_" + friendId + "_" + String.valueOf(System.currentTimeMillis());
                            //send owner data to friend
                            mAuth = FirebaseAuth.getInstance();
                            Map<String, Object> map = new HashMap<>();
                            map.put("avatar", data.getAvatar());
                            map.put("name", data.getName());
                            map.put("nameL", data.getNameL());
                            map.put("phone", data.getPhone());
                            map.put("id", mAuth.getCurrentUser().getUid());
                            map.put("lastmessage", encrypI);
                            map.put("lastsender", mAuth.getCurrentUser().getUid());
                            map.put("lastsenderava", data.getAvatar());
                            map.put("messDate", currTime);
                            mData.child(friendId).child(mAuth.getCurrentUser().getUid()).updateChildren(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    sendIpre(String.valueOf(downloadUrl));
                                }
                            });
                        }

                    }
                });
    }

    private void sendIpre(String link) {
        encrypL = link;
        encrypL = encryption.encryptOrNull(encrypL);
        final Map<String, Object> map = new HashMap<>();
        map.put("linkI", encrypL);
        map.put("time", currTime);
        map.put("messId", messidL);
        map.put("seen", false);
        map.put("type", "image");
        map.put("deleted", false);
        map.put("statue", "✔");
        map.put("from", mAuth.getCurrentUser().getUid());
        mData.child(mAuth.getCurrentUser().getUid()).child(friendId).child(Global.Messages).child(messidL)
                .updateChildren(map).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                mData.child(friendId).child(mAuth.getCurrentUser().getUid()).child(Global.Messages).child(messidL)
                        .updateChildren(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        //send friend data to owner
                        mAuth = FirebaseAuth.getInstance();
                        Map<String, Object> map = new HashMap<>();
                        map.put("avatar", userData.getAvatar());
                        map.put("name", userData.getName());
                        map.put("nameL", userData.getNameL());
                        map.put("phone", data.getPhone());
                        map.put("lastmessage", encrypI);
                        map.put("lastsender", mAuth.getCurrentUser().getUid());
                        map.put("lastsenderava", data.getAvatar());
                        map.put("messDate", currTime);
                        map.put("id", friendId);
                        //  ------------
                        mData.child(mAuth.getCurrentUser().getUid()).child(friendId).updateChildren(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {

                                relativeLayout.setVisibility(View.GONE);
                                sendMessNotify(encrypI, messidL);

                            }
                        })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        relativeLayout.setVisibility(View.GONE);

                                    }
                                });
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(Chat.this, R.string.Failed, Toast.LENGTH_SHORT).show();
                relativeLayout.setVisibility(View.GONE);
            }
        });
    }

    private void sendFpre(String link, String filename) {
        encrypL = link;
        encrypL = encryption.encryptOrNull(encrypL);
        final Map<String, Object> map = new HashMap<>();
        map.put("linkF", encrypL);
        map.put("time", currTime);
        map.put("messId", messidL);
        map.put("filename", filename);
        map.put("seen", false);
        map.put("type", "file");
        map.put("deleted", false);
        map.put("statue", "✔");
        map.put("from", mAuth.getCurrentUser().getUid());
        mData.child(mAuth.getCurrentUser().getUid()).child(friendId).child(Global.Messages).child(messidL)
                .updateChildren(map).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                mData.child(friendId).child(mAuth.getCurrentUser().getUid()).child(Global.Messages).child(messidL)
                        .updateChildren(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        //send friend data to owner
                        mAuth = FirebaseAuth.getInstance();
                        Map<String, Object> map = new HashMap<>();
                        map.put("avatar", userData.getAvatar());
                        map.put("name", userData.getName());
                        map.put("nameL", userData.getNameL());
                        map.put("phone", data.getPhone());
                        map.put("lastmessage", encrypF);
                        map.put("lastsender", mAuth.getCurrentUser().getUid());
                        map.put("lastsenderava", data.getAvatar());
                        map.put("messDate", currTime);
                        map.put("id", friendId);
                        //  ------------
                        mData.child(mAuth.getCurrentUser().getUid()).child(friendId).updateChildren(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {

                                relativeLayout.setVisibility(View.GONE);
                                sendMessNotify(encrypF, messidL);
                            }
                        })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        relativeLayout.setVisibility(View.GONE);
                                    }
                                });
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(Chat.this, R.string.Failed, Toast.LENGTH_SHORT).show();
                relativeLayout.setVisibility(View.GONE);
            }
        });
    }

    private void sendVpre(String link, long time) {
        encrypL = link;
        encrypL = encryption.encryptOrNull(encrypL);
        final Map<String, Object> map = new HashMap<>();
        map.put("linkV", encrypL);
        map.put("time", currTime);
        map.put("messId", messidL);
        map.put("duration", getHumanTimeText(time));
        map.put("seen", false);
        map.put("type", "voice");
        map.put("deleted", false);
        map.put("statue", "✔");
        map.put("from", mAuth.getCurrentUser().getUid());
        mData.child(mAuth.getCurrentUser().getUid()).child(friendId).child(Global.Messages).child(messidL)
                .updateChildren(map).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                mData.child(friendId).child(mAuth.getCurrentUser().getUid()).child(Global.Messages).child(messidL)
                        .updateChildren(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        //send friend data to owner
                        mAuth = FirebaseAuth.getInstance();
                        Map<String, Object> map = new HashMap<>();
                        map.put("avatar", userData.getAvatar());
                        map.put("name", userData.getName());
                        map.put("nameL", userData.getNameL());
                        map.put("phone", data.getPhone());
                        map.put("lastmessage", encrypV);
                        map.put("lastsender", mAuth.getCurrentUser().getUid());
                        map.put("lastsenderava", data.getAvatar());
                        map.put("messDate", currTime);
                        map.put("id", friendId);
                        //  ------------
                        mData.child(mAuth.getCurrentUser().getUid()).child(friendId).updateChildren(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {

                                relativeLayout.setVisibility(View.GONE);
                                sendMessNotify(encrypV, messidL);
                            }
                        })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        relativeLayout.setVisibility(View.GONE);
                                    }
                                });
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(Chat.this, R.string.Failed, Toast.LENGTH_SHORT).show();
                relativeLayout.setVisibility(View.GONE);

            }
        });
    }

    private void sendVideopre(String link, long time, String thumbL) {
        encrypL = link;
        encrypL = encryption.encryptOrNull(encrypL);
        final Map<String, Object> map = new HashMap<>();
        map.put("linkVideo", encrypL);
        map.put("time", currTime);
        map.put("messId", messidL);
        map.put("thumb", thumbL);
        map.put("duration", getHumanTimeText(time));
        map.put("seen", false);
        map.put("type", "video");
        map.put("deleted", false);
        map.put("statue", "✔");
        map.put("from", mAuth.getCurrentUser().getUid());
        mData.child(mAuth.getCurrentUser().getUid()).child(friendId).child(Global.Messages).child(messidL)
                .updateChildren(map).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                mData.child(friendId).child(mAuth.getCurrentUser().getUid()).child(Global.Messages).child(messidL)
                        .updateChildren(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        //send friend data to owner
                        mAuth = FirebaseAuth.getInstance();
                        Map<String, Object> map = new HashMap<>();
                        map.put("avatar", userData.getAvatar());
                        map.put("name", userData.getName());
                        map.put("nameL", userData.getNameL());
                        map.put("phone", data.getPhone());
                        map.put("lastmessage", encrypVideo);
                        map.put("lastsender", mAuth.getCurrentUser().getUid());
                        map.put("lastsenderava", data.getAvatar());
                        map.put("messDate", currTime);
                        map.put("id", friendId);
                        //  ------------
                        mData.child(mAuth.getCurrentUser().getUid()).child(friendId).updateChildren(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {

                                relativeLayout.setVisibility(View.GONE);
                                sendMessNotify(encrypVideo, messidL);
                            }
                        })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        relativeLayout.setVisibility(View.GONE);
                                    }
                                });
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(Chat.this, R.string.Failed, Toast.LENGTH_SHORT).show();
                relativeLayout.setVisibility(View.GONE);

            }
        });
    }

    private boolean startRecording() {
        mRecorder = new MediaRecorder();
        mRecorder.setAudioChannels(2);
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.HE_AAC);
        mRecorder.setAudioEncodingBitRate(48000);

        mRecorder.setAudioSamplingRate(16000);
        mOutputFile = getOutputFile();
        mOutputFile.getParentFile().mkdirs();
        mRecorder.setOutputFile(mOutputFile.getAbsolutePath());

        try {
            mRecorder.prepare();
            mRecorder.start();
            return true;
        } catch (Exception e) {
            return false;
        }
    }


    protected void stopRecording(boolean saveFile) {
        try {
            mRecorder.stop();
            mRecorder.release();
            mRecorder = null;
            if (!saveFile && mOutputFile != null) {
                mOutputFile.delete();
            }
        } catch (NullPointerException e) {

        }

    }

    private File getOutputFile() {
        return new File(Environment.getExternalStorageDirectory().getAbsolutePath().toString()
                + "/" + getResources().getString(R.string.app_name) + "/Voice Notes/" + "rec_voice_" + String.valueOf(System.currentTimeMillis())
                + ".m4a");
    }

    private String getHumanTimeText(long milliseconds) {
        return String.format("%02d:%02d",
                TimeUnit.MILLISECONDS.toMinutes(milliseconds),
                TimeUnit.MILLISECONDS.toSeconds(milliseconds) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(milliseconds)));
    }

    private void editInf() {
        name.setText(Global.currname);
        if (String.valueOf(Global.currAva).equals("no")) {
            Picasso.get()
                    .load(R.drawable.profile)
                    .error(R.drawable.errorimg)
                    .into(ava);
        } else {
            Picasso.get()
                    .load(Global.currAva)
                    .error(R.drawable.errorimg)
                    .into(ava);
        }
        if (Global.check_int(this))
            state.setVisibility(View.VISIBLE);
        else
            state.setVisibility(View.GONE);

        if (Global.onstate) {
            if (typingR)
                state.setText(R.string.typing);

            else
                state.setText(getResources().getString(R.string.online));
        } else {
            state.setText(GetTime.getTimeAgo(Global.currtime, Chat.this));
            final ExecutorService es = Executors.newCachedThreadPool();
            ScheduledExecutorService ses = Executors.newSingleThreadScheduledExecutor();
            ses.scheduleAtFixedRate(new Runnable() {
                @Override
                public void run() {
                    es.submit(new Runnable() {
                        @Override
                        public void run() {
                            state.setText(GetTime.getTimeAgo(Global.currtime, Chat.this));
                            Toast.makeText(Chat.this, GetTime.getTimeAgo(Global.currtime, Chat.this), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }, 0, 1, TimeUnit.MINUTES);
        }

    }

    public void typingit() {

        if (Global.onstate) {
            if (typingR)
                state.setText(R.string.typing);

            else
                state.setText(getResources().getString(R.string.online));

        } else {
            state.setText(GetTime.getTimeAgo(Global.currtime, Chat.this));
            final ExecutorService es = Executors.newCachedThreadPool();
            ScheduledExecutorService ses = Executors.newSingleThreadScheduledExecutor();
            ses.scheduleAtFixedRate(new Runnable() {
                @Override
                public void run() {
                    es.submit(new Runnable() {
                        @Override
                        public void run() {
                            state.setText(GetTime.getTimeAgo(Global.currtime, Chat.this));
                            Toast.makeText(Chat.this, GetTime.getTimeAgo(Global.currtime, Chat.this), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }, 0, 1, TimeUnit.MINUTES);
        }
    }

    @Override
    public void onResume() {
        //get realtime time
        h.postDelayed(runnable = new Runnable() {
            public void run() {
                if (!Global.onstate)
                    state.setText(GetTime.getTimeAgo(Global.currtime, Chat.this));
                h.postDelayed(runnable, TIMEUPDATE);
            }
        }, TIMEUPDATE);
        //resume
        super.onResume();
        if (!Global.onstate)
            state.setText(GetTime.getTimeAgo(Global.currtime, Chat.this));

        AppBack myApp = (AppBack) this.getApplication();
        if (myApp.wasInBackground) {
            //init data
            Map<String, Object> map = new HashMap<>();
            map.put(Global.Online, true);
            myData.child(mAuth.getCurrentUser().getUid()).updateChildren(map);
            Global.local_on = true;
        }
        myApp.stopActivityTransitionTimer();
        Global.currentpageid = friendId;
        //clear all notifications
        NotificationManager notificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
        try {
            if (notificationManager != null) {
                notificationManager.cancelAll();
                int count = 0;
                //store it again
                ((AppBack) getApplication()).editSharePrefs().putInt("numN" + mAuth.getCurrentUser().getUid(), count);
                ((AppBack) getApplication()).editSharePrefs().apply();
                ShortcutBadger.applyCount(this, count);
            }
        } catch (NullPointerException e) {
            //nothing
        }
        //read messages
        if (code == 0) {
            zeroCount();
            readM();
        }
        //check contact exist
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(Chat.this, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(Chat.this, new String[]{Manifest.permission.READ_CONTACTS}, 1);
            } else {
                contactExistsLay(contactExists(Chat.this, Global.currphone));
            }
        } else
            contactExistsLay(contactExists(Chat.this, Global.currphone));


        Global.currentactivity = this;
    }

    @Override
    public void onPause() {
        h.removeCallbacks(runnable); //stop handler when activity not visible
        super.onPause();
        ((AppBack) this.getApplication()).startActivityTransitionTimer();
        try {
            stopRecording(false);
        } catch (RuntimeException e) {
        }
        setResult(RESULT_CANCELED);
        Global.currentpageid = "";

    }

    private void getSize() {
        if (message.getLineCount() == message.getMaxLines()) {
            message.setMaxLines((message.getLineCount() + 1));
        }

    }

    private void sendMessNotify(final String message, final String Mid) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference(Global.tokens);
        databaseReference.orderByKey().equalTo(friendId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Tokens tokens = snapshot.getValue(Tokens.class);
                    String Ptoken = FirebaseInstanceId.getInstance().getToken();
                    Map<String, String> map = new HashMap<>();
                    map.put("title", Ptoken + "#" + mAuth.getCurrentUser().getUid() + "#" + Global.nameLocal + "#" + Global.avaLocal + "#" + Mid);
                    map.put("message", message);
                    Sender sender = new Sender(tokens.getTokens(), map);
                    fcm.send(sender)
                            .enqueue(new Callback<FCMresp>() {
                                @Override
                                public void onResponse(Call<FCMresp> call, Response<FCMresp> response) {
                                }

                                @Override
                                public void onFailure(Call<FCMresp> call, Throwable t) {

                                }
                            });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void goP(View view) {
        Intent intent = new Intent(this, Profile.class);
        intent.putExtra("idP", friendId);
        startActivity(intent);
    }

    public boolean contactExists(Context context, String number) {
        Uri lookupUri = Uri.withAppendedPath(
                ContactsContract.PhoneLookup.CONTENT_FILTER_URI,
                Uri.encode(number));
        String[] mPhoneNumberProjection = {ContactsContract.PhoneLookup._ID, ContactsContract.PhoneLookup.NUMBER, ContactsContract.PhoneLookup.DISPLAY_NAME};
        Cursor cur = context.getContentResolver().query(lookupUri, mPhoneNumberProjection, null, null, null);
        try {
            if (cur.moveToFirst()) {
                return true;
            }
        } finally {
            if (cur != null)
                cur.close();
        }
        return false;
    }

    public void contactExistsLay(boolean exist) {
        boolean check = preferences.getBoolean("close_" + friendId + "_" + mAuth.getCurrentUser().getUid(), false);
        if (!exist && !check)
            existlay.setVisibility(View.VISIBLE);
        else
            existlay.setVisibility(View.GONE);

    }

}