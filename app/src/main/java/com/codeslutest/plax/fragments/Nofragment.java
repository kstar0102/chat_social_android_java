package com.codeslutest.plax.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import java.util.ArrayList;

import com.codeslutest.plax.R;
import com.codeslutest.plax.adapters.NoAdapter;
import com.codeslutest.plax.models.Notifimodel;
import com.codeslutest.plax.notify.FriendActivity;

public class Nofragment extends Fragment {
    LinearLayout request;
    ListView listView;
    TextView badge;
    public static Nofragment newInstance() {
        Nofragment notificationFrag = new Nofragment();
        return notificationFrag;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){
//        final MainActivity current = ((MainActivity)getActivity());
//        current.notifylay();

        final View view = inflater.inflate(R.layout.fragment_notifi, container, false);
        badge = view.findViewById(R.id.dialogUnreadBubble);
        listView = view.findViewById(R.id.listView);
        badge.setText("5");

        Notifimodel notifimodel1 = new Notifimodel("Chemical Group", "Invited you to follow Chemical group", "Yesterday", R.drawable.socal1);
        Notifimodel notifimodel2 = new Notifimodel("De la Salle", "New post by Jone", "Mon", R.drawable.social2);
        Notifimodel notifimodel3 = new Notifimodel("New Social Group", "You Posted New Social Group", "Mon", R.drawable.social3);

        ArrayList<Object> notify = new ArrayList<>();
        notify.add("Social Group");
        notify.add(notifimodel1);
        notify.add(notifimodel2);
        notify.add(notifimodel3);

        listView.setAdapter(new NoAdapter(getActivity(), notify));

        request = view.findViewById(R.id.friend_req);
        request.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), FriendActivity.class);
                startActivity(intent);
            }
        });
        return view;
    }
}
