package com.andrew.link.fragments;

import android.os.Bundle;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.fragment.app.Fragment;

import java.util.ArrayList;

import com.andrew.link.R;
import com.andrew.link.adapters.GalleryAdapter;
import com.andrew.link.adapters.Group;
import com.andrew.link.models.GalleryModel;

public class GalleryFragment extends Fragment {
    SparseArray<Group> groups = new SparseArray<Group>();
    public static GalleryFragment newInstance() {
        GalleryFragment fragment = new GalleryFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fram_message, container, false);
        ListView listView = (ListView)view.findViewById(R.id.listView);

        GalleryModel john = new GalleryModel(R.drawable.img1, R.drawable.img2, R.drawable.img3);
        GalleryModel jane = new GalleryModel(R.drawable.img4, R.drawable.img5, R.drawable.img6);
        GalleryModel james = new GalleryModel(R.drawable.img7, R.drawable.img8, R.drawable.img9);

        ArrayList<Object> people = new ArrayList<>();
        people.add("July");
        people.add(john);
        people.add(jane);
        people.add("June");
        people.add(james);

        listView.setAdapter(new GalleryAdapter(getContext(), people));

        return view;
    }
}



