package com.codeslutest.plax.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SectionIndexer;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;
import com.vanniktech.emoji.EmojiTextView;

import java.util.ArrayList;
import java.util.List;

import com.codeslutest.plax.R;
import com.codeslutest.plax.global.AppBack;
import com.codeslutest.plax.global.Global;
import com.codeslutest.plax.lists.UserData;

/**
 * Created by CodeSlu on 04/11/18.
 */

public class InviteAdapter extends RecyclerView.Adapter<InviteAdapter.UserListViewHolder> implements SectionIndexer {

    ArrayList<UserData> userList;
    private ArrayList<Integer> mSectionPositions;
    Context context;
    //Firebase
    FirebaseAuth mAuth;

    public InviteAdapter(ArrayList<UserData> userList) {
        this.userList = userList;

    }

    @NonNull
    @Override
    public UserListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_row_i, null, false);
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
            if (!((AppBack) context.getApplicationContext()).shared().getBoolean("dark" + mAuth.getCurrentUser().getUid(), false)) {
                holder.name.setTextColor(context.getResources().getColor(R.color.black));
                holder.statue.setTextColor(context.getResources().getColor(R.color.mid_grey));
            } else {
                holder.name.setTextColor(context.getResources().getColor(R.color.white));
                holder.statue.setTextColor(context.getResources().getColor(R.color.light_mid_grey));
            }
        }
        holder.statue.setText(context.getResources().getString(R.string.inviteuser)+" "+context.getResources().getString(R.string.app_name));
        holder.name.setText(userList.get(position).getNameL());

            Picasso.get()
                    .load(R.drawable.profile)
                    .placeholder(R.drawable.placeholder_gray) .error(R.drawable.errorimg)

                    .into(holder.ava);


            holder.on_wire.setVisibility(View.GONE);

//avatar
        holder.ava.setFocusableInTouchMode(false);
        holder.ava.setFocusable(false);
        holder.ava.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Global.inviteNums.indexOf(userList.get(position).getPhone()) == -1) {
                    Global.inviteNums.add(userList.get(position).getPhone());
                    holder.overlay.setVisibility(View.VISIBLE);
                    holder.done.setVisibility(View.VISIBLE);
                } else {
                    Global.inviteNums.remove(Global.inviteNums.indexOf(userList.get(position).getPhone()));
                    holder.overlay.setVisibility(View.GONE);
                    holder.done.setVisibility(View.GONE);
                }

            }
        });
        //All layout
        holder.ly.setFocusableInTouchMode(false);
        holder.ly.setFocusable(false);
        holder.ly.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(Global.inviteNums.size() < 256) {
                    if (Global.inviteNums.indexOf(userList.get(position).getPhone()) == -1) {
                        Global.inviteNums.add(userList.get(position).getPhone());
                        holder.overlay.setVisibility(View.VISIBLE);
                        holder.done.setVisibility(View.VISIBLE);
                    } else {
                        Global.inviteNums.remove(Global.inviteNums.indexOf(userList.get(position).getPhone()));
                        holder.overlay.setVisibility(View.GONE);
                        holder.done.setVisibility(View.GONE);
                    }
                }else
                    Toast.makeText(context, context.getString(R.string.reach_max), Toast.LENGTH_SHORT).show();

            }
        });
        //avatar
        holder.callV.setFocusableInTouchMode(false);
        holder.callV.setFocusable(false);
        holder.callV.setVisibility(View.GONE);

        //avatar
        holder.callA.setFocusableInTouchMode(false);
        holder.callA.setFocusable(false);
        holder.callA.setVisibility(View.GONE);

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
            if(!userList.get(i).getNameL().isEmpty()) {
                String section = String.valueOf(userList.get(i).getNameL().charAt(0)).toUpperCase();
                if (!sections.contains(section)) {
                    sections.add(section);
                    mSectionPositions.add(i);
                }
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
        RoundedImageView ava, on_wire, overlay;
        ImageView callA, callV,done;
        RelativeLayout ly;

        UserListViewHolder(View view) {
            super(view);
            name = view.findViewById(R.id.nameC);
            overlay = view.findViewById(R.id.overlay);
            ava = view.findViewById(R.id.avaC);
            on_wire = view.findViewById(R.id.on_wire);
            statue = view.findViewById(R.id.statue);
            done = view.findViewById(R.id.done);
            callA = view.findViewById(R.id.callA);
            callV = view.findViewById(R.id.callV);
            ly = view.findViewById(R.id.lyContact);

        }
    }

}