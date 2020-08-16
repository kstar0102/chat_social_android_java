package ar.codeslu.plax.fragments;


import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ShortcutInfo;
import android.content.pm.ShortcutManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Icon;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.squareup.picasso.Picasso;
import com.stfalcon.chatkit.commons.ImageLoader;
import com.stfalcon.chatkit.dialogs.DialogsList;
import com.stfalcon.chatkit.dialogs.DialogsListAdapter;
import com.stfalcon.chatkit.utils.DateFormatter;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import ar.codeslu.plax.Chat;
import ar.codeslu.plax.Contacts;
import ar.codeslu.plax.Groups.AddGroup;
import ar.codeslu.plax.MainActivity;
import ar.codeslu.plax.Qr;
import ar.codeslu.plax.R;
import ar.codeslu.plax.custom.ChatCelect;
import ar.codeslu.plax.datasetters.DialogData;
import ar.codeslu.plax.floatdepart.AddFriend;
import ar.codeslu.plax.floatdepart.BroadcastActivity;
import ar.codeslu.plax.floatdepart.CreateGroup;
import ar.codeslu.plax.floatdepart.QRActivity;
import ar.codeslu.plax.global.AppBack;
import ar.codeslu.plax.global.Global;
import ar.codeslu.plax.holders.DialogHolder;
import ar.codeslu.plax.lists.UserData;
import ar.codeslu.plax.models.DefaultDialog;

import uk.co.samuelwall.materialtaptargetprompt.MaterialTapTargetPrompt;
import xute.storyview.StoryModel;

import com.stfalcon.chatkit.me.UserIn;

/**
 * Created by CodeSlu
 */
public class Chats extends Fragment
        implements DialogsListAdapter.OnDialogClickListener<DefaultDialog>,
        DialogsListAdapter.OnDialogLongClickListener<DefaultDialog> {

    //view
    FloatingActionButton fab;
    View view;
    //arrays
    //firebase
    DatabaseReference mData, mBlock, mMute;
    FirebaseAuth mAuth;
    ArrayList<DefaultDialog> dialogs;
    //new
    ImageLoader imageLoader;
    static DialogsListAdapter<DefaultDialog> dialogsAdapter;
    DialogsList dialogsList;
    static String idR = "";
    //get Message query

    //stories
    ImageView sora;
    //Firebase
    DatabaseReference myData, mUserDB, mOtherData, mTime;

    //vars
    ArrayList<StoryModel> otherS;
    ArrayList<UserIn> tempUser;

    ArrayList<UserData> userList, contactList, searchL;
    String name = "", ava = "", id = "";
    int enter = 0;
    long now = 0;

    //enc

    int count = 0;
    String idTemp;

    boolean first = true;
    private Activity mActivity;

    public Chats() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
//        final MainActivity current = ((MainActivity)getActivity());
//        current.mainTop();

        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_chats, container, false);
        fab = (FloatingActionButton) view.findViewById(R.id.fab);
        sora = view.findViewById(R.id.sora);
        dialogs = new ArrayList<>();
        //chat screen list
        dialogsList = (DialogsList) view.findViewById(R.id.dialogsList);
        mAuth = FirebaseAuth.getInstance();
        mMute = FirebaseDatabase.getInstance().getReference(Global.MUTE);
        mBlock = FirebaseDatabase.getInstance().getReference(Global.BLOCK);
        sora.setVisibility(View.GONE);

        imageLoader = new ImageLoader() {
            @Override
            public void loadImage(ImageView imageView, String url, Object payload) {

            }
        };


        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LayoutInflater layoutInflater = LayoutInflater.from(getContext());
                View promptView = layoutInflater.inflate(R.layout.dialog_float, null);
                final AlertDialog dialog = new AlertDialog.Builder(getContext(),R.style.CustomAlertDialog).create();
                dialog.setView(promptView);
                Button addFriendBtn = promptView.findViewById(R.id.dialog_addfriend);
                Button creatGroupBtn = promptView.findViewById(R.id.diglog_creatgroup);
                Button creatSocialBtn = promptView.findViewById(R.id.dialog_creatsocial);
                Button qrBtn = promptView.findViewById(R.id.dialog_qr);
                Button brodcartBtn = promptView.findViewById(R.id.dialog_broadcast);
                Button customerBtn = promptView.findViewById(R.id.dialog_customer);

                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                WindowManager.LayoutParams abc= dialog.getWindow().getAttributes();
                abc.gravity = Gravity.BOTTOM | Gravity.RIGHT;
                abc.x = 50;   //x position
                abc.y = 400;   //y position
                dialog.show();
                Window window = dialog.getWindow();
                window.setLayout(900, LinearLayout.LayoutParams.WRAP_CONTENT);

                addFriendBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Dexter.withActivity(mActivity).withPermissions(Manifest.permission.READ_CONTACTS)
                            .withListener(new MultiplePermissionsListener() {
                                @Override
                                public void onPermissionsChecked(MultiplePermissionsReport report) {

                                    if (report.areAllPermissionsGranted())
                                        startActivity(new Intent(mActivity, AddFriend.class));
                                    else
                                        Toast.makeText(mActivity, mActivity.getString(R.string.acc_per), Toast.LENGTH_SHORT).show();
                                }
                                @Override
                                public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {

                                    token.continuePermissionRequest();

                                }
                        }).check();
