package ar.codeslu.plax.holders;

import android.util.Log;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;
import com.stfalcon.chatkit.dialogs.DialogsListAdapterG;

import ar.codeslu.plax.R;
import ar.codeslu.plax.global.AppBack;
import ar.codeslu.plax.global.Global;
import ar.codeslu.plax.models.GroupDialog;

/**
 * Created by mostafa on 21/03/19.
 */


public class DialogHolderG extends DialogsListAdapterG.DialogViewHolder<GroupDialog> {

    FirebaseAuth mAuth;
    RoundedImageView ava, avaSmall;

    public DialogHolderG(View itemView) {
        super(itemView);
    }

    @Override
    public void onBind(GroupDialog dialog) {
        super.onBind(dialog);
        mAuth = FirebaseAuth.getInstance();
        ava = itemView.findViewById(R.id.dialogAvatarC);
        avaSmall = itemView.findViewById(R.id.dialogLastMessageUserAvatarC);

        try {


            if (String.valueOf(dialog.getDialogPhoto()).equals("no")) {
                Picasso.get()
                        .load(R.drawable.group)
                        .error(R.drawable.errorimg)
                        .into(ava);
            } else {
                Picasso.get()
                        .load(dialog.getDialogPhoto())
                        .error(R.drawable.errorimg)
                        .into(ava);
            }


            if (dialog.getLastMessage().getId().equals(mAuth.getCurrentUser().getUid())) {
                if (Global.avaLocal.equals("no")) {
                    Picasso.get()
                            .load(R.drawable.group)
                            .error(R.drawable.errorimg)
                            .into(avaSmall);
                } else {
                    Picasso.get()
                            .load(Global.avaLocal)
                            .error(R.drawable.errorimg)
                            .into(avaSmall);
                }

            } else {
                if (String.valueOf(dialog.getLastSenderAva()).equals("no")) {
                    Picasso.get()
                            .load(R.drawable.group)
                            .error(R.drawable.errorimg)
                            .into(avaSmall);
                } else {
                    Picasso.get()
                            .load(dialog.getLastSenderAva())
                            .error(R.drawable.errorimg)
                            .into(avaSmall);
                }

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