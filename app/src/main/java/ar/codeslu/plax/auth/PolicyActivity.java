package ar.codeslu.plax.auth;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import ar.codeslu.plax.MainActivity;
import ar.codeslu.plax.R;

public class PolicyActivity extends AppCompatActivity {
    Button nextBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_policy);
        nextBtn = findViewById(R.id.next_btn);
        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PolicyActivity.this, Login.class);
                startActivity(intent);
            }
        });
    }
}
