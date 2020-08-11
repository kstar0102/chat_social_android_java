package ar.codeslu.plax.adapters;

import android.Manifest;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;
import com.vanniktech.emoji.EmojiTextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import ar.codeslu.plax.R;
import ar.codeslu.plax.calls.CallingActivity;
import ar.codeslu.plax.calls.CallingActivityVideo;
import ar.codeslu.plax.global.AppBack;
import ar.codeslu.plax.global.Global;
import ar.codeslu.plax.lists.BlockL;
import ar.codeslu.plax.lists.UserData;

/**
 * Created by CodeSlu
 */

public class  BlockMuteA extends RecyclerView.Adapter<BlockMuteA.Holder> {

    Context context;
    FirebaseAuth mAuth;
    DatabaseReference mBlock;
    DatabaseReference mMute;
    ArrayList<String> array;
    boolean block = false;

    public BlockMuteA(ArrayList<String> array, boolean block) {
        this.array = array;
        this.block = block;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.block_raw, null, false);
        context = parent.getContext().getApplicationContext();
        mAuth = FirebaseAuth.getInstance();
        mBlock = FirebaseDatabase.getInstance().getReference(Global.BLOCK);
        mMute = FirebaseDatabase.getInstance().getReference(Global.MUTE);

        return new Holder(layoutView);
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        if (mAuth.getCurrentUser() != null) {
            if (!((AppBack) Global.mainActivity.getApplication()).shared().getBoolean("dark" + mAuth.getCurrentUser().getUid(), false)) {
                holder.name.setTextColor(Global.conMain.getResources().getColor(R.color.black));
            } else {
                holder.name.setTextColor(Global.conMain.getResources().getColor(R.color.white));
            }
        }

        try {
            if (halbine(Global.tempUser, array.get(position)) != -1) {
                holder.name.setText(Global.tempUser.get(halbine(Global.tempUser, array.get(position))).getName());

                if (Global.tempUser.get(halbine(Global.tempUser, array.get(position))).getAvatar().equals("no")) {
                    Picasso.get()
                            .load(R.drawable.profile)
                            .placeholder(R.drawable.placeholder_gray)
                            .error(R.drawable.errorimg)
                            .into(holder.ava);
                } else {
                    Picasso.get()
                            .load(Global.tempUser.get(halbine(Global.tempUser, array.get(position))).getAvatar())
                            .placeholder(R.drawable.placeholder_gray) .error(R.drawable.errorimg)

                            .into(holder.ava);
                }
            }

        } catch (NullPointerException e) {

        }

        holder.delete.setFocusableInTouchMode(false);
        holder.delete.setFocusable(false);
        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Global.check_int(context)) {
                    if (block) {
                        ((AppBack) context).getBlock();

                        if (Global.blockList.contains(array.get(position)))
                            Global.blockList.remove(array.get(position));

                        Map<String, Object> map = new HashMap<>();
                        map.put("list", Global.blockList);
                        mBlock.child(mAuth.getCurrentUser().getUid()).updateChildren(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                ((AppBack) context).setBlock();
                                Toast.makeText(context, context.getString(R.string.re_blok), Toast.LENGTH_SHORT).show();
                            }
                        })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(context, context.getString(R.string.error), Toast.LENGTH_SHORT).show();

                                    }
                                });
                    } else {
                        ((AppBack) context).getMute();

                        if (Global.mutelist.contains(array.get(position)))
                            Global.mutelist.remove(array.get(position));

                        Map<String, Object> map = new HashMap<>();
                        map.put("list", Global.mutelist);
                        mMute.child(mAuth.getCurrentUser().getUid()).updateChildren(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                ((AppBack) context).setMute();
                                Toast.makeText(context, context.getString(R.string.re_mute), Toast.LENGTH_SHORT).show();
                            }
                        })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(context, context.getString(R.string.error), Toast.LENGTH_SHORT).show();

                                    }
                                });
                    }
                } else
                    Toast.makeText(context, context.getString(R.string.check_int), Toast.LENGTH_SHORT).show();

            }
        });


    }

    @Override
    public int getItemCount() {
        return array.size();
    }

    class Holder extends RecyclerView.ViewHolder {
        EmojiTextView name;
        RoundedImageView ava;
        ImageView delete;

        Holder(View view) {
            super(view);
            name = view.findViewById(R.id.nameC);
            ava = view.findViewById(R.id.avaC);
            delete = view.findViewById(R.id.delete);
        }
    }

    public int halbine(ArrayList<UserData> ml, String id) {
        int j = 0, i = 0;
        for (i = 0; i < ml.size(); i++) {
            if (ml.get(i).getId().equals(id)) {
                j = 1;
                break;
            }

        }
        if (j == 1)
            return i;
        else
            return -1;

    }

}
