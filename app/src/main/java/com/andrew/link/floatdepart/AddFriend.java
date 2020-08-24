package com.andrew.link.floatdepart;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.andrew.link.R;
import com.andrew.link.adapters.ContactsU;
import com.andrew.link.adapters.SearchAdapter;
import com.andrew.link.models.AddFriendModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

import com.google.firebase.database.ChildEventListener;
import com.stfalcon.chatkit.me.UserIn;

public class AddFriend extends AppCompatActivity {
     ImageView backbtn;
     EditText searchEdit;
     LinearLayout friendBtn, contactBtn, BtnLayout, searchlayout;
     ListView listView;
    private ContactsU mUserListAdapter;
    ArrayList Listitem=new ArrayList<>();
    ArrayList<UserIn> userList = new ArrayList<UserIn>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addfriend);

        backbtn = findViewById(R.id.back_btn);
        searchEdit = findViewById(R.id.search_edit);
        friendBtn = findViewById(R.id.friendBtn);
        contactBtn = findViewById(R.id.contact_btn);
        BtnLayout = findViewById(R.id.BtnLayout);
        searchlayout = findViewById(R.id.search_layout);
        listView = findViewById(R.id.listView);
        SearchAdapter myAdapter=new SearchAdapter(AddFriend.this, R.layout.item_search_friend, Listitem);
        FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();
        DatabaseReference mDbRef = mDatabase.getReference("Users");

        ChildEventListener childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
                UserIn user = dataSnapshot.getValue(UserIn.class);
                userList.add(user);
                Log.e("user:", String.valueOf(userList));
            }
            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String previousChildName) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String previousChildName) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        mDbRef.addChildEventListener(childEventListener);
        listView.setAdapter(myAdapter);

        searchEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String string = searchEdit.getText().toString();

                for(int i = 0; i < userList.size(); i++){
                    if(userList.get(i).getPhone().contains(string)){
                        Listitem.add(new AddFriendModel(userList.get(i).getId(), userList.get(i).getName(), userList.get(i).getPhone(), userList.get(i).getAvatar()));
                    }
                }
                BtnLayout.setVisibility(View.GONE);
                searchlayout.setVisibility(View.VISIBLE);

                if(string.length() == 0){
                    BtnLayout.setVisibility(View.VISIBLE);
                    searchlayout.setVisibility(View.GONE);
                }
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
