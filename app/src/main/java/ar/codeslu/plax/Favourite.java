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
import com.squareup.picasso.Picasso;
import com.stfalcon.chatkit.me.MessageFav;
import com.stfalcon.chatkit.me.UserIn;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import ar.codeslu.plax.adapters.BlockMuteA;
import ar.codeslu.plax.adapters.FavA;
import ar.codeslu.plax.global.AppBack;
import ar.codeslu.plax.global.Global;
import ar.codeslu.plax.lists.BlockL;
import ar.codeslu.plax.lists.UserData;
import dmax.dialog.SpotsDialog;
/**
 * Created by CodeSlu
 */
public class Favourite extends AppCompatActivity {


    DatabaseReference mFav;
    FirebaseAuth mAuth;
    FavA adapter;
    RecyclerView recyclerView;
    ImageView sora;
    ImageView bg;
    ArrayList<MessageFav> arrayList;
    Query query;
    ValueEventListener child;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favourite);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        Global.currentactivity = this;

        mAuth = FirebaseAuth.getInstance();
        bg = findViewById(R.id.bg);

        //dark mode init
        try {
            if (mAuth.getCurrentUser() != null) {
                if (!((AppBack) getApplication()).shared().getBoolean("dark" + mAuth.getCurrentUser().getUid(), false)) {
                    getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                    Picasso.get()
                            .load(R.drawable.bg2)
                            .into(bg);
                } else {
                    getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                    Picasso.get()
                            .load(R.drawable.bg3)
                            .into(bg);
                }
            }
        } catch (NullPointerException e) {

        }

        if (Global.ADMOB_ENABLE) {
            AdView mAdView = findViewById(R.id.adView);
            AdRequest adRequest = new AdRequest.Builder().build();
            mAdView.loadAd(adRequest);
        }

        ((AppBack) getApplication()).getFav();

        arrayList = new ArrayList<>(Global.FavMess);

        recyclerView = findViewById(R.id.recycle);
        sora = findViewById(R.id.sora);

        adapter = new FavA(arrayList);
        recyclerView.setAdapter(adapter);

        mFav = FirebaseDatabase.getInstance().getReference(Global.FAV);

        sora.setVisibility(View.GONE);
        recyclerView.setVisibility(View.VISIBLE);
        bg.setVisibility(View.VISIBLE);

        recyclerView.setLayoutManager(new LinearLayoutManager(Favourite.this));


        query = mFav.child(mAuth.getCurrentUser().getUid());
        child = query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (mAuth.getCurrentUser() != null) {
                    if (dataSnapshot.exists()) {
                        ((AppBack) getApplication()).getFav();
                        Global.FavMess.clear();
                        for (DataSnapshot data : dataSnapshot.getChildren()) {
                            MessageFav message = data.getValue(MessageFav.class);
                            Global.FavMess.add(message);
                        }
                        arrange();
                        adapter = new FavA(Global.FavMess);
                        adapter.notifyItemMoved(0, Global.FavMess.size());
                        adapter.notifyDataSetChanged();
                        recyclerView.setAdapter(adapter);
                        ((AppBack) getApplication()).setFav();
                        sora.setVisibility(View.GONE);
                        recyclerView.setVisibility(View.VISIBLE);
                        bg.setVisibility(View.VISIBLE);

                    } else {
                        ((AppBack) getApplication()).getFav();
                        Global.FavMess.clear();
                        ((AppBack) getApplication()).setFav();
                        adapter = new FavA(Global.FavMess);
                        adapter.notifyItemRangeRemoved(0, Global.FavMess.size());
                        adapter.notifyDataSetChanged();
                        recyclerView.setAdapter(adapter);
                        recyclerView.setLayoutManager(new LinearLayoutManager(Favourite.this));
                        sora.setVisibility(View.VISIBLE);
                        recyclerView.setVisibility(View.GONE);
                        bg.setVisibility(View.GONE);

                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    private static void arrange() {
        MessageFav temp;
        for (int i = 0; i < Global.FavMess.size(); i++) {
            if (i != Global.FavMess.size() - 1) {
                if (Global.FavMess.get(i).getFavtime() < Global.FavMess.get(i + 1).getFavtime()) {
                    temp = Global.FavMess.get(i);
                    Global.FavMess.set(i, Global.FavMess.get(i + 1));
                    Global.FavMess.set(i + 1, temp);
                    arrange();
                    break;
                }
            }
        }
    }

    @Override
    protected void onDestroy() {
        query.removeEventListener(child);
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Global.currentactivity = this;
        DatabaseReference mData = FirebaseDatabase.getInstance().getReference(Global.USERS);
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
