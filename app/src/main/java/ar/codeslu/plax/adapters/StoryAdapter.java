package ar.codeslu.plax.adapters;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

import org.w3c.dom.Text;

import java.util.ArrayList;

import ar.codeslu.plax.R;
import ar.codeslu.plax.lists.StoryList;
import ar.codeslu.plax.lists.StoryListRetr;
import xute.storyview.StoryModel;
import xute.storyview.StoryView;

public class StoryAdapter extends RecyclerView.Adapter<StoryAdapter.Holder> {
Context context;
FirebaseAuth mAuth;
ArrayList<StoryListRetr> array;


    public StoryAdapter(ArrayList<StoryListRetr> array) {
        this.array = array;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.story_raw, null, false);
        context = parent.getContext();
        mAuth = FirebaseAuth.getInstance();
        return new Holder(layoutView);
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        holder.storyView.setImageUris(array.get(position).getListS(),context);
        holder.username.setText(array.get(position).getName());
        holder.time.setText(array.get(position).getListS().get(position).time);
    }

    @Override
    public int getItemCount() {
        return array.size();
    }

    class  Holder extends RecyclerView.ViewHolder
    {
        StoryView storyView;
        TextView username,time;
        Holder(View itemView) {
            super(itemView);
            storyView = itemView.findViewById(R.id.storyView);
            username = itemView.findViewById(R.id.username);
            time = itemView.findViewById(R.id.timeS);
        }
    }


}
