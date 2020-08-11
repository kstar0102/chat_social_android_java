package ar.codeslu.plax.fragments;


import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.CallLog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
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
import com.stfalcon.chatkit.me.GroupIn;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ar.codeslu.plax.Contacts;
import ar.codeslu.plax.R;
import ar.codeslu.plax.adapters.Calllogs;
import ar.codeslu.plax.datasetters.DialogData;
import ar.codeslu.plax.global.AppBack;
import ar.codeslu.plax.global.Global;
import ar.codeslu.plax.lists.StoryListRetr;
import ar.codeslu.plax.lists.UserData;
import ar.codeslu.plax.lists.calls;

/**
 * Created by CodeSlu
 */
public class Calls extends Fragment {

    View view;
    FloatingActionButton fab;
    RecyclerView recyclerView;
    Calllogs adapter;

    //firebase
    FirebaseAuth mAuth;
    DatabaseReference mCalls;
    LinearLayout sora;
    private Activity mActivity;

    public Calls() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_calls, container, false);
        fab = (FloatingActionButton) view.findViewById(R.id.fab);
        sora = view.findViewById(R.id.sora);

        sora.setVisibility(View.GONE);

        recyclerView = view.findViewById(R.id.callList);

        //firebase
        mAuth = FirebaseAuth.getInstance();
        mCalls = FirebaseDatabase.getInstance().getReference(Global.CALLS);


        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dexter.withActivity(mActivity)
                        .withPermissions(Manifest.permission.READ_CONTACTS)
                        .withListener(new MultiplePermissionsListener() {
                            @Override
                            public void onPermissionsChecked(MultiplePermissionsReport report) {

                                if (report.areAllPermissionsGranted())
                                    startActivity(new Intent(Global.mainActivity, Contacts.class));

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


        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

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

        try {
            ((AppBack) mActivity.getApplication()).getCalls();

        } catch (NullPointerException e) {

        }

        if (mAuth.getCurrentUser() != null) {

        mCalls.child(mAuth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (mAuth.getCurrentUser() != null) {

                    if (!dataSnapshot.exists()) {
                        Global.callList.clear();
                        try {


                            ((AppBack) mActivity.getApplication()).setCalls();

                            sora.setVisibility(View.VISIBLE);
                            recyclerView.setVisibility(View.GONE);


                        } catch (NullPointerException e) {

                        }
                        adapter.notifyItemRangeRemoved(0, Global.callList.size());
                        adapter.notifyDataSetChanged();

                    } else {


                        for (DataSnapshot data1 : dataSnapshot.getChildren()) {
                            mCalls.child(mAuth.getCurrentUser().getUid()).child(data1.getKey())
                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            for (DataSnapshot data2 : dataSnapshot.getChildren()) {
                                                calls call2 = data2.getValue(calls.class);
                                                if (halbine(Global.callList, call2.getId()) == -1) {
                                                    Global.callList.add(call2);
                                                    arrange();
                                                    try {


                                                        ((AppBack) mActivity.getApplication()).setCalls();
                                                    } catch (NullPointerException e) {

                                                    }
                                                    adapter.notifyDataSetChanged();

                                                } else {
                                                    Global.callList.set(halbine(Global.callList, call2.getId()), call2);
                                                    arrange();
                                                    try {


                                                        ((AppBack) mActivity.getApplication()).setCalls();
                                                    } catch (NullPointerException e) {

                                                    }
                                                    adapter.notifyDataSetChanged();

                                                }


                                            }
                                            try {

                                                if (Global.callList.size() == 0) {
                                                    sora.setVisibility(View.VISIBLE);
                                                    recyclerView.setVisibility(View.GONE);
                                                } else {
                                                    sora.setVisibility(View.GONE);
                                                    recyclerView.setVisibility(View.VISIBLE);

                                                }
                                            } catch (NullPointerException e) {

                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                        }
                                    });

                        }

                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
        try {
            adapter = new Calllogs();
            recyclerView.setLayoutManager(new LinearLayoutManager(mActivity));
            recyclerView.setAdapter(adapter);
            adapter.notifyDataSetChanged();

        } catch (NullPointerException e) {

        }

        return view;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        mActivity = (Activity) context;
        Global.currentfragment = mActivity;

    }


    private void arrange() {

        calls temp;
        for (int i = 0; i < Global.callList.size(); i++) {
            if (i != Global.callList.size() - 1) {
                if (Global.callList.get(i).getTime() < Global.callList.get(i + 1).getTime()) {
                    temp = Global.callList.get(i);
                    Global.callList.set(i, Global.callList.get(i + 1));
                    Global.callList.set(i + 1, temp);
                    arrange();
                    break;
                }
            }

        }

        adapter.notifyDataSetChanged();

    }

    public int halbine(ArrayList<calls> ml, String id) {
        int j = 0, i = 0;

        try {
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
        catch (NullPointerException e)
        {
            return -1;

        }

    }

    @Override
    public void onResume() {
        super.onResume();
        Global.currentfragment = mActivity;
        try{
        AppBack myApp = (AppBack) mActivity.getApplication();
        DatabaseReference muse = FirebaseDatabase.getInstance().getReference(Global.USERS);
        if (myApp.wasInBackground) {
            //init data
            Map<String, Object> map = new HashMap<>();
            map.put(Global.Online, true);
            if(mAuth.getCurrentUser() != null)
                muse.child(mAuth.getCurrentUser().getUid()).updateChildren(map);
            Global.local_on = true;
            //lock screen
            ((AppBack) mActivity.getApplication()).lockscreen(((AppBack) mActivity.getApplication()).shared().getBoolean("lock", false));
        }
        myApp.stopActivityTransitionTimer();
    }catch (NullPointerException e)
    {

    }


    }
    @Override
    public void onPause() {
        super.onPause();
        Global.currentfragment = null;

    }
}