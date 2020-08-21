package com.codeslutest.plax.fragments;


import android.Manifest;
import android.app.Activity;
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
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.stfalcon.chatkit.commons.ImageLoader;
import com.stfalcon.chatkit.dialogs.DialogsListAdapterG;
import com.stfalcon.chatkit.dialogs.DialogsListG;
import com.stfalcon.chatkit.utils.DateFormatter;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.codeslutest.plax.Groups.AddGroup;
import com.codeslutest.plax.Groups.Group;
import com.codeslutest.plax.MainActivity;
import com.codeslutest.plax.R;
import com.codeslutest.plax.custom.GroupCelect;
import com.codeslutest.plax.datasetters.DialogDataG;
import com.codeslutest.plax.global.AppBack;
import com.codeslutest.plax.global.Global;
import com.codeslutest.plax.holders.DialogHolderG;
import com.codeslutest.plax.models.GroupDialog;

import com.stfalcon.chatkit.me.GroupIn;


/**
 * Created by CodeSlu
 */
public class Groups extends Fragment
        implements DialogsListAdapterG.OnDialogClickListener<GroupDialog>,
        DialogsListAdapterG.OnDialogLongClickListener<GroupDialog> {

    //view
    FloatingActionButton fab;
    View view;
    //arrays
    //firebase
    DatabaseReference mData, mGroup, myData, mBlock, mMute;
    FirebaseAuth mAuth;
    ArrayList<GroupDialog> dialogs;
    ArrayList<GroupIn> tempUser;
    //new
    ImageLoader imageLoader;
    static DialogsListAdapterG<GroupDialog> dialogsAdapter;
    DialogsListG dialogsList;
    static String idR = "";
    //get Message query
    int count = 0;
    LinearLayout sora;
    private Activity mActivity;

    public Groups() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_groups, container, false);
        fab = (FloatingActionButton) view.findViewById(R.id.fab);
        sora = view.findViewById(R.id.sora);
        dialogs = new ArrayList<>();

        //chat screen list
        dialogsList = (DialogsListG) view.findViewById(R.id.dialogsList);
