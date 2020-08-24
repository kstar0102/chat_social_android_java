package com.andrew.link.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.fragment.app.Fragment;

import java.util.ArrayList;

import com.andrew.link.R;
import com.andrew.link.adapters.DocmentAdapter;
import com.andrew.link.models.DocmentModel;

public class DocumentFragment extends Fragment {

    public static DocumentFragment newInstance() {
        DocumentFragment fragment = new DocumentFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){
        final View view = inflater.inflate(R.layout.fram_message, container, false);
        ListView listView = (ListView)view.findViewById(R.id.listView);

        DocmentModel doc1 = new DocmentModel("programming Schedule", R.drawable.doc_word);
        DocmentModel doc2 = new DocmentModel("Introducing JSX-React", R.drawable.doc_word);
        DocmentModel doc3 = new DocmentModel("Handbook-JSX-TypeScript", R.drawable.doc_pdf);
        DocmentModel doc4 = new DocmentModel("CodeIgniter Tutorial", R.drawable.doc_pdf);
        DocmentModel doc5 = new DocmentModel("CodeIgniter4 User Guide", R.drawable.doc_word);

        ArrayList<Object> docment = new ArrayList<>();
        docment.add("June 24");
        docment.add(doc1);
        docment.add(doc2);
        docment.add(doc3);
        docment.add("June 10");
        docment.add(doc4);
        docment.add(doc5);
//
        listView.setAdapter(new DocmentAdapter(getContext(), docment));

        return view;
    }
}
