package com.andrew.link.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import com.andrew.link.R;
import com.andrew.link.models.BroadModel;

public class ContactAdapter extends BaseAdapter {
    private ArrayList<Object> personArray;
    private LayoutInflater inflater;
    private static final int TYPE_PERSON = 0;
    private static final int TYPE_DIVIDER = 1;

    public ContactAdapter(Context context, ArrayList<Object> personArray) {
        this.personArray = personArray;
        this.inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return personArray.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public Object getItem(int position) {
        return personArray.get(position);
    }

    @Override
    public int getViewTypeCount() {
        // TYPE_PERSON and TYPE_DIVIDER
        return 2;
    }

    @Override
    public int getItemViewType(int position) {
        if (getItem(position) instanceof BroadModel) {
            return TYPE_PERSON;
        }

        return TYPE_DIVIDER;
    }

    @Override
    public boolean isEnabled(int position) {
        return (getItemViewType(position) == TYPE_PERSON);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        int type = getItemViewType(position);
        if (convertView == null) {
            switch (type) {
                case TYPE_PERSON:
                    convertView = inflater.inflate(R.layout.item_contactlist_body, parent, false);
                    break;
                case TYPE_DIVIDER:
                    convertView = inflater.inflate(R.layout.item_broadlist_header, parent, false);
                    break;
            }
        }

        switch (type) {
            case TYPE_PERSON:
                BroadModel broad = (BroadModel)getItem(position);
                ImageView avaimg = (ImageView)convertView.findViewById(R.id.dialogAvatarC);
                TextView name = convertView.findViewById(R.id.dialogName);
                TextView des = convertView.findViewById(R.id.dialogLastMessage);
                avaimg.setImageResource(broad.getImage());
                name.setText(broad.getName());
                des.setText(broad.getDes());
                break;
            case TYPE_DIVIDER:
                TextView title = (TextView)convertView.findViewById(R.id.header_section);
                String titleString = (String)getItem(position);
                title.setText(titleString);
                break;
        }

        return convertView;
    }
}
