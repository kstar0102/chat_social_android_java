package ar.codeslu.plax.adapters;

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

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.NativeExpressAdView;
import com.google.firebase.auth.FirebaseAuth;
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;
import com.stfalcon.chatkit.me.GroupIn;
import com.vanniktech.emoji.EmojiTextView;

import java.util.ArrayList;
import java.util.List;

import ar.codeslu.plax.Groups.Group;
import ar.codeslu.plax.R;
import ar.codeslu.plax.global.AppBack;
import ar.codeslu.plax.global.Global;
import ar.codeslu.plax.global.encryption;
import ar.codeslu.plax.lists.UserData;


/**
 * Created by CodeSlu on 04/11/18.
 */

public class ForwardAdapterG extends RecyclerView.Adapter<ForwardAdapterG.UserListViewHolder> implements SectionIndexer {

    ArrayList<GroupIn> userList;
    private ArrayList<Integer> mSectionPositions;
    Context context;
    //Firebase
    FirebaseAuth mAuth;


    public ForwardAdapterG(ArrayList<GroupIn> userList) {
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
        Global.forwardids = new ArrayList<>();

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

        holder.name.setText(userList.get(position).getName());
        holder.statue.setVisibility(View.GONE);

        if (String.valueOf(encryption.decryptOrNull(userList.get(position).getAvatar())).equals("no")) {
            Picasso.get()
                    .load(R.drawable.profile)
                    .placeholder(R.drawable.placeholder_gray) .error(R.drawable.errorimg)

                    .into(holder.ava);
        } else {
            Picasso.get()
                    .load(encryption.decryptOrNull(userList.get(position).getAvatar()))
                    .placeholder(R.drawable.placeholder_gray) .error(R.drawable.errorimg)

                    .into(holder.ava);
        }

            holder.on_wire.setVisibility(View.GONE);

//avatar
        holder.ava.setFocusableInTouchMode(false);
        holder.ava.setFocusable(false);
        holder.ava.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Global.forwardids.indexOf(userList.get(position).getId()) == -1) {
                    Global.forwardids.add(userList.get(position).getId());
                    holder.overlay.setVisibility(View.VISIBLE);
                    holder.done.setVisibility(View.VISIBLE);
                } else {
                    Global.forwardids.remove(Global.forwardids.indexOf(userList.get(position).getId()));
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
                if(Global.forwardids.size() < 256) {
                    if (Global.forwardids.indexOf(userList.get(position).getId()) == -1) {
                        Global.forwardids.add(userList.get(position).getId());
                        holder.overlay.setVisibility(View.VISIBLE);
                        holder.done.setVisibility(View.VISIBLE);
                    } else {
                        Global.forwardids.remove(Global.forwardids.indexOf(userList.get(position).getId()));
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
        return userList.size();
    }

    public void filterList(ArrayList<GroupIn> filteredList) {
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
            String section = String.valueOf(userList.get(i).getName().charAt(0)).toUpperCase();
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
        RoundedImageView ava, on_wire, overlay;
        ImageView callA, callV,done;
        RelativeLayout ly;
        NativeExpressAdView adView;

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
            adView =  view.findViewById(R.id.adView);

        }
    }

}