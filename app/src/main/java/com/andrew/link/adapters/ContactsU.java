package com.andrew.link.adapters;

import android.Manifest;
import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SectionIndexer;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.NativeExpressAdView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
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
import java.util.List;

import com.andrew.link.Chat;
import com.andrew.link.R;
import com.andrew.link.calls.CallingActivity;
import com.andrew.link.calls.CallingActivityVideo;
import com.andrew.link.global.AppBack;
import com.andrew.link.global.Global;
import com.andrew.link.lists.BlockL;
import com.andrew.link.lists.UserData;


/**
 * Created by CodeSlu on 04/11/18.
 */

public class ContactsU extends RecyclerView.Adapter<ContactsU.UserListViewHolder> implements SectionIndexer {

    ArrayList<UserData> userList;
    private ArrayList<Integer> mSectionPositions;

    Context cn, context;

    //Firebase
    FirebaseAuth mAuth;
    DatabaseReference mlogs, mBlock;


    public ContactsU(ArrayList<UserData> userList) {
        this.userList = userList;

    }

    @NonNull
    @Override
    public UserListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_row, null, false);
        RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutView.setLayoutParams(lp);
        cn = parent.getContext();
        context = cn.getApplicationContext();
        mAuth = FirebaseAuth.getInstance();
        mlogs = FirebaseDatabase.getInstance().getReference(Global.CALLS);
        mBlock = FirebaseDatabase.getInstance().getReference(Global.BLOCK);

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

        if (String.valueOf(userList.get(position).getAvatar()).equals("no")||String.valueOf(userList.get(position).getAvatar()).isEmpty()) {
            Picasso.get()
                    .load(R.drawable.profile)
                    .placeholder(R.drawable.placeholder_gray).error(R.drawable.errorimg)

                    .into(holder.ava);
        } else {
            Picasso.get()
                    .load(userList.get(position).getAvatar())
                    .placeholder(R.drawable.placeholder_gray).error(R.drawable.errorimg)

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
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
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
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        });
        //avatar
        holder.callV.setFocusableInTouchMode(false);
        holder.callV.setFocusable(false);
        holder.callV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Dexter.withActivity(Global.mainActivity)
                        .withPermissions(Manifest.permission.RECORD_AUDIO, Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE)
                        .withListener(new MultiplePermissionsListener() {
                            @Override
                            public void onPermissionsChecked(MultiplePermissionsReport report) {
                                if (report.areAllPermissionsGranted()) {
                                    try {
                                        if (Global.check_int(context)) {
                                            holder.callV.setEnabled(false);

                                            mBlock.child(userList.get(position).getId()).addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                    if (dataSnapshot.exists()) {
                                                        ((AppBack) context).getBlock();
                                                        ((AppBack) context).getBlockCurr(userList.get(position).getId());

                                                        BlockL blockL = dataSnapshot.getValue(BlockL.class);
                                                        Global.blockList.clear();
                                                        Global.blockList = blockL.getList();
                                                        ((AppBack) context).setBlockCurr(userList.get(position).getId());
                                                        holder.callV.setEnabled(true);

                                                        if (!Global.currblockList.contains(mAuth.getCurrentUser().getUid()) && !Global.blockList.contains(userList.get(position).getId()) && !userList.get(position).getPhone().equals("t88848992hisuseri9483828snothereri9949ghtnow009933")) {
                                                            String callid = mAuth.getCurrentUser().getUid() + System.currentTimeMillis();
                                                            Intent jumptocall = new Intent(context, CallingActivityVideo.class);
                                                            jumptocall.putExtra("UserId", userList.get(position).getId());
                                                            jumptocall.putExtra("name", userList.get(position).getName());
                                                            jumptocall.putExtra("ava", userList.get(position).getAvatar());
                                                            jumptocall.putExtra("id", userList.get(position).getId());
                                                            jumptocall.putExtra("channel_id", callid);
                                                            jumptocall.putExtra("out", true);
                                                            jumptocall.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                                            context.startActivity(jumptocall);
                                                        } else
                                                            Toast.makeText(context, R.string.cannot_user, Toast.LENGTH_SHORT).show();

                                                    } else {
                                                        holder.callV.setEnabled(true);

                                                        ((AppBack) context.getApplicationContext()).getBlock();
                                                        if (!Global.blockList.contains(userList.get(position).getId())) {
                                                            String callid = mAuth.getCurrentUser().getUid() + System.currentTimeMillis();
                                                            Intent jumptocall = new Intent(context, CallingActivityVideo.class);
                                                            jumptocall.putExtra("UserId", userList.get(position).getId());
                                                            jumptocall.putExtra("name", userList.get(position).getName());
                                                            jumptocall.putExtra("ava", userList.get(position).getAvatar());

                                                            jumptocall.putExtra("id", userList.get(position).getId());
                                                            jumptocall.putExtra("channel_id", callid);
                                                            jumptocall.putExtra("out", true);
                                                            jumptocall.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                                                            context.startActivity(jumptocall);
                                                        } else
                                                            Toast.makeText(context, R.string.cannot_user, Toast.LENGTH_SHORT).show();
                                                    }
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                                }
                                            });
                                        } else
                                            Toast.makeText(context, R.string.check_int, Toast.LENGTH_SHORT).show();
                                    } catch (NullPointerException e) {
                                        Toast.makeText(context, context.getResources().getString(R.string.error), Toast.LENGTH_SHORT).show();
                                        holder.callV.setEnabled(true);

                                    }
                                } else
                                    Toast.makeText(context, context.getResources().getString(R.string.acc_per), Toast.LENGTH_SHORT).show();


                            }

                            @Override
                            public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {

                                token.continuePermissionRequest();

                            }
                        }).check();

            }
        });
        //avatar
        holder.callA.setFocusableInTouchMode(false);
        holder.callA.setFocusable(false);
        holder.callA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Dexter.withActivity(Global.mainActivity)
                        .withPermissions(Manifest.permission.RECORD_AUDIO, Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE)
                        .withListener(new MultiplePermissionsListener() {
                            @Override
                            public void onPermissionsChecked(MultiplePermissionsReport report) {
                                if (report.areAllPermissionsGranted()) {
                                    try {
                                        if (Global.check_int(context)) {
                                            holder.callA.setEnabled(false);

                                            mBlock.child(userList.get(position).getId()).addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                    try {
                                                        if (dataSnapshot.exists()) {


                                                            ((AppBack) context).getBlock();
                                                            ((AppBack) context).getBlockCurr(userList.get(position).getId());
                                                            BlockL blockL = dataSnapshot.getValue(BlockL.class);
                                                            Global.blockList.clear();
                                                            Global.blockList = blockL.getList();
                                                            ((AppBack) context).setBlockCurr(userList.get(position).getId());

                                                            holder.callA.setEnabled(true);

                                                            if (!Global.currblockList.contains(mAuth.getCurrentUser().getUid()) && !Global.blockList.contains(userList.get(position).getId()) && !userList.get(position).getPhone().equals("t88848992hisuseri9483828snothereri9949ghtnow009933")) {
                                                                String callid = mAuth.getCurrentUser().getUid() + System.currentTimeMillis();
                                                                Intent jumptocall = new Intent(context, CallingActivity.class);
                                                                jumptocall.putExtra("name", userList.get(position).getName());
                                                                jumptocall.putExtra("ava", userList.get(position).getAvatar());
                                                                jumptocall.putExtra("out", true);
                                                                jumptocall.putExtra("channel_id", callid);
                                                                jumptocall.putExtra("UserId", userList.get(position).getId());
                                                                jumptocall.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                                                                context.startActivity(jumptocall);
                                                            } else
                                                                Toast.makeText(context, R.string.cannot_user, Toast.LENGTH_SHORT).show();
                                                        } else {

                                                            holder.callA.setEnabled(true);

                                                            if (!Global.blockList.contains(userList.get(position).getId())) {
                                                                String callid = mAuth.getCurrentUser().getUid() + System.currentTimeMillis();

                                                                Intent jumptocall = new Intent(context, CallingActivity.class);
                                                                jumptocall.putExtra("name", userList.get(position).getName());
                                                                jumptocall.putExtra("ava", userList.get(position).getAvatar());
                                                                jumptocall.putExtra("out", true);
                                                                jumptocall.putExtra("channel_id", callid);
                                                                jumptocall.putExtra("UserId", userList.get(position).getId());
                                                                jumptocall.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                                                                context.startActivity(jumptocall);
                                                            } else
                                                                Toast.makeText(context, R.string.cannot_user, Toast.LENGTH_SHORT).show();
                                                        }
                                                    } catch (NullPointerException e) {

                                                    }

                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                                }
                                            });
                                        } else
                                            Toast.makeText(context, R.string.check_int, Toast.LENGTH_SHORT).show();

                                    } catch (NullPointerException e) {
                                        Toast.makeText(context, context.getResources().getString(R.string.error), Toast.LENGTH_SHORT).show();
                                        holder.callA.setEnabled(true);

                                    }
                                } else
                                    Toast.makeText(context, context.getResources().getString(R.string.acc_per), Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {

                                token.continuePermissionRequest();

                            }
                        }).check();

            }
        });
        if (Global.ADMOB_ENABLE) {
            holder.adView.loadAd(new AdRequest.Builder().build());
            holder.adView.setAdListener(new AdListener() {
                @Override
                public void onAdLoaded() {
                    super.onAdLoaded();
                    if (position % 5 == 0)
                        holder.adView.setVisibility(View.VISIBLE);
                    else
                        holder.adView.setVisibility(View.GONE);
                }


                @Override
                public void onAdOpened() {
                    super.onAdOpened();
                }

                @Override
                public void onAdLeftApplication() {
                    super.onAdLeftApplication();
                }

                @Override
                public void onAdFailedToLoad(int i) {
                    super.onAdFailedToLoad(i);
                    holder.adView.setVisibility(View.GONE);

                }

                @Override
                public void onAdClosed() {
                    super.onAdClosed();
                    holder.adView.setVisibility(View.GONE);
                }
            });
        }
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
        NativeExpressAdView adView;

        UserListViewHolder(View view) {
            super(view);
            name = view.findViewById(R.id.nameC);
            ava = view.findViewById(R.id.avaC);
            on_wire = view.findViewById(R.id.on_wire);
            statue = view.findViewById(R.id.statue);
            callA = view.findViewById(R.id.callA);
            callV = view.findViewById(R.id.callV);
            ly = view.findViewById(R.id.lyContact);
            adView = view.findViewById(R.id.adView);

        }
    }


}