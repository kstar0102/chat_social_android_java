package ar.codeslu.plax.custom;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

import ar.codeslu.plax.R;
import ar.codeslu.plax.global.AppBack;
import ar.codeslu.plax.global.Global;

/**
 * Created by CodeSlu on 05/03/19.
 */


public class FavSelect extends Dialog {

    private Context c;
    public Dialog d;
    private Button  unstar;
    private FirebaseAuth mAuth;
    private DatabaseReference  mFav;
    private String messID;
    LinearLayout dialogM;



    public FavSelect(Context context, String friendid) {
        super(context);
        this.c = context;
        this.messID = friendid;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.select_dialog_fav);
        unstar = findViewById(R.id.unstar);
        dialogM = findViewById(R.id.dialogM);

        if (Global.DARKSTATE)
            dialogM.setBackground(ContextCompat.getDrawable(c, R.drawable.dialog_d));
        else
            dialogM.setBackground(ContextCompat.getDrawable(c, R.drawable.dialog_w));

        //firebase
        mAuth = FirebaseAuth.getInstance();
        mFav = FirebaseDatabase.getInstance().getReference(Global.FAV);
        unstar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                mFav.child(mAuth.getCurrentUser().getUid()).child(messID).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                    }
                });
            }
        });

    }
}