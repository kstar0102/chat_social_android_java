package ar.codeslu.plax.settingsitems;

import android.database.Cursor;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.io.File;

import ar.codeslu.plax.R;
import ar.codeslu.plax.global.AppBack;

public class TestWall extends AppCompatActivity {

    Uri pathW;
    LinearLayout ly;
    Button cancel,setw;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_wall);
        ly = findViewById(R.id.messagesList);
setw = findViewById(R.id.setw);
cancel= findViewById(R.id.cancel);
        if(getIntent() != null)
        {
           pathW = Uri.parse(getIntent().getExtras().getString("wall334"));
            File f = new File(getRealPathFromURI(pathW));
            Drawable d = Drawable.createFromPath(f.getAbsolutePath());
            ly.setBackground(d);
        }
cancel.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        finish();
    }
});
setw.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        ((AppBack) getApplication()).editSharePrefs().putString("wall", String.valueOf(pathW));
        ((AppBack) getApplication()).editSharePrefs().apply();
        Toast.makeText(TestWall.this, getResources().getString(R.string.wallsucc), Toast.LENGTH_SHORT).show();
        finish();
    }
});
    }
    private String getRealPathFromURI(Uri contentURI) {
        Cursor cursor = getContentResolver().query(contentURI, null, null, null, null);
        if (cursor == null) {
            return contentURI.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            return cursor.getString(idx);
        }
    }
}

