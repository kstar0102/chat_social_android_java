package ar.codeslu.plax.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import ar.codeslu.plax.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class Groups extends Fragment {

    View view;

    public Groups() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_groups, container, false);


        return view;
    }

}