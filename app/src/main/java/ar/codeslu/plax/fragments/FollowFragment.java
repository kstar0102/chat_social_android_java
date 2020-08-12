package ar.codeslu.plax.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;

import androidx.fragment.app.Fragment;

import java.util.ArrayList;

import ar.codeslu.plax.R;
import ar.codeslu.plax.adapters.FollowFragAdapter;
import ar.codeslu.plax.adapters.MessageAdapter;
import ar.codeslu.plax.follow.CreateSocial;
import ar.codeslu.plax.follow.SocialFollow;
import ar.codeslu.plax.models.MessageModel;

public class FollowFragment extends Fragment {
    ListView listView;
    ArrayList Listitem=new ArrayList<>();
    LinearLayout creatSocialBtn;
    public static FollowFragment newInstance() {
        FollowFragment fragment = new FollowFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){
        final View view = inflater.inflate(R.layout.fragment_follow , container, false);
        listView = view.findViewById(R.id.listView);
        creatSocialBtn = view.findViewById(R.id.add_social);

        Listitem.add(new MessageModel("Chemical Group", "New idea", R.drawable.socal1));
        Listitem.add(new MessageModel("De la salla", "Everything are here", R.drawable.socal2));

        FollowFragAdapter myAdapter=new FollowFragAdapter(getActivity(),R.layout.item_follow_custom, Listitem);
        listView.setAdapter(myAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getActivity(), SocialFollow.class);
                startActivity(intent);
            }
        });

        creatSocialBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), CreateSocial.class);
                startActivity(intent);
            }
        });
        return view;
    }
}
