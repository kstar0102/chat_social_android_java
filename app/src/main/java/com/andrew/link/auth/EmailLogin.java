package com.andrew.link.auth;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.andrew.link.R;

public class EmailLogin extends AppCompatActivity {
    Button nextBtn, PhoneNum;
    EditText EmailEdit;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emaillogin);

        nextBtn = findViewById(R.id.nextBtn_email);
        PhoneNum = findViewById(R.id.email_phoneNum_btn);
        EmailEdit = findViewById(R.id.emailEdittext);

        PhoneNum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EmailLogin.this, Login.class);
                startActivity(intent);
            }
        });

        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EmailLogin.this, EmailVerifyAcitivity.class);
                intent.putExtra("emailStr", EmailEdit.getText().toString());
                startActivity(intent);
            }
        });
    }
}
