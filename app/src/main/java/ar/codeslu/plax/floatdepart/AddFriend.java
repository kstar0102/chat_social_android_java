package ar.codeslu.plax.floatdepart;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

import ar.codeslu.plax.R;

public class AddFriend extends AppCompatActivity {
     ImageView backbtn, nextbtn;
     EditText searchEdit;
     LinearLayout friendBtn, contactBtn, numberlayout, namelayout, BtnLayout;
     Button person1, person2, nameBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addfriend);

        backbtn = findViewById(R.id.back_btn);
        searchEdit = findViewById(R.id.search_edit);
        friendBtn = findViewById(R.id.friendBtn);
        contactBtn = findViewById(R.id.contact_btn);
        nextbtn = findViewById(R.id.next_btn);
        numberlayout = findViewById(R.id.numberlayout);
        namelayout = findViewById(R.id.namelayout);
        BtnLayout = findViewById(R.id.BtnLayout);
        person1 = findViewById(R.id.numberAdd);
        person2 = findViewById(R.id.numberAdd1);
        nameBtn = findViewById(R.id.nameAdd);

        nextbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println(searchEdit.getText().toString());
                String string = searchEdit.getText().toString();
                if (string == "janet"){
                    namelayout.setVisibility(View.VISIBLE);
                    BtnLayout.setVisibility(View.GONE);
                }else {
                    BtnLayout.setVisibility(View.GONE);
                    numberlayout.setVisibility(View.VISIBLE);
                    namelayout.setVisibility(View.GONE);
                }

            }
        });

        person1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AddFriend.this, RequestNumber.class);
                intent.putExtra("type", "phone");
                startActivity(intent);
            }
        });

        nameBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AddFriend.this, RequestNumber.class);
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
