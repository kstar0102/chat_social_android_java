package ar.codeslu.plax.fragments;


import android.Manifest;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

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
import java.util.List;

import ar.codeslu.plax.Groups.AddGroup;
import ar.codeslu.plax.Groups.Group;
import ar.codeslu.plax.R;
import ar.codeslu.plax.custom.GroupCelect;
import ar.codeslu.plax.datasetters.DialogDataG;
import ar.codeslu.plax.global.AppBack;
import ar.codeslu.plax.global.Global;
import ar.codeslu.plax.holders.DialogHolderG;
import ar.codeslu.plax.models.GroupDialog;

import com.stfalcon.chatkit.me.GroupIn;


/**
 * A simple {@link Fragment} subclass.
 */
public class Groups extends Fragment
        implements DialogsListAdapterG.OnDialogClickListener<GroupDialog>,
        DialogsListAdapterG.OnDialogLongClickListener<GroupDialog> {

    //view
    FloatingActionButton fab;
    View view;
    //arrays
    //firebase
    DatabaseReference mData;
    FirebaseAuth mAuth;
    ArrayList<GroupDialog> dialogs;
    //new
    ImageLoader imageLoader;
    static DialogsListAdapterG<GroupDialog> dialogsAdapter;
    DialogsListG dialogsList;
    static String idR = "";
    //get Message query

    public Groups() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_groups, container, false);
       fab = (FloatingActionButton) view.findViewById(R.id.fab);
        dialogs = new ArrayList<>();
        //chat screen list
        dialogsList = (DialogsListG) view.findViewById(R.id.dialogsList);
        mAuth = FirebaseAuth.getInstance();
        imageLoader = new ImageLoader() {
            @Override
            public void loadImage(ImageView imageView, String url, Object payload) {

            }
        };
        if (mAuth.getCurrentUser() != null)
            getChats();

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Dexter.withActivity(getActivity())
                        .withPermissions(Manifest.permission.READ_CONTACTS)
                        .withListener(new MultiplePermissionsListener() {
                            @Override public void onPermissionsChecked(MultiplePermissionsReport report) {

                                if(report.areAllPermissionsGranted())
                                    startActivity(new Intent(Global.mainActivity, AddGroup.class));

                                else
                                    Toast.makeText(getActivity(), getString(R.string.acc_per), Toast.LENGTH_SHORT).show();


                            }
                            @Override public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {

                                token.continuePermissionRequest();

                            }
                        }).check();
            }
        });
        return view;
    }

    private void getChats() {
        final long[] keyOnce = {0};
        final int[] i = {0};
        //getride of old data
        if(mAuth.getCurrentUser() != null)
            ((AppBack) Global.mainActivity.getApplication()).getdialogdbG(mAuth.getCurrentUser().getUid());
        mData = FirebaseDatabase.getInstance().getReference(Global.USERS).child(mAuth.getCurrentUser().getUid()).child(Global.GROUPS);
        Query query = mData.orderByChild("messDate");
        query.keepSynced(true);
        initAdapter();
        //if offline
        if(!Global.check_int(Global.mainActivity))
        {
            //update the list
            dialogsAdapter.clear();
            dialogsAdapter.setItems(DialogDataG.getDialogs(getActivity()));
            dialogsAdapter.notifyDataSetChanged();
        }
        else
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
                            ((AppBack) Global.mainActivity.getApplication()).setdialogdbG(mAuth.getCurrentUser().getUid());

                        //update the list
                        dialogsAdapter.clear();
                        dialogsAdapter.setItems(DialogDataG.getDialogs(getActivity()));
                        dialogsAdapter.notifyDataSetChanged();
                    }

                    i[0]++;
                }
            }
            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                GroupIn chats = dataSnapshot.getValue(GroupIn.class);
                String tempID ="";
                try {
                    if(Global.yourM)
                        tempID = chats.getLastsender();
                    else
                        tempID = mAuth.getCurrentUser().getUid();


                    if(tempID.equals(mAuth.getCurrentUser().getUid()))
                        tempID = "DUMMY";

                    if (!tempID.equals("DUMMY")) {

                        try {
                            if (Global.diaGGG.size() != 0 && chats.getId() != null && dialogsAdapter.halbine(Global.diaGGG, chats.getId()) != -1)
                                Global.diaGGG.set(dialogsAdapter.halbine(Global.diaGGG, chats.getId()), chats);
                        } catch (NullPointerException e) {
                            Global.yourM = true;
                        }

                        //local store
                        ((AppBack) Global.mainActivity.getApplication()).setdialogdbG(mAuth.getCurrentUser().getUid());
                        dialogsAdapter.setItems(DialogDataG.getDialogs(getActivity()));
                        dialogsAdapter.notifyDataSetChanged();
                    }
                    else if(tempID.equals("DUMMY"))
                    {
                        boolean isUpdated = dialogsAdapter.updateDialogWithMessage(Global.diaGGG, Global.Dialogid, Global.groupG);

                        if (!isUpdated) {
                            Global.diaGGG.add(Global.DialogonelistG.get(0));
                            if (mAuth.getCurrentUser() != null)
                                ((AppBack) Global.mainActivity.getApplication()).setdialogdbG(mAuth.getCurrentUser().getUid());

                            dialogsAdapter.notifyDataSetChanged();

                        }
                        else
                            Global.diaGGG = dialogsAdapter.updateLocal();

                        if (mAuth.getCurrentUser() != null)
                            ((AppBack) Global.mainActivity.getApplication()).setdialogdbG(mAuth.getCurrentUser().getUid());


                        //update the list
                        dialogsAdapter.clear();
                        dialogsAdapter.setItems(DialogDataG.getDialogs(getActivity()));
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
                GroupIn chats = dataSnapshot.getValue(GroupIn.class);

                if (Global.diaGGG.size() != 0) {
                    if (dialogsAdapter.halbine(Global.diaGGG, chats.getId()) != -1)
                        Global.diaGGG.remove(dialogsAdapter.halbine(Global.diaGGG, chats.getId()));
                }
                //local store
                ((AppBack) Global.mainActivity.getApplication()).setdialogdbG(mAuth.getCurrentUser().getUid());
                dialogsAdapter.clear();


                dialogsAdapter.setItems(DialogDataG.getDialogs(getActivity()));
                dialogsAdapter.notifyDataSetChanged();

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


    //for example
    public void onNewMessage() {
        mAuth = FirebaseAuth.getInstance();

        ((AppBack) Global.mainActivity.getApplication()).getdialogdbG(mAuth.getCurrentUser().getUid());
        boolean isUpdated = dialogsAdapter.updateDialogWithMessage(Global.diaGGG, Global.Dialogid, Global.groupG);

        if (!isUpdated) {
            Global.diaGGG.add(Global.DialogonelistG.get(0));
            if (mAuth.getCurrentUser() != null)
                ((AppBack) Global.mainActivity.getApplication()).setdialogdbG(mAuth.getCurrentUser().getUid());

            dialogsAdapter.notifyDataSetChanged();

        }
        else
            Global.diaGGG = dialogsAdapter.updateLocal();


        if (mAuth.getCurrentUser() != null)
            ((AppBack) Global.mainActivity.getApplication()).setdialogdbG(mAuth.getCurrentUser().getUid());


        //update the list
        dialogsAdapter.clear();
        dialogsAdapter.setItems(DialogDataG.getDialogs(getActivity()));
        dialogsAdapter.notifyDataSetChanged();

    }

    //for example
    private void onNewDialog(GroupDialog dialog) {
        dialogsAdapter.addItem(dialog);
    }

    public static void refreshL(String id) {
        dialogsAdapter.deleteById(id);
    }
}