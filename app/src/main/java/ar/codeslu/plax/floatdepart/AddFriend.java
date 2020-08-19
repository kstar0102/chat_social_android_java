package ar.codeslu.plax.floatdepart;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

import ar.codeslu.plax.Chat;
import ar.codeslu.plax.R;
import ar.codeslu.plax.adapters.ContactsU;
import ar.codeslu.plax.adapters.SearchAdapter;
import ar.codeslu.plax.global.Global;
import ar.codeslu.plax.lists.UserData;
import ar.codeslu.plax.models.AddFriendModel;
import ar.codeslu.plax.models.FriModel;
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
                System.out.println(searchEdit.getText().toString());
                String string = searchEdit.getText().toString();
                Log.e("textchange", String.valueOf(string.length()));

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
