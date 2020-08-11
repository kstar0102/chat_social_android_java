package ar.codeslu.plax.auth;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import ar.codeslu.plax.R;

public class EmailVerifyAcitivity extends AppCompatActivity {
    TextView email1, email2, wrongBtn;
    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emailverify);

        wrongBtn = findViewById(R.id.wrongemail);
        email1 = findViewById(R.id.email_txt_1);
        email2 = findViewById(R.id.email_txt_2);

        String email = getIntent().getStringExtra("emailStr");

        if(email != null){
            email1.setText(email);
            email2.setText(email);
        }

        wrongBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }
}
