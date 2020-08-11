package ar.codeslu.plax.floatdepart;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import ar.codeslu.plax.R;
import de.hdodenhof.circleimageview.CircleImageView;

public class RequestNumber extends AppCompatActivity {
    ImageView backBtn;
    CircleImageView userPic;
    TextView username, useremail, userphone;
    String gettype, getname;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reqnumber);
        init();
        backBtn = findViewById(R.id.back_btn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        Intent intent = getIntent();
        gettype = intent.getStringExtra("type");
        if(gettype != null){
            useremail.setText("@janet");
            userPic.setImageResource(R.drawable.janate);
            username.setText("Janet Fowler");
            userphone.setVisibility(View.VISIBLE);
            userphone.setText("+ 123456789012");
        }
        useremail.setText("@janet");
        userPic.setImageResource(R.drawable.janate);
        username.setText("Janet Fowler");
    }

    private void init(){
        userPic = findViewById(R.id.userPic);
        username = findViewById(R.id.username);
        useremail = findViewById(R.id.userEmail);
        userphone = findViewById(R.id.userphone);
    }
}
