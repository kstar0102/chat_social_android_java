package ar.codeslu.plax.fragments;


import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.stfalcon.chatkit.commons.ImageLoader;
import com.stfalcon.chatkit.dialogs.DialogsList;
import com.stfalcon.chatkit.dialogs.DialogsListAdapter;
import com.stfalcon.chatkit.utils.DateFormatter;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import ar.codeslu.plax.Chat;
import ar.codeslu.plax.Contacts;
import ar.codeslu.plax.R;
import ar.codeslu.plax.custom.ChatCelect;
import ar.codeslu.plax.datasetters.DialogData;
import ar.codeslu.plax.global.AppBack;
import ar.codeslu.plax.global.Global;
import ar.codeslu.plax.holders.DialogHolder;
import ar.codeslu.plax.models.DefaultDialog;

import com.stfalcon.chatkit.me.UserIn;

/**
 * A simple {@link Fragment} subclass.
 */
public class Chats extends Fragment
        implements DialogsListAdapter.OnDialogClickListener<DefaultDialog>,
        DialogsListAdapter.OnDialogLongClickListener<DefaultDialog> {

    //view
    FloatingActionButton fab;
    View view;
    //arrays
    //firebase
    DatabaseReference mData;
    FirebaseAuth mAuth;
    ArrayList<DefaultDialog> dialogs;
    //new
    ImageLoader imageLoader;
    static DialogsListAdapter<DefaultDialog> dialogsAdapter;
    DialogsList dialogsList;
    static String idR = "";
    //get Message query

    public Chats() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_chats, container, false);
        fab = (FloatingActionButton) view.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (ContextCompat.checkSelfPermission(Global.mainActivity, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(Global.mainActivity, new String[]{Manifest.permission.READ_CONTACTS}, 1);
                    } else {
                        startActivity(new Intent(Global.mainActivity, Contacts.class));
                    }
                } else
                    startActivity(new Intent(Global.mainActivity, Contacts.class));
            }
        });
        dialogs = new ArrayList<>();
        //chat screen list
        dialogsList = (DialogsList) view.findViewById(R.id.dialogsList);

        imageLoader = new ImageLoader() {
            @Override
            public void loadImage(ImageView imageView, String url, Object payload) {
                if (String.valueOf(url).equals("no")) {
                    Picasso.get()
                            .load(R.drawable.profile)
                            .error(R.drawable.errorimg)
                            .into(imageView);
                } else {
                    Picasso.get()
                            .load(url)
                            .error(R.drawable.errorimg)
                            .into(imageView);
                }
            }
        };
        mAuth = FirebaseAuth.getInstance();

        if (mAuth.getCurrentUser() != null)
            getChats();

        return view;
    }

    private void getChats() {
        final long[] keyOnce = {0};
        final int[] i = {0};
        //getride of old data
        if(mAuth.getCurrentUser() != null)
        ((AppBack) Global.mainActivity.getApplication()).getdialogdb(mAuth.getCurrentUser().getUid());

        mData = FirebaseDatabase.getInstance().getReference(Global.CHATS).child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        Query query = mData.orderByChild("messDate");
        query.keepSynced(true);
        initAdapter();
        //if offline
        if(!Global.check_int(Global.mainActivity))
        {
            //update the list
            dialogsAdapter.clear();
            dialogsAdapter.setItems(DialogData.getDialogs());
            dialogsAdapter.notifyDataSetChanged();
        }
        //if online
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                keyOnce[0] = dataSnapshot.getChildrenCount();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        query.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if (dataSnapshot.exists()) {
                    if (Global.check_int(Global.mainActivity)) {
                        UserIn chats = dataSnapshot.getValue(UserIn.class);
                        try {
                            if (chats.getId() != null) {
                                if (dialogsAdapter.halbine(Global.diaG, chats.getId()) == -1)
                                    Global.diaG.add(chats);
                                else
                                    Global.diaG.set(dialogsAdapter.halbine(Global.diaG, chats.getId()), chats);
                            }
                        } catch (NullPointerException e) {
                        }
                    }
                    //check only in global list range
                    if (i[0] >= keyOnce[0] - 1) {
                        if (Global.check_int(Global.mainActivity)) {
                            //local store
                           ((AppBack) Global.mainActivity.getApplication()).setdialogdb(mAuth.getCurrentUser().getUid());
                        }
                        //update the list
                        dialogsAdapter.clear();

                        dialogsAdapter.setItems(DialogData.getDialogs());
                        dialogsAdapter.notifyDataSetChanged();
                    }

                    i[0]++;
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                UserIn chats = dataSnapshot.getValue(UserIn.class);
                String tempID ="";
                try {
                    if(Global.yourM)
                     tempID = chats.getLastsender();
                    else
                        tempID = mAuth.getCurrentUser().getUid();


                    if(tempID.equals(mAuth.getCurrentUser().getUid()))
                    tempID = "DUMMY";

                    if (!tempID.equals("DUMMY") && Global.check_int(getActivity())) {

                        try {
                            if (Global.diaG.size() != 0 && chats.getId() != null && dialogsAdapter.halbine(Global.diaG, chats.getId()) != -1)
                                Global.diaG.set(dialogsAdapter.halbine(Global.diaG, chats.getId()), chats);
                        } catch (NullPointerException e) {
                            Global.yourM = true;
                        }

                        //local store
                        ((AppBack) Global.mainActivity.getApplication()).setdialogdb(mAuth.getCurrentUser().getUid());
                        dialogsAdapter.setItems(DialogData.getDialogs());
                        dialogsAdapter.notifyDataSetChanged();
                    }
                    else if(tempID.equals("DUMMY")  && Global.check_int(getActivity()))
                    {
                        boolean isUpdated = dialogsAdapter.updateDialogWithMessage(Global.diaG, Global.Dialogid, Global.userrG);

                        if (!isUpdated) {
                            Global.diaG.add(Global.Dialogonelist.get(0));
                            if (mAuth.getCurrentUser() != null)
                                ((AppBack) Global.mainActivity.getApplication()).setdialogdb(mAuth.getCurrentUser().getUid());

                            dialogsAdapter.notifyDataSetChanged();

                        }
                        else
                            Global.diaG = dialogsAdapter.updateLocal();

                        if (mAuth.getCurrentUser() != null)
                            ((AppBack) Global.mainActivity.getApplication()).setdialogdb(mAuth.getCurrentUser().getUid());


                        //update the list
                        dialogsAdapter.clear();
                        dialogsAdapter.setItems(DialogData.getDialogs());
                        dialogsAdapter.notifyDataSetChanged();

                    }
                }
               catch (NullPointerException e) {
                   Global.yourM = true;

                    }

                Global.yourM = true;

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                UserIn chats = dataSnapshot.getValue(UserIn.class);

                if (Global.diaG.size() != 0) {
                    if (dialogsAdapter.halbine(Global.diaG, chats.getId()) != -1)
                        Global.diaG.remove(dialogsAdapter.halbine(Global.diaG, chats.getId()));
                }
                //local store
                ((AppBack) Global.mainActivity.getApplication()).setdialogdb(mAuth.getCurrentUser().getUid());
                dialogsAdapter.clear();


                dialogsAdapter.setItems(DialogData.getDialogs());
                dialogsAdapter.notifyDataSetChanged();

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


//        query.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                if (dataSnapshot.exists()) {
//                    if (userList.size() != 0)
//                        userList.clear();
//                    for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
//                        UserIn author = childSnapshot.getValue(UserIn.class);
//                        userList.add(author);
//                    }
//                    Collections.reverse(userList);
//                    Global.userG = userList;
//
//
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });

    }


    @Override
    public void onDialogClick(DefaultDialog dialog) {
        //firebase init
        Intent intent = new Intent(Global.mainActivity, Chat.class);
        intent.putExtra("id", dialog.getId());
        intent.putExtra("name", dialog.getDialogName());
        intent.putExtra("ava", dialog.getDialogPhoto());
        intent.putExtra("phone", dialog.getDialogPhone());
        intent.putExtra("screen", dialog.isDialogscreen());
        Global.currphone = dialog.getDialogPhone();
        Global.mainActivity.startActivity(intent);
    }


    @Override
    public void onDialogLongClick(DefaultDialog dialog) {
        idR = dialog.getId();
        if (Global.check_int(Global.mainActivity)) {
            ChatCelect cdd = new ChatCelect(Global.mainActivity, dialog.getId());
            cdd.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            cdd.getWindow().getAttributes().windowAnimations = R.style.CustomDialogAnimation;
            cdd.show();
        } else
            Toast.makeText(Global.mainActivity, R.string.check_int, Toast.LENGTH_SHORT).show();
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
                    return getString(R.string.date_header_yesterday);
                } else {
                    return DateFormatter.format(date, DateFormatter.Template.STRING_DAY_MONTH_YEAR);
                }
            }
        });

    }


    //for example
    public void onNewMessage() {
        mAuth = FirebaseAuth.getInstance();

        boolean isUpdated = dialogsAdapter.updateDialogWithMessage(Global.diaG, Global.Dialogid, Global.userrG);

        if (!isUpdated) {
            Global.diaG.add(Global.Dialogonelist.get(0));
            if (mAuth.getCurrentUser() != null)
                ((AppBack) Global.mainActivity.getApplication()).setdialogdb(mAuth.getCurrentUser().getUid());

            dialogsAdapter.notifyDataSetChanged();

        }
else
        Global.diaG = dialogsAdapter.updateLocal();


        if (mAuth.getCurrentUser() != null)
            ((AppBack) Global.mainActivity.getApplication()).setdialogdb(mAuth.getCurrentUser().getUid());


        //update the list
        dialogsAdapter.clear();
        dialogsAdapter.setItems(DialogData.getDialogs());
        dialogsAdapter.notifyDataSetChanged();

    }

    //for example
    private void onNewDialog(DefaultDialog dialog) {
        dialogsAdapter.addItem(dialog);
    }

    public static void refreshL() {
        dialogsAdapter.deleteById(idR);
    }
}