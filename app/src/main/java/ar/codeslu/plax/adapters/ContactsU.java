package ar.codeslu.plax.adapters;

import android.content.Context;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SectionIndexer;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;
import com.vanniktech.emoji.EmojiTextView;

import java.util.ArrayList;
import java.util.List;

import ar.codeslu.plax.Chat;
import ar.codeslu.plax.R;
import ar.codeslu.plax.global.AppBack;
import ar.codeslu.plax.global.Global;
import ar.codeslu.plax.lists.UserData;

/**
 * Created by mostafa on 04/11/18.
 */

public class ContactsU extends RecyclerView.Adapter<ContactsU.UserListViewHolder> implements SectionIndexer {

    ArrayList<UserData> userList;
    private ArrayList<Integer> mSectionPositions;

    Context context;
    //Firebase
    FirebaseAuth mAuth;

    public ContactsU(ArrayList<UserData> userList) {
        this.userList = userList;

    }

    @NonNull
    @Override
    public UserListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_row, null, false);
        RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutView.setLayoutParams(lp);
        context = parent.getContext();
        mAuth = FirebaseAuth.getInstance();

        UserListViewHolder rcv = new UserListViewHolder(layoutView);
        return rcv;
    }

    @Override
    public void onBindViewHolder(@NonNull final UserListViewHolder holder, final int position) {
        //dark theme init
        if (mAuth.getCurrentUser() != null) {
            if (!((AppBack) Global.mainActivity.getApplication()).shared().getBoolean("dark" + mAuth.getCurrentUser().getUid(), false)) {
                holder.name.setTextColor(Global.conMain.getResources().getColor(R.color.black));
                holder.statue.setTextColor(Global.conMain.getResources().getColor(R.color.mid_grey));
            } else {
                holder.name.setTextColor(Global.conMain.getResources().getColor(R.color.white));
                holder.statue.setTextColor(Global.conMain.getResources().getColor(R.color.light_mid_grey));
            }
        }

        holder.name.setText(userList.get(position).getNameL());
        holder.statue.setText(userList.get(position).getStatue());

        if (String.valueOf(userList.get(position).getAvatar()).equals("no")) {
            Picasso.get()
                    .load(R.drawable.profile)
                    .error(R.drawable.errorimg)
                    .into(holder.ava);
        } else {
            Picasso.get()
                    .load(userList.get(position).getAvatar())
                    .error(R.drawable.errorimg)
                    .into(holder.ava);
        }
        //check int for online
        if (Global.check_int(context)) {
            holder.on_wire.setVisibility(View.VISIBLE);
            if (userList.get(position).isOnline()) {
                holder.on_wire.setBackgroundColor(context.getResources().getColor(R.color.green));
            } else {
                holder.on_wire.setBackgroundColor(context.getResources().getColor(R.color.red));
            }
        } else
            holder.on_wire.setVisibility(View.GONE);

//avatar
        holder.ava.setFocusableInTouchMode(false);
        holder.ava.setFocusable(false);
        holder.ava.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, Chat.class);
                intent.putExtra("id", userList.get(position).getId());
                intent.putExtra("name", userList.get(position).getName());
                intent.putExtra("ava", userList.get(position).getAvatar());
                intent.putExtra("phone", userList.get(position).getPhone());
                intent.putExtra("ccode", 1);
                intent.putExtra("screen", userList.get(position).isScreen());
                context.startActivity(intent);
            }
        });
        //All layout
        holder.ly.setFocusableInTouchMode(false);
        holder.ly.setFocusable(false);
        holder.ly.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, Chat.class);
                intent.putExtra("id", userList.get(position).getId());
                intent.putExtra("name", userList.get(position).getName());
                intent.putExtra("ava", userList.get(position).getAvatar());
                intent.putExtra("phone", userList.get(position).getPhone());
                intent.putExtra("ccode", 1);
                intent.putExtra("screen", userList.get(position).isScreen());
                context.startActivity(intent);
            }
        });
        //avatar
        holder.callV.setFocusableInTouchMode(false);
        holder.callV.setFocusable(false);
        holder.callV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Global.check_int(context))
                    Toast.makeText(context, "V", Toast.LENGTH_SHORT).show();
                else
                    Toast.makeText(context, R.string.check_int, Toast.LENGTH_SHORT).show();
            }
        });
        //avatar
        holder.callA.setFocusableInTouchMode(false);
        holder.callA.setFocusable(false);
        holder.callA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Global.check_int(context))
                    Toast.makeText(context, "A", Toast.LENGTH_SHORT).show();
                else
                    Toast.makeText(context, R.string.check_int, Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public void filterList(ArrayList<UserData> filteredList) {
        userList = filteredList;
        notifyDataSetChanged();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }
//Alpha index lib func

    @Override
    public int getSectionForPosition(int position) {
        return 0;
    }

    @Override
    public Object[] getSections() {
        List<String> sections = new ArrayList<>(26);
        mSectionPositions = new ArrayList<>(26);
        for (int i = 0, size = userList.size(); i < size; i++) {
            String section = String.valueOf(userList.get(i).getNameL().charAt(0)).toUpperCase();
            if (!sections.contains(section)) {
                sections.add(section);
                mSectionPositions.add(i);
            }
        }
        return sections.toArray(new String[0]);
    }

    @Override
    public int getPositionForSection(int sectionIndex) {
        return mSectionPositions.get(sectionIndex);
    }

    class UserListViewHolder extends RecyclerView.ViewHolder {
        EmojiTextView name, statue;
        RoundedImageView ava, on_wire;
        ImageView callA, callV;
        RelativeLayout ly;

        UserListViewHolder(View view) {
            super(view);
            name = view.findViewById(R.id.nameC);
            ava = view.findViewById(R.id.avaC);
            on_wire = view.findViewById(R.id.on_wire);
            statue = view.findViewById(R.id.statue);
            callA = view.findViewById(R.id.callA);
            callV = view.findViewById(R.id.callV);
            ly = view.findViewById(R.id.lyContact);

        }
    }

}