package ar.codeslu.plax.story;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.vanniktech.emoji.EmojiEditText;
import com.vanniktech.emoji.EmojiTextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import ar.codeslu.plax.MainActivity;
import ar.codeslu.plax.Mute;
import ar.codeslu.plax.R;
import ar.codeslu.plax.adapters.ArchieveA;
import ar.codeslu.plax.adapters.BlockMuteA;
import ar.codeslu.plax.adapters.EditStoryA;
import ar.codeslu.plax.global.AppBack;
import ar.codeslu.plax.global.Global;
import ar.codeslu.plax.global.encryption;
import ar.codeslu.plax.lists.BlockL;
import ar.codeslu.plax.lists.StoryList;
import ar.codeslu.plax.lists.StoryListRetr;
import ar.codeslu.plax.lists.UserData;

import xute.storyview.StoryModel;

public class Archieve extends AppCompatActivity {

    RecyclerView storyList;
    ArchieveA adapter;
    DatabaseReference mData;
    FirebaseAuth mAuth;

    ImageView sora;
    private EmojiTextView arch;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_archieve);
        Global.currentactivity = this;
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        storyList = findViewById(R.id.storylist);
        sora = findViewById(R.id.sora);
        arch = findViewById(R.id.arch);
        mAuth = FirebaseAuth.getInstance();
        mData = FirebaseDatabase.getInstance().getReference(Global.USERS);

        ((AppBack) getApplication()).getStoryArch();

        //dark mode init
        if (mAuth.getCurrentUser() != null) {
            if (!((AppBack) getApplication()).shared().getBoolean("dark" + mAuth.getCurrentUser().getUid(), false)) {
                getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            } else {
                getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            }
        }

        try {
            if (mAuth.getCurrentUser() != null) {
                if (!((AppBack) Global.mainActivity.getApplication()).shared().getBoolean("dark" + mAuth.getCurrentUser().getUid(), false)) {
                    arch.setTextColor(Global.conMain.getResources().getColor(R.color.black));

                } else {
                    arch.setTextColor(Global.conMain.getResources().getColor(R.color.white));
                }
            }
        } catch (NullPointerException e) {

        }

        //////




        if(Global.check_int(this)) {

            mData.child(mAuth.getCurrentUser().getUid()).child(Global.myStoryS).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Global.ArchiveList.clear();
                    for (DataSnapshot snap : dataSnapshot.getChildren()) {
                        StoryList post = snap.getValue(StoryList.class);
                        if (halbineAAA(Global.ArchiveList, post.getId()) == -1) {
                            Global.ArchiveList.add(new StoryModel(encryption.decryptOrNull(post.getLink()), getString(R.string.me), AppBack.getTimeAgo(post.getTime(), Global.mainActivity), post.getTime(), post.getId()));

                        }
                    }
                        arrange();
                        ((AppBack) getApplication()).setStoryArch();
                        adapter = new ArchieveA(Global.ArchiveList);
                        storyList.setAdapter(adapter);
                        storyList.setLayoutManager(new GridLayoutManager(Archieve.this, 4, RecyclerView.VERTICAL, false));
                        adapter.notifyDataSetChanged();

                        if (Global.ArchiveList.size() > 0) {
                            sora.setVisibility(View.GONE);
                            storyList.setVisibility(View.VISIBLE);
                        } else {
                            sora.setVisibility(View.VISIBLE);
                            storyList.setVisibility(View.GONE);
                        }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
        else
        {
            adapter = new ArchieveA(Global.ArchiveList);
            storyList.setAdapter(adapter);
            storyList.setLayoutManager(new GridLayoutManager(Archieve.this, 4, RecyclerView.VERTICAL, false));
            adapter.notifyDataSetChanged();
        }

    }

    @Override
    public void onResume() {
        super.onResume();
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
    public void onPause() {
        super.onPause();
        ((AppBack) this.getApplication()).startActivityTransitionTimer();
        Global.currentactivity = null;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(Archieve.this, MainActivity.class));
        finish();
    }

    public int halbineAAA(ArrayList<StoryModel> ml, String id) {
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

    private void arrange() {
        StoryModel temp;
long time1=0,time2=0;
        for (int i = 0; i < Global.ArchiveList.size(); i++) {
            if (i != Global.ArchiveList.size() - 1) {
                String [] all = Global.ArchiveList.get(i).getId().split("_");
                if(all.length>1)
                 time1 = Long.parseLong(all[1]);
                String [] all2 = Global.ArchiveList.get(i+1).getId().split("_");
                if(all2.length>1)
                     time2 = Long.parseLong(all2[1]);

                if (time1 < time2) {
                    temp = Global.ArchiveList.get(i);
                    Global.ArchiveList.set(i, Global.ArchiveList.get(i + 1));
                    Global.ArchiveList.set(i + 1, temp);
                    arrange();
                    break;
                }
            }

        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }
}
