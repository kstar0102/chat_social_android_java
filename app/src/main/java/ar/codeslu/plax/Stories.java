package ar.codeslu.plax;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;

import java.util.ArrayList;

import ar.codeslu.plax.adapters.StoryAdapter;
import ar.codeslu.plax.lists.StoryList;

public class Stories extends AppCompatActivity {
    RecyclerView storyList;
    StoryAdapter adapter;
    ArrayList<StoryList> array;
    ImageView addStory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stories);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        storyList = findViewById(R.id.storylist);
        addStory = findViewById(R.id.newS);
        array = new ArrayList<>();
        adapter = new StoryAdapter(array);


    }

}
