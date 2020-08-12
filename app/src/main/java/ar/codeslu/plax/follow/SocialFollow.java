package ar.codeslu.plax.follow;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;

import ar.codeslu.plax.R;
import ar.codeslu.plax.adapters.FollowAdapter;
import ar.codeslu.plax.adapters.MainAdapter;
import ar.codeslu.plax.custom.ChatCelect;

public class SocialFollow extends AppCompatActivity {
    ViewPager vp;
    private Activity mActivity;
    ImageView backbtn, menubtn;
    LinearLayout social_profile;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_socail);

        vp = findViewById(R.id.view_pager_main);
        backbtn = findViewById(R.id.back_btn);
        menubtn = findViewById(R.id.social_menu);

        TabLayout tabLayout = findViewById(R.id.main_tab_layout);
        tabLayout.addTab(tabLayout.newTab().setText("Home"));
        tabLayout.addTab(tabLayout.newTab().setText("Followers"));
        tabLayout.addTab(tabLayout.newTab().setText("Creator"));
        tabLayout.getTabAt(0).setIcon(R.drawable.selected1);
        tabLayout.getTabAt(1).setIcon(R.drawable.friend_icon);
        tabLayout.getTabAt(2).setIcon(R.drawable.chat_icon);
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        social_profile = findViewById(R.id.social_profile);

        FollowAdapter followAdapter = new FollowAdapter(getSupportFragmentManager(), tabLayout.getTabCount());
        vp.setAdapter(followAdapter);

        vp.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                vp.setCurrentItem(tab.getPosition());
                switch (tab.getPosition())
                {
                    case 0:
                        tab.setIcon(R.drawable.selected1);
                        tabLayout.getTabAt(1).setIcon(R.drawable.friend_icon);
                        tabLayout.getTabAt(2).setIcon(R.drawable.chat_icon);

                        break;
                    case 1:
                        tab.setIcon(R.drawable.selected2);
                        tabLayout.getTabAt(0).setIcon(R.drawable.chat_icon);
                        tabLayout.getTabAt(2).setIcon(R.drawable.chat_icon);
                        break;
                    case 2:
                        tab.setIcon(R.drawable.selected1);
                        tabLayout.getTabAt(0).setIcon(R.drawable.chat_icon);
                        tabLayout.getTabAt(1).setIcon(R.drawable.friend_icon);
                        break;
                }
            }
            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }
            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        backbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        menubtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                 LayoutInflater layoutInflater = LayoutInflater.from(SocialFollow.this);
                View promptView = layoutInflater.inflate(R.layout.dialog_social, null);
                final AlertDialog dialog = new AlertDialog.Builder(SocialFollow.this, R.style.CustomAlertDialog).create();
                dialog.setView(promptView);

                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                WindowManager.LayoutParams abc= dialog.getWindow().getAttributes();
                abc.gravity = Gravity.TOP | Gravity.RIGHT;
                abc.x = 10;   //x position
                abc.y = 200;   //y position
                dialog.show();
                Window window = dialog.getWindow();
                window.setLayout(600, LinearLayout.LayoutParams.WRAP_CONTENT);
            }
        });

        social_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SocialFollow.this, SocialProfile.class);
                startActivity(intent);
            }
        });
    }
}
