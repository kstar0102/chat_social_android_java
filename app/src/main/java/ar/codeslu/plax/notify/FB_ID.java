package ar.codeslu.plax.notify;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import ar.codeslu.plax.global.Global;
import ar.codeslu.plax.lists.Tokens;


/**
 * Created by Cryp2Code on 9/4/2018.
 */

public class FB_ID extends FirebaseInstanceIdService{
    @Override
    public void onTokenRefresh() {
        super.onTokenRefresh();
        String refreshToken = FirebaseInstanceId.getInstance().getToken();
        updateToken(refreshToken);
    }

    private void updateToken(String refreshToken) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference tokens = database.getReference(Global.tokens);
        Tokens tk = new Tokens(refreshToken);
        if(FirebaseAuth.getInstance().getCurrentUser() != null)
        {
            tokens.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(tk);

        }
    }
}
