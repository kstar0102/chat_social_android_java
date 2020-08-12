package ar.codeslu.plax.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.fragment.app.Fragment;

import com.alphabetik.Alphabetik;

import java.util.ArrayList;

import ar.codeslu.plax.R;
import ar.codeslu.plax.adapters.BroadAdapter;
import ar.codeslu.plax.adapters.ContactAdapter;
import ar.codeslu.plax.adapters.DocmentAdapter;
import ar.codeslu.plax.floatdepart.BroadcastActivity;
import ar.codeslu.plax.models.BroadModel;
import ar.codeslu.plax.models.DocmentModel;

public class ContactFragment extends Fragment {

    final String[] orderedData = new String[]{"Ann Carroll", "Carol Clark", "Janet Fowler", "Jason Boyd", "Jeffery Nicholas Dunn"};
    public static ContactFragment newInstance() {
        ContactFragment fragment = new ContactFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){
        final View view = inflater.inflate(R.layout.fragment_contact , container, false);

        ListView listView = (ListView)view.findViewById(R.id.listView);

        BroadModel broad1 = new BroadModel("Ann Carroll", "+63 1234567890", R.drawable.ann);
        BroadModel broad2 = new BroadModel("Carol Clark", "+1 23452342345", R.drawable.carol);
        BroadModel broad3 = new BroadModel("Janet Fowler", "+7 51234567890", R.drawable.janate);
        BroadModel broad4 = new BroadModel("Jason Boyd", "+44 1234567890", R.drawable.jason);
        BroadModel broad5 = new BroadModel("Jeffery Lawreence", "+63 2215486154", R.drawable.jeffery);
        BroadModel broad6 = new BroadModel("Nicholas Dunn", "+966 432456324", R.drawable.nicolas);

        ArrayList<Object> docment = new ArrayList<>();
        docment.add("A");
        docment.add(broad1);
        docment.add("C");
        docment.add(broad2);
        docment.add("J");
        docment.add(broad3);
        docment.add(broad4);
        docment.add(broad5);
        docment.add("N");
        docment.add(broad6);
//
        listView.setAdapter(new ContactAdapter(getActivity(), docment));


        Alphabetik alphabetik = (Alphabetik) view.findViewById(R.id.alphSectionIndex);
        alphabetik.onSectionIndexClickListener(new Alphabetik.SectionIndexClickListener() {
            @Override
            public void onItemClick(View view, int position, String character) {
                String info = " Position = " + position + " Char = " + character;
                Log.i("View: ", view + "," + info);
                //Toast.makeText(getBaseContext(), info, Toast.LENGTH_SHORT).show();
                listView.smoothScrollToPosition(getPositionFromData(character));
            }
        });
        return view;
    }
    private int getPositionFromData(String character) {
        int position = 0;
        for (String s : orderedData) {
            String letter = "" + s.charAt(0);
            if (letter.equals("" + character)) {
                return position;
            }
            position++;
        }
        return 0;
    }
}
