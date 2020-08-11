package ar.codeslu.plax.adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.telephony.TelephonyManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;

import ar.codeslu.plax.R;
import ar.codeslu.plax.global.Global;
import ar.codeslu.plax.lists.CountryToPhonePrefix;
import ar.codeslu.plax.lists.StoryList;
import ar.codeslu.plax.story.StoryView;
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
