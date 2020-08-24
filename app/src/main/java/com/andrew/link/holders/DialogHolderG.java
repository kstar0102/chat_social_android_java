package com.andrew.link.holders;

import android.view.View;
import android.widget.ImageView;

import com.google.firebase.auth.FirebaseAuth;
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;
import com.stfalcon.chatkit.dialogs.DialogsListAdapterG;

import com.andrew.link.R;
import com.andrew.link.global.AppBack;
import com.andrew.link.global.Global;
import com.andrew.link.models.GroupDialog;

/**
 * Created by CodeSlu on 21/03/19.
 */


public class DialogHolderG extends DialogsListAdapterG.DialogViewHolder<GroupDialog> {

    FirebaseAuth mAuth;
    RoundedImageView ava;
    ImageView mute, block;

    public DialogHolderG(View itemView) {
        super(itemView);
    }

    @Override
    public void onBind(GroupDialog dialog) {
        super.onBind(dialog);
        mAuth = FirebaseAuth.getInstance();
        ava = itemView.findViewById(R.id.dialogAvatarC);
        mute = itemView.findViewById(R.id.mute);
        block = itemView.findViewById(R.id.block);

        try {

            block.setVisibility(View.GONE);

            if (Global.mutelist.contains(dialog.getId()))
                mute.setVisibility(View.VISIBLE);
            else
                mute.setVisibility(View.GONE);


        } catch (NullPointerException e) {

        }


        try {
            if (String.valueOf(dialog.getDialogPhoto()).equals("no")) {
                Picasso.get()
                        .load(R.drawable.group)
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