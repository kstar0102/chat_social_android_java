package ar.codeslu.plax;

import android.os.Bundle;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.View;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import ar.codeslu.plax.adapters.BlockMuteA;
import ar.codeslu.plax.global.AppBack;
import ar.codeslu.plax.global.Global;
import ar.codeslu.plax.lists.BlockL;
import ar.codeslu.plax.lists.UserData;

public class Mute extends AppCompatActivity {


    DatabaseReference mMute, mUser;
    FirebaseAuth mAuth;
    BlockMuteA adapter;
    RecyclerView recyclerView;
    ImageView sora;
    ValueEventListener child;
    Query query;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mute);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        Global.currentactivity = this;

        mAuth = FirebaseAuth.getInstance();

        //dark mode init
        try {
            if (mAuth.getCurrentUser() != null) {
                if (!((AppBack) getApplication()).shared().getBoolean("dark" + mAuth.getCurrentUser().getUid(), false)) {
                    getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                } else {
                    getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                }
            }
        } catch (NullPointerException e) {

        }

        if (Global.ADMOB_ENABLE) {
            AdView mAdView = findViewById(R.id.adView);
            AdRequest adRequest = new AdRequest.Builder().build();
            mAdView.loadAd(adRequest);
        }

        recyclerView = findViewById(R.id.recycle);
        sora = findViewById(R.id.sora);

        mMute = FirebaseDatabase.getInstance().getReference(Global.MUTE);
        mUser = FirebaseDatabase.getInstance().getReference(Global.USERS);

        sora.setVisibility(View.GONE);
        recyclerView.setVisibility(View.VISIBLE);

        if(!Global.check_int(this)) {
            sora.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        }


        Global.tempUser = new ArrayList<>();

       query = mMute.child(mAuth.getCurrentUser().getUid());
       child = query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(mAuth.getCurrentUser()!=null) {

                    if (dataSnapshot.exists()) {
                    ((AppBack) getApplication()).getMute();
                    BlockL blockL = dataSnapshot.getValue(BlockL.class);
                    Global.mutelist.clear();
                    Global.tempUser.clear();
                    Global.mutelist = blockL.getList();
                    ((AppBack) getApplication()).setMute();
                    getUsers();
                    sora.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);


                } else {
                        ((AppBack) getApplication()).getMute();
                        Global.mutelist.clear();
                        Global.tempUser.clear();
                        ((AppBack) getApplication()).setMute();
                        adapter.notifyItemRangeRemoved(0, Global.mutelist.size());
                        adapter.notifyDataSetChanged();
                        adapter = new BlockMuteA(Global.mutelist, false);
                        recyclerView.setAdapter(adapter);
                        recyclerView.setLayoutManager(new LinearLayoutManager(Mute.this));
                        sora.setVisibility(View.VISIBLE);
                        recyclerView.setVisibility(View.GONE);

                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        if (Global.check_int(this)) {
            ((AppBack) getApplication()).getMute();
            adapter = new BlockMuteA(Global.mutelist, false);
            recyclerView.setAdapter(adapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(Mute.this));
        }

    }

    public void getUsers() {
        for (int i = 0; i < Global.mutelist.size(); i++) {
            int finalI = i;
            mUser.child(Global.mutelist.get(i)).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    UserData userData = dataSnapshot.getValue(UserData.class);
                    Global.tempUser.add(userData);
                    if (finalI == Global.mutelist.size() - 1) {
                        adapter.notifyDataSetChanged();
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        query.removeEventListener(child);
    }

    @Override
    protected void onResume() {
        super.onResume();
        DatabaseReference mData = FirebaseDatabase.getInstance().getReference(Global.USERS);
        Global.currentactivity = this;
        AppBack myApp = (AppBack) this.getApplication();
        if (myApp.wasInBackground) {
            //init data
            Map<String, Object> map = new HashMap<>();
            map.put(Global.Online, true);
            if(mAuth.getCurrentUser() != null)
                mData.child(mAuth.getCurrentUser().getUid()).updateChildren(map);
            Global.local_on = true;
            //lock screen
            ((AppBack) getApplication()).lockscreen(((AppBack) getApplication()).shared().getBoolean("lock", false));
        }

        myApp.stopActivityTransitionTimer();
    }
    @Override
    protected void onPause() {
        super.onPause();
        ((AppBack) this.getApplication()).startActivityTransitionTimer();
        Global.currentactivity = null;

    }
}
