package ar.codeslu.plax.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import ar.codeslu.plax.BuildConfig;
import ar.codeslu.plax.Chat;
import ar.codeslu.plax.R;
import ar.codeslu.plax.floatdepart.AddFriend;
import ar.codeslu.plax.models.AddFriendModel;
import ar.codeslu.plax.models.FriModel;

public class SearchAdapter extends ArrayAdapter {
    ArrayList listItem = new ArrayList<>();

    public SearchAdapter(Context context, int textViewResourceId, ArrayList objects) {
        super(context, textViewResourceId, objects);
        listItem = objects;
    }

    @Override
    public int getCount() {
        return super.getCount();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        AddFriendModel model = (AddFriendModel) listItem.get(position);

        View v = convertView;
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        v = inflater.inflate(R.layout.item_search_friend, null);
        TextView name = (TextView) v.findViewById(R.id.dialogName);
        TextView des = (TextView) v.findViewById(R.id.dialogLastMessage);
        ImageView imageView = (ImageView) v.findViewById(R.id.dialogAvatarC);
        Button Addfriend = (Button)v.findViewById(R.id.add_btn);
        name.setText(model.getName());
        des.setText(model.getPhone());
        imageView.setImageResource(R.drawable.profile);
        Addfriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), Chat.class);
                intent.putExtra("id", model.getId());
                intent.putExtra("name", model.getName());
                intent.putExtra("ava", model.getAva());
                intent.putExtra("phone", model.getPhone());
                getContext().startActivity(intent);
            }
        });
        return v;

    }
}
