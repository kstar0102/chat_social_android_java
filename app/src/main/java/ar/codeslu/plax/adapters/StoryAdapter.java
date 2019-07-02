package ar.codeslu.plax.adapters;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

import ar.codeslu.plax.R;
import ar.codeslu.plax.lists.StoryList;
import xute.storyview.StoryModel;
import xute.storyview.StoryView;

public class StoryAdapter extends RecyclerView.Adapter<StoryAdapter.Holder> {
Context context;
FirebaseAuth mAuth;
ArrayList<StoryList> array;


    public StoryAdapter(ArrayList<StoryList> array) {
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
        holder.storyView.resetStoryVisits();
        ArrayList<StoryModel> uris = new ArrayList<>();
        uris.add(new StoryModel("https://www.planwallpaper.com/static/images/animals-4.jpg","Steve","Yesterday"));
        uris.add(new StoryModel("https://static.boredpanda.com/blog/wp-content/uuuploads/albino-animals/albino-animals-3.jpg","Grambon","10:15 PM"));
        holder.storyView.setImageUris(uris);
    }

    @Override
    public int getItemCount() {
        return array.size();
    }

    class  Holder extends RecyclerView.ViewHolder
    {
        StoryView storyView;
        Holder(View itemView) {
            super(itemView);
            storyView = itemView.findViewById(R.id.storyView);
        }
    }


}
