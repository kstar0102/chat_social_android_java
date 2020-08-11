package ar.codeslu.plax.notify;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

import ar.codeslu.plax.R;
import ar.codeslu.plax.adapters.FriActAdapter;
import ar.codeslu.plax.models.FriModel;

public class FriendActivity extends AppCompatActivity {
    ImageView backbtn;
    ListView listView;
    ArrayList Listitem=new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friendrequest);

        backbtn = findViewById(R.id.back_btn);
        listView = findViewById(R.id.listView);

        Listitem.add(new FriModel("Janet Fowler", "+63 1234567890",R.drawable.janate));
        Listitem.add(new FriModel("Jason boyd", "@jason",R.drawable.jason));
        Listitem.add(new FriModel("Nicholas dunn", "+63 4325611354",R.drawable.nicolas));
        Listitem.add(new FriModel("Carol Clark", "+1 54354354343",R.drawable.carol));
        Listitem.add(new FriModel("Ann Carroll", "@annCarrol",R.drawable.ann));
        Listitem.add(new FriModel("Jeffrey Lawerence", "+3 3424256464",R.drawable.jeffery));

        FriActAdapter myAdapter=new FriActAdapter(FriendActivity.this, R.layout.item_friend, Listitem);
        listView.setAdapter(myAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(FriendActivity.this, FriIndiviReq.class);
                startActivity(intent);
            }
        });

        backbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }
}
