package ar.codeslu.plax.notify;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import ar.codeslu.plax.R;

public class FriendActivity extends AppCompatActivity {
    ImageView backbtn;
    ListView listView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friendrequest);

        backbtn = findViewById(R.id.back_btn);
        listView = findViewById(R.id.listView);
    }
}
