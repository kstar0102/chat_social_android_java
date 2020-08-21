package com.codeslutest.plax.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.fragment.app.Fragment;

import java.util.ArrayList;

import com.codeslutest.plax.R;
import com.codeslutest.plax.adapters.HistoryAdapter;
import com.codeslutest.plax.custom.DiaHisSelect;
import com.codeslutest.plax.models.HistoryModel;

public class HistroyFragment extends Fragment {

    public static HistroyFragment newInstance() {
        HistroyFragment histroyFragment = new HistroyFragment();
        return histroyFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){
        final View view = inflater.inflate(R.layout.fragment_history, container, false);
        ListView listView = view.findViewById(R.id.listView);
        HistoryModel historyModel1 = new HistoryModel("Janet Fowler", "Today. 4.45 PM", "2", "0",R.drawable.janate);
        HistoryModel historyModel2 = new HistoryModel("Jason Boyd", "Today. 10.25 AM", "3","0", R.drawable.jason);
        HistoryModel historyModel3 = new HistoryModel("Nicholas Dunn", "Yesterday. 9.45 PM", "0","1", R.drawable.nicolas);
        HistoryModel historyModel4 = new HistoryModel("Carol Clark", "Yesterday. 10.45 PM", "0","1", R.drawable.carol);
        HistoryModel historyModel5 = new HistoryModel("Ann Carroll", "Yesterday.4.45 PM", "0", "1",R.drawable.ann);

        ArrayList<Object> docment = new ArrayList<>();
        docment.add("Missed Calls");
        docment.add(historyModel1);
        docment.add(historyModel2);
        docment.add("Other Calls");
        docment.add(historyModel3);
        docment.add(historyModel4);
        docment.add(historyModel5);

        listView.setAdapter(new HistoryAdapter(getActivity(), docment));
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                DiaHisSelect cdd = new DiaHisSelect(getActivity(), String.valueOf(position));
                cdd.show();
                return false;
            }
        });
        return view;
    }
}
