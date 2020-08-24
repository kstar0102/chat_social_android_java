package com.andrew.link.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

import com.andrew.link.R;
import com.andrew.link.global.Global;
import com.andrew.link.story.StoryView;
import dmax.dialog.SpotsDialog;
import xute.storyview.StoryModel;


public class ArchieveA extends RecyclerView.Adapter<ArchieveA.Holder> {
    Context context;
    FirebaseAuth mAuth;
    ArrayList<StoryModel> array;
    ArrayList<StoryModel> single;
    //compress
    ArrayList<String> localContacts, ContactsId;
    android.app.AlertDialog dialogg;

    public ArchieveA(ArrayList<StoryModel> array) {
        this.array = array;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.story_raw_a, null, false);
        context = parent.getContext();
        mAuth = FirebaseAuth.getInstance();

        //arrays init
        localContacts = new ArrayList<>();
        ContactsId = new ArrayList<>();
        //loader
        if (Global.DARKSTATE) {
            dialogg = new SpotsDialog.Builder()
                    .setContext(context)
                    .setMessage(R.string.pleasW)
                    .setTheme(R.style.darkDialog)
                    .setCancelable(true)
                    .build();
        } else {
            dialogg = new SpotsDialog.Builder()
                    .setContext(context)
                    .setMessage(R.string.pleasW)
                    .setCancelable(true)
                    .build();
        }
        return new Holder(layoutView);
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        single = new ArrayList<>();
        single.add(array.get(position));
        holder.storyView.setImageUris(single, context);
        holder.storyView.calculateSweepAngle(0,context);
        holder.name.setText(context.getResources().getString(R.string.you));

    }

    @Override
    public int getItemCount() {
        return array.size();
    }

    class Holder extends RecyclerView.ViewHolder {
        StoryView storyView;
        TextView name;

        Holder(View itemView) {
            super(itemView);
            storyView = itemView.findViewById(R.id.storyView);
            name = itemView.findViewById(R.id.username);
        }
    }


}
