package com.codeslutest.plax.floatdepart;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.alphabetik.Alphabetik;

import java.util.ArrayList;

import com.codeslutest.plax.R;
import com.codeslutest.plax.adapters.BroadAdapter;
import com.codeslutest.plax.models.BroadModel;

public class BroadcastActivity extends AppCompatActivity {
    Button nextBtn;
    ImageView backbtn;
    final String[] orderedData = new String[]{"Ann Carroll", "Carol Clark", "Janet Fowler", "Jason Boyd", "Jeffery Nicholas Dunn"};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_broadcast);

        nextBtn = findViewById(R.id.next_btn);
        backbtn = findViewById(R.id.back_btn);

        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(BroadcastActivity.this, BroadSend.class);
                startActivity(intent);
            }
        });

        backbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        ListView listView = (ListView)findViewById(R.id.listView);

        BroadModel broad1 = new BroadModel("Ann Carroll", "+63 1234567890", R.drawable.ann);
        BroadModel broad2 = new BroadModel("Carol Clark", "+1 23452342345", R.drawable.carol);
        BroadModel broad3 = new BroadModel("Janet Fowler", "+7 51234567890", R.drawable.janate);
        BroadModel broad4 = new BroadModel("Jason Boyd", "+44 1234567890", R.drawable.jason);
        BroadModel broad5 = new BroadModel("Jeffery Lawreence", "+63 2215486154", R.drawable.jeffery);
        BroadModel broad6 = new BroadModel("Nicholas Dunn", "+966 432456324", R.drawable.nicolas);

        ArrayList<Object> docment = new ArrayList<>();
        docment.add("A");
        docment.add(broad1);
        docment.add("C");
        docment.add(broad2);
        docment.add("J");
        docment.add(broad3);
        docment.add(broad4);
        docment.add(broad5);
        docment.add("N");
        docment.add(broad6);
//
        listView.setAdapter(new BroadAdapter(BroadcastActivity.this, docment));


        Alphabetik alphabetik = (Alphabetik) findViewById(R.id.alphSectionIndex);
        alphabetik.onSectionIndexClickListener(new Alphabetik.SectionIndexClickListener() {
            @Override
            public void onItemClick(View view, int position, String character) {
                String info = " Position = " + position + " Char = " + character;
                Log.i("View: ", view + "," + info);
                //Toast.makeText(getBaseContext(), info, Toast.LENGTH_SHORT).show();
                listView.smoothScrollToPosition(getPositionFromData(character));
            }
        });

    }
    private int getPositionFromData(String character) {
        int position = 0;
        for (String s : orderedData) {
            String letter = "" + s.charAt(0);
            if (letter.equals("" + character)) {
                return position;
            }
            position++;
        }
        return 0;
    }
}
