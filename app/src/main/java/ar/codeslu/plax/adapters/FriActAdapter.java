package ar.codeslu.plax.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import ar.codeslu.plax.R;
import ar.codeslu.plax.models.FriModel;

public class FriActAdapter extends ArrayAdapter {
    ArrayList listItem = new ArrayList<>();

    public FriActAdapter(Context context, int textViewResourceId, ArrayList objects) {
        super(context, textViewResourceId, objects);
        listItem = objects;
    }

    @Override
    public int getCount() {
        return super.getCount();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        FriModel model = (FriModel) listItem.get(position);

        View v = convertView;
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//        v = inflater.inflate(R.layout.item_friend, null);
//        TextView textView = (TextView) v.findViewById(R.id.textView_one);
//        ImageView imageView = (ImageView) v.findViewById(R.id.img_one);
//        textView.setText(model.getListText());
//        imageView.setImageResource(R.drawable.ae);
//        imageView.setImageResource(R.drawable.ae);
//        imageView.setImageResource(R.drawable.ae);
        return v;

    }
}
