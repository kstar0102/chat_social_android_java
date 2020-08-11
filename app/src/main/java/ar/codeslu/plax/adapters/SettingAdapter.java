package ar.codeslu.plax.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

import ar.codeslu.plax.R;
import ar.codeslu.plax.global.AppBack;
import ar.codeslu.plax.global.Global;
import ar.codeslu.plax.settings.SettingList;

/**
 * Created by CodeSlu on 22/03/19.
 */


public class SettingAdapter extends BaseAdapter {
    ArrayList<SettingList> list;
    Context conn;
    TextView name;
    ImageView photo;

    //firebase
    FirebaseAuth mAuth;

    public SettingAdapter(ArrayList<SettingList> list, Context conn) {
        this.list = list;
        this.conn = conn;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int i) {
        return list.get(i).getItemName();
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        view = view.inflate(conn, R.layout.setting_row, null);
        name = view.findViewById(R.id.settingName);
        photo = view.findViewById(R.id.settingPhoto);
        mAuth = FirebaseAuth.getInstance();
        if (mAuth.getCurrentUser() != null) {
            if (!((AppBack) conn.getApplicationContext()).shared().getBoolean("dark" + mAuth.getCurrentUser().getUid(), false))
                name.setTextColor( conn.getApplicationContext().getResources().getColor(R.color.black));
                else
                name.setTextColor( conn.getApplicationContext().getResources().getColor(R.color.white));
        }
        name.setText(list.get(i).getItemName());
        switch (list.get(i).getItemPhoto())
        {
            case 0:
                photo.setImageDrawable(conn.getResources().getDrawable(R.drawable.ic_chat_b));
                break;
            case 1:
                photo.setImageDrawable(conn.getResources().getDrawable(R.drawable.ic_call_b));
                break;
            case 2:
                photo.setImageDrawable(conn.getResources().getDrawable(R.drawable.ic_privacy));
                break;
          case 3:
                photo.setImageDrawable(conn.getResources().getDrawable(R.drawable.ic_notifications_b));
                break;
            case 4:
                photo.setImageDrawable(conn.getResources().getDrawable(R.drawable.ic_language_b));
                break;
            case 5:
                photo.setImageDrawable(conn.getResources().getDrawable(R.drawable.ic_delete_b));
                break;
            case 6:
                photo.setImageDrawable(conn.getResources().getDrawable(R.drawable.ic_priority_b));
                break;
            case 7:
                photo.setImageDrawable(conn.getResources().getDrawable(R.drawable.ic_info_b));
                break;
        }
        return view;
    }
}