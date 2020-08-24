package com.andrew.link.holders;

import android.view.View;
import android.widget.ImageView;

import com.andrew.link.R;
import com.andrew.link.global.AppBack;
import com.andrew.link.global.Global;
import com.andrew.link.models.DefaultDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;
import com.stfalcon.chatkit.dialogs.DialogsListAdapter;
import de.hdodenhof.circleimageview.CircleImageView;


public class DialogHolder extends DialogsListAdapter.DialogViewHolder<DefaultDialog> {

    FirebaseAuth mAuth;
    RoundedImageView ava;
    CircleImageView checkonline, statuscheck;
    ImageView mute, block;

    public DialogHolder(View itemView) {
        super(itemView);
    }


    @Override
    public void onBind(DefaultDialog dialog) {
        super.onBind(dialog);
        mAuth = FirebaseAuth.getInstance();
        ava = itemView.findViewById(R.id.dialogAvatarC);
        mute = itemView.findViewById(R.id.mute);
        block = itemView.findViewById(R.id.block);
        checkonline = itemView.findViewById(R.id.dialogUnreadcheck);
        statuscheck = itemView.findViewById(R.id.statuscheck);

        if (!
                Global.onstate) {
            statuscheck.setImageResource(R.color.red);
        }else{
            statuscheck.setImageResource(R.color.green);
        }

        try {
            if (Global.mutelist.contains(dialog.getId()))
                mute.setVisibility(View.VISIBLE);
            else
                mute.setVisibility(View.GONE);

            if (Global.blockList.contains(dialog.getId()))
                block.setVisibility(View.VISIBLE);
            else
                block.setVisibility(View.GONE);
        } catch (NullPointerException e) {

        }

        try {
            if (String.valueOf(dialog.getDialogPhoto()).equals("no")) {
                Picasso.get()
                        .load(R.drawable.profile)
                        .placeholder(R.drawable.placeholder_gray).error(R.drawable.errorimg)
                        .into(ava);
            } else {
                Picasso.get()
                        .load(dialog.getDialogPhoto())
                        .placeholder(R.drawable.placeholder_gray).error(R.drawable.errorimg)
                        .into(ava);
            }
        } catch (NullPointerException e) {

        }


        //dark mode init
        if (mAuth.getCurrentUser() != null) {
            if (!((AppBack) Global.mainActivity.getApplication()).shared().getBoolean("dark" + mAuth.getCurrentUser().getUid(), false)) {
                tvName.setTextColor(Global.conMain.getResources().getColor(R.color.black));
                tvDate.setTextColor(Global.conMain.getResources().getColor(R.color.black));
                tvLastMessage.setTextColor(Global.conMain.getResources().getColor(R.color.mid_grey));
            } else {
                tvName.setTextColor(Global.conMain.getResources().getColor(R.color.white));
                tvDate.setTextColor(Global.conMain.getResources().getColor(R.color.white));
                tvLastMessage.setTextColor(Global.conMain.getResources().getColor(R.color.light_mid_grey));
            }
        }
    }
}