//                        Intent intent = new Intent(getActivity(), AddFriend.class);
//                        startActivity(intent);
                    }
                });

                creatGroupBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
//                        Dexter.withActivity(mActivity).withPermissions(Manifest.permission.READ_CONTACTS)
//                            .withListener(new MultiplePermissionsListener() {
//                                @Override
//                                public void onPermissionsChecked(MultiplePermissionsReport report) {
//
//                                    if (report.areAllPermissionsGranted())
//                                        startActivity(new Intent(Global.mainActivity, AddGroup.class));
//                                    else
//                                        Toast.makeText(mActivity, getString(R.string.acc_per), Toast.LENGTH_SHORT).show();
//                                }
//
//                                @Override
//                                public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
//                                    token.continuePermissionRequest();
//                                }
//                        }).check();
                        Intent intent = new Intent(getActivity(), CreateGroup.class);
                        startActivity(intent );
                    }
                });

                qrBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getActivity(), QRActivity.class);
                        startActivity(intent);
                    }
                });

                brodcartBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getActivity(), BroadcastActivity.class);
                        startActivity(intent);
                    }
                });
//
            }
        });

        dialogsList.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0 || dy < 0 && fab.isShown())
                    fab.hide();
            }

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if (newState == RecyclerView.SCROLL_STATE_IDLE)
                    fab.show();
                super.onScrollStateChanged(recyclerView, newState);
            }
        });

        if (mAuth.getCurrentUser() != null) {

            if (((AppBack) mActivity.getApplication()).shared().getBoolean("hinter" + mAuth.getCurrentUser().getUid(), true)) {
                ((AppBack) mActivity.getApplication()).editSharePrefs().putBoolean("hinter" + mAuth.getCurrentUser().getUid(), false);
                ((AppBack) mActivity.getApplication()).editSharePrefs().apply();
//                view.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
//                    @Override
//                    public void onGlobalLayout() {
//                        view.getViewTreeObserver().removeOnGlobalLayoutListener(this);
//                        new MaterialTapTargetPrompt.Builder(mActivity)
//                            .setTarget(R.id.fab)
//                            .setBackgroundColour(ContextCompat.getColor(getContext(), R.color.colorPrimary))
//                            .setPrimaryText(getString(R.string.contacts))
//                            .setSecondaryText(getString(R.string.you_can_hint))
//                            .setPromptStateChangeListener(new MaterialTapTargetPrompt.PromptStateChangeListener() {
//                                @Override
//                                public void onPromptStateChanged(MaterialTapTargetPrompt prompt, int state) {
//                                    if (state == MaterialTapTargetPrompt.STATE_FOCAL_PRESSED) {
//                                        prompt.finish();
//                                        ((AppBack) mActivity.getApplication()).editSharePrefs().putBoolean("hinter" + mAuth.getCurrentUser().getUid(), false);
//                                        ((AppBack) mActivity.getApplication()).editSharePrefs().apply();
//                                    }
//                                }
//                            })
//                            .show();
//                    }
//                });
            }

            getChats();
        }
        return view;
    }


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mActivity = (Activity) context;
        Global.currentfragment = mActivity;
    }

    private void getChats() {
        final long[] keyOnce = {0};
        final int[] i = {0};
        //getride of old data
        if (mAuth.getCurrentUser() != null) {
            if (isAdded()) {
                ((AppBack) mActivity.getApplication()).getdialogdb(mAuth.getCurrentUser().getUid());
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1)
                    shortcuts();
            }
        }
        mData = FirebaseDatabase.getInstance().getReference(Global.CHATS).child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        Query query = mData.orderByChild("messDate");
        query.keepSynced(true);
        initAdapter();

        //if offline
        try {
            if (Global.diaG.size() == 0) {
                sora.setVisibility(View.VISIBLE);
                dialogsList.setVisibility(View.GONE);
            } else {
                sora.setVisibility(View.GONE);
                dialogsList.setVisibility(View.VISIBLE);

            }
        } catch (NullPointerException e) {

        }

        if (!Global.check_int(mActivity)) {
            //update the list
            dialogsAdapter.clear();
            dialogsAdapter.setItems(DialogData.getDialogs(Global.diaG));
            dialogsAdapter.notifyDataSetChanged();
            mActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    count = 0;
                    for (int j = 0; j < Global.diaG.size(); j++) {
                        count += Global.diaG.get(j).getNoOfUnread();
                    }
//                    try {
//                        if (count == 0)
//                            meowBottomNavigation.setCount(0, "empty");
//                        else if (count > 99)
//                            meowBottomNavigation.setCount(0, String.valueOf(99));
//                        else
//                            meowBottomNavigation.setCount(0, String.valueOf(count));
//                    } catch (NullPointerException e) {
//
//                    }
                }
            });


        } else {
            tempUser = new ArrayList<>(Global.diaG);
            Global.diaG.clear();
            dialogsAdapter.clear();
            dialogsAdapter.setItems(DialogData.getDialogs(tempUser));
            dialogsAdapter.notifyDataSetChanged();

            mActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    count = 0;
                    for (int j = 0; j < tempUser.size(); j++) {
                        count += tempUser.get(j).getNoOfUnread();
                    }
//                    try {
//                        if (count == 0)
//                            meowBottomNavigation.setCount(0, "empty");
//                        else if (count > 99)
//                            meowBottomNavigation.setCount(0, String.valueOf(99));
//                        else
//                            meowBottomNavigation.setCount(0, String.valueOf(count));
//                    } catch (NullPointerException e) {
//
//                    }
                }
            });
        }

        //if online
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                keyOnce[0] = dataSnapshot.getChildrenCount();
                Global.diaG.clear();
                query.addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                        if (dataSnapshot.exists()) {
                            UserIn chats = dataSnapshot.getValue(UserIn.class);
                            try {
                                if (chats.getId() != null) {
                                    if (dialogsAdapter.halbine(Global.diaG, chats.getId()) == -1)
                                        Global.diaG.add(chats);
                                    else
                                        Global.diaG.set(dialogsAdapter.halbine(Global.diaG, chats.getId()), chats);

                                    dialogsAdapter.notifyDataSetChanged();
                                }
                            } catch (NullPointerException e) {
                            }

                            //check only in global list range
                            if (i[0] >= keyOnce[0] - 1) {
                                if (mAuth.getCurrentUser() != null) {
                                    if (isAdded()) {
                                        ((AppBack) mActivity.getApplication()).setdialogdb(mAuth.getCurrentUser().getUid());
                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1)
                                            shortcuts();
                                    }
                                }
                                //update the list
                                dialogsAdapter.clear();
                                dialogsAdapter.setItems(DialogData.getDialogs(Global.diaG));
                                dialogsAdapter.notifyDataSetChanged();
                                count = 0;
                                for (int j = 0; j < Global.diaG.size(); j++) {
                                    count += Global.diaG.get(j).getNoOfUnread();
                                }
                                try {
//                                    if (count == 0)
//                                        meowBottomNavigation.setCount(0, "empty");
//                                    else if (count > 99)
//                                        meowBottomNavigation.setCount(0, String.valueOf(99));
//                                    else
//                                        meowBottomNavigation.setCount(0, String.valueOf(count));

                                    if (Global.diaG.size() == 0) {
                                        sora.setVisibility(View.VISIBLE);
                                        dialogsList.setVisibility(View.GONE);
                                    } else {
                                        sora.setVisibility(View.GONE);
                                        dialogsList.setVisibility(View.VISIBLE);
                                    }
                                } catch (NullPointerException e) {

                                }
                            }
                            i[0]++;
                        }
                    }

                    @Override
                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                        UserIn chats = dataSnapshot.getValue(UserIn.class);
                        try {

                            if (chats.getId() != null && chats.getName() != null) {
                                if (dialogsAdapter.halbine(Global.diaG, chats.getId()) == -1)
                                    Global.diaG.add(chats);
                                else
                                    Global.diaG.set(dialogsAdapter.halbine(Global.diaG, chats.getId()), chats);


                                if (mAuth.getCurrentUser() != null) {
                                    if (isAdded()) {
                                        ((AppBack) mActivity.getApplication()).setdialogdb(mAuth.getCurrentUser().getUid());
                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1)
                                            shortcuts();
                                    }
                                }
                                dialogsAdapter.notifyDataSetChanged();

                                if (mAuth.getCurrentUser() != null) {
                                    if (isAdded()) {
                                        ((AppBack) mActivity.getApplication()).setdialogdb(mAuth.getCurrentUser().getUid());
                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1)
                                            shortcuts();
                                    }
                                }

                                //update the list
                                dialogsAdapter.clear();
                                dialogsAdapter.setItems(DialogData.getDialogs(Global.diaG));
                                dialogsAdapter.notifyDataSetChanged();
                                count = 0;
                                for (int j = 0; j < Global.diaG.size(); j++) {
                                    count += Global.diaG.get(j).getNoOfUnread();
                                }
                                try {
//                                    if (count == 0)
//                                        meowBottomNavigation.setCount(0, "empty");
//                                    else if (count > 99)
//                                        meowBottomNavigation.setCount(0, String.valueOf(99));
//                                    else
//                                        meowBottomNavigation.setCount(0, String.valueOf(count));

                                    if (Global.diaG.size() == 0) {
                                        sora.setVisibility(View.VISIBLE);
                                        dialogsList.setVisibility(View.GONE);
                                    } else {
                                        sora.setVisibility(View.GONE);
                                        dialogsList.setVisibility(View.VISIBLE);

                                    }
                                } catch (NullPointerException e) {

                                }
                            }

                        } catch (
                                NullPointerException e) {

                        }


                    }

                    @Override
                    public void onChildRemoved(DataSnapshot dataSnapshot) {
                        UserIn chats = dataSnapshot.getValue(UserIn.class);

                        if (Global.diaG.size() != 0) {
                            if (dialogsAdapter.halbine(Global.diaG, chats.getId()) != -1)
                                Global.diaG.remove(dialogsAdapter.halbine(Global.diaG, chats.getId()));
                        }
                        //local store
                        if (mAuth.getCurrentUser() != null) {
                            if (isAdded()) {
                                ((AppBack) mActivity.getApplication()).setdialogdb(mAuth.getCurrentUser().getUid());
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1)
                                    shortcuts();
                            }
                        }
                        dialogsAdapter.clear();
                        dialogsAdapter.setItems(DialogData.getDialogs(Global.diaG));
                        dialogsAdapter.notifyDataSetChanged();

                        count = 0;
                        for (int j = 0; j < Global.diaG.size(); j++) {
                            count += Global.diaG.get(j).getNoOfUnread();
                        }
                        try {
//                            if (count == 0)
//                                meowBottomNavigation.setCount(0, "empty");
//                            else if (count > 99)
//                                meowBottomNavigation.setCount(0, String.valueOf(99));
//                            else
//                                meowBottomNavigation.setCount(0, String.valueOf(count));

                            if (Global.diaG.size() == 0) {
                                sora.setVisibility(View.VISIBLE);
                                dialogsList.setVisibility(View.GONE);
                            } else {
                                sora.setVisibility(View.GONE);
                                dialogsList.setVisibility(View.VISIBLE);

                            }
                        } catch (NullPointerException e) {

                        }


                    }

                    @Override
                    public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mBlock.child(mAuth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (mAuth.getCurrentUser() != null)
                            dialogsAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

        mMute.child(mAuth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (mAuth.getCurrentUser() != null)
                            dialogsAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    @Override
    public void onDialogClick(DefaultDialog dialog) {
        //firebase init
        Intent intent = new Intent(mActivity, Chat.class);
        intent.putExtra("id", dialog.getId());
        intent.putExtra("name", dialog.getDialogName());
        intent.putExtra("ava", dialog.getDialogPhoto());
        intent.putExtra("phone", dialog.getDialogPhone());
        intent.putExtra("screen", dialog.isDialogscreen());
        Global.currphone = dialog.getDialogPhone();
        mActivity.startActivity(intent);
    }

    @Override
    public void onDialogLongClick(DefaultDialog dialog) {
        idR = dialog.getId();
        if (Global.check_int(mActivity)) {
            ChatCelect cdd = new ChatCelect(mActivity, dialog.getId());
            cdd.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            cdd.getWindow().getAttributes().windowAnimations = R.style.CustomDialogAnimation;
            cdd.requestWindowFeature(Window.FEATURE_NO_TITLE);
            WindowManager.LayoutParams abc= cdd.getWindow().getAttributes();
            abc.gravity = Gravity.TOP | Gravity.RIGHT;
            abc.x = 20;   //x position
            abc.y = dialogsList.getChildAt(0).getTop() + 300;   //y position

            cdd.show();
        } else
            Toast.makeText(mActivity, R.string.check_int, Toast.LENGTH_SHORT).show();
    }

    private void initAdapter() {

        dialogsAdapter = new DialogsListAdapter<>(R.layout.item_dialog_custom, DialogHolder.class, imageLoader);
        dialogsAdapter.setOnDialogClickListener(this);
        dialogsAdapter.setOnDialogLongClickListener(this);
        dialogsList.setAdapter(dialogsAdapter);
        dialogsAdapter.setDatesFormatter(new DateFormatter.Formatter() {
            @Override
            public String format(Date date) {
                if (DateFormatter.isToday(date)) {
                    DateFormat format = new SimpleDateFormat("hh:mm aa");
                    String timee = format.format(date);
                    return timee;
                } else if (DateFormatter.isYesterday(date)) {
                    return mActivity.getString(R.string.date_header_yesterday);
                } else {
                    return DateFormatter.format(date, DateFormatter.Template.STRING_DAY_MONTH_YEAR);
                }
            }
        });

        Global.globalChatsAdapter = dialogsAdapter;

    }


    public void updatedialog(Activity conn) {
        mAuth = FirebaseAuth.getInstance();

        ((AppBack) conn.getApplication()).getdialogdb(mAuth.getCurrentUser().getUid());

        if (dialogsAdapter.halbine(Global.diaG, Global.Dialogonelist.get(0).getId()) == -1)
            Global.diaG.add(Global.Dialogonelist.get(0));
        else
            Global.diaG.set(dialogsAdapter.halbine(Global.diaG, Global.Dialogonelist.get(0).getId()), Global.Dialogonelist.get(0));


        if (mAuth.getCurrentUser() != null) {
            if (isAdded()) {
                ((AppBack) mActivity.getApplication()).setdialogdb(mAuth.getCurrentUser().getUid());
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1)
                    shortcuts();
            }
        }
        dialogsAdapter.notifyDataSetChanged();


        if (mAuth.getCurrentUser() != null) {
            ((AppBack) conn.getApplication()).setdialogdb(mAuth.getCurrentUser().getUid());
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1)
                shortcuts();
        }

        //update the list
        dialogsAdapter.clear();
        dialogsAdapter.setItems(DialogData.getDialogs(Global.diaG));
        dialogsAdapter.notifyDataSetChanged();

        count = 0;
        for (int j = 0; j < Global.diaG.size(); j++) {
            count += Global.diaG.get(j).getNoOfUnread();
        }
        try {
//            try {
//                if (count == 0)
//                    meowBottomNavigation.setCount(0, "empty");
//                else if (count > 99)
//                    meowBottomNavigation.setCount(0, String.valueOf(99));
//                else
//                    meowBottomNavigation.setCount(0, String.valueOf(count));
//            } catch (NullPointerException e) {
//
//            }

            if (Global.diaG.size() == 0) {
                sora.setVisibility(View.VISIBLE);
                dialogsList.setVisibility(View.GONE);
            } else {
                sora.setVisibility(View.GONE);
                dialogsList.setVisibility(View.VISIBLE);

            }
        } catch (NullPointerException e) {

        }


    }

    //for example
    private void onNewDialog(DefaultDialog dialog) {
        dialogsAdapter.addItem(dialog);
    }

    public static void refreshL() {
        dialogsAdapter.deleteById(idR);
    }




    @Override
    public void onResume() {
        super.onResume();

        try {
            Global.currentfragment = mActivity;
            AppBack myApp = (AppBack) mActivity.getApplication();
            DatabaseReference muse = FirebaseDatabase.getInstance().getReference(Global.USERS);
            if (myApp.wasInBackground) {
                //init data
                Map<String, Object> map = new HashMap<>();
                map.put(Global.Online, true);
                if (mAuth.getCurrentUser() != null)
                    muse.child(mAuth.getCurrentUser().getUid()).updateChildren(map);
                Global.local_on = true;
                //lock screen
                ((AppBack) mActivity.getApplication()).lockscreen(((AppBack) mActivity.getApplication()).shared().getBoolean("lock", false));
            }
            myApp.stopActivityTransitionTimer();


        } catch (NullPointerException e) {

        }


    }

    @Override
    public void onPause() {
        super.onPause();
        Global.currentfragment = null;

    }

    List<ShortcutInfo> listS;

    @RequiresApi(api = Build.VERSION_CODES.N_MR1)
    private void shortcuts() {
        try {
            ((AppBack) mActivity.getApplication()).getdialogdb(mAuth.getCurrentUser().getUid());
            ((AppBack) mActivity.getApplication()).getdialogdbG(mAuth.getCurrentUser().getUid());
            listS = new ArrayList<>();
            ShortcutManager shortcutManager = mActivity.getSystemService(ShortcutManager.class);
            Intent intent = new Intent(mActivity, MainActivity.class);
            intent.putExtra("codetawgeh", 3);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setAction(Intent.ACTION_VIEW);
            ShortcutInfo dynamicShortcut1 = new ShortcutInfo.Builder(mActivity, "addstory")
                    .setShortLabel(getString(R.string.stories))
                    .setLongLabel(getString(R.string.stories))
                    .setIcon(Icon.createWithResource(mActivity, R.drawable.stories))
                    .setIntent(intent)
                    .setRank(3)
                    .build();
            if (Global.diaGGG.size() > 0) {
                Intent intentG = new Intent(mActivity, MainActivity.class);
                intentG.putExtra("name", Global.diaGGG.get(Global.diaGGG.size() - 1).getName());
                intentG.putExtra("id", Global.diaGGG.get(Global.diaGGG.size() - 1).getId());
                intentG.putExtra("ava", Global.diaGGG.get(Global.diaGGG.size() - 1).getAvatar());
                intentG.putExtra("codetawgeh", 2);
                intentG.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intentG.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intentG.setAction(Intent.ACTION_VIEW);
                if(!Global.diaGGG.get(Global.diaGGG.size() - 1).getName().isEmpty()) {
                    ShortcutInfo dynamicShortcut2 = new ShortcutInfo.Builder(mActivity, "group")
                            .setShortLabel(Global.diaGGG.get(Global.diaGGG.size() - 1).getName())
                            .setLongLabel(Global.diaGGG.get(Global.diaGGG.size() - 1).getName())
                            .setIcon(Icon.createWithResource(mActivity, R.drawable.ic_group_b))
                            .setIntent(intentG)
                            .setRank(2)
                            .build();
                    listS.add(dynamicShortcut2);
                }
            }
            if (Global.diaG.size() > 0) {
                Intent intentU1 = new Intent(mActivity, MainActivity.class);
                intentU1.putExtra("name", Global.diaG.get(Global.diaG.size() - 1).getName());
                intentU1.putExtra("id", Global.diaG.get(Global.diaG.size() - 1).getId());
                intentU1.putExtra("ava", Global.diaG.get(Global.diaG.size() - 1).getAvatar());
                intentU1.putExtra("codetawgeh", 1);
                intentU1.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intentU1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intentU1.setAction(Intent.ACTION_VIEW);
                if(!Global.diaG.get(Global.diaG.size() - 1).getName().isEmpty()) {

                    ShortcutInfo dynamicShortcut3 = new ShortcutInfo.Builder(mActivity, "user1")
                            .setShortLabel(Global.diaG.get(Global.diaG.size() - 1).getName())
                            .setLongLabel(Global.diaG.get(Global.diaG.size() - 1).getName())
                            .setIcon(Icon.createWithResource(mActivity, R.drawable.ic_person_b))
                            .setIntent(intentU1)
                            .setRank(0)
                            .build();
                    listS.add(dynamicShortcut3);
                }

            }
            if (Global.diaG.size() > 1) {
                Intent intentU2 = new Intent(mActivity, MainActivity.class);
                intentU2.putExtra("name", Global.diaG.get(Global.diaG.size() - 2).getName());
                intentU2.putExtra("id", Global.diaG.get(Global.diaG.size() - 2).getId());
                intentU2.putExtra("ava", Global.diaG.get(Global.diaG.size() - 2).getAvatar());
                intentU2.putExtra("codetawgeh", 1);
                intentU2.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intentU2.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intentU2.setAction(Intent.ACTION_VIEW);
                if(!Global.diaG.get(Global.diaG.size() - 2).getName().isEmpty()) {

                    ShortcutInfo dynamicShortcut4 = new ShortcutInfo.Builder(mActivity, "user2")
                            .setShortLabel(Global.diaG.get(Global.diaG.size() - 2).getName())
                            .setLongLabel(Global.diaG.get(Global.diaG.size() - 2).getName())
                            .setIcon(Icon.createWithResource(mActivity, R.drawable.ic_person_b))
                            .setIntent(intentU2)
                            .setRank(1)
                            .build();
                    listS.add(dynamicShortcut4);
                }

            }
            listS.add(dynamicShortcut1);
            shortcutManager.setDynamicShortcuts(listS);
        } catch (NullPointerException e) {

        }

    }

}