//        if (isAdded())
//            meowBottomNavigation = mActivity.findViewById(R.id.meownav);

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
                Dexter.withActivity(mActivity)
                        .withPermissions(Manifest.permission.READ_CONTACTS)
                        .withListener(new MultiplePermissionsListener() {
                            @Override
                            public void onPermissionsChecked(MultiplePermissionsReport report) {

                                if (report.areAllPermissionsGranted())
                                    startActivity(new Intent(Global.mainActivity, AddGroup.class));

                                else
                                    Toast.makeText(mActivity, getString(R.string.acc_per), Toast.LENGTH_SHORT).show();


                            }

                            @Override
                            public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {

                                token.continuePermissionRequest();

                            }
                        }).check();
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

        if (mAuth.getCurrentUser() != null)
            getChats();
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
                ((AppBack) mActivity.getApplication()).getdialogdbG(mAuth.getCurrentUser().getUid());
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1)
                    shortcuts();
            }
        }

        mData = FirebaseDatabase.getInstance().getReference(Global.USERS).child(mAuth.getCurrentUser().getUid()).child(Global.GROUPS);
        Query query = mData.orderByChild("messDate");
        query.keepSynced(true);
        initAdapter();
        try {
            if (Global.diaGGG.size() == 0) {
                sora.setVisibility(View.VISIBLE);
                dialogsList.setVisibility(View.GONE);
            } else {
                sora.setVisibility(View.GONE);
                dialogsList.setVisibility(View.VISIBLE);

            }
        } catch (NullPointerException e) {

        }


        //if offline
        if (!Global.check_int(Global.mainActivity)) {
            //update the list
            dialogsAdapter.clear();
            dialogsAdapter.setItems(DialogDataG.getDialogs(mActivity, Global.diaGGG));
            dialogsAdapter.notifyDataSetChanged();
            mActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    count = 0;
                    for (int j = 0; j < Global.diaGGG.size(); j++) {
                        count += Global.diaGGG.get(j).getNoOfUnread();
                    }
//                    try {
//                        if (count == 0)
//                            meowBottomNavigation.setCount(2, "empty");
//                        else if (count > 99)
//                            meowBottomNavigation.setCount(2, String.valueOf(99));
//                        else
//                            meowBottomNavigation.setCount(2, String.valueOf(count));
//                    } catch (NullPointerException e) {
//
//                    }
                }
            });
        } else {
            tempUser = new ArrayList<>(Global.diaGGG);
            Global.diaGGG.clear();
            dialogsAdapter.clear();
            dialogsAdapter.setItems(DialogDataG.getDialogs(mActivity, tempUser));
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
//                            meowBottomNavigation.setCount(2, "empty");
//                        else if (count > 99)
//                            meowBottomNavigation.setCount(2, String.valueOf(99));
//                        else
//                            meowBottomNavigation.setCount(2, String.valueOf(count));
//                    } catch (NullPointerException e) {
//
//                    }
                }
            });
        }
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                keyOnce[0] = dataSnapshot.getChildrenCount();
                Global.diaGGG.clear();
                query.addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                        if (dataSnapshot.exists()) {
                            GroupIn chats = dataSnapshot.getValue(GroupIn.class);
                            try {
                                if (chats.getId() != null) {
                                    if (dialogsAdapter.halbine(Global.diaGGG, chats.getId()) == -1)
                                        Global.diaGGG.add(chats);
                                    else
                                        Global.diaGGG.set(dialogsAdapter.halbine(Global.diaGGG, chats.getId()), chats);
                                }
                            } catch (NullPointerException e) {
                            }

                            //check only in global list range
                            if (i[0] >= keyOnce[0] - 1) {
                                //local store
                                if (mAuth.getCurrentUser() != null) {
                                    if (isAdded()) {
                                        ((AppBack) mActivity.getApplication()).setdialogdbG(mAuth.getCurrentUser().getUid());
                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1)
                                            shortcuts();
                                    }
                                }
                                changelast();

                            }

                            i[0]++;
                        }
                    }

                    @Override
                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                        GroupIn chats = dataSnapshot.getValue(GroupIn.class);

                        try {
                            if (chats.getId() != null && chats.getName() != null) {
                                if (dialogsAdapter.halbine(Global.diaGGG, chats.getId()) == -1)
                                    Global.diaGGG.add(chats);
                                else
                                    Global.diaGGG.set(dialogsAdapter.halbine(Global.diaGGG, chats.getId()), chats);


                                if (mAuth.getCurrentUser() != null) {
                                    if (isAdded()) {
                                        ((AppBack) mActivity.getApplication()).setdialogdbG(mAuth.getCurrentUser().getUid());
                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1)
                                            shortcuts();
                                    }
                                }
                                dialogsAdapter.notifyDataSetChanged();


                                if (mAuth.getCurrentUser() != null) {
                                    if (isAdded()) {
                                        ((AppBack) mActivity.getApplication()).setdialogdbG(mAuth.getCurrentUser().getUid());
                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1)
                                            shortcuts();
                                    }
                                }

                                //update the list
                                dialogsAdapter.clear();
                                dialogsAdapter.setItems(DialogDataG.getDialogs(mActivity, Global.diaGGG));
                                dialogsAdapter.notifyDataSetChanged();

                                count = 0;
                                for (int j = 0; j < Global.diaGGG.size(); j++) {
                                    count += Global.diaGGG.get(j).getNoOfUnread();
                                }
                                try {
//                                    if (count == 0)
//                                        meowBottomNavigation.setCount(2, "empty");
//                                    else if (count > 99)
//                                        meowBottomNavigation.setCount(2, String.valueOf(99));
//                                    else
//                                        meowBottomNavigation.setCount(2, String.valueOf(count));

                                    if (Global.diaGGG.size() == 0) {
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
                        GroupIn chats = dataSnapshot.getValue(GroupIn.class);

                        if (Global.diaGGG.size() != 0) {
                            if (dialogsAdapter.halbine(Global.diaGGG, chats.getId()) != -1)
                                Global.diaGGG.remove(dialogsAdapter.halbine(Global.diaGGG, chats.getId()));
                        }
                        //local store
                        if (mAuth.getCurrentUser() != null) {
                            if (isAdded()) {
                                ((AppBack) mActivity.getApplication()).setdialogdbG(mAuth.getCurrentUser().getUid());
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1)
                                    shortcuts();
                            }
                        }
                        dialogsAdapter.clear();


                        dialogsAdapter.setItems(DialogDataG.getDialogs(mActivity, Global.diaGGG));
                        dialogsAdapter.notifyDataSetChanged();

                        count = 0;
                        for (int j = 0; j < Global.diaGGG.size(); j++) {
                            count += Global.diaGGG.get(j).getNoOfUnread();
                        }
                        try {
//                            if (count == 0)
//                                meowBottomNavigation.setCount(2, "empty");
//                            else if (count > 99)
//                                meowBottomNavigation.setCount(2, String.valueOf(99));
//                            else
//                                meowBottomNavigation.setCount(2, String.valueOf(count));

                            if (Global.diaGGG.size() == 0) {
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


        mBlock.child(mAuth.getCurrentUser().

                getUid()).

                addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (mAuth.getCurrentUser() != null)
                            dialogsAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

        mMute.child(mAuth.getCurrentUser().

                getUid()).

                addValueEventListener(new ValueEventListener() {
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

    public void changelast() {
        for (int i = 0; i < Global.diaGGG.size(); i++) {
            mGroup = FirebaseDatabase.getInstance().getReference(Global.GROUPS).child(Global.diaGGG.get(i).getId());
            int finalI = i;
            mGroup.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    GroupIn groupIn = dataSnapshot.getValue(GroupIn.class);
                    try {
                        if (halbine(Global.diaGGG, groupIn.getId()) != -1) {
                            if (!groupIn.getLastsenderava().equals(Global.diaGGG.get(halbine(Global.diaGGG, groupIn.getId())).getLastsenderava())) {
                                Map<String, Object> map = new HashMap<>();
                                map.put("lastsenderava", groupIn.getLastsenderava());

                                myData = FirebaseDatabase.getInstance().getReference(Global.USERS).child(mAuth.getCurrentUser().getUid()).child(Global.GROUPS).child(groupIn.getId());
                                myData.updateChildren(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Global.diaGGG.get(halbine(Global.diaGGG, groupIn.getId())).setLastsenderava(groupIn.getLastsenderava());
                                        //local store
                                        if (mAuth.getCurrentUser() != null) {
                                            if (isAdded()) {
                                                ((AppBack) mActivity.getApplication()).setdialogdbG(mAuth.getCurrentUser().getUid());
                                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1)
                                                    shortcuts();
                                            }
                                        }
                                        if (finalI == Global.diaGGG.size() - 1) {

                                            //update the list
                                            dialogsAdapter.clear();
                                            dialogsAdapter.setItems(DialogDataG.getDialogs(mActivity, Global.diaGGG));
                                            dialogsAdapter.notifyDataSetChanged();

                                            count = 0;
                                            for (int j = 0; j < Global.diaGGG.size(); j++) {
                                                count += Global.diaGGG.get(j).getNoOfUnread();
                                            }
                                            try {
//                                                if (count == 0)
//                                                    meowBottomNavigation.setCount(2, "empty");
//                                                else if (count > 99)
//                                                    meowBottomNavigation.setCount(2, String.valueOf(99));
//                                                else
//                                                    meowBottomNavigation.setCount(2, String.valueOf(count));

                                                if (Global.diaGGG.size() == 0) {
                                                    sora.setVisibility(View.VISIBLE);
                                                    dialogsList.setVisibility(View.GONE);
                                                } else {
                                                    sora.setVisibility(View.GONE);
                                                    dialogsList.setVisibility(View.VISIBLE);

                                                }
                                            } catch (NullPointerException e) {

                                            }


                                        }

                                    }
                                });

                            }
                        }
                    } catch (NullPointerException e) {

                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });


        }
        //update the list
        dialogsAdapter.clear();
        dialogsAdapter.setItems(DialogDataG.getDialogs(mActivity, Global.diaGGG));
        dialogsAdapter.notifyDataSetChanged();
        count = 0;
        for (int j = 0; j < Global.diaGGG.size(); j++) {
            count += Global.diaGGG.get(j).getNoOfUnread();
        }
        try {
//            if (count == 0)
//                meowBottomNavigation.setCount(2, "empty");
//            else if (count > 99)
//                meowBottomNavigation.setCount(2, String.valueOf(99));
//            else
//                meowBottomNavigation.setCount(2, String.valueOf(count));

            if (Global.diaGGG.size() == 0) {
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
    public void onDialogClick(GroupDialog dialog) {
        //firebase init
        Intent intent = new Intent(Global.mainActivity, Group.class);
        intent.putExtra("id", dialog.getId());
        intent.putExtra("name", dialog.getDialogName());
        intent.putExtra("ava", dialog.getDialogPhoto());
        Global.mainActivity.startActivity(intent);
    }


    @Override
    public void onDialogLongClick(GroupDialog dialog) {
        idR = dialog.getId();
        if (Global.check_int(Global.mainActivity)) {
            GroupCelect cdd = new GroupCelect(Global.mainActivity, dialog.getId());
            cdd.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            cdd.getWindow().getAttributes().windowAnimations = R.style.CustomDialogAnimation;
            cdd.show();
        } else
            Toast.makeText(Global.mainActivity, R.string.check_int, Toast.LENGTH_SHORT).show();
    }

    private void initAdapter() {

        dialogsAdapter = new DialogsListAdapterG<>(R.layout.item_dialog_custom, DialogHolderG.class, imageLoader);
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
                    return getString(R.string.date_header_yesterday);
                } else {
                    return DateFormatter.format(date, DateFormatter.Template.STRING_DAY_MONTH_YEAR);
                }
            }
        });
    }



    public void updatedialog(Activity conn) {
        mAuth = FirebaseAuth.getInstance();
        ((AppBack) conn.getApplication()).getdialogdbG(mAuth.getCurrentUser().getUid());

        if (dialogsAdapter.halbine(Global.diaGGG, Global.DialogonelistG.get(0).getId()) == -1)
            Global.diaGGG.add(Global.DialogonelistG.get(0));
        else
            Global.diaGGG.set(dialogsAdapter.halbine(Global.diaGGG, Global.DialogonelistG.get(0).getId()), Global.DialogonelistG.get(0));

        if (mAuth.getCurrentUser() != null) {
            if (isAdded()) {
                ((AppBack) conn.getApplication()).setdialogdbG(mAuth.getCurrentUser().getUid());
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1)
                    shortcuts();
            }
        }
        dialogsAdapter.notifyDataSetChanged();




        if (mAuth.getCurrentUser() != null) {
            if (isAdded()) {
                ((AppBack) conn.getApplication()).setdialogdbG(mAuth.getCurrentUser().getUid());
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1)
                    shortcuts();
            }
        }

        //update the list
        dialogsAdapter.clear();
        dialogsAdapter.setItems(DialogDataG.getDialogs(mActivity, Global.diaGGG));
        dialogsAdapter.notifyDataSetChanged();
        count = 0;
        for (int j = 0; j < Global.diaGGG.size(); j++) {
            count += Global.diaGGG.get(j).getNoOfUnread();
        }
        try {
//            if (count == 0)
//                meowBottomNavigation.setCount(2, "empty");
//            else if (count > 99)
//                meowBottomNavigation.setCount(2, String.valueOf(99));
//            else
//                meowBottomNavigation.setCount(2, String.valueOf(count));

            if (Global.diaGGG.size() == 0) {
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
    private void onNewDialog(GroupDialog dialog) {
        dialogsAdapter.addItem(dialog);
    }

    public static void refreshL(String id) {
        dialogsAdapter.deleteById(id);
    }

    public int halbine(ArrayList<GroupIn> ml, String id) {
        int j = 0, i = 0;
        for (i = 0; i < ml.size(); i++) {
            if (ml.get(i).getId().equals(id)) {
                j = 1;
                break;
            }

        }
        if (j == 1)
            return i;
        else
            return -1;

    }

    @Override
    public void onResume() {
        super.onResume();
        Global.currentfragment = mActivity;
        try {
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
            ((AppBack) Global.mainActivity.getApplication()).getdialogdb(mAuth.getCurrentUser().getUid());
            ((AppBack) Global.mainActivity.getApplication()).getdialogdbG(mAuth.getCurrentUser().getUid());
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