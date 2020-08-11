package ar.codeslu.plax.adapters;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;
import com.vanniktech.emoji.EmojiTextView;

import java.util.ArrayList;

import ar.codeslu.plax.R;
import ar.codeslu.plax.custom.profileSelect;
import ar.codeslu.plax.global.Global;
import ar.codeslu.plax.lists.UserData;

public class GroupAdapter extends RecyclerView.Adapter<GroupAdapter.Holder> {
    Context context;
    FirebaseAuth mAuth;
    ArrayList<UserData> array;
    boolean admins;
    String groupId;

    public GroupAdapter(ArrayList<UserData> array, boolean admins,String groupid) {
        this.array = array;
        this.admins = admins;
        this.groupId = groupid;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.group_raw, null, false);
        context = parent.getContext();
        mAuth = FirebaseAuth.getInstance();
        return new Holder(layoutView);
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int i) {

        if (admins)
            holder.crown.setVisibility(View.VISIBLE);
        else
            holder.crown.setVisibility(View.GONE);

        if (String.valueOf(array.get(i).getAvatar()).equals("no")) {
            Picasso.get()
                    .load(R.drawable.profile)
                    .placeholder(R.drawable.placeholder_gray) .error(R.drawable.errorimg)

                    .into(holder.ava);
        } else {
            Picasso.get()
                    .load(array.get(i).getAvatar())
                    .placeholder(R.drawable.placeholder_gray) .error(R.drawable.errorimg)

                    .into(holder.ava);
        }

        holder.name.setText(array.get(i).getName());


        if (Global.DARKSTATE)
            holder.name.setTextColor(Color.WHITE);
        else
            holder.name.setTextColor(Color.BLACK);


        holder.ly.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if(!array.get(i).getId().equals(mAuth.getCurrentUser().getUid())) {
                    if (Global.check_int(Global.mainActivity)) {
                        profileSelect cdd = new profileSelect(context, groupId, array.get(i).getId(), admins);
                        cdd.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        cdd.getWindow().getAttributes().windowAnimations = R.style.CustomDialogAnimation;
                        cdd.show();
                    } else
                        Toast.makeText(Global.mainActivity, R.string.check_int, Toast.LENGTH_SHORT).show();
                }
                else
                    Toast.makeText(Global.mainActivity, R.string.can_mng, Toast.LENGTH_SHORT).show();

                return false;
            }
        });


    }

    @Override
    public int getItemCount() {
        return array.size();
    }

    class Holder extends RecyclerView.ViewHolder {
        ImageView crown;
        RoundedImageView ava;
        EmojiTextView name;
        LinearLayout ly;

        Holder(View itemView) {
            super(itemView);
            ava = itemView.findViewById(R.id.avaC);
            crown = itemView.findViewById(R.id.admin);
            name = itemView.findViewById(R.id.nameC);
            ly = itemView.findViewById(R.id.ly);

        }
    }


}
