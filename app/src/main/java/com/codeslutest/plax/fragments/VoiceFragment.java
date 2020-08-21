package com.codeslutest.plax.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.codeslutest.plax.R;

public class VoiceFragment extends Fragment {
    public static VoiceFragment newInstance() {
        VoiceFragment fragment = new VoiceFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){
        final View view = inflater.inflate(R.layout.fram_message, container, false);
//        listView = view.findViewById(R.id.simpleListView);
//
//        Listitem.add(new MessageModel("Janet Fowler", "Hello, There?", R.drawable.janate));
//        Listitem.add(new MessageModel("Jason Boyd", "Hello", R.drawable.jason));
//        Listitem.add(new MessageModel("Nicholas Dunn", "Hello, Jone", R.drawable.nicolas));
//
//        MessageAdapter myAdapter=new MessageAdapter(getActivity(),R.layout.item_dialog_custom, Listitem);
//        listView.setAdapter(myAdapter);

        return view;
    }
}
