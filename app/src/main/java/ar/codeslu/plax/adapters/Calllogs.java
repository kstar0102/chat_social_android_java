package ar.codeslu.plax.adapters;

import android.Manifest;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.NativeExpressAdView;
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
import ar.codeslu.plax.lists.calls;


public class Calllogs extends RecyclerView.Adapter<Calllogs.Holder> {
    Context context;
    FirebaseAuth mAuth;
    DatabaseReference mlogs, mBlock, mUser;
    String called = "";


    public Calllogs() {
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.call_row, null, false);
        context = parent.getContext().getApplicationContext();
        mAuth = FirebaseAuth.getInstance();
        mlogs = FirebaseDatabase.getInstance().getReference(Global.CALLS);
        mBlock = FirebaseDatabase.getInstance().getReference(Global.BLOCK);
        mUser = FirebaseDatabase.getInstance().getReference(Global.USERS);

        return new Holder(layoutView);
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {

        if (mAuth.getCurrentUser() != null) {
            if (!((AppBack) Global.mainActivity.getApplication()).shared().getBoolean("dark" + mAuth.getCurrentUser().getUid(), false)) {
                holder.name.setTextColor(Global.conMain.getResources().getColor(R.color.black));
                holder.time.setTextColor(Global.conMain.getResources().getColor(R.color.mid_grey));
            } else {
                holder.name.setTextColor(Global.conMain.getResources().getColor(R.color.white));
                holder.time.setTextColor(Global.conMain.getResources().getColor(R.color.light_mid_grey));
            }
        }

        holder.name.setText(Global.callList.get(position).getName());
        holder.time.setText(AppBack.getTimeAgo(Global.callList.get(position).getTime(), context));

        try {
            if(!Global.callList.get(position).getAva().isEmpty()) {
                if (String.valueOf(Global.callList.get(position).getAva()).equals("no")) {
                    Picasso.get()
                            .load(R.drawable.profile)
                            .placeholder(R.drawable.placeholder_gray) .error(R.drawable.errorimg)

                            .into(holder.ava);
                } else {
                    Picasso.get()
                            .load(Global.callList.get(position).getAva())
                            .placeholder(R.drawable.placeholder_gray) .error(R.drawable.errorimg)

                            .into(holder.ava);
                }
            }
        }catch (NullPointerException e)
        {
            Picasso.get()
                    .load(R.drawable.profile)
                    .placeholder(R.drawable.placeholder_gray) .error(R.drawable.errorimg)
                    .into(holder.ava);
        }

        if (mAuth.getCurrentUser() != null)
        {
            try {
                if (Global.callList.get(position).getFrom().equals(mAuth.getCurrentUser().getUid())) {
                    holder.state.setImageResource(R.drawable.ic_outc);
                }
                else {
                    try {
                        if (Global.callList.get(position).getDur() == 0)
                            holder.state.setImageResource(R.drawable.ic_miss);

                        else
                            holder.state.setImageResource(R.drawable.ic_in);
                    } catch (NullPointerException e) {
                        holder.state.setImageResource(0);

                    }


                }
            }
            catch (NullPointerException e)
            {

            }


        }


        //call
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


                                            if (!Global.callList.get(position).getFrom().equals(mAuth.getCurrentUser().getUid()))
                                                called = Global.callList.get(position).getFrom();
                                            else
                                                called = Global.callList.get(position).getTo();


                                            mUser.child(called).addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                    if (dataSnapshot.exists()) {
                                                        UserData userData = dataSnapshot.getValue(UserData.class);
                                                        if (!userData.getPhone().equals("t88848992hisuseri9483828snothereri9949ghtnow009933")) {
                                                            holder.callV.setEnabled(true);

                                                            mBlock.child(called).addListenerForSingleValueEvent(new ValueEventListener() {
                                                                @Override
                                                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                                    holder.callV.setEnabled(true);
                                                                    if (dataSnapshot.exists()) {
                                                                        ((AppBack) context).getBlock();
                                                                        ((AppBack) context).getBlockCurr(called);

                                                                        BlockL blockL = dataSnapshot.getValue(BlockL.class);
                                                                        Global.blockList.clear();
                                                                        Global.blockList = blockL.getList();
                                                                        ((AppBack) context).setBlockCurr(called);
                                                                        if (!Global.currblockList.contains(mAuth.getCurrentUser().getUid()) && !Global.blockList.contains(called)) {
                                                                            String callid = mAuth.getCurrentUser().getUid()+ System.currentTimeMillis();
                                                                            Intent jumptocall = new Intent(context, CallingActivityVideo.class);
                                                                            jumptocall.putExtra("UserId", called);
                                                                            jumptocall.putExtra("name", Global.callList.get(position).getName());
                                                                            jumptocall.putExtra("ava", Global.callList.get(position).getAva());
                                                                            jumptocall.putExtra("id", called);
                                                                            jumptocall.putExtra("out", true);
                                                                            jumptocall.putExtra("channel_id", callid);
                                                                            jumptocall.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                                                                            context.startActivity(jumptocall);
                                                                        } else
                                                                            Toast.makeText(context, R.string.cannot_user, Toast.LENGTH_SHORT).show();

                                                                    } else {
                                                                        ((AppBack) context).getBlock();
                                                                        if (!Global.blockList.contains(called)) {
                                                                            String callid = mAuth.getCurrentUser().getUid()+ System.currentTimeMillis();
                                                                            Intent jumptocall = new Intent(context, CallingActivityVideo.class);
                                                                            jumptocall.putExtra("UserId", called);
                                                                            jumptocall.putExtra("name", Global.callList.get(position).getName());
                                                                            jumptocall.putExtra("ava", Global.callList.get(position).getAva());

                                                                            jumptocall.putExtra("id", called);
                                                                            jumptocall.putExtra("out", true);
                                                                            jumptocall.putExtra("channel_id", callid);
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

                                                        } else {
                                                            Toast.makeText(context, R.string.cannot_user, Toast.LENGTH_SHORT).show();
                                                            holder.callV.setEnabled(true);

                                                        }


                                                    } else {
                                                        Toast.makeText(context, R.string.cannot_user, Toast.LENGTH_SHORT).show();
                                                        holder.callV.setEnabled(true);
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

                                            if (!Global.callList.get(position).getFrom().equals(mAuth.getCurrentUser().getUid()))
                                                called = Global.callList.get(position).getFrom();
                                            else
                                                called = Global.callList.get(position).getTo();


                                            mUser.child(called).addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                    holder.callA.setEnabled(true);

                                                    if (dataSnapshot.exists()) {
                                                        UserData userData = dataSnapshot.getValue(UserData.class);
                                                        if (!userData.getPhone().equals("t88848992hisuseri9483828snothereri9949ghtnow009933")) {


                                                            mBlock.child(called).addListenerForSingleValueEvent(new ValueEventListener() {
                                                                @Override
                                                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                                    holder.callA.setEnabled(true);

                                                                    if (dataSnapshot.exists()) {
                                                                        ((AppBack) context).getBlock();
                                                                        ((AppBack) context).getBlockCurr(called);
                                                                        BlockL blockL = dataSnapshot.getValue(BlockL.class);
                                                                        Global.blockList.clear();
                                                                        Global.blockList = blockL.getList();
                                                                        ((AppBack) context).setBlockCurr(called);


                                                                        if (!Global.currblockList.contains(mAuth.getCurrentUser().getUid()) && !Global.blockList.contains(called)) {
                                                                            String callid = mAuth.getCurrentUser().getUid()+ System.currentTimeMillis();

                                                                            Intent jumptocall = new Intent(context, CallingActivity.class);
                                                                            jumptocall.putExtra("name", Global.callList.get(position).getName());
                                                                            jumptocall.putExtra("ava", Global.callList.get(position).getAva());
                                                                            jumptocall.putExtra("out", true);
                                                                            jumptocall.putExtra("channel_id", callid);
                                                                            jumptocall.putExtra("UserId", called);
                                                                            jumptocall.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);


                                                                            context.startActivity(jumptocall);
                                                                        } else
                                                                            Toast.makeText(context, R.string.cannot_user, Toast.LENGTH_SHORT).show();
                                                                    } else {
                                                                        ((AppBack) context).getBlock();
                                                                        if (!Global.blockList.contains(called)) {
                                                                            String callid = mAuth.getCurrentUser().getUid()+ System.currentTimeMillis();

                                                                            Intent jumptocall = new Intent(context, CallingActivity.class);
                                                                            jumptocall.putExtra("name", Global.callList.get(position).getName());
                                                                            jumptocall.putExtra("ava", Global.callList.get(position).getAva());
                                                                            jumptocall.putExtra("out", true);
                                                                            jumptocall.putExtra("channel_id", callid);
                                                                            jumptocall.putExtra("UserId", called);
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
                                                            Toast.makeText(context, R.string.cannot_user, Toast.LENGTH_SHORT).show();


                                                    } else
                                                        Toast.makeText(context, R.string.cannot_user, Toast.LENGTH_SHORT).show();
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                                }
                                            });

                                        } else
                                            Toast.makeText(context, R.string.check_int, Toast.LENGTH_SHORT).show();
                                    } catch (NullPointerException e) {
                                        Toast.makeText(context, context.getResources().getString(R.string.error), Toast.LENGTH_SHORT).show();
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
        if(Global.ADMOB_ENABLE) {
            holder.adView.loadAd(new AdRequest.Builder().build());
            holder.adView.setAdListener(new AdListener() {
                @Override
                public void onAdLoaded() {
                    super.onAdLoaded();
                    if( position %5 == 0)
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
        return Global.callList.size();
    }

    class Holder extends RecyclerView.ViewHolder {
        EmojiTextView name, time;
        RoundedImageView ava;
        ImageView callA, callV, state;
        NativeExpressAdView adView;

        Holder(View view) {
            super(view);
            name = view.findViewById(R.id.nameC);
            ava = view.findViewById(R.id.avaC);
            time = view.findViewById(R.id.time);
            callA = view.findViewById(R.id.callA);
            callV = view.findViewById(R.id.callV);
            state = view.findViewById(R.id.state);
            adView =  view.findViewById(R.id.adView);
        }
    }


    private String getHumanTimeText(long milliseconds) {
        return String.format("%02d:%02d",
                TimeUnit.MILLISECONDS.toMinutes(milliseconds),
                TimeUnit.MILLISECONDS.toSeconds(milliseconds) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(milliseconds)));
    }


}
