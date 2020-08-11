package ar.codeslu.plax.floatdepart;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import ar.codeslu.plax.R;

public class CreateGroup extends AppCompatActivity {
    ImageView backBtn;
    Button createBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_creategroup);

        backBtn = findViewById(R.id.back_btn);
        createBtn = findViewById(R.id.create_btn);

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }
}
