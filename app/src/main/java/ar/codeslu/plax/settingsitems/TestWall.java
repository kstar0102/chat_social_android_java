package ar.codeslu.plax.settingsitems;

import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.provider.MediaStore;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import ar.codeslu.plax.R;
import ar.codeslu.plax.global.AppBack;
import ar.codeslu.plax.global.Global;

public class TestWall extends AppCompatActivity {

    Uri pathW;
    RelativeLayout ly;
    Button cancel, setw;
    DatabaseReference mData;
    FirebaseAuth mAuth;
   ImageView bg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_wall);
        mAuth = FirebaseAuth.getInstance();
        mData = FirebaseDatabase.getInstance().getReference(Global.USERS);
        bg = findViewById(R.id.bg);
        Global.currentactivity = this;

        DisplayMetrics displaymetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        int imagewidth = (int) Math.round(displaymetrics.widthPixels * 0.99);
        int imageheight = (int) Math.round(displaymetrics.heightPixels);
        //dark mode init
        if (mAuth.getCurrentUser() != null) {
            if (!((AppBack) getApplication()).shared().getBoolean("dark" + mAuth.getCurrentUser().getUid(), false)) {
                getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            } else {
                getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            }
        }
        ly = findViewById(R.id.messagesList);
        setw = findViewById(R.id.setw);
        cancel = findViewById(R.id.cancel);
        if (getIntent() != null) {
            pathW = Uri.parse(getIntent().getExtras().getString("wall334"));
            File f = new File(getRealPathFromURI(pathW));
            Picasso.get()
                    .load(pathW)
                    .resize(imagewidth, imageheight)
                    .error(R.drawable.bg2)
                    .into(bg);

        }
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        setw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((AppBack) getApplication()).editSharePrefs().putString("wall", String.valueOf(pathW));
                ((AppBack) getApplication()).editSharePrefs().apply();
                Toast.makeText(TestWall.this, getResources().getString(R.string.wallsucc), Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    private String getRealPathFromURI(Uri contentURI) {
        Cursor cursor = getContentResolver().query(contentURI, null, null, null, null);
        if (cursor == null) {
            return contentURI.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            return cursor.getString(idx);
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
}

