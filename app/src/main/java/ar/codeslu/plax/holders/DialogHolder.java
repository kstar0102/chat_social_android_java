package ar.codeslu.plax.holders;

import android.view.View;

import com.google.firebase.auth.FirebaseAuth;
import com.stfalcon.chatkit.dialogs.DialogsListAdapter;

import ar.codeslu.plax.R;
import ar.codeslu.plax.global.AppBack;
import ar.codeslu.plax.global.Global;
import ar.codeslu.plax.models.DefaultDialog;

/**
 * Created by mostafa on 21/03/19.
 */


public class DialogHolder extends DialogsListAdapter.DialogViewHolder<DefaultDialog> {

    FirebaseAuth mAuth;
    public DialogHolder(View itemView) {
        super(itemView);
    }

    @Override
    public void onBind(DefaultDialog dialog) {
        super.onBind(dialog);
        mAuth = FirebaseAuth.getInstance();
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