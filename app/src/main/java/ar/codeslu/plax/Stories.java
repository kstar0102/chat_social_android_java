package ar.codeslu.plax;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;

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

        addStory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Stories.this,AddStory.class));
            }
        });

    }

}
