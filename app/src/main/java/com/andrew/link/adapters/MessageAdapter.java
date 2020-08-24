package com.andrew.link.adapters;

import android.content.Context;
import android.icu.util.Calendar;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import com.andrew.link.R;
import com.andrew.link.models.MessageModel;

public class MessageAdapter extends ArrayAdapter {
    ArrayList listItem = new ArrayList<>();
    public MessageAdapter(@NonNull Context context, int textViewResourceId, ArrayList objects) {
        super(context, textViewResourceId, objects);
        listItem = objects;
    }

    @Override
    public int getCount() {
        return super.getCount();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        MessageModel model = (MessageModel) listItem.get(position);

        View v = convertView;
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        v = inflater.inflate(R.layout.item_dialog_custom, null);
        TextView badge = v.findViewById(R.id.dialogUnreadBubble);
        badge.setVisibility(View.GONE);
        TextView name = (TextView) v.findViewById(R.id.dialogName);
        TextView time = v.findViewById(R.id.dialogDate);

        Date currentTime = Calendar.getInstance().getTime();
        DateFormat format = new SimpleDateFormat("hh:mm aa");
        String timee = format.format(currentTime);
        time.setText(timee);
        TextView des = (TextView) v.findViewById(R.id.dialogLastMessage);
        ImageView imageView = (ImageView) v.findViewById(R.id.dialogAvatarC);
        name.setText(model.getListname());
        des.setText(model.getListdes());
        imageView.setImageResource(model.getListImage());
        return v;

    }
}
