package ar.codeslu.plax.story;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.jgabrielfreitas.core.BlurImageView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import ar.codeslu.plax.Favourite;
import ar.codeslu.plax.MainActivity;
import ar.codeslu.plax.R;
import ar.codeslu.plax.global.AppBack;
import ar.codeslu.plax.global.Global;
import xute.storyview.StoryModel;
import xute.storyview.StoryPlayerProgressView;
import xute.storyview.StoryPreference;

public class StoryPlayer extends AppCompatActivity implements StoryPlayerProgressView.StoryPlayerListener {
    public static final String STORY_IMAGE_KEY = "storyImages";
    StoryPlayerProgressView storyPlayerProgressView;
    ImageView imageView, edit;
    TextView name;
    TextView time;
    ArrayList<StoryModel> stories;
    StoryPreference storyPreference;
    DatabaseReference mData;
    FirebaseAuth mAuth;
    private float x1, x2, y1, y2;
    Long t1, t2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_story_player);
        Global.currentactivity = this;
        mAuth = FirebaseAuth.getInstance();
        mData = FirebaseDatabase.getInstance().getReference(Global.USERS);
        storyPlayerProgressView = findViewById(R.id.progressBarView);
        name = findViewById(R.id.storyUserName);
        time = findViewById(R.id.storyTime);
        storyPlayerProgressView.setSingleStoryDisplayTime(5500);
        imageView = findViewById(R.id.storyImage);
        edit = findViewById(R.id.edit);
        edit.setVisibility(View.GONE);
        storyPreference = new StoryPreference(this);
        Intent intent = getIntent();
        if (intent != null) {
            stories = intent.getParcelableArrayListExtra(STORY_IMAGE_KEY);
            initStoryProgressView();
        }
        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(StoryPlayer.this, EditStory.class));

            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        storyPlayerProgressView.resumeProgress();
        Global.currentactivity = this;

        if (mAuth.getCurrentUser() != null) {
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


    }

    @Override
    public void onPause() {
        super.onPause();
        storyPlayerProgressView.pauseProgress();
        ((AppBack) this.getApplication()).startActivityTransitionTimer();
        Global.currentactivity = null;
    }

    private void initStoryProgressView() {
        if (stories != null && stories.size() > 0) {
            storyPlayerProgressView.setStoryPlayerListener(this);
            storyPlayerProgressView.setProgressBarsCount(stories.size());
            setTouchListener();
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setTouchListener() {
        imageView.setOnTouchListener(new View.OnTouchListener() {
            private static final long CLICK_DURATION = 500;

            @Override
            public boolean onTouch(View view, MotionEvent event) {
                switch (event.getAction()) {

                    case MotionEvent.ACTION_DOWN:
                        x1 = event.getX();
                        y1 = event.getY();
                        t1 = System.currentTimeMillis();
                        storyPlayerProgressView.pauseProgress();
                        return true;
                    case MotionEvent.ACTION_UP:
                        x2 = event.getX();
                        y2 = event.getY();
                        t2 = System.currentTimeMillis();
                        if ((t2 - t1) >= CLICK_DURATION) {
                            storyPlayerProgressView.resumeProgress();
                        } else {
                            storyPlayerProgressView.index = storyPlayerProgressView.index + 1;
                            storyPlayerProgressView.startAnimating(true);

                        }


                        return true;
                }
                return true;
            }
        });
    }


    @Override
    public void onStartedPlaying(int index) {
        loadImage(index);
        if (stories.size() > 0)
            name.setText(stories.get(stories.size() - 1).name);

        time.setText(stories.get(index).time);

        mAuth = FirebaseAuth.getInstance();

        if (mAuth.getCurrentUser() != null) {
            if (stories.get(index).id.contains(mAuth.getCurrentUser().getUid()))
                edit.setVisibility(View.VISIBLE);
            else
                edit.setVisibility(View.GONE);
        } else
            edit.setVisibility(View.GONE);


        storyPreference.setStoryVisited(stories.get(index).imageUri);
    }

    @Override
    public void onFinishedPlaying() {
        finish();
    }

    private void loadImage(int index) {
        storyPlayerProgressView.pauseProgress();
        try {
            Glide.with(this)
                    .load(stories.get(index).imageUri)
                    .transition(DrawableTransitionOptions.withCrossFade(800))
                    .listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                            storyPlayerProgressView.resumeProgress();
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                            storyPlayerProgressView.resumeProgress();
                            return false;
                        }
                    })
                    .into(imageView);
        } catch (IllegalArgumentException e) {

        }
    }


